-- Create voucher_documents table for email voucher reception
CREATE TABLE voucher_documents (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_data BYTEA,
    mime_type VARCHAR(100),
    file_size BIGINT,
    sender_email VARCHAR(255),
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    attached_voucher_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_voucher_documents_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_voucher_documents_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- Create indexes for performance
CREATE INDEX idx_voucher_documents_company_tenant ON voucher_documents(company_id, tenant_id);
CREATE INDEX idx_voucher_documents_tenant_pending ON voucher_documents(tenant_id) WHERE attached_voucher_id IS NULL;
CREATE INDEX idx_voucher_documents_received_at ON voucher_documents(received_at DESC);
CREATE INDEX idx_voucher_documents_attached_voucher ON voucher_documents(attached_voucher_id) WHERE attached_voucher_id IS NOT NULL;