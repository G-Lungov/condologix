const { Client, LocalAuth} = require('whatsapp-web.js');
const qrcode = require('qrcode-terminal');

class WhatsAppService {
    constructor() {
        this.client = new Client({
            authStrategy: new LocalAuth(),
            puppeteer: {
                headless: true,
                args: ['--no-sandbox', '--disable-setuid-sandbox']
            }

        });

        this.isReady = false;
        this.setupEventListeners();

    }

    setupEventListeners() {
        //QR Code Generation
        this.client.on('qr', (qr) => {
            console.log('WhatsApp QR Code:');
            qrcode.generate(qr, { small: true });
        });

        //Client ready
        this.client.on('ready', () => {
            console.log('WhatsApp client is ready!');
            this.isReady = true;
        });

        //Authentication failure
        this.client.on('auth_failure', (msg) => {
            console.error('WhatsApp authentication failed:', msg);
            this.isReady = false;
        });

        //Disconnected
        this.client.on('disconnected', (reason) => {
            console.log('WhatsApp client disconnected:', reason);
            this.isReady = false;
        });
    }

    async initialize() {
        try {
            await this.client.initialize();
            console.log('WhatsApp client initialized');
        } catch (error) {
            console.error('Error initializing WhatsApp client:', error);
            throw error;
        }
    }

    async sendMessage(phoneNumber, message) {
        if (!this.isReady) {
            throw new Error('WhatsApp client is not ready. Please scan QR code first.');
        }

        try {
            // Format phone number (remove any non-digits and add country code if needed)
            const formattedNumber = this.formatPhoneNumber(phoneNumber);
            const chatId = `${formattedNumber}@c.us`;
            
            const result = await this.client.sendMessage(chatId, message);
            console.log('Message sent successfully:', result.id._serialized);
            return { success: true, messageId: result.id._serialized };
        } catch (error) {
            console.error('Error sending WhatsApp message:', error);
            throw error;
        }
    }

    formatPhoneNumber(phoneNumber) {
        // Remove all non-digit characters
        let cleaned = phoneNumber.replace(/\D/g, '');
        
        // If it doesn't start with country code, assume Brazil (+55)
        if (cleaned.length === 11 && cleaned.startsWith('11')) {
            cleaned = '55' + cleaned;
        } else if (cleaned.length === 10) {
            cleaned = '55' + cleaned;
        }
        
        return cleaned;
    }

    isClientReady() {
        return this.isReady;
    }

    async disconnect() {
        if (this.client) {
            await this.client.destroy();
            this.isReady = false;
        }
    }
}

module.exports = WhatsAppService;