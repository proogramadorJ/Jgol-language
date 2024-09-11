package com.pedrodev.jgol.interpreter

// TODO decidir se as palavras reservadas de jgol serão essas mesmo.

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
        "este" to TokenType.THIS, // TODO rever se "this" vai ser traduzido como "este" mesmo.
        "verdadeiro" to TokenType.TRUE,
        "variavel" to TokenType.VAR,
        "enquanto" to TokenType.WHILE
    )
}