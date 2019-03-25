package eu.ditas.tub;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class Crypto {

    public static final String CIPHER_METHOD = "RSA/ECB/OAEPWithSHA-256AndSHA-256Padding";

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize,SecureRandom.getInstance("SHA1PRNG"));
        return keyPairGenerator.genKeyPair();
    }

    public static PublicKey importKey(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(key);
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        return pubKey;
    }

    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    private static Cipher getCipher(int optmode, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        oaepFromInit.init(optmode,key,oaepParams,new SecureRandom());
        return oaepFromInit;
    }

    public static byte[] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(encrypted);
    }
}
