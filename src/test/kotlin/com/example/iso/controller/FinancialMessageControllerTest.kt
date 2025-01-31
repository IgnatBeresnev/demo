package com.example.iso.controller

import com.example.iso.TestApplication
import com.example.iso.domain.MessageStatus
import com.example.iso.dto.FinancialMessageRequest
import com.example.iso.dto.MessageStatusUpdateRequest
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
import java.util.UUID

@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinancialMessageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should create new financial message`() {
        // given
        val request = FinancialMessageRequest(
            messageTypeId = 1,
            businessMessageIdentifier = "TEST-BMI-${UUID.randomUUID()}",
            messageDefinitionIdentifier = "pacs.008.001.08",
            payload = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
                    <FIToFICstmrCdtTrf>
                        <GrpHdr>
                            <MsgId>TEST-MSG-ID</MsgId>
                            <CreDtTm>2024-01-01T10:00:00</CreDtTm>
                            <NbOfTxs>1</NbOfTxs>
                        </GrpHdr>
                    </FIToFICstmrCdtTrf>
                </Document>
            """.trimIndent()
        )

        // when/then
        mockMvc.perform(
            post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.messageType.id").value(1))
            .andExpect(jsonPath("$.businessMessageIdentifier").value(request.businessMessageIdentifier))
            .andExpect(jsonPath("$.status").value("RECEIVED"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should process financial message`() {
        // given
        val request = FinancialMessageRequest(
            messageTypeId = 1,
            businessMessageIdentifier = "TEST-BMI-${UUID.randomUUID()}",
            messageDefinitionIdentifier = "pacs.008.001.08",
            payload = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
                    <FIToFICstmrCdtTrf>
                        <GrpHdr>
                            <MsgId>TEST-MSG-ID</MsgId>
                            <CreDtTm>2024-01-01T10:00:00</CreDtTm>
                            <NbOfTxs>1</NbOfTxs>
                        </GrpHdr>
                    </FIToFICstmrCdtTrf>
                </Document>
            """.trimIndent()
        )

        val createResult = mockMvc.perform(
            post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val messageId = objectMapper.readTree(createResult.response.contentAsString)
            .get("id").asText()

        // when/then
        mockMvc.perform(post("/messages/$messageId/process"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("COMPLETED"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should update message status`() {
        // given
        val request = FinancialMessageRequest(
            messageTypeId = 1,
            businessMessageIdentifier = "TEST-BMI-${UUID.randomUUID()}",
            messageDefinitionIdentifier = "pacs.008.001.08",
            payload = "<test>payload</test>"
        )

        val createResult = mockMvc.perform(
            post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val messageId = objectMapper.readTree(createResult.response.contentAsString)
            .get("id").asText()

        val statusUpdate = MessageStatusUpdateRequest(
            status = MessageStatus.PROCESSING,
            errorDescription = null
        )

        // when/then
        mockMvc.perform(
            patch("/messages/$messageId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("PROCESSING"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should get messages by status`() {
        // given
        val request = FinancialMessageRequest(
            messageTypeId = 1,
            businessMessageIdentifier = "TEST-BMI-${UUID.randomUUID()}",
            messageDefinitionIdentifier = "pacs.008.001.08",
            payload = "<test>payload</test>"
        )

        mockMvc.perform(
            post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        // when/then
        mockMvc.perform(get("/messages/by-status/RECEIVED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].status").value("RECEIVED"))
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should handle invalid XML payload`() {
        // given
        val request = FinancialMessageRequest(
            messageTypeId = 1,
            businessMessageIdentifier = "TEST-BMI-${UUID.randomUUID()}",
            messageDefinitionIdentifier = "pacs.008.001.08",
            payload = "invalid xml"
        )

        // when/then
        mockMvc.perform(
            post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should handle not found message`() {
        mockMvc.perform(get("/messages/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound)
    }
}
