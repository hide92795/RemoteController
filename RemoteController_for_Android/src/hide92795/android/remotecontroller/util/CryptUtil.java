package hide92795.android.remotecontroller.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.java_websocket.util.Base64;

public class CryptUtil {
	public static String decrypt(String text, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		String result = "";
		String[] datas = text.split(":");

		String iv_b64 = datas[0];
		String encrypted_b64 = datas[1];

		byte[] iv = Base64Coder.decode(iv_b64);
		byte[] text_b = Base64Coder.decode(encrypted_b64);

		Key keySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);

		byte[] decrypted = cipher.doFinal(text_b);
		result = new String(decrypted, Charset.forName("UTF-8"));
		return result;
	}

	public static String encrypt(String text, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String result = "";
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);

		byte[] iv = cipher.getIV();
		byte[] encrypted = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));

		String iv_b64 = String.valueOf(Base64Coder.encode(iv));
		String encrypted_b64 = String.valueOf(Base64Coder.encode(encrypted));

		result = iv_b64 + ":" + encrypted_b64;
		return result;
	}

	public static RSAKeyExchangePair rsaKeyExchange(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		String[] rsa_key_sa = data.split(":");
		String modules_s = rsa_key_sa[0];
		String publicExponent_s = rsa_key_sa[1];

		BigInteger modules = new BigInteger(modules_s);
		BigInteger publicExponent = new BigInteger(publicExponent_s);

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modules, publicExponent);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// Gen key
		char[] password = UUID.randomUUID().toString().toCharArray();
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password, salt, 10, 128);
		SecretKey tmp = factory.generateSecret(spec);

		byte[] key = tmp.getEncoded();

		byte[] common_key_base64 = Base64.encodeBytesToBytes(key);
		byte[] common_key_encrypted = cipher.doFinal(common_key_base64);
		char[] common_key_base64_encoded = Base64Coder.encode(common_key_encrypted);
		return new RSAKeyExchangePair(key, common_key_base64_encoded);
	}

	public static class RSAKeyExchangePair {
		public final byte[] key;
		public final char[] common_key_base64_encoded;

		public RSAKeyExchangePair(byte[] key, char[] common_key_base64_encoded) {
			this.key = key;
			this.common_key_base64_encoded = common_key_base64_encoded;
		}
	}
}
