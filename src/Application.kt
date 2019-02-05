package com.websocketReport

import com.websocketReport.db.Manager
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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

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
                    val frameSent = incoming.receiveOrNull() ?: break
                    when (frameSent) {
                        is Frame.Text -> {
                            val objDb = Manager("USER", "PASSWORD", "127.0.0.1", "DBNAME")
                            val idPortinformer = frameSent.readText().toInt()
                            // Iterate over all the connections
                            val textToSend = "${client.name} report: ${objDb.runSelectQuery("SELECT * FROM trips_logs where fk_portinformer = $idPortinformer;", listOf("ts_main_event_field_val"))}"
                            for (other in clients.toList()) {
                                other.session.outgoing.send(Frame.Text(textToSend))
                            }

                        } else -> {
                            println("Frame type not allowed")
                        }
                    }
                }
            } finally {
                clients.remove(client)
            }
        }
    }
}

