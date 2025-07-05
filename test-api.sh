#!/bin/bash

echo "Testing Voucher Reception API..."

# Test 1: Try with different company slugs
echo -e "\n1. Testing with slug 'test-company':"
curl -s -X POST http://localhost:8080/api/voucher-reception/documents \
  -H "Content-Type: application/json" \
  -H "X-Company-Slug: test-company" \
  -d '{"filename":"test.pdf","fileData":"VGVzdA==","mimeType":"application/pdf","fileSize":1024,"senderEmail":"test@example.com"}'

echo -e "\n\n2. Testing with slug 'test':"
curl -s -X POST http://localhost:8080/api/voucher-reception/documents \
  -H "Content-Type: application/json" \
  -H "X-Company-Slug: test" \
  -d '{"filename":"test.pdf","fileData":"VGVzdA==","mimeType":"application/pdf","fileSize":1024,"senderEmail":"test@example.com"}'

echo -e "\n\n3. Testing with slug 'acme':"
curl -s -X POST http://localhost:8080/api/voucher-reception/documents \
  -H "Content-Type: application/json" \
  -H "X-Company-Slug: acme" \
  -d '{"filename":"test.pdf","fileData":"VGVzdA==","mimeType":"application/pdf","fileSize":1024,"senderEmail":"test@example.com"}'

echo -e "\n\nNote: You need to create a company first. The slug is derived from the company name."
echo "For example, 'Test Company' becomes 'test-company' as a slug."