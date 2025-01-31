package com.example.iso.dto

import com.example.iso.domain.FinancialMessage
import com.example.iso.domain.MessageStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime
import java.util.UUID

data class FinancialMessageRequest(
    @field:NotNull(message = "Message type ID is required")
    val messageTypeId: Int,

    @field:NotBlank(message = "Business message identifier is required")
    @field:Pattern(
        regexp = "^[A-Za-z0-9\\-\\+\\?/:().,'']{1,35}$",
        message = "Invalid business message identifier format"
    )
    val businessMessageIdentifier: String,

    @field:NotBlank(message = "Message definition identifier is required")
    val messageDefinitionIdentifier: String,

    val businessService: String? = null,
    val marketPractice: String? = null,
    val senderReference: String? = null,

    @field:NotBlank(message = "XML payload is required")
    val payload: String
)

data class FinancialMessageResponse(
    val id: UUID,
    val messageType: MessageTypeResponse,
    val businessMessageIdentifier: String,
    val creationDate: OffsetDateTime,
    val messageDefinitionIdentifier: String,
    val businessService: String?,
    val marketPractice: String?,
    val senderReference: String?,
    val status: MessageStatus,
    val errorDescription: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun fromDomain(message: FinancialMessage) = FinancialMessageResponse(
            id = message.id,
            messageType = MessageTypeResponse.fromDomain(message.messageType),
            businessMessageIdentifier = message.businessMessageIdentifier,
            creationDate = message.creationDate,
            messageDefinitionIdentifier = message.messageDefinitionIdentifier,
            businessService = message.businessService,
            marketPractice = message.marketPractice,
            senderReference = message.senderReference,
            status = message.status,
            errorDescription = message.errorDescription,
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
        )
    }
}

data class MessageStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: MessageStatus,
    
    val errorDescription: String? = null
)

data class FinancialMessageSummary(
    val id: UUID,
    val messageType: String,
    val businessMessageIdentifier: String,
    val status: MessageStatus,
    val createdAt: OffsetDateTime
) {
    companion object {
        fun fromDomain(message: FinancialMessage) = FinancialMessageSummary(
            id = message.id,
            messageType = message.messageType.typeCode,
            businessMessageIdentifier = message.businessMessageIdentifier,
            status = message.status,
            createdAt = message.createdAt
        )
    }
}
