package com.example.iso.domain

import java.time.OffsetDateTime
import java.util.UUID

data class FinancialMessage(
    val id: UUID = UUID.randomUUID(),
    val messageType: MessageType,
    val businessMessageIdentifier: String,
    val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val messageDefinitionIdentifier: String,
    val businessService: String? = null,
    val marketPractice: String? = null,
    val senderReference: String? = null,
    val status: MessageStatus = MessageStatus.RECEIVED,
    val payload: String, // XML content
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    val errorDescription: String? = null
) {
    companion object {
        private val BMI_PATTERN = Regex("^[A-Za-z0-9\\-\\+\\?/:().,'']{1,35}$")
        
        fun validateBusinessMessageIdentifier(bmi: String): Boolean {
            return BMI_PATTERN.matches(bmi)
        }
    }

    init {
        require(validateBusinessMessageIdentifier(businessMessageIdentifier)) {
            "Invalid business message identifier format"
        }
        require(payload.isNotBlank()) {
            "Message payload cannot be empty"
        }
        require(messageDefinitionIdentifier.isNotBlank()) {
            "Message definition identifier cannot be empty"
        }
    }

    fun withStatus(newStatus: MessageStatus, error: String? = null): FinancialMessage {
        return copy(
            status = newStatus,
            errorDescription = error,
            updatedAt = OffsetDateTime.now()
        )
    }
}
