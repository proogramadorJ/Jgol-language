package com.pedrodev.jgol.interpreter

class Jgol {

    companion object {
        var hadError: Boolean = false
    }

    fun run(source: String) {
        val scanner = Scanner(source)
        val tokens: List<Token> = scanner.scanTokens()

        tokens.forEach { token ->
            println(token)
        }
    }
}