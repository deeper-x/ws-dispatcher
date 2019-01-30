package com.websocketReport

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import java.time.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ChatClient(val session: DefaultWebSocketSession) {
    companion object { var lastId = AtomicInteger(0) }
    val id = lastId.getAndIncrement()
    val name = "client-$id"
}


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        get("/") {
            call.respondText("TO IMPLEMENT", contentType = ContentType.Text.Plain)
        }

        var clients = Collections.synchronizedSet(LinkedHashSet<ChatClient>())

        webSocket("/ws-server") { // this: DefaultWebSocketSession
            val client = ChatClient(this)
            clients.add(client)
            try {
                while (true) {
                    val frame = incoming.receive()
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            // Iterate over all the connections
                            val textToSend = "${client.name} said: $text"
                            for (other in clients.toList()) {
                                other.session.outgoing.send(Frame.Text(textToSend))
                            }
                        }
                    }
                }
            } finally {
                clients.remove(client)
            }
        }
    }
}

