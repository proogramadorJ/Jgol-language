package com.pedrodev.jgol.interpreter

class JgolInstance(var klass: JgolClass) {

    private var fields = mutableMapOf<String, Any>()

    fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = klass.findMethod(name.lexeme)

        // TODO testar isso ver se não vai quebrar o que já funcionava antes de incluir o 'this'
        if (method != null && method is JgolFunction) return method.bind(this)

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
