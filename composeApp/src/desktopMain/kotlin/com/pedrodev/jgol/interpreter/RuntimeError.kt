package com.pedrodev.jgol.interpreter

class RuntimeError(val token: Token, message: String) : RuntimeException(message)