package com.estoque.estocadao.routes

import com.estoque.estocadao.models.*
import com.estoque.estocadao.services.StockService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.stockRoutes() {
    val stockService = StockService()

    route("/stock") {
        get {
            val items = stockService.getAllStockItems()
            call.respond(items)
        }

        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@get
            }

            val item = stockService.getStockItemById(id)
            if (item != null) {
                call.respond(item)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Item não encontrado"))
            }
        }

        post {
            val request = try {
                call.receive<CreateStockRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Dados inválidos"))
                return@post
            }

            val item = stockService.createStockItem(request)
            // ⚠️ CORREÇÃO AQUI
            if (item != null) {
                call.respond(HttpStatusCode.Created, item)
            } else {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Produto não encontrado"))
            }
        }

        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@put
            }

            val request = try {
                call.receive<UpdateStockRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Dados inválidos"))
                return@put
            }

            val updated = stockService.updateStockItem(id, request)
            if (updated != null) {
                call.respond(updated)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Item não encontrado"))
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@delete
            }

            // PRIMEIRO: verificar se o item existe
            val item = stockService.getStockItemById(id)
            if (item == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Item não encontrado"))
                return@delete
            }

            // SEGUNDO: tentar deletar
            val deleted = stockService.deleteStockItem(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error", "Erro ao deletar item"))
            }
        }

        get("summary") {
            val summary = stockService.getStockSummary()
            call.respond(summary)
        }
    }
}