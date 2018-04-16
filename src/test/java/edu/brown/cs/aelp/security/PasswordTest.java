package edu.brown.cs.aelp.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Test;

import cs.brown.edu.aelp.security.Password;

public class PasswordTest {
  @Test
  public void generateSaltTests() throws NoSuchAlgorithmException {
    byte[] salt = Password.generateSalt();
    assertEquals(salt.length, 8, 0);
  }

  @Test
  public void hashingPasswordTest()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = Password.generateSalt();
    String pw = "hunter2";
    Password.hashPassword(pw, salt);
  }

  @Test
  public void validPassword()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = Password.generateSalt();
    String pw = "hunter2";
    byte[] hashedPw = Password.hashPassword(pw, salt);
    String guess = "hunter2";
    assertTrue(Password.authenticate(guess, hashedPw, salt));
  }

  @Test
  public void invalidPassword()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = Password.generateSalt();
    String pw = "hunter2";
    byte[] hashedPw = Password.hashPassword(pw, salt);
    String guess = "********";
    assertFalse(Password.authenticate(guess, hashedPw, salt));
  }
}
