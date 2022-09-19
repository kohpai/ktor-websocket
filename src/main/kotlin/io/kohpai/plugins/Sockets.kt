package io.kohpai.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/chat") { // websocketSession
            send("You are connected!")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receiveText = frame.readText()
                send("You said: $receiveText")
            }
        }
    }
}
