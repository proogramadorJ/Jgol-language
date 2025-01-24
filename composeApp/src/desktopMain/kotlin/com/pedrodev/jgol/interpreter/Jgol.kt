package com.pedrodev.jgol.interpreter

import com.pedrodev.jgol.ide.SessionLogs

class Jgol {

    companion object {
        var hadError: Boolean = false
        private var hadRuntimeError: Boolean = false
        private val interpreter = Interpreter()

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[Linha $line ] Erro $where: $message")
            hadError = true
        }

        fun error(token: Token, message: String) {
            if (token.type === TokenType.EOF) {
                report(token.line, " no final", message)
            } else {
                report(token.line, (" em '" + token.lexeme) + "'", message)
            }
        }

        fun runtimeError(error: RuntimeError) {
            System.err.println((error.message + "\n[linha] " + error.token.line) + "]")
            hadRuntimeError = true
        }
    }

    fun run(source: String) {
        val startTime = System.currentTimeMillis()

        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)
        val statements = parser.parse()

        if (hadError) {
            hadError = false
            finishProgram(startTime)
            return
        }

        val resolver = Resolver(interpreter)
        resolver.resolve(statements)

        if (hadError) {
            hadError = false
            finishProgram(startTime)
            return
        }

        interpreter.interpret(statements)
        finishProgram(startTime)

    }

    fun finishProgram(startTime: Long) {
        val endTime = System.currentTimeMillis() - startTime
        val msgEnd = "Execução finalizada em $endTime Milissegundos..."
        println(msgEnd)
        SessionLogs.log(msgEnd)
    }
}