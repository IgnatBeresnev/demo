package com.example.iso.controller

import com.example.iso.TestApplication
import com.example.iso.dto.MessageTypeRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageTypeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Sql("/db/test-data/clear-message-types.sql")
    fun `should create new message type`() {
        // given
        val request = MessageTypeRequest(
            typeCode = "PACS.008.001.08",
            description = "FIToFICustomerCreditTransfer",
            schemaVersion = "8.0"
        )

        // when/then
        mockMvc.perform(
            post("/message-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.typeCode").value(request.typeCode))
            .andExpect(jsonPath("$.description").value(request.description))
            .andExpect(jsonPath("$.schemaVersion").value(request.schemaVersion))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should get message type by id`() {
        mockMvc.perform(get("/message-types/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.typeCode").value("PACS.008.001.08"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should get message type by code`() {
        mockMvc.perform(get("/message-types/by-code/PACS.008.001.08"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.typeCode").value("PACS.008.001.08"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should get all message types`() {
        mockMvc.perform(get("/message-types"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should update message type`() {
        // given
        val request = MessageTypeRequest(
            typeCode = "PACS.008.001.08",
            description = "Updated description",
            schemaVersion = "8.1"
        )

        // when/then
        mockMvc.perform(
            put("/message-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.description").value("Updated description"))
            .andExpect(jsonPath("$.schemaVersion").value("8.1"))
    }

    @Test
    fun `should validate message type code`() {
        mockMvc.perform(get("/message-types/validate/PACS.008.001.08"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.valid").value(true))

        mockMvc.perform(get("/message-types/validate/invalid-format"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.valid").value(false))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql")
    fun `should handle invalid message type creation`() {
        // given
        val request = MessageTypeRequest(
            typeCode = "invalid-format",
            description = "Invalid format"
        )

        // when/then
        mockMvc.perform(
            post("/message-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should handle not found message type`() {
        mockMvc.perform(get("/message-types/999"))
            .andExpect(status().isNotFound)
    }
}
