package com.pedrodev.jgol.interpreter

import java.util.*

class Resolver(private val interpreter: Interpreter) : Expr.Visitor<Void?>, Stmt.Visitor<Void?> {

    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()
    private var currentFunction: FunctionType = FunctionType.NONE

    override fun visitAssignExpr(expr: Expr.Assign): Void? {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
        return null
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitCallExpr(expr: Expr.Call): Void? {
        resolve(expr.callee)
        expr.arguments.forEach { resolve(it) }
        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Void? {
        resolve(expr.expression)
        return null
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Void? {
        return null
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Void? {
        resolve(expr.right)
        return null
    }

    override fun visitVariableExpr(expr: Expr.Variable): Void? {
        if (scopes.isNotEmpty() && scopes.peek()[expr.name.lexeme] == false) {
            Jgol.error(expr.name, "Não é possível ler a variável local em seu próprio inicializador.")
        }
        resolveLocal(expr, expr.name)
        return null
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.size - 1 downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        beginScope()
        resolve(stmt.statements)
        endScope()
        return null
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun beginScope() {
        scopes.push(mutableMapOf())
    }

    fun resolve(statements: List<Stmt>) {
        statements.forEach { resolve(it) }
    }

    private fun resolve(statement: Stmt) {
        statement.accept(this)
    }

    private fun resolve(expression: Expr) {
        expression.accept(this)
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(function: Stmt.Function): Void? {
        declare(function.name)
        define(function.name)
        resolveFunction(function, FunctionType.FUNCTION)
        return null
    }

    private fun resolveFunction(function: Stmt.Function, type: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = type

        beginScope()
        function.params.forEach {
            declare(it)
            define(it)
        }
        resolve(function.body)
        endScope()
        currentFunction = enclosingFunction
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        stmt.elseBranch?.let { resolve(it) }
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void? {
        if (currentFunction == FunctionType.NONE) {
            Jgol.error(stmt.keyword, "Não é possível retornar fora de uma função.")
        }
        stmt.value?.let { resolve(it) }
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        resolve(stmt.condition)
        resolve(stmt.body)
        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        declare(stmt.name)
        stmt.initializer?.let { resolve(it) }
        define(stmt.name)
        return null
    }

    private fun define(name: Token) {
        if (scopes.isNotEmpty()) {
            scopes.peek()[name.lexeme] = true
        }
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) return

        val scope = scopes.peek()
        if (scope.containsKey(name.lexeme)) {
            Jgol.error(name, "Já existe uma variável com este nome neste escopo.")
        }
        scope[name.lexeme] = false
    }
}
