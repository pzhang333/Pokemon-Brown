package cs.brown.edu.aelp.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class contains functions to generate salts, hash passwords, and compare
 * a password with the hashed password given a salt.
 */
public final class Password {

  private static final int DERIVED_KEY_LENGTH = 160;
  private static final int ITERATIONS = 50000;
  private static final int BYTES_IN_64_BITS = 8;

  /**
   * Private constructor to prevent instantiation.
   */
  private Password() {
  }

  /**
   * Given the parameters, generates a salted password according to the PBKDF2
   * specs.
   *
   * @param password
   *          the password to hash
   * @param salt
   *          the SHA1 salt (use the generateSalt function in the class)
   * @return the salted password
   * @throws NoSuchAlgorithmException
   *           if the specified algorithm doesn't exist
   * @throws InvalidKeySpecException
   *           if the the key spec is invalid
   */
  public static byte[] hashPassword(String password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String algorithmToUse = "PBKDF2WithHmacSHA1";

    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS,
        DERIVED_KEY_LENGTH);

    SecretKeyFactory f = SecretKeyFactory.getInstance(algorithmToUse);

    return f.generateSecret(spec).getEncoded();
  }

  /**
   * A function that evaluates a given password with the specified parameters
   * and outputs true only if the given password is correct.
   *
   * @param attempt
   *          the attempted password
   * @param hashedPw
   *          the stored, hashed password
   * @param salt
   *          the salt used to generate the stored password
   * @return true if the 2 passwords match, false otherwise
   * @throws InvalidKeySpecException
   *           if the the key spec is invalid
   * @throws NoSuchAlgorithmException
   *           if the specified algorithm doesn't exist
   */
  public static boolean authenticate(String attempt, byte[] hashedPw,
      byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
    byte[] hashedAttempt = hashPassword(attempt, salt);

    return Arrays.equals(hashedAttempt, hashedPw);
  }

  /**
   * Generates a SHA1 salt.
   *
   * @return a salt
   * @throws NoSuchAlgorithmException
   *           if the specified algorithm doesn't exist
   */
  public static byte[] generateSalt() throws NoSuchAlgorithmException {
    SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");

    byte[] salt = new byte[BYTES_IN_64_BITS];
    rng.nextBytes(salt);

    return salt;
  }
}
