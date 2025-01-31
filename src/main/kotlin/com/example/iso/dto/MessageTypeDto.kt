package com.example.iso.dto

import com.example.iso.domain.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MessageTypeRequest(
    @field:NotBlank(message = "Type code is required")
    @field:Pattern(
        regexp = "^[A-Z]{4}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{2}$",
        message = "Invalid message type code format. Expected format: XXXX.NNN.NNN.NN (e.g., pacs.008.001.08)"
    )
    val typeCode: String,
    
    val description: String? = null,
    val schemaVersion: String? = null
) {
    fun toDomain(): MessageType = MessageType(
        typeCode = typeCode,
        description = description,
        schemaVersion = schemaVersion
    )
}

data class MessageTypeResponse(
    val id: Int,
    val typeCode: String,
    val description: String?,
    val schemaVersion: String?
) {
    companion object {
        fun fromDomain(messageType: MessageType) = MessageTypeResponse(
            id = messageType.id!!,
            typeCode = messageType.typeCode,
            description = messageType.description,
            schemaVersion = messageType.schemaVersion
        )
    }
}
