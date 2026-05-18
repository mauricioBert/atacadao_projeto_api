package com.estoque.estocadao.services

import com.estoque.estocadao.database.SupabaseDatabase
import com.estoque.estocadao.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.util.UUID

class ProductService {
    private val client = SupabaseDatabase.httpClient
    private val baseUrl = "${SupabaseDatabase.SUPABASE_URL}/rest/v1"

    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = client.get("$baseUrl/products") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
            }
            val jsonString = response.body<String>()
            println("Resposta getAllProducts: $jsonString")
            Json.decodeFromString<List<Product>>(jsonString)
        } catch (e: Exception) {
            println("Erro ao buscar produtos: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductById(id: String): Product? {
        return try {
            val response = client.get("$baseUrl/products") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                parameter("id", "eq.$id")
            }
            val jsonString = response.body<String>()
            println("Resposta getProductById: $jsonString")
            val products = Json.decodeFromString<List<Product>>(jsonString)
            products.firstOrNull()
        } catch (e: Exception) {
            println("Erro ao buscar produto: ${e.message}")
            null
        }
    }

    suspend fun createProduct(request: CreateProductRequest): Product? {
        val productId = UUID.randomUUID().toString()

        // Formato correto para o Supabase
        val product = """
            {
                "id": "$productId",
                "name": "${request.name}",
                "description": "${request.description}",
                "sku": "${request.sku}",
                "category": "${request.category}"
            }
        """.trimIndent()

        return try {
            val response = client.post("$baseUrl/products") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                header("Content-Type", "application/json")
                header("Prefer", "return=representation")
                setBody(product)
            }

            val jsonString = response.body<String>()
            println("Resposta createProduct: $jsonString")
            val createdProduct = Json.decodeFromString<List<Product>>(jsonString).firstOrNull()
            createdProduct
        } catch (e: Exception) {
            println("Erro ao criar produto: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun updateProduct(id: String, request: UpdateProductRequest): Product? {
        val updates = mutableListOf<String>()
        request.name?.let { updates.add("\"name\":\"$it\"") }
        request.description?.let { updates.add("\"description\":\"$it\"") }
        request.sku?.let { updates.add("\"sku\":\"$it\"") }
        request.category?.let { updates.add("\"category\":\"$it\"") }

        return if (updates.isNotEmpty()) {
            val updateBody = "{${updates.joinToString(",")}}"
            try {
                val response = client.patch("$baseUrl/products") {
                    header("apikey", SupabaseDatabase.SUPABASE_KEY)
                    header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                    header("Content-Type", "application/json")
                    header("Prefer", "return=representation")
                    parameter("id", "eq.$id")
                    setBody(updateBody)
                }
                val jsonString = response.body<String>()
                println("Resposta updateProduct: $jsonString")
                val updated = Json.decodeFromString<List<Product>>(jsonString).firstOrNull()
                updated ?: getProductById(id)
            } catch (e: Exception) {
                println("Erro ao atualizar produto: ${e.message}")
                null
            }
        } else {
            getProductById(id)
        }
    }

    suspend fun deleteProduct(id: String): Boolean {
        return try {
            client.delete("$baseUrl/products") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                parameter("id", "eq.$id")
            }
            true
        } catch (e: Exception) {
            println("Erro ao deletar produto: ${e.message}")
            false
        }
    }

    suspend fun productExists(id: String): Boolean {
        return getProductById(id) != null
    }
}