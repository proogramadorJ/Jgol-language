package com.pedrodev.jgol.interpreter

object Keywords {
    var jgolKeywords: Map<String, TokenType> = hashMapOf(
        "e" to TokenType.AND,
        "classe" to TokenType.CLASS,
        "senao" to TokenType.ELSE,
        "falso" to TokenType.FALSE,
        "para" to TokenType.FOR,
        "funcao" to TokenType.FUN,
        "se" to TokenType.IF,
        "nulo" to TokenType.NIL,
        "ou" to TokenType.OR,
        "escreva" to TokenType.PRINT,
        "retorne" to TokenType.RETURN,
        "superior" to TokenType.SUPER,
        "este" to TokenType.THIS,
        "verdadeiro" to TokenType.TRUE,
        "variavel" to TokenType.VAR,
        "enquanto" to TokenType.WHILE,

        "escolha" to TokenType.ESCOLHA,
        "caso" to TokenType.CASO,
        "padrao" to TokenType.PADRAO
    )
}
