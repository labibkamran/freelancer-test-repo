/**
 * REAI Email Voucher Reception Worker
 * Processes incoming emails and forwards document attachments to REAI API
 * Files larger than 5MB are ignored
 */

import PostalMime from 'postal-mime';

const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

interface Env {
	API_TOKEN: string;
	VOUCHER_API_BASE: string;
}

interface EmailMessage {
	from: string;
	to: string;
	headers: Headers;
	raw: ReadableStream<Uint8Array>;
}

interface DocumentPayload {
	filename: string;
	mimeType: string;
	fileSize: number;
	fileData: string; // base64 encoded
	senderEmail: string;
}

export default {
	async email(message: EmailMessage, env: Env, ctx: ExecutionContext): Promise<void> {
		try {
			// Parse email content
			const rawEmail = await new Response(message.raw).arrayBuffer();
			const email = await PostalMime.parse(rawEmail);
			
			// Extract company slug from recipient email (e.g., acme@ea.reai.no -> acme)
			const recipientEmail = message.to;
			const companySlug = recipientEmail.split('@')[0];
			
			if (!companySlug) {
				console.error('Could not extract company slug from recipient:', recipientEmail);
				return;
			}
			
			// Process attachments
			const attachments = email.attachments || [];
			console.log(`Processing ${attachments.length} attachments for company: ${companySlug}`);
			
			for (const attachment of attachments) {
				if (attachment.size > MAX_FILE_SIZE) {
					console.log(`Skipping attachment ${attachment.filename}: size ${attachment.size} exceeds 5MB limit`);
					continue;
				}
				
				if (!attachment.content) {
					console.log(`Skipping attachment ${attachment.filename}: no content`);
					continue;
				}
				
				// Convert content to base64
				const fileData = btoa(String.fromCharCode(...new Uint8Array(attachment.content)));
				
				const payload: DocumentPayload = {
					filename: attachment.filename || 'unnamed_file',
					mimeType: attachment.mimeType || 'application/octet-stream',
					fileSize: attachment.size,
					fileData: fileData,
					senderEmail: message.from
				};
				
				// Send to REAI API
				await sendToReaiApi(companySlug, payload, env);
			}
			
		} catch (error) {
			console.error('Error processing email:', error);
		}
	},

	async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
		// Health check endpoint
		if (request.method === 'GET' && new URL(request.url).pathname === '/health') {
			return new Response('OK', { status: 200 });
		}
		
		return new Response('Email worker active', { status: 200 });
	},
} satisfies ExportedHandler<Env>;

async function sendToReaiApi(companySlug: string, payload: DocumentPayload, env: Env): Promise<void> {
	try {
		const response = await fetch(`${env.VOUCHER_API_BASE}/documents`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${env.API_TOKEN}`,
				'X-Company-Slug': companySlug,
			},
			body: JSON.stringify(payload),
		});
		
		if (!response.ok) {
			throw new Error(`API request failed: ${response.status} ${response.statusText}`);
		}
		
		console.log(`Successfully uploaded document ${payload.filename} for company ${companySlug}`);
		
	} catch (error) {
		console.error(`Failed to upload document ${payload.filename} for company ${companySlug}:`, error);
		throw error;
	}
}
