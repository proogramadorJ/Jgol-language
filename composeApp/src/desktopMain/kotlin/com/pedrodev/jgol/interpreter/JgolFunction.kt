package com.pedrodev.jgol.interpreter

class JgolFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment
) : JgolCallable {

    fun bind(instance: JgolInstance): JgolFunction {
        val environment = Environment(closure)
        environment.define("este", instance) //TODO veriricar se precisa traduzir aqui tbm
        return JgolFunction(declaration, environment)
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)

        for (i in declaration.params.indices) {
            environment.define(declaration.params[i].lexeme, arguments[i])
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }
        return null
    }

    override fun arity(): Int {
        return declaration.params.size
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}