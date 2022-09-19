package io.kohpai.plugins

import io.kohpai.Connection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.Collections

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections =
            Collections.synchronizedSet(LinkedHashSet<Connection?>())
        webSocket("/chat") { // websocketSession
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receiveText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receiveText"
                    connections.forEach { it!!.session.send(textWithUsername) }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing ${thisConnection.name}!")
                connections -= thisConnection
            }
        }
    }
}
