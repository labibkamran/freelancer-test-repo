-- Add AI extraction fields to voucher_reception_documents table
ALTER TABLE voucher_reception_documents 
ADD COLUMN ai_extraction_data JSONB,
ADD COLUMN extraction_status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN extraction_date TIMESTAMP,
ADD COLUMN processing_errors TEXT;

-- Create invoice_ai_extractions table
CREATE TABLE IF NOT EXISTS invoice_ai_extractions
(
    id                              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    voucher_reception_document_id   INTEGER   NOT NULL,
    extraction_data                 JSONB     NOT NULL,
    extraction_status               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    extraction_date                 TIMESTAMP,
    processing_errors               TEXT,
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_invoice_ai_extractions_voucher_reception_document 
        FOREIGN KEY (voucher_reception_document_id) 
        REFERENCES voucher_reception_documents (id) ON DELETE CASCADE
);

-- Create indexes for efficient querying
CREATE INDEX idx_voucher_reception_ai_extraction_status 
ON voucher_reception_documents(extraction_status);

CREATE INDEX idx_voucher_reception_ai_extraction_date 
ON voucher_reception_documents(extraction_date);

CREATE INDEX idx_invoice_ai_extractions_status 
ON invoice_ai_extractions(extraction_status);

CREATE INDEX idx_invoice_ai_extractions_document_id 
ON invoice_ai_extractions(voucher_reception_document_id);

CREATE INDEX idx_invoice_ai_extractions_date 
ON invoice_ai_extractions(extraction_date); 