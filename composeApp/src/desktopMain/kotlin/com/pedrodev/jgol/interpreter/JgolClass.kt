package com.pedrodev.jgol.interpreter

class JgolClass(var name: String, var methods: Map<String, JgolFunction>?) : JgolCallable {

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance = JgolInstance(this)
        return instance
    }

    override fun arity(): Int {
        return 0
    }

    override fun toString(): String {
        return name
    }

    fun findMethod(name: String): Any? {
        if (methods?.containsKey(name) == true) {
            return methods!![name]
        }
        return null
    }
}
