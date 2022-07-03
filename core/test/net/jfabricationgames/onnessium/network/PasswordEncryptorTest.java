package net.jfabricationgames.onnessium.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;

public class PasswordEncryptorTest {
	
	@Test
	public void testEmptyString() {
		String empty = "";
		
		String encrypted = PasswordEncryptor.encrypt(empty);
		String decrypted = PasswordEncryptor.decrypt(encrypted);
		
		assertEquals("", encrypted);
		assertEquals("", decrypted);
	}
	
	@Test
	public void testEncryptAndDecrypt() {
		String plain = "save_password_1!";
		
		String encrypted = PasswordEncryptor.encrypt(plain);
		String decrypted = PasswordEncryptor.decrypt(encrypted);
		
		assertNotEquals(plain, encrypted);
		assertEquals(plain, decrypted);
	}
}
