-- Create enum for message status
CREATE TYPE message_status AS ENUM ('RECEIVED', 'VALIDATED', 'PROCESSING', 'COMPLETED', 'ERROR');

-- Create table for message types
CREATE TABLE message_types (
    id SERIAL PRIMARY KEY,
    type_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    schema_version VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT message_types_type_code_check CHECK (type_code ~ '^[A-Z]{4}\.[0-9]{3}\.[0-9]{3}\.[0-9]{2}$')
);

-- Create table for financial messages
CREATE TABLE financial_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_type_id INTEGER NOT NULL REFERENCES message_types(id),
    business_message_identifier VARCHAR(35) NOT NULL UNIQUE,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    message_definition_identifier VARCHAR(35) NOT NULL,
    business_service VARCHAR(35),
    market_practice VARCHAR(35),
    sender_reference VARCHAR(35),
    status message_status NOT NULL DEFAULT 'RECEIVED',
    payload XML NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    error_description TEXT,
    CONSTRAINT financial_messages_bmi_check CHECK (business_message_identifier ~ '^[A-Za-z0-9\-\+\?/:().,'']{1,35}$')
);

-- Create index for faster lookups
CREATE INDEX idx_financial_messages_message_type ON financial_messages(message_type_id);
CREATE INDEX idx_financial_messages_status ON financial_messages(status);
CREATE INDEX idx_financial_messages_creation_date ON financial_messages(creation_date);

-- Insert common ISO 20022 message types
INSERT INTO message_types (type_code, description, schema_version) VALUES
    ('pacs.008.001.08', 'FIToFICustomerCreditTransfer', '8.0'),
    ('pacs.009.001.08', 'FinancialInstitutionCreditTransfer', '8.0'),
    ('pacs.002.001.10', 'FIToFIPaymentStatusReport', '10.0'),
    ('pain.001.001.09', 'CustomerCreditTransferInitiation', '9.0'),
    ('camt.053.001.08', 'BankToCustomerStatement', '8.0'),
    ('camt.054.001.08', 'BankToCustomerDebitCreditNotification', '8.0'),
    ('head.001.001.01', 'BusinessApplicationHeader', '1.0');

-- Create function to update timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for updating timestamp
CREATE TRIGGER update_financial_messages_updated_at
    BEFORE UPDATE ON financial_messages
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
