package com.estoque.estocadao.models

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Serializable
data class StockItem(
    val id: String = UUID.randomUUID().toString(),
    val product_id: String,
    val quantity: Int,
    val unit_price: Double,
    val location: String,
    val updated_at: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
)

@Serializable
data class CreateStockRequest(
    val product_id: String,
    val quantity: Int,
    val unit_price: Double,
    val location: String
)

@Serializable
data class UpdateStockRequest(
    val quantity: Int? = null,
    val unit_price: Double? = null,
    val location: String? = null
)

@Serializable
data class StockSummary(
    val product_id: String,
    val product_name: String,
    val total_quantity: Int
)