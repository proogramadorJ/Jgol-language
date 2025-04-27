package com.pedrodev.jgol.interpreter


// TODO testar/forçar todos os possiveis erros gerados durante o parsing

class Parser(private val tokens: List<Token>) {

    private class ParseError : RuntimeException()

    private var current = 0

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = or()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()

            when (expr) {
                is Expr.Variable -> {
                    val name = expr.name
                    return Expr.Assign(name, value)
                }

                is Expr.ArrayAccess -> {
                    val array: Expr.ArrayAccess = expr
                    return Expr.ArraySet(expr.token, array.array, array.index, value)
                }

                is Expr.Get -> {
                    val get: Expr.Get = expr
                    return Expr.Set(get.obj, get.name, value)
                }

                else -> error(equals, "Destino de atribuição inválido.")
            }
        }
        return expr
    }

    private fun or(): Expr {
        var expr = and()

        while (match(TokenType.OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()

        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL
            )
        ) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return call()
    }

    private fun call(): Expr {
        var expr = primary()

        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else if (match(TokenType.LEFT_BRACKET)) {
                val index = expression()
                consume(TokenType.RIGHT_BRACKET, "Esperado ']' após o índice.")

                // TODO obter a localização real do token
                expr = Expr.ArrayAccess(Token(TokenType.LEFT_BRACKET, "[", null, 0), expr, index)

            } else if (match(TokenType.DOT)) {
                val name = consume(TokenType.IDENTIFIER, "Esperado nome da propriedade após '.'.")
                expr = Expr.Get(expr, name)
            } else {
                break
            }
        }
        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    error(peek(), "Não pode ter mais de 255 argumentos.")
                }
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }
        val paren = consume(TokenType.RIGHT_PAREN, "Esperado ')' depois de argumentos.")
        return Expr.Call(callee, paren, arguments)
    }

    private fun primary(): Expr {
        if (match(TokenType.LEFT_BRACKET)) {
            val elements = mutableListOf<Expr>()
            if (!check(TokenType.RIGHT_BRACKET)) {
                do {
                    elements.add(expression())
                } while (match(TokenType.COMMA))
            }
            consume(TokenType.RIGHT_BRACKET, "Esperado ']' após os elementos do array.")
            return Expr.ArrayLiteral(elements)
        }
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.NIL)) return Expr.Literal(null)
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }
        if (match(TokenType.SUPER)) {
            val keyword = previous()
            consume(TokenType.DOT, "Esperado '.' depois de 'superior'.")
            val method = consume(TokenType.IDENTIFIER, "Esperado o nome do metodo da super classe.")
            return Expr.Super(keyword, method)
        }
        if (match(TokenType.THIS)) return Expr.This(previous())
        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Esperado ')' após a expressão.")
            return Expr.Grouping(expr)
        }
        throw error(peek(), "Esperada expressão")
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        Jgol.error(token, message)
        return ParseError()
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS,
                TokenType.FUN,
                TokenType.VAR,
                TokenType.FOR,
                TokenType.IF,
                TokenType.WHILE,
                TokenType.PRINT,
                TokenType.RETURN -> return

                else -> {} // TODO ver se isso não vai quebrar
            }
            advance()
        }
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }
        return statements
    }

    private fun declaration(): Stmt? {
        return try {
            when {
                match(TokenType.CLASS) -> classDeclaration()
                match(TokenType.FUN) -> function("function")
                match(TokenType.VAR) -> varDeclaration()
                else -> statement()
            }
        } catch (error: ParseError) {
            synchronize()
            null
        }
    }


    private fun classDeclaration(): Stmt.Class? {
        val name = consume(TokenType.IDENTIFIER, "Esperado nome da classe.")

        var superclass: Expr.Variable? = null

        if (match(TokenType.LESS)) {
            consume(TokenType.IDENTIFIER, "Esperado o nome da super classe.")
            superclass = Expr.Variable(previous())
        }

        consume(TokenType.LEFT_BRACE, "Esperado '{' antes do corpo da classe.")

        val methods: MutableList<Stmt.Function> = mutableListOf()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"))
        }

        consume(TokenType.RIGHT_BRACE, "Esperado '}' depois do corpo da classe.")
        // TODO verificar se não vai quebrar quando não tiver super class
        return Stmt.Class(name, superclass, methods)
    }


    private fun function(kind: String): Stmt.Function {
        val name = consume(TokenType.IDENTIFIER, "Esperado $kind nome.")
        consume(TokenType.LEFT_PAREN, "Esperado '(' depois $kind nome.")
        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Não pode ter mais de 255 parâmetros.")
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Nome do parâmetro esperado."))
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Esperado ')' depois dos parâmetros.")
        consume(TokenType.LEFT_BRACE, "Esperado '{' antes $kind corpo.")
        val body = block()
        return Stmt.Function(name, parameters, body)
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Esperado nome da variavel.")

        var initializer: Expr? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Esperado ';' depois da declaração de variavel.")
        return Stmt.Var(name, initializer)
    }

    private fun statement(): Stmt {
        return when {
            match(TokenType.FOR) -> forStatement()
            match(TokenType.IF) -> ifStatement()
            match(TokenType.PRINT) -> printStatement()
            match(TokenType.RETURN) -> returnStatement()
            match(TokenType.WHILE) -> whileStatement()
            match(TokenType.ESCOLHA) -> switchStatement()
            match(TokenType.LEFT_BRACE) -> Stmt.Block(block())
            else -> expressionStatement()
        }
    }

    private fun switchStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Esperado '(' depois de 'escolha'.")
        val value = expression()
        consume(TokenType.RIGHT_PAREN, "Esperado ')' depois da expressão.")
        consume(TokenType.LEFT_BRACE, "Esperado '{' depois da condição 'escolha'.")

        val cases = mutableListOf<Pair<Expr, Stmt>>()
        var defaultCase: Stmt? = null

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.CASO)) {
                val caseValue = expression()
                consume(TokenType.COLON, "Esperado ':' depois do valor do caso.")

                val statements = mutableListOf<Stmt>()
                while (!check(TokenType.CASO) && !check(TokenType.PADRAO) && !check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                    statements.add(declaration() ?: break)
                }

                val caseBody = if (statements.size == 1) {
                    statements[0]
                } else {
                    Stmt.Block(statements)
                }

                cases.add(Pair(caseValue, caseBody))
            } else if (match(TokenType.PADRAO)) {
                if (defaultCase != null) {
                    error(previous(), "Não pode ter mais de um caso padrão.")
                }

                consume(TokenType.COLON, "Esperado ':' depois de 'padrao'.")

                val statements = mutableListOf<Stmt>()
                while (!check(TokenType.CASO) && !check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                    statements.add(declaration() ?: break)
                }

                defaultCase = if (statements.size == 1) {
                    statements[0]
                } else {
                    Stmt.Block(statements)
                }
            } else {
                throw error(peek(), "Esperado 'caso' ou 'padrao'.")
            }
        }

        consume(TokenType.RIGHT_BRACE, "Esperado '}' depois dos casos.")

        return Stmt.Switch(value, cases, defaultCase)
    }

    private fun returnStatement(): Stmt.Return {
        val keyword = previous()
        var value: Expr? = null
        if (!check(TokenType.SEMICOLON)) {
            value = expression()
        }
        consume(TokenType.SEMICOLON, "Esperado ';' depois de retornar valor.")
        return Stmt.Return(keyword, value)
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Esperado '(' depois de 'for'.")

        val initializer = when {
            match(TokenType.SEMICOLON) -> null
            match(TokenType.VAR) -> varDeclaration()
            else -> expressionStatement()
        }

        var condition: Expr? = null
        if (!check(TokenType.SEMICOLON)) {
            condition = expression()
        }
        consume(TokenType.SEMICOLON, "Esperado ';' depois da condição do laço 'para'.")

        var increment: Expr? = null
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression()
        }
        consume(TokenType.RIGHT_PAREN, "Esperado ')' depois da cláusula 'para'.")

        var body = statement()

        if (increment != null) {
            body = Stmt.Block(listOf(body, Stmt.Expression(increment)))
        }

        if (condition == null) condition = Expr.Literal(true)
        body = Stmt.While(condition, body)

        return if (initializer != null) {
            Stmt.Block(listOf(initializer, body))
        } else body
    }

    private fun whileStatement(): Stmt.While {
        consume(TokenType.LEFT_PAREN, "Esperado '(' depois de 'enquanto'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Esperado ')' depois da condição.")
        val body = statement()
        return Stmt.While(condition, body)
    }

    private fun ifStatement(): Stmt.If {
        consume(TokenType.LEFT_PAREN, "Esperado '(' depois de 'se'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Esperado ')' depois da condição 'se'.")
        val thenBranch = statement()
        val elseBranch = if (match(TokenType.ELSE)) statement() else null
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }

        consume(TokenType.RIGHT_BRACE, "Esperado '}' depois do  bloco.")
        return statements
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Esperado ';' depois de expressão.")
        return Stmt.Expression(expr)
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Esperado ';' depois do valor.")
        return Stmt.Print(value)
    }
}
