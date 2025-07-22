-- Add AI extraction support to vouchers and create invoice AI extractions table

-- Add AI extraction reference to vouchers table
ALTER TABLE vouchers ADD COLUMN ai_extraction_id BIGINT;

-- Create invoice AI extractions table
CREATE TABLE invoice_ai_extractions (
    id BIGSERIAL PRIMARY KEY,
    voucher_reception_document_id BIGINT NOT NULL,
    extraction_data JSONB NOT NULL,
    extraction_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    extraction_date TIMESTAMP,
    processing_errors TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ai_extraction_voucher_document 
        FOREIGN KEY (voucher_reception_document_id) 
        REFERENCES voucher_reception_documents(id) 
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_ai_extractions_document_id ON invoice_ai_extractions(voucher_reception_document_id);
CREATE INDEX idx_ai_extractions_status ON invoice_ai_extractions(extraction_status);
CREATE INDEX idx_vouchers_ai_extraction_id ON vouchers(ai_extraction_id);

-- Add comments for documentation
COMMENT ON TABLE invoice_ai_extractions IS 'Stores AI extraction results from invoice processing';
COMMENT ON COLUMN invoice_ai_extractions.extraction_data IS 'JSONB data containing AI extracted invoice information';
COMMENT ON COLUMN invoice_ai_extractions.extraction_status IS 'Status: PENDING, PROCESSING, COMPLETED, FAILED, CONVERTED_TO_VOUCHER';
COMMENT ON COLUMN vouchers.ai_extraction_id IS 'Reference to AI extraction that created this voucher';
