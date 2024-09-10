package com.pedrodev.jgol.interpreter

class Token(val type: TokenType, val lexeme: String, val literal: Any?, var line: Int) {

    override fun toString(): String {
        return "$type $lexeme $literal"
    }

}