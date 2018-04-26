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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Trade {

  public enum TRADE_STATUS {
    BUSY,
    OPEN,
    CANCELED,
    COMPLETE
  }

  private User player1;
  private User player2;

  private TRADE_STATUS status = TRADE_STATUS.OPEN;
  private boolean p1Accepted = false;
  private boolean p2Accepted = false;

  private Map<Integer, Integer> p1ItemsOffer = new HashMap<>();
  private Map<Integer, Integer> p2ItemsOffer = new HashMap<>();
  private int p1CurrencyOffer = 0;
  private int p2CurrencyOffer = 0;
  private Set<Pokemon> p1PokemonOffer = new HashSet<>();
  private Set<Pokemon> p2PokemonOffer = new HashSet<>();

  public Trade(User p1, User p2) {
    this.player1 = p1;
    this.player2 = p2;
  }

  public void setStatus(TRADE_STATUS s) {
    this.status = s;
  }

  public User getUser1() {
    return this.player1;
  }

  public User getUser2() {
    return this.player2;
  }

  private void invalidate() {
    this.p1Accepted = false;
    this.p2Accepted = false;
  }

  private void completeTrade() {
    // ...
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

  public boolean setItems(Map<Integer, Integer> items, boolean user1) {
    User u = user1 ? this.player1 : this.player2;
    for (int i : items.keySet()) {
      if (u.getInventory().getItemAmount(i) < items.get(i)) {
        return false;
      }
    }
    if (user1) {
      this.p1ItemsOffer = items;
    } else {
      this.p2ItemsOffer = items;
    }
    this.invalidate();
    return true;
  }

  public boolean setCurrency(int curr, boolean user1) {
    User u = user1 ? this.player1 : this.player2;
    if (u.getCurrency() < curr) {
      return false;
    }
    if (user1) {
      this.p1CurrencyOffer = curr;
    } else {
      this.p2CurrencyOffer = curr;
    }
    this.invalidate();
    return true;
  }

  public boolean setPokemon(Set<Integer> pokemon, boolean user1) {
    User u = user1 ? this.player1 : this.player1;
    for (int id : pokemon) {
      if (u.getPokemonById(id) == null) {
        return false;
      }
    }
    Set<Pokemon> newPokemon = pokemon.stream().map(id -> u.getPokemonById(id))
        .collect(Collectors.toSet());
    if (user1) {
      this.p1PokemonOffer = newPokemon;
    } else {
      this.p2PokemonOffer = newPokemon;
    }
    this.invalidate();
    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean isSameTrade(JsonObject o, boolean user1) {
    int id1;
    int id2;
    int curr1;
    int curr2;
    JsonArray pokemon1;
    JsonArray pokemon2;
    Map<Integer, Integer> items1;
    Map<Integer, Integer> items2;
    if (user1) {
      id1 = o.get("me_id").getAsInt();
      id2 = o.get("other_id").getAsInt();
      curr1 = o.get("me_currency").getAsInt();
      curr2 = o.get("other_currency").getAsInt();
      pokemon1 = o.get("me_pokemon").getAsJsonArray();
      pokemon2 = o.get("other_pokemon").getAsJsonArray();
      items1 = Main.GSON().fromJson(o.get("me_items"), Map.class);
      items2 = Main.GSON().fromJson(o.get("other_items"), Map.class);
    } else {
      id2 = o.get("me_id").getAsInt();
      id1 = o.get("other_id").getAsInt();
      curr2 = o.get("me_currency").getAsInt();
      curr1 = o.get("other_currency").getAsInt();
      pokemon2 = o.get("me_pokemon").getAsJsonArray();
      pokemon1 = o.get("other_pokemon").getAsJsonArray();
      items2 = Main.GSON().fromJson(o.get("me_items"), Map.class);
      items1 = Main.GSON().fromJson(o.get("other_items"), Map.class);
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
        && true_p2_pokemon.equals(p2_pokemon) && items1.equals(p1ItemsOffer)
        && items2.equals(p2ItemsOffer);
  }

  public static class TradeAdapter implements JsonSerializer<Trade> {

    @Override
    public JsonElement serialize(Trade src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("status", src.status.ordinal());
      o.addProperty("user1_id", src.player1.getId());
      o.addProperty("user2_id", src.player2.getId());
      o.addProperty("p1_accepted", src.p1Accepted);
      o.addProperty("p2_accepted", src.p2Accepted);
      o.add("p1_items", Main.GSON().toJsonTree(src.p1ItemsOffer));
      o.add("p2_items", Main.GSON().toJsonTree(src.p2ItemsOffer));
      o.addProperty("p1_currency", src.p1CurrencyOffer);
      o.addProperty("p2_currency", src.p2CurrencyOffer);
      o.add("p1_pokemon", Main.GSON().toJsonTree(src.p1PokemonOffer));
      o.add("p2_pokemon", Main.GSON().toJsonTree(src.p2PokemonOffer));
      return o;
    }

  }

}
