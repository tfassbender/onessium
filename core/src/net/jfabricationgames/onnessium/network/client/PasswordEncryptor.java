package net.jfabricationgames.onnessium.network.client;

/**
 * Encrypts passwords with a simple vigenere encryption.
 */
public class PasswordEncryptor {
	
	private static final char[] ENCRYPTION_KEY = "chDh39vc!cfkh3124CV$§jkfl3hi5o43h5o12fsg4fg".toCharArray();
	
	private PasswordEncryptor() {}
	
	public static String encrypt(String plainText) {
		char[] plain = plainText.toCharArray();
		char[] output = new char[plain.length];
		
		for (int i = 0; i < plain.length; i++) {
			int result = (plain[i] + ENCRYPTION_KEY[i % ENCRYPTION_KEY.length]) % 128;
			output[i] = (char) result;
		}
		
		return new String(output);
	}
	
	public static String decrypt(String encryptedText) {
		char[] plain = encryptedText.toCharArray();
		char[] output = new char[plain.length];
		
		for (int i = 0; i < plain.length; i++) {
			int result = ((plain[i] - ENCRYPTION_KEY[i % ENCRYPTION_KEY.length]) + 128) % 128;
			output[i] = (char) result;
		}
		
		return new String(output);
	}
}
