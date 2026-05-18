package com.estoque.estocadao.services

import com.estoque.estocadao.database.SupabaseDatabase
import com.estoque.estocadao.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import java.util.UUID

class StockService {
    private val client = SupabaseDatabase.httpClient
    private val baseUrl = "${SupabaseDatabase.SUPABASE_URL}/rest/v1"
    private val productService = ProductService()

    suspend fun getAllStockItems(): List<StockItem> {
        return try {
            val response: String = client.get("$baseUrl/stock_items") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
            }.body()
            Json.decodeFromString<List<StockItem>>(response)
        } catch (e: Exception) {
            println("Erro ao buscar itens de estoque: ${e.message}")
            emptyList()
        }
    }

    suspend fun getStockItemById(id: String): StockItem? {
        return try {
            val response: String = client.get("$baseUrl/stock_items") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                parameter("id", "eq.$id")
            }.body()
            val items = Json.decodeFromString<List<StockItem>>(response)
            items.firstOrNull()
        } catch (e: Exception) {
            println("Erro ao buscar item de estoque: ${e.message}")
            null
        }
    }

    suspend fun createStockItem(request: CreateStockRequest): StockItem? {
        // Verificar produto existe
        val productCheck = try {
            val response = client.get("$baseUrl/products") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                parameter("id", "eq.${request.product_id}")
            }
            response.status.value == 200 && response.body<String>().isNotEmpty() && response.body<String>() != "[]"
        } catch (e: Exception) {
            false
        }

        if (!productCheck) {
            println("Produto não encontrado: ${request.product_id}")
            return null
        }

        // Criar item de estoque usando texto puro
        val stockItemId = UUID.randomUUID().toString()

        val jsonBody = """
        {
            "id": "$stockItemId",
            "product_id": "${request.product_id}",
            "quantity": ${request.quantity},
            "unit_price": ${request.unit_price},
            "location": "${request.location}"
        }
    """.trimIndent()

        return try {
            val response = client.post("$baseUrl/stock_items") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                header("Content-Type", "application/json")
                setBody(jsonBody)
            }

            if (response.status.value in 200..299) {
                println("Item criado com sucesso! ID: $stockItemId")
                getStockItemById(stockItemId)
            } else {
                println("Erro na resposta: ${response.status.value}")
                null
            }
        } catch (e: Exception) {
            println("Erro ao criar: ${e.message}")
            null
        }
    }

    suspend fun updateStockItem(id: String, request: UpdateStockRequest): StockItem? {
        // Construir JSON manualmente
        val updates = mutableListOf<String>()
        request.quantity?.let { updates.add("\"quantity\": $it") }
        request.unit_price?.let { updates.add("\"unit_price\": $it") }
        request.location?.let { updates.add("\"location\": \"$it\"") }

        return if (updates.isNotEmpty()) {
            val updateBody = "{${updates.joinToString(",")}}"
            println("Atualizando com JSON: $updateBody")

            try {
                val response = client.patch("$baseUrl/stock_items") {
                    header("apikey", SupabaseDatabase.SUPABASE_KEY)
                    header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                    header("Content-Type", "application/json")
                    header("Prefer", "return=representation")
                    parameter("id", "eq.$id")
                    setBody(updateBody)
                }

                val jsonString = response.body<String>()
                println("Resposta update: $jsonString")

                // Tentar decodificar a resposta
                try {
                    val items = Json.decodeFromString<List<StockItem>>(jsonString)
                    items.firstOrNull() ?: getStockItemById(id)
                } catch (e: Exception) {
                    println("Erro ao decodificar: ${e.message}")
                    getStockItemById(id)
                }
            } catch (e: Exception) {
                println("Erro ao atualizar item de estoque: ${e.message}")
                e.printStackTrace()
                null
            }
        } else {
            getStockItemById(id)
        }
    }

    suspend fun deleteStockItem(id: String): Boolean {
        return try {
            client.delete("$baseUrl/stock_items") {
                header("apikey", SupabaseDatabase.SUPABASE_KEY)
                header("Authorization", "Bearer ${SupabaseDatabase.SUPABASE_KEY}")
                parameter("id", "eq.$id")
            }
            true
        } catch (e: Exception) {
            println("Erro ao deletar item de estoque: ${e.message}")
            false
        }
    }

    suspend fun getStockSummary(): List<StockSummary> {
        return try {
            val allProducts = productService.getAllProducts()
            val allStock = getAllStockItems()
            val summary = mutableMapOf<String, Pair<String, Int>>()

            for (stock in allStock) {
                val product = allProducts.find { it.id == stock.product_id }
                if (product != null) {
                    val current = summary[stock.product_id]
                    val total = (current?.second ?: 0) + stock.quantity
                    summary[stock.product_id] = Pair(product.name, total)
                }
            }

            summary.map { (id, info) ->
                StockSummary(id, info.first, info.second)
            }
        } catch (e: Exception) {
            println("Erro ao gerar resumo: ${e.message}")
            emptyList()
        }
    }
}