package com.example.wydatki.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val sort: Int = 0
)

@Serializable
data class Expense(
    val id: String,
    val categoryId: String,
    val amount: Double,
    val description: String,
    val date: String,
    val note: String = ""
)

@Serializable
data class Income(
    val id: String,
    val source: String,
    val amount: Double,
    val date: String,
    val note: String = ""
)

@Serializable
data class AppData(
    val version: Int = 1,
    val categories: List<Category>,
    val expenses: List<Expense>,
    val incomes: List<Income>
)
