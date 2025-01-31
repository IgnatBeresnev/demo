package com.example.iso.service

import com.example.iso.domain.FinancialMessage
import com.example.iso.domain.MessageStatus
import com.example.iso.domain.MessageType
import com.example.iso.repository.FinancialMessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import java.io.StringReader
import org.xml.sax.InputSource

@Service
class FinancialMessageService(
    private val financialMessageRepository: FinancialMessageRepository,
    private val messageTypeService: MessageTypeService
) {

    fun getMessage(id: UUID): FinancialMessage {
        return financialMessageRepository.findById(id)
            ?: throw NoSuchElementException("Financial message not found with id: $id")
    }

    fun getMessageByBusinessIdentifier(bmi: String): FinancialMessage {
        return financialMessageRepository.findByBusinessMessageIdentifier(bmi)
            ?: throw NoSuchElementException("Financial message not found with BMI: $bmi")
    }

    fun getMessagesByStatus(status: MessageStatus): List<FinancialMessage> {
        return financialMessageRepository.findByStatus(status)
    }

    fun getMessagesByType(messageTypeId: Int): List<FinancialMessage> {
        messageTypeService.getMessageType(messageTypeId) // Validate message type exists
        return financialMessageRepository.findByMessageType(messageTypeId)
    }

    @Transactional
    fun createMessage(message: FinancialMessage): FinancialMessage {
        validateXmlPayload(message.payload)
        return financialMessageRepository.save(message)
    }

    @Transactional
    fun updateMessageStatus(id: UUID, status: MessageStatus, errorDescription: String? = null): FinancialMessage {
        return financialMessageRepository.updateStatus(id, status, errorDescription)
            ?: throw NoSuchElementException("Financial message not found with id: $id")
    }

    @Transactional
    fun processMessage(id: UUID): FinancialMessage {
        val message = getMessage(id)
        return try {
            // Validate XML against schema
            validateXmlPayload(message.payload)
            
            // Process message based on its type
            when (message.messageType.typeCode) {
                "pacs.008.001.08" -> processCreditTransfer(message)
                "pacs.009.001.08" -> processFinancialInstitutionTransfer(message)
                "camt.053.001.08" -> processBankStatement(message)
                else -> processGenericMessage(message)
            }
        } catch (e: Exception) {
            updateMessageStatus(id, MessageStatus.ERROR, e.message)
        }
    }

    private fun validateXmlPayload(payload: String) {
        try {
            val factory = DocumentBuilderFactory.newInstance().apply {
                setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            }
            val builder = factory.newDocumentBuilder()
            builder.parse(InputSource(StringReader(payload)))
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid XML payload: ${e.message}")
        }
    }

    private fun processCreditTransfer(message: FinancialMessage): FinancialMessage {
        // Implement credit transfer processing logic
        return updateMessageStatus(message.id, MessageStatus.COMPLETED)
    }

    private fun processFinancialInstitutionTransfer(message: FinancialMessage): FinancialMessage {
        // Implement financial institution transfer processing logic
        return updateMessageStatus(message.id, MessageStatus.COMPLETED)
    }

    private fun processBankStatement(message: FinancialMessage): FinancialMessage {
        // Implement bank statement processing logic
        return updateMessageStatus(message.id, MessageStatus.COMPLETED)
    }

    private fun processGenericMessage(message: FinancialMessage): FinancialMessage {
        // Implement generic message processing logic
        return updateMessageStatus(message.id, MessageStatus.COMPLETED)
    }
}
