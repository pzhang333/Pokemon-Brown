package cs.brown.edu.aelp.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemon.Main;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Trade {

  public enum TRADE_STATUS {
    BUSY,
    OPEN,
    CANCELED,
    COMPLETE,
    FAILED
  }

  private User player1;
  private User player2;

  private TRADE_STATUS status = TRADE_STATUS.OPEN;
  private boolean p1Accepted = false;
  private boolean p2Accepted = false;

  private int p1CurrencyOffer = 0;
  private int p2CurrencyOffer = 0;
  private Set<Pokemon> p1PokemonOffer = new HashSet<>();
  private Set<Pokemon> p2PokemonOffer = new HashSet<>();

  public Trade(User p1, User p2) {
    this.player1 = p1;
    this.player2 = p2;
  }

  public TRADE_STATUS getStatus() {
    return this.status;
  }

  public void setStatus(TRADE_STATUS s) {
    this.status = s;
  }

  public User getUser1() {
    return this.player1;
  }

  public User other(User u) {
    return this.player1.equals(u) ? this.player2 : this.player1;
  }

  public User getUser2() {
    return this.player2;
  }

  protected void invalidate() {
    this.p1Accepted = false;
    this.p2Accepted = false;
  }

  private void completeTrade() {
    int newsize1 = this.player1.getTeam().size() + this.p2PokemonOffer.size()
        - this.p1PokemonOffer.size();
    int newsize2 = this.player2.getTeam().size() + this.p1PokemonOffer.size()
        - this.p2PokemonOffer.size();
    if (newsize1 <= 0 || newsize1 > 5 || newsize2 <= 0 || newsize2 > 5) {
      this.setStatus(TRADE_STATUS.FAILED);
      System.out.println("Trade failed.");
    } else {
      this.player1.setCurrency(this.player1.getCurrency() + this.p2CurrencyOffer
          - this.p1CurrencyOffer);
      this.player2.setCurrency(this.player2.getCurrency() + this.p1CurrencyOffer
          - this.p2CurrencyOffer);
      for (Pokemon p : this.p2PokemonOffer) {
        p.setOwner(this.player1);
        this.player1.addPokemonToTeam(p);
        this.player2.removePokemonFromTeam(p);
      }
      for (Pokemon p : this.p1PokemonOffer) {
        p.setOwner(this.player2);
        this.player2.addPokemonToTeam(p);
        this.player1.removePokemonFromTeam(p);
      }
      if (!this.player1.getActivePokemon().getOwner().equals(this.player1)) {
        this.player1.setActivePokemon(this.player1.getTeam().get(0));
      }
      if (!this.player2.getActivePokemon().getOwner().equals(this.player2)) {
        this.player2.setActivePokemon(this.player2.getTeam().get(0));
      }
      this.setStatus(TRADE_STATUS.COMPLETE);
      System.out.printf("Completed trade between %s and %s.%n",
          this.player1.getUsername(), player2.getUsername());
      System.out.printf("%s gave away %d coins and %d pokemon.%n",
          player1.getUsername(), this.p1CurrencyOffer,
          this.p1PokemonOffer.size());
      System.out.printf("%s gave away %d coins and %d pokemon.%n",
          player2.getUsername(), this.p2CurrencyOffer,
          this.p2PokemonOffer.size());
    }
    PacketSender.sendTradePacket(this.player1, this);
    PacketSender.sendTradePacket(this.player2, this);
    this.player1.setActiveTrade(null);
    this.player2.setActiveTrade(null);
  }

  public boolean involves(User u) {
    return u.equals(this.player1) || u.equals(this.player2);
  }

  public void setAccepted(boolean user1) {
    if (user1) {
      this.p1Accepted = true;
    } else {
      this.p2Accepted = true;
    }
    if (this.p1Accepted && this.p2Accepted) {
      this.completeTrade();
    }
  }

  public boolean setCurrency(int curr, boolean user1) {
    User u = user1 ? this.player1 : this.player2;
    if (u.getCurrency() < curr || curr < 0
        || other(u).getCurrency() + curr < 0) {
      return false;
    }
    if (user1 && this.p1CurrencyOffer != curr) {
      this.p1CurrencyOffer = curr;
      this.invalidate();
    } else if (!user1 && this.p2CurrencyOffer != curr) {
      this.p2CurrencyOffer = curr;
      this.invalidate();
    }
    return true;
  }

  public boolean setPokemon(Set<Integer> pokemon, boolean user1) {
    User u = user1 ? this.player1 : this.player2;
    System.out.println("Setting pokemon for user1: " + user1);
    if (pokemon.size() >= 5) {
      System.out.println(pokemon.size() + " is too many");
      return false;
    }
    for (int id : pokemon) {
      System.out.println("Checking pokemon id: " + id);
      if (u.getPokemonById(id) == null || u.getPokemonById(id).isStored()) {
        System.out.println("Stored: " + u.getPokemonById(id).isStored());
        return false;
      }
    }
    Set<Pokemon> newPokemon = pokemon.stream().map(id -> u.getPokemonById(id))
        .collect(Collectors.toSet());
    if (user1 && !this.p1PokemonOffer.equals(newPokemon)) {
      this.p1PokemonOffer = newPokemon;
      this.invalidate();
    } else if (!user1 && !this.p2PokemonOffer.equals(newPokemon)) {
      this.p2PokemonOffer = newPokemon;
      this.invalidate();
    }
    return true;
  }

  public boolean isSameTrade(JsonObject o, boolean user1) {
    int id1;
    int id2;
    int curr1;
    int curr2;
    JsonArray pokemon1;
    JsonArray pokemon2;
    if (user1) {
      id1 = o.get("id").getAsInt();
      id2 = o.get("other_id").getAsInt();
      curr1 = o.get("me_currency").getAsInt();
      curr2 = o.get("other_currency").getAsInt();
      pokemon1 = o.get("me_pokemon").getAsJsonArray();
      pokemon2 = o.get("other_pokemon").getAsJsonArray();
    } else {
      id2 = o.get("id").getAsInt();
      id1 = o.get("other_id").getAsInt();
      curr2 = o.get("me_currency").getAsInt();
      curr1 = o.get("other_currency").getAsInt();
      pokemon2 = o.get("me_pokemon").getAsJsonArray();
      pokemon1 = o.get("other_pokemon").getAsJsonArray();
    }
    Set<Integer> true_p1_pokemon = p1PokemonOffer.stream().map(p -> p.getId())
        .collect(Collectors.toSet());
    Set<Integer> p1_pokemon = new HashSet<Integer>();
    for (JsonElement p : pokemon1) {
      p1_pokemon.add(p.getAsInt());
    }
    Set<Integer> true_p2_pokemon = p2PokemonOffer.stream().map(p -> p.getId())
        .collect(Collectors.toSet());
    Set<Integer> p2_pokemon = new HashSet<Integer>();
    for (JsonElement p : pokemon2) {
      p2_pokemon.add(p.getAsInt());
    }
    return id1 == this.player1.getId() && id2 == this.player2.getId()
        && curr1 == this.p1CurrencyOffer && curr2 == this.p2CurrencyOffer
        && true_p1_pokemon.equals(p1_pokemon)
        && true_p2_pokemon.equals(p2_pokemon);
  }

  public static class TradeAdapter implements JsonSerializer<Trade> {

    @Override
    public JsonElement serialize(Trade src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("status", src.status.ordinal());
      o.addProperty("p1_id", src.player1.getId());
      o.addProperty("p2_id", src.player2.getId());
      o.addProperty("p1_accepted", src.p1Accepted);
      o.addProperty("p2_accepted", src.p2Accepted);
      o.addProperty("p1_currency", src.p1CurrencyOffer);
      o.addProperty("p2_currency", src.p2CurrencyOffer);
      o.add("p1_pokemon", Main.GSON().toJsonTree(src.p1PokemonOffer));
      o.add("p2_pokemon", Main.GSON().toJsonTree(src.p2PokemonOffer));
      return o;
    }

  }

}
