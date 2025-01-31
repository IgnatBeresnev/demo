INSERT INTO message_types (id, type_code, description, schema_version, created_at)
VALUES 
    (1, 'PACS.008.001.08', 'FIToFICustomerCreditTransfer', '8.0', CURRENT_TIMESTAMP),
    (2, 'PACS.009.001.08', 'FinancialInstitutionCreditTransfer', '8.0', CURRENT_TIMESTAMP);

-- Reset the sequence to ensure next ID is after our test data
SELECT setval('message_types_id_seq', (SELECT MAX(id) FROM message_types));
