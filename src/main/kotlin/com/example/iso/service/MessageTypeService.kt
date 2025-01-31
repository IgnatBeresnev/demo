package com.example.iso.service

import com.example.iso.domain.MessageType
import com.example.iso.repository.MessageTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageTypeService(private val messageTypeRepository: MessageTypeRepository) {

    fun getMessageType(id: Int): MessageType {
        return messageTypeRepository.findById(id)
            ?: throw NoSuchElementException("Message type not found with id: $id")
    }

    fun getMessageTypeByCode(typeCode: String): MessageType {
        return messageTypeRepository.findByTypeCode(typeCode)
            ?: throw NoSuchElementException("Message type not found with code: $typeCode")
    }

    fun getAllMessageTypes(): List<MessageType> {
        return messageTypeRepository.findAll()
    }

    @Transactional
    fun createMessageType(messageType: MessageType): MessageType {
        require(messageType.id == null) { "Message type ID must be null for creation" }
        messageTypeRepository.findByTypeCode(messageType.typeCode)?.let {
            throw IllegalStateException("Message type with code ${messageType.typeCode} already exists")
        }
        return messageTypeRepository.save(messageType)
    }

    @Transactional
    fun updateMessageType(id: Int, messageType: MessageType): MessageType {
        val existing = getMessageType(id)
        val updated = messageType.copy(id = existing.id)
        return messageTypeRepository.save(updated)
    }

    fun validateMessageType(typeCode: String): Boolean {
        return MessageType.validateTypeCode(typeCode)
    }
}
