package com.pedrodev.jgol.interpreter

import com.pedrodev.jgol.ide.SessionLogs

class Jgol {
    private val interpreter = Interpreter()
    private var outputHandler: ((String) -> Unit)? = null
    private var inputHandler: (() -> String)? = null

    var hadError: Boolean = false
    private var hadRuntimeError: Boolean = false

    init {
        interpreter.setJgolInstance(this)
        currentInstance = this
    }

    fun setOutputHandler(handler: (String) -> Unit) {
        outputHandler = handler
        interpreter.setOutputHandler(handler)
    }

    fun setInputHandler(handler: () -> String) {
        inputHandler = handler
        interpreter.setInputHandler(handler)
    }

    fun reportError(line: Int, where: String, message: String) {
        val errorMsg = "[Linha $line ] Erro $where: $message"
        if (outputHandler != null) {
            outputHandler?.invoke(errorMsg)
        } else {
            System.err.println(errorMsg)
        }
        this.hadError = true
        Jgol.hadError = true
        throw RuntimeException("Stopping after first error")
    }

    fun reportError(token: Token, message: String) {
        if (token.type === TokenType.EOF) {
            reportError(token.line, " no final", message)
        } else {
            reportError(token.line, (" em '" + token.lexeme) + "'", message)
        }
    }

    fun reportRuntimeError(error: RuntimeError) {
        val errorMsg = (error.message + "\n[linha] " + error.token.line) + "]"
        if (outputHandler != null) {
            outputHandler?.invoke(errorMsg)
        } else {
            System.err.println(errorMsg)
        }
        this.hadRuntimeError = true
    }

    companion object {
        var hadError: Boolean = false
        private var hadRuntimeError: Boolean = false
        private var currentInstance: Jgol? = null
        fun error(line: Int, message: String) {
            report(line, "", message)
            throw RuntimeException("Stopping after first error")
        }

        private fun report(line: Int, where: String, message: String) {
            val errorMsg = "[Linha $line ] Erro $where: $message"

            if (currentInstance?.outputHandler != null) {
                currentInstance?.outputHandler?.invoke(errorMsg)
            } else {
                System.err.println(errorMsg)
            }

            hadError = true
        }

        fun error(token: Token, message: String) {
            if (token.type === TokenType.EOF) {
                report(token.line, " no final", message)
            } else {
                report(token.line, (" em '" + token.lexeme) + "'", message)
            }
            throw RuntimeException("Stopping after first error")
        }

        fun runtimeError(error: RuntimeError) {
            val errorMsg = (error.message + "\n[linha] " + error.token.line) + "]"

            if (currentInstance?.outputHandler != null) {
                currentInstance?.outputHandler?.invoke(errorMsg)
            } else {
                System.err.println(errorMsg)
            }

            hadRuntimeError = true
        }
    }

    fun run(source: String, onFinish: (() -> Unit)? = null) {
        val startTime = System.currentTimeMillis()

        try {
            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens)
            val statements = parser.parse()

            if (this.hadError) {
                this.hadError = false
                Jgol.hadError = false
                finishProgram(startTime, onFinish)
                return
            }

            val resolver = Resolver(interpreter)
            resolver.resolve(statements)

            if (this.hadError) {
                this.hadError = false
                Jgol.hadError = false
                finishProgram(startTime, onFinish)
                return
            }

            interpreter.interpret(statements)
            finishProgram(startTime, onFinish)
        } catch (e: RuntimeException) {
            finishProgram(startTime, onFinish)
        }
    }

    fun finishProgram(startTime: Long, onFinish: (() -> Unit)? = null) {
        val endTime = System.currentTimeMillis() - startTime
        val msgEnd = "Execução finalizada em $endTime Milissegundos..."
        if (outputHandler != null) {
            outputHandler?.invoke(msgEnd)
        } else {
            println(msgEnd)
        }
        SessionLogs.log(msgEnd)

        onFinish?.invoke()
    }
}
