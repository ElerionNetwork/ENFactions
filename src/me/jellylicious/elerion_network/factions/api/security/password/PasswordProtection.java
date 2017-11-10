package me.jellylicious.elerion_network.factions.api.security.password;

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

public class PasswordProtection {
	
	//Higher iterations mean hackers have to hack harder and computing is more expensive.
	private static final int iterations = 20*1000;
	private static final int saltLength = 32;
	private static final int desiredKeyLength = 256;
	
	public String getSaltedHash(String password) throws Exception {
		byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength);
		return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
	}
	
	public boolean check(String password, String stored) throws Exception {
		String[] saltAndPass = stored.split("\\$");
		if(saltAndPass.length != 2) {
			throw new IllegalStateException("The stored password has the form of 'salt$hash'.");
		}
		String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
		return hashOfInput.equals(saltAndPass[1]);
	}
	
	private String hash(String password, byte[] salt) throws Exception {
		if(password == null || password.length() == 0) throw new IllegalArgumentException("Empty passwords are not supported.");
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLength));
		return Base64.encodeBase64String(key.getEncoded());
	}

}
