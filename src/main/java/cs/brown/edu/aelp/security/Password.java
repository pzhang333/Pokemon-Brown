package cs.brown.edu.aelp.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * This class contains functions related to securely storing passwords in our database.
 * We justify our choice of hashing algorithm, number of iterations, etc. in our README.
 */
public final class Password {
    public static byte[] getHashedPassword(String password, byte[] salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithmToUse = "PBKDF2WithHmacSHA1";
        int derivedKeyLen = 160;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLen);

        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithmToUse);

        return f.generateSecret(spec).getEncoded();
    }

    public static boolean authenticate(String attempt, byte[] hashedPw, byte[] salt, int iterations) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] hashedAttempt = getHashedPassword(attempt, salt, iterations);

        return Arrays.equals(hashedAttempt, hashedPw);
    }

    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");

        byte[] salt = new byte[8];
        rng.nextBytes(salt);

        return salt;
    }
}
