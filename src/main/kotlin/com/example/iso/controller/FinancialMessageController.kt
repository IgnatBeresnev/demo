package com.example.iso.controller

import com.example.iso.domain.FinancialMessage
import com.example.iso.domain.MessageStatus
import com.example.iso.dto.*
import com.example.iso.service.FinancialMessageService
import com.example.iso.service.MessageTypeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/messages")
class FinancialMessageController(
    private val financialMessageService: FinancialMessageService,
    private val messageTypeService: MessageTypeService
) {

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: UUID): ResponseEntity<FinancialMessageResponse> {
        val message = financialMessageService.getMessage(id)
        return ResponseEntity.ok(FinancialMessageResponse.fromDomain(message))
    }

    @GetMapping("/by-bmi/{bmi}")
    fun getMessageByBusinessIdentifier(@PathVariable bmi: String): ResponseEntity<FinancialMessageResponse> {
        val message = financialMessageService.getMessageByBusinessIdentifier(bmi)
        return ResponseEntity.ok(FinancialMessageResponse.fromDomain(message))
    }

    @GetMapping("/by-status/{status}")
    fun getMessagesByStatus(@PathVariable status: MessageStatus): ResponseEntity<List<FinancialMessageSummary>> {
        val messages = financialMessageService.getMessagesByStatus(status)
        return ResponseEntity.ok(messages.map { FinancialMessageSummary.fromDomain(it) })
    }

    @GetMapping("/by-type/{messageTypeId}")
    fun getMessagesByType(@PathVariable messageTypeId: Int): ResponseEntity<List<FinancialMessageSummary>> {
        val messages = financialMessageService.getMessagesByType(messageTypeId)
        return ResponseEntity.ok(messages.map { FinancialMessageSummary.fromDomain(it) })
    }

    @PostMapping
    fun createMessage(@Valid @RequestBody request: FinancialMessageRequest): ResponseEntity<FinancialMessageResponse> {
        val messageType = messageTypeService.getMessageType(request.messageTypeId)
        val message = financialMessageService.createMessage(
            FinancialMessage(
                messageType = messageType,
                businessMessageIdentifier = request.businessMessageIdentifier,
                messageDefinitionIdentifier = request.messageDefinitionIdentifier,
                businessService = request.businessService,
                marketPractice = request.marketPractice,
                senderReference = request.senderReference,
                payload = request.payload
            )
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(FinancialMessageResponse.fromDomain(message))
    }

    @PostMapping("/{id}/process")
    fun processMessage(@PathVariable id: UUID): ResponseEntity<FinancialMessageResponse> {
        val message = financialMessageService.processMessage(id)
        return ResponseEntity.ok(FinancialMessageResponse.fromDomain(message))
    }

    @PatchMapping("/{id}/status")
    fun updateMessageStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: MessageStatusUpdateRequest
    ): ResponseEntity<FinancialMessageResponse> {
        val message = financialMessageService.updateMessageStatus(id, request.status, request.errorDescription)
        return ResponseEntity.ok(FinancialMessageResponse.fromDomain(message))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to e.message!!))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to e.message!!))
    }
}
