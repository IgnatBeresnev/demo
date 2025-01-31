package com.example.iso.repository

import com.example.iso.config.TestDatabaseConfig
import com.example.iso.domain.MessageType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@Import(TestDatabaseConfig::class, MessageTypeJdbcRepository::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MessageTypeRepositoryTest {

    @Autowired
    private lateinit var messageTypeRepository: MessageTypeRepository

    @Test
    @Sql("/db/test-data/clear-message-types.sql")
    fun `should create new message type`() {
        // given
        val messageType = MessageType(
            typeCode = "PACS.008.001.08",
            description = "FIToFICustomerCreditTransfer",
            schemaVersion = "8.0"
        )

        // when
        val saved = messageTypeRepository.save(messageType)

        // then
        assertNotNull(saved.id)
        assertEquals(messageType.typeCode, saved.typeCode)
        assertEquals(messageType.description, saved.description)
        assertEquals(messageType.schemaVersion, saved.schemaVersion)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find message type by id`() {
        // when
        val found = messageTypeRepository.findById(1)

        // then
        assertNotNull(found)
        assertEquals("PACS.008.001.08", found?.typeCode)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find message type by type code`() {
        // when
        val found = messageTypeRepository.findByTypeCode("PACS.008.001.08")

        // then
        assertNotNull(found)
        assertEquals(1, found?.id)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should find all message types`() {
        // when
        val messageTypes = messageTypeRepository.findAll()

        // then
        assertEquals(2, messageTypes.size)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql", "/db/test-data/insert-message-types.sql")
    fun `should update existing message type`() {
        // given
        val messageType = messageTypeRepository.findById(1)!!.copy(
            description = "Updated description"
        )

        // when
        val updated = messageTypeRepository.save(messageType)

        // then
        assertEquals("Updated description", updated.description)
    }

    @Test
    @Sql("/db/test-data/clear-message-types.sql")
    fun `should validate message type code format`() {
        // given
        val invalidMessageType = MessageType(
            typeCode = "invalid-format",
            description = "Invalid format"
        )

        // then
        assertThrows<IllegalArgumentException> {
            messageTypeRepository.save(invalidMessageType)
        }
    }
}
