package com.pedrodev.jgol.interpreter.error;

class ErrorHandler {

    companion object {

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line ] Error $where: $message")
        }
    }

}
