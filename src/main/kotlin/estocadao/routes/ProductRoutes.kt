package com.estoque.estocadao.routes

import com.estoque.estocadao.models.*
import com.estoque.estocadao.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.productRoutes() {
    val productService = ProductService()

    route("/products") {
        get {
            val products = productService.getAllProducts()
            call.respond(products)
        }

        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@get
            }

            val product = productService.getProductById(id)
            if (product != null) {
                call.respond(product)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Produto não encontrado"))
            }
        }

        post {
            val request = try {
                call.receive<CreateProductRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Dados inválidos"))
                return@post
            }

            val product = productService.createProduct(request)
            // ⚠️ CORREÇÃO AQUI: verificar se product não é null
            if (product != null) {
                call.respond(HttpStatusCode.Created, product)
            } else {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Erro ao criar produto"))
            }
        }

        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@put
            }

            val request = try {
                call.receive<UpdateProductRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "Dados inválidos"))
                return@put
            }

            val updated = productService.updateProduct(id, request)
            if (updated != null) {
                call.respond(updated)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Produto não encontrado"))
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad Request", "ID não fornecido"))
                return@delete
            }

            // PRIMEIRO: verificar se o produto existe
            val product = productService.getProductById(id)
            if (product == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Not Found", "Produto não encontrado"))
                return@delete
            }

            // SEGUNDO: tentar deletar
            val deleted = productService.deleteProduct(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error", "Erro ao deletar produto"))
            }
        }
    }
}