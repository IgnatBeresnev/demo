package com.example.iso

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import com.example.iso.config.TestConfig
import com.example.iso.config.TestDatabaseConfig

@SpringBootApplication
@Import(TestConfig::class, TestDatabaseConfig::class)
class TestApplication
