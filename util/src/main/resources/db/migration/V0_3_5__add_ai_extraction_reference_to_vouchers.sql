-- Add AI extraction reference to vouchers table
ALTER TABLE vouchers 
ADD COLUMN ai_extraction_id INTEGER;

-- Add foreign key constraint
ALTER TABLE vouchers 
ADD CONSTRAINT fk_vouchers_ai_extraction 
FOREIGN KEY (ai_extraction_id) 
REFERENCES invoice_ai_extractions (id) ON DELETE SET NULL;

-- Create index for efficient querying
CREATE INDEX idx_vouchers_ai_extraction_id 
ON vouchers(ai_extraction_id); 