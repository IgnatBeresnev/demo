package com.example.iso.repository

import com.example.iso.config.TestDatabaseConfig
import com.example.iso.domain.FinancialMessage
import com.example.iso.domain.MessageStatus
import com.example.iso.domain.MessageType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

@JdbcTest
@Import(TestDatabaseConfig::class, FinancialMessageJdbcRepository::class, MessageTypeJdbcRepository::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class FinancialMessageRepositoryTest {

    @Autowired
    private lateinit var financialMessageRepository: FinancialMessageRepository

    @Autowired
    private lateinit var messageTypeRepository: MessageTypeRepository

    private lateinit var testMessageType: MessageType

    @BeforeEach
    fun setup() {
        testMessageType = messageTypeRepository.findById(1)
            ?: throw IllegalStateException("Test message type not found")
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should create new financial message`() {
        // given
        val message = createTestMessage()

        // when
        val saved = financialMessageRepository.save(message)

        // then
        assertNotNull(saved)
        assertEquals(message.businessMessageIdentifier, saved.businessMessageIdentifier)
        assertEquals(MessageStatus.RECEIVED, saved.status)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find message by id`() {
        // given
        val message = financialMessageRepository.save(createTestMessage())

        // when
        val found = financialMessageRepository.findById(message.id)

        // then
        assertNotNull(found)
        assertEquals(message.id, found?.id)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find message by business identifier`() {
        // given
        val message = financialMessageRepository.save(createTestMessage())

        // when
        val found = financialMessageRepository.findByBusinessMessageIdentifier(message.businessMessageIdentifier)

        // then
        assertNotNull(found)
        assertEquals(message.businessMessageIdentifier, found?.businessMessageIdentifier)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find messages by status`() {
        // given
        val message = financialMessageRepository.save(createTestMessage())
        financialMessageRepository.updateStatus(message.id, MessageStatus.PROCESSING)

        // when
        val messages = financialMessageRepository.findByStatus(MessageStatus.PROCESSING)

        // then
        assertEquals(1, messages.size)
        assertEquals(MessageStatus.PROCESSING, messages[0].status)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find messages by type`() {
        // given
        financialMessageRepository.save(createTestMessage())

        // when
        val messages = financialMessageRepository.findByMessageType(testMessageType.id!!)

        // then
        assertEquals(1, messages.size)
        assertEquals(testMessageType.id, messages[0].messageType.id)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should update message status`() {
        // given
        val message = financialMessageRepository.save(createTestMessage())

        // when
        val updated = financialMessageRepository.updateStatus(
            message.id,
            MessageStatus.COMPLETED,
            "Processing completed successfully"
        )

        // then
        assertNotNull(updated)
        assertEquals(MessageStatus.COMPLETED, updated?.status)
        assertEquals("Processing completed successfully", updated?.errorDescription)
    }

    private fun createTestMessage() = FinancialMessage(
        messageType = testMessageType,
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
}
