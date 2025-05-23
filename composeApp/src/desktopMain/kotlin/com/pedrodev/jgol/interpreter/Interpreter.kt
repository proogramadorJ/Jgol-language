package com.pedrodev.jgol.interpreter

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Void?> {

    val globals = Environment()
    private var environment = globals
    private val locals: MutableMap<Expr, Int> = mutableMapOf()

    private var outputHandler: ((String) -> Unit)? = null
    private var inputHandler: (() -> String)? = null
    private var inputCallback: ((String) -> Unit)? = null
    private var waitingForInput = false

    private var jgolInstance: Jgol? = null

    fun setJgolInstance(jgol: Jgol) {
        jgolInstance = jgol
    }

    fun setOutputHandler(handler: (String) -> Unit) {
        outputHandler = handler
    }

    fun setInputHandler(handler: () -> String) {
        inputHandler = handler
    }

    fun setInputCallback(callback: (String) -> Unit) {
        inputCallback = callback
    }

    fun isWaitingForInput(): Boolean {
        return waitingForInput
    }

    fun provideInput(input: String) {
        if (waitingForInput && inputCallback != null) {
            waitingForInput = false
            inputCallback?.invoke(input)
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }
        return value
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }

            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }
            TokenType.PLUS -> {
                when {
                    left is Double && right is Double -> left + right
                    left is String && right is String -> left + right
                    left is String && right is Double -> left + stringify(right)
                    left is Double && right is String -> stringify(left) + right
                    else -> throw RuntimeError(
                        expr.operator,
                        "Os operandos devem ser dois números ou duas strings."
                    )
                }
            }

            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }

            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            else -> null
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = expr.arguments.map { evaluate(it) }

        if (callee !is JgolCallable) {
            throw RuntimeError(expr.paren, "Só pode chamar funções e classes.")
        }

        if (arguments.size != callee.arity()) {
            throw RuntimeError(
                expr.paren,
                "Esperado ${callee.arity()} argumentos mas recebeu ${arguments.size}."
            )
        }

        return callee.call(this, arguments)
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operandos devem ser números.")
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        if (left == null && right == null) return true
        if (left == null) return false
        return left == right
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }

            else -> null
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return lookUpVariable(expr.name, expr)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val obj = evaluate(expr.obj)
        if (obj is JgolInstance) {
            return obj.get(expr.name)
        }
        throw RuntimeError(expr.name, "Apenas instancias possuem atributos.")
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj = evaluate(expr.obj)

        if (obj !is JgolInstance) {
            throw RuntimeError(expr.name, "Apenas instancias possuem atributos.")
        }

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        return lookUpVariable(expr.keyword, expr)
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        val distance: Int? = locals[expr]
        val superclass: Any? = distance?.let { environment.getAt(it, "superior") }
        val obj = distance?.let { environment.getAt(it - 1, "este") }
        val method =
            if (superclass is JgolClass) superclass.findMethod(expr.method.lexeme) else null
        if (method == null) {
            throw RuntimeError(expr.method, "Propriedade indefinida '${expr.method.lexeme}'.")
        }
        return if (method is JgolFunction) method.bind(obj) else null
    }

    override fun visitArrayLiteralExpr(expr: Expr.ArrayLiteral): Any {
        val elements = expr.elements.map { evaluate(it) }
        return elements
    }

    override fun visitArrayAccessExpr(expr: Expr.ArrayAccess): Any? {
        val array = evaluate(expr.array)
        val index = evaluate(expr.index)

        if (array !is List<*>) {
            throw RuntimeError(expr.token, "Só é possível indexar arrays.")
        }

        if (index !is Double) {
            throw RuntimeError(expr.token, "O índice do array deve ser um número.")
        }

        val list = array as List<Any?>
        val idx = index.toInt()

        if (idx < 0 || idx >= list.size) {
            throw RuntimeError(expr.token, "Índice do array fora dos limites.")
        }

        return list[idx]
    }

    override fun vistiArraySetExpr(expr: Expr.ArraySet): Any? {
        val array = evaluate(expr.array)
        val index = evaluate(expr.index)
        val value = evaluate(expr.value)

        if (array !is List<*>) {
            throw RuntimeError(expr.token, "Só é possível indexar arrays.")
        }

        if (index !is Double) {
            throw RuntimeError(expr.token, "O índice do array deve ser um número.")
        }

        val list = array as MutableList<Any?>
        val idx = index.toInt()

        if (idx < 0 || idx >= list.size) {
            throw RuntimeError(expr.token, "Índice do array fora dos limites.")
        }

        list[idx] = value
        return value
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        val distance = locals[expr]
        return if (distance != null) {
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "O operando deve ser um número.")
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                try {
                    execute(statement)
                } catch (error: RuntimeError) {
                    if (jgolInstance != null) {
                        jgolInstance?.reportRuntimeError(error)
                    } else {
                        Jgol.runtimeError(error)
                    }
                    break
                }
            }
        } catch (error: RuntimeError) {
            if (jgolInstance != null) {
                jgolInstance?.reportRuntimeError(error)
            } else {
                Jgol.runtimeError(error)
            }
        }
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    private fun execute(statement: Stmt) {
        statement.accept(this)
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nulo"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return obj.toString()
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            statements.forEach { execute(it) }
        } finally {
            this.environment = previous
        }
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void? {
        val function = JgolFunction(stmt, environment)
        environment.define(stmt.name.lexeme, function)
        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        val value = evaluate(stmt.expression)
        val output = stringify(value)
        if (outputHandler != null) {
            outputHandler?.invoke(output)
        } else {
            println(output)
        }
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void? {
        val value = if (stmt.value != null) evaluate(stmt.value) else null
        throw value?.let { Return(it) }!!
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        val value = stmt.initializer?.let { evaluate(it) }
        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void? {
        var superclass: Any? = null
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass)
            if (superclass !is JgolClass) {
                throw RuntimeError(stmt.superclass.name, "Super classe precisa ser uma classe.")
            }
        }

        environment.define(stmt.name.lexeme, null)

        if (stmt.superclass != null) {
            environment = Environment(environment)
            environment.define("superior", superclass)
        }

        val methods = mutableMapOf<String, JgolFunction>()
        stmt.methods.forEach { method ->
            val function = JgolFunction(method, environment)
            methods[method.name.lexeme] = function
        }
        val kclass = if (superclass is JgolClass) JgolClass(
            stmt.name.lexeme,
            superclass,
            methods
        ) else JgolClass(stmt.name.lexeme, null, methods)

        environment.assign(stmt.name, kclass)
        return null
    }

    override fun visitSwitchStmt(stmt: Stmt.Switch): Void? {
        val switchValue = evaluate(stmt.value)

        for ((caseExpr, caseStmt) in stmt.cases) {
            val caseValue = evaluate(caseExpr)

            if (isEqual(switchValue, caseValue)) {
                execute(caseStmt)
                return null
            }
        }

        if (stmt.defaultCase != null) {
            execute(stmt.defaultCase)
        }

        return null
    }

    private val clockFunctionDefiniton = object : JgolCallable {
        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return System.currentTimeMillis().toDouble() / 1000.0
        }

        override fun arity(): Int {
            return 0
        }

    }

    private val stdInDefinition = object : JgolCallable {
        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
            val rawValue = if (inputHandler != null) {
                inputHandler?.invoke() ?: ""
            } else {
                val input = java.util.Scanner(System.`in`)
                input.nextLine()
            }

            return try {
                rawValue.trim().toDouble()
            } catch (e: Exception) {
                try {
                    rawValue.toBoolean()
                } catch (e: Exception) {
                    rawValue
                }
            }
        }

        override fun arity(): Int {
            return 0
        }
    }


    init {
        globals.define("clock", clockFunctionDefiniton)
        globals.define("leia", stdInDefinition)
    }
}
