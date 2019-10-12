package net.minecord.beautyindicator.model

class Combat(val nameToRestore: String?, private val insertedSeconds: Int) {
    var seconds: Int = 0
        private set

    init {
        this.seconds = insertedSeconds
    }

    fun resetSeconds() {
        seconds = insertedSeconds
    }

    fun doUpdate() {
        seconds--
    }
}
