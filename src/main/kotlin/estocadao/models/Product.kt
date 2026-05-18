package com.estoque.estocadao.models

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Serializable
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val sku: String,
    val category: String,
    val created_at: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    val updated_at: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val sku: String,
    val category: String
)

@Serializable
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val sku: String? = null,
    val category: String? = null
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)