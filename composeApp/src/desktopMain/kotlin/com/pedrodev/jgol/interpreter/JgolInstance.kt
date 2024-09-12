package com.pedrodev.jgol.interpreter

class JgolInstance(var klass: JgolClass) {

    private var fields = mutableMapOf<String, Any>()

    fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

       val method = klass.findMethod(name.lexeme)
        if(method != null) return method

        throw RuntimeError(name, "Propriedade '" + name.lexeme + "' indefinida.")
    }

    fun set(name: Token, value: Any?) {
        if (value != null) {
            fields[name.lexeme] = value
        }
    }

    override fun toString(): String {
        return klass.name + " instancia"
    }
}
