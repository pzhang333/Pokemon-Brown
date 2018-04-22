package edu.brown.cs.aelp.pokemmo.pokemon;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class PokemonTest {
  @Test
  public void basicConstructionTests() {
    Pokemon a = new Pokemon.Builder(1).build();
    assertNotNull(a);
    Pokemon b = new Pokemon.Builder(2)
        .withGender(1)
        .withExp(1000)
        .withMaxHp(250)
        .build();
    assertNotNull(b);
  }
}
