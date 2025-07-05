# Local Testing Setup - Email Voucher Reception

## Prerequisites
- REAI app running locally on `localhost:8080`
- Cloudflare account with Email Routing enabled
- Ngrok or Cloudflare Tunnel installed

## Option 1: Using Ngrok

### 1. Install Ngrok
```bash
# Download from https://ngrok.com/download
# Or use package manager:
brew install ngrok  # macOS
snap install ngrok  # Ubuntu
```

### 2. Start Your Local REAI App
```bash
./gradlew bootRun
# Verify it's running at http://localhost:8080
```

### 3. Create Ngrok Tunnel
```bash
ngrok http 8080
```

You'll see output like:
```
Session Status                online
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://abc123-unique.ngrok-free.app -> http://localhost:8080
```

### 4. Update Worker Environment
```bash
cd cloudflare-worker

# Update development environment to use ngrok URL
npx wrangler secret put VOUCHER_API_BASE --env development
# Enter: https://abc123-unique.ngrok-free.app/api/voucher-reception
```

### 5. Deploy Worker with Dev Environment
```bash
npx wrangler deploy --env development
```

### 6. Test Email Flow
```bash
# Send test email to your configured address
# Or use the test script:
curl -X POST https://reai-email-worker-dev.{your-account}.workers.dev/test \
  -H "Content-Type: application/json" \
  -d '{
    "to": "test-company@ea.reai.no",
    "from": "supplier@example.com",
    "attachment": "base64-encoded-test-file"
  }'
```

## Option 2: Using Cloudflare Tunnel

### 1. Install Cloudflare Tunnel
```bash
# macOS
brew install cloudflared

# Linux
wget -q https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb
sudo dpkg -i cloudflared-linux-amd64.deb
```

### 2. Login to Cloudflare
```bash
cloudflared tunnel login
```

### 3. Create Tunnel
```bash
cloudflared tunnel create reai-dev
```

### 4. Configure Tunnel
Create `~/.cloudflared/config.yml`:
```yaml
tunnel: reai-dev
credentials-file: /home/{user}/.cloudflared/{tunnel-id}.json

ingress:
  - hostname: reai-dev.{your-domain}.com
    service: http://localhost:8080
  - service: http_status:404
```

### 5. Run Tunnel
```bash
cloudflared tunnel run reai-dev
```

### 6. Update Worker Environment
```bash
npx wrangler secret put VOUCHER_API_BASE --env development
# Enter: https://reai-dev.{your-domain}.com/api/voucher-reception
```

## Testing the Complete Flow

### 1. Monitor Logs
Open multiple terminals:

```bash
# Terminal 1 - REAI App logs
./gradlew bootRun

# Terminal 2 - Ngrok/Tunnel
ngrok http 8080

# Terminal 3 - Worker logs
npx wrangler tail --env development
```

### 2. Create Test Company
```bash
# Use your REAI app to create a test company
# Or directly in database:
psql -U postgres -d reai
INSERT INTO companies (name, tenant_id) VALUES ('Test Company', 1);
```

### 3. Send Test Email
Use the test script to simulate email:
```bash
./test-email-reception.sh
```

### 4. Verify Results

Check each component:
1. **Worker Logs**: Should show email received and processed
2. **Ngrok Web Interface**: http://localhost:4040 shows requests
3. **REAI App Logs**: Should show incoming POST to /api/voucher-reception/documents
4. **Database**: Check voucher_documents table

```sql
SELECT * FROM voucher_documents WHERE company_id = 1;
```

## Troubleshooting

### Common Issues:

1. **502 Bad Gateway from Ngrok**
   - Ensure REAI app is running on 8080
   - Check Spring Security allows /api/voucher-reception/**

2. **Worker Can't Connect**
   - Verify VOUCHER_API_BASE is correct
   - Check worker environment matches deployment

3. **No Emails Received**
   - Verify Email Routing is configured
   - Check MX records are propagated
   - Use `npx wrangler tail` to see if worker is triggered

### Debug Commands:
```bash
# Check worker deployment
npx wrangler deployments list

# View worker configuration
npx wrangler secret list --env development

# Test worker directly
curl https://reai-email-worker-dev.{account}.workers.dev/health

# Check Ngrok status
curl http://localhost:4040/api/tunnels
```

## Complete Test Workflow

1. **Start all services**:
   ```bash
   ./gradlew bootRun &
   ngrok http 8080 &
   npx wrangler tail --env development
   ```

2. **Send test email** to `test@ea.reai.no`

3. **Watch the flow**:
   - Email → Cloudflare Email Routing
   - → Worker processes
   - → Ngrok tunnel
   - → Local REAI app
   - → PostgreSQL

4. **Verify** document saved in database