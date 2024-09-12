package com.pedrodev.jgol.interpreter

interface JgolCallable {
    fun call(interpreter: Interpreter, arguments: List<Any?>) : Any?
    fun arity(): Int
}