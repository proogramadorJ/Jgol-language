package com.pedrodev.jgol.terminal

class Terminal {

    external fun init()

    companion object {
        init {
            System.load("C:\\Users\\pedro\\Desenvolvimento\\desktop\\Jgol\\Jgol\\jni\\dll\\terminal.dll")
        }
    }
}