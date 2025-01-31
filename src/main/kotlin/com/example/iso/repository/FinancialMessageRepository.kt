package com.example.iso.repository

import com.example.iso.domain.FinancialMessage
import com.example.iso.domain.MessageStatus
import com.example.iso.domain.MessageType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

interface FinancialMessageRepository {
    fun findById(id: UUID): FinancialMessage?
    fun findByBusinessMessageIdentifier(bmi: String): FinancialMessage?
    fun findByStatus(status: MessageStatus): List<FinancialMessage>
    fun findByMessageType(messageTypeId: Int): List<FinancialMessage>
    fun save(message: FinancialMessage): FinancialMessage
    fun updateStatus(id: UUID, status: MessageStatus, errorDescription: String? = null): FinancialMessage?
}

@Repository
class FinancialMessageJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val messageTypeRepository: MessageTypeRepository
) : FinancialMessageRepository {

    private val rowMapper = RowMapper<FinancialMessage> { rs: ResultSet, _: Int ->
        val messageType = messageTypeRepository.findById(rs.getInt("message_type_id"))
            ?: throw IllegalStateException("Message type not found")

        FinancialMessage(
            id = UUID.fromString(rs.getString("id")),
            messageType = messageType,
            businessMessageIdentifier = rs.getString("business_message_identifier"),
            creationDate = rs.getObject("creation_date", OffsetDateTime::class.java),
            messageDefinitionIdentifier = rs.getString("message_definition_identifier"),
            businessService = rs.getString("business_service"),
            marketPractice = rs.getString("market_practice"),
            senderReference = rs.getString("sender_reference"),
            status = MessageStatus.valueOf(rs.getString("status")),
            payload = rs.getString("payload"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            errorDescription = rs.getString("error_description")
        )
    }

    override fun findById(id: UUID): FinancialMessage? {
        return jdbcTemplate.query(
            "SELECT * FROM financial_messages WHERE id = ?",
            rowMapper,
            id
        ).firstOrNull()
    }

    override fun findByBusinessMessageIdentifier(bmi: String): FinancialMessage? {
        return jdbcTemplate.query(
            "SELECT * FROM financial_messages WHERE business_message_identifier = ?",
            rowMapper,
            bmi
        ).firstOrNull()
    }

    override fun findByStatus(status: MessageStatus): List<FinancialMessage> {
        return jdbcTemplate.query(
            "SELECT * FROM financial_messages WHERE status = ?::message_status",
            rowMapper,
            status.name
        )
    }

    override fun findByMessageType(messageTypeId: Int): List<FinancialMessage> {
        return jdbcTemplate.query(
            "SELECT * FROM financial_messages WHERE message_type_id = ?",
            rowMapper,
            messageTypeId
        )
    }

    override fun save(message: FinancialMessage): FinancialMessage {
        val sql = """
            INSERT INTO financial_messages (
                id, message_type_id, business_message_identifier, creation_date,
                message_definition_identifier, business_service, market_practice,
                sender_reference, status, payload, error_description
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::message_status, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                message_type_id = EXCLUDED.message_type_id,
                business_message_identifier = EXCLUDED.business_message_identifier,
                creation_date = EXCLUDED.creation_date,
                message_definition_identifier = EXCLUDED.message_definition_identifier,
                business_service = EXCLUDED.business_service,
                market_practice = EXCLUDED.market_practice,
                sender_reference = EXCLUDED.sender_reference,
                status = EXCLUDED.status,
                payload = EXCLUDED.payload,
                error_description = EXCLUDED.error_description,
                updated_at = CURRENT_TIMESTAMP
        """.trimIndent()

        jdbcTemplate.update(sql,
            message.id,
            message.messageType.id,
            message.businessMessageIdentifier,
            message.creationDate,
            message.messageDefinitionIdentifier,
            message.businessService,
            message.marketPractice,
            message.senderReference,
            message.status.name,
            message.payload,
            message.errorDescription
        )

        return findById(message.id)!!
    }

    override fun updateStatus(id: UUID, status: MessageStatus, errorDescription: String?): FinancialMessage? {
        val updated = jdbcTemplate.update(
            """
            UPDATE financial_messages 
            SET status = ?::message_status, 
                error_description = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """.trimIndent(),
            status.name,
            errorDescription,
            id
        )

        return if (updated > 0) findById(id) else null
    }
}
