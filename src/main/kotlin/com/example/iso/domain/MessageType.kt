package com.example.iso.domain

import java.time.OffsetDateTime

data class MessageType(
    val id: Int? = null,
    val typeCode: String,
    val description: String? = null,
    val schemaVersion: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    companion object {
        private val TYPE_CODE_PATTERN = Regex("^[A-Z]{4}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{2}$")
        
        fun validateTypeCode(typeCode: String): Boolean {
            return TYPE_CODE_PATTERN.matches(typeCode)
        }
    }

    init {
        require(validateTypeCode(typeCode)) { 
            "Invalid message type code format. Expected format: XXXX.NNN.NNN.NN (e.g., pacs.008.001.08)" 
        }
    }
}
