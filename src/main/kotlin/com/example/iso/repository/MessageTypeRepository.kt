package com.example.iso.repository

import com.example.iso.domain.MessageType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.OffsetDateTime

interface MessageTypeRepository {
    fun findById(id: Int): MessageType?
    fun findByTypeCode(typeCode: String): MessageType?
    fun findAll(): List<MessageType>
    fun save(messageType: MessageType): MessageType
}

@Repository
class MessageTypeJdbcRepository(private val jdbcTemplate: JdbcTemplate) : MessageTypeRepository {
    
    private val rowMapper = RowMapper<MessageType> { rs: ResultSet, _: Int ->
        MessageType(
            id = rs.getInt("id"),
            typeCode = rs.getString("type_code"),
            description = rs.getString("description"),
            schemaVersion = rs.getString("schema_version"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java)
        )
    }

    override fun findById(id: Int): MessageType? {
        return jdbcTemplate.query(
            "SELECT * FROM message_types WHERE id = ?",
            rowMapper,
            id
        ).firstOrNull()
    }

    override fun findByTypeCode(typeCode: String): MessageType? {
        return jdbcTemplate.query(
            "SELECT * FROM message_types WHERE type_code = ?",
            rowMapper,
            typeCode
        ).firstOrNull()
    }

    override fun findAll(): List<MessageType> {
        return jdbcTemplate.query("SELECT * FROM message_types", rowMapper)
    }

    override fun save(messageType: MessageType): MessageType {
        if (messageType.id == null) {
            val id = jdbcTemplate.queryForObject("""
                INSERT INTO message_types (type_code, description, schema_version)
                VALUES (?, ?, ?)
                RETURNING id
            """.trimIndent(),
                Int::class.java,
                messageType.typeCode,
                messageType.description,
                messageType.schemaVersion
            )
            return findById(id!!)!!
        } else {
            jdbcTemplate.update("""
                UPDATE message_types 
                SET type_code = ?, description = ?, schema_version = ?
                WHERE id = ?
            """.trimIndent(),
                messageType.typeCode,
                messageType.description,
                messageType.schemaVersion,
                messageType.id
            )
            return findById(messageType.id)!!
        }
    }
}
