/**
 * REAI Email Voucher Reception Worker
 * Processes incoming emails and forwards document attachments to REAI API
 * Files larger than 25MB are ignored
 */

import PostalMime from 'postal-mime';

const MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB in bytes

interface Env {
	API_TOKEN: string;
	VOUCHER_API_BASE: string;
}

// Use the official ForwardableEmailMessage interface from Cloudflare
interface ForwardableEmailMessage {
	readonly from: string;
	readonly to: string;
	readonly headers: Headers;
	readonly raw: ReadableStream<Uint8Array>;
	readonly rawSize: number;
	
	setReject(reason: string): void;
	forward(rcptTo: string, headers?: Headers): Promise<void>;
	reply(message: EmailMessage): Promise<void>;
}

interface EmailMessage {
	readonly from: string;
	readonly to: string;
}

interface DocumentPayload {
	filename: string;
	mimeType: string;
	fileSize: number;
	fileData: string; // base64 encoded
	senderEmail: string;
}

export default {
	async email(message: ForwardableEmailMessage, env: Env, _ctx: ExecutionContext): Promise<void> {
		try {
			// Check if message is too large (early exit)
			if (message.rawSize > MAX_FILE_SIZE * 2) {
				console.log(`Email too large: ${message.rawSize} bytes`);
				return;
			}
			
			// Parse email content using PostalMime with base64 encoding for attachments
			const rawEmail = await new Response(message.raw).arrayBuffer();
			const email = await PostalMime.parse(rawEmail, {
				attachmentEncoding: 'base64' // This returns attachments as base64 strings directly
			});
			
			// Extract company slug from recipient email (e.g., acme@ea.reai.no -> acme)
			const recipientEmail = message.to;
			const companySlug = recipientEmail.split('@')[0].toLowerCase();
			
			if (!companySlug) {
				console.error('Could not extract company slug from recipient:', recipientEmail);
				return;
			}
			
			// Process attachments
			const attachments = email.attachments || [];
			console.log(`Processing ${attachments.length} attachments for company: ${companySlug}`);
			
			for (const attachment of attachments) {
				if (!attachment.content) {
					console.log(`Skipping attachment ${attachment.filename}: no content`);
					continue;
				}
				
				// With attachmentEncoding: 'base64', content is already a base64 string
				const fileData = attachment.content as string;
				
				// Calculate size from base64 string (rough estimate: base64 is ~33% larger than original)
				const estimatedSize = Math.floor((fileData.length * 3) / 4);
				
				if (estimatedSize > MAX_FILE_SIZE) {
					console.log(`Skipping attachment ${attachment.filename}: estimated size ${estimatedSize} exceeds 25MB limit`);
					continue;
				}
				
				const payload: DocumentPayload = {
					filename: attachment.filename || 'unnamed_file',
					mimeType: attachment.mimeType || 'application/octet-stream',
					fileSize: estimatedSize,
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
				'X-Company-Slug': companySlug,
				'Authorization': `Bearer ${env.API_TOKEN}`,
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
