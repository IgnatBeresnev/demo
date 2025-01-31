package com.example.iso.controller

import com.example.iso.dto.MessageTypeRequest
import com.example.iso.dto.MessageTypeResponse
import com.example.iso.service.MessageTypeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message-types")
class MessageTypeController(private val messageTypeService: MessageTypeService) {

    @GetMapping
    fun getAllMessageTypes(): ResponseEntity<List<MessageTypeResponse>> {
        val messageTypes = messageTypeService.getAllMessageTypes()
        return ResponseEntity.ok(messageTypes.map { MessageTypeResponse.fromDomain(it) })
    }

    @GetMapping("/{id}")
    fun getMessageType(@PathVariable id: Int): ResponseEntity<MessageTypeResponse> {
        val messageType = messageTypeService.getMessageType(id)
        return ResponseEntity.ok(MessageTypeResponse.fromDomain(messageType))
    }

    @GetMapping("/by-code/{typeCode}")
    fun getMessageTypeByCode(@PathVariable typeCode: String): ResponseEntity<MessageTypeResponse> {
        val messageType = messageTypeService.getMessageTypeByCode(typeCode)
        return ResponseEntity.ok(MessageTypeResponse.fromDomain(messageType))
    }

    @PostMapping
    fun createMessageType(
        @Valid @RequestBody request: MessageTypeRequest
    ): ResponseEntity<MessageTypeResponse> {
        val messageType = messageTypeService.createMessageType(request.toDomain())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(MessageTypeResponse.fromDomain(messageType))
    }

    @PutMapping("/{id}")
    fun updateMessageType(
        @PathVariable id: Int,
        @Valid @RequestBody request: MessageTypeRequest
    ): ResponseEntity<MessageTypeResponse> {
        val messageType = messageTypeService.updateMessageType(id, request.toDomain())
        return ResponseEntity.ok(MessageTypeResponse.fromDomain(messageType))
    }

    @GetMapping("/validate/{typeCode}")
    fun validateMessageType(@PathVariable typeCode: String): ResponseEntity<Map<String, Boolean>> {
        val isValid = messageTypeService.validateMessageType(typeCode)
        return ResponseEntity.ok(mapOf("valid" to isValid))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to e.message!!))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleConflict(e: IllegalStateException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapOf("error" to e.message!!))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to e.message!!))
    }
}
