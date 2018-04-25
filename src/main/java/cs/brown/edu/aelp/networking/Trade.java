package cs.brown.edu.aelp.networking;

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

  private enum TRADE_STATUS {
    REQUEST_PENDING, OPEN, CANCELED, COMPLETE
  }

  private User player1;
  private User player2;

  private TRADE_STATUS status = TRADE_STATUS.REQUEST_PENDING;
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

  public void broadcast() {
    if (!player1.isConnected() || !player2.isConnected()) {
      this.status = TRADE_STATUS.CANCELED;
    }
    PacketSender.sendTradePacket(player1, this);
    PacketSender.sendTradePacket(player2, this);
  }

  @SuppressWarnings("unchecked")
  private boolean isSameTrade(JsonObject o) {
    boolean simple = o.get("user1_id").getAsInt() == this.player1.getId()
        && o.get("user2_id").getAsInt() == this.player2.getId()
        && o.get("p1_currency").getAsInt() == this.p1CurrencyOffer
        && o.get("p2_currency").getAsInt() == this.p2CurrencyOffer;
    Set<Integer> true_p1_pokemon = p1PokemonOffer.stream().map(p -> p.getId())
        .collect(Collectors.toSet());
    Set<Integer> p1_pokemon = new HashSet<Integer>();
    for (JsonElement p : o.get("p1_pokemon_ids").getAsJsonArray()) {
      p1_pokemon.add(p.getAsInt());
    }
    Set<Integer> true_p2_pokemon = p2PokemonOffer.stream().map(p -> p.getId())
        .collect(Collectors.toSet());
    Set<Integer> p2_pokemon = new HashSet<Integer>();
    for (JsonElement p : o.get("p2_pokemon_ids").getAsJsonArray()) {
      p2_pokemon.add(p.getAsInt());
    }
    Map<Integer, Integer> p1Items = Main.GSON().fromJson(o.get("p1_items"),
        Map.class);
    Map<Integer, Integer> p2Items = Main.GSON().fromJson(o.get("p2_items"),
        Map.class);
    return simple && true_p1_pokemon.equals(p1_pokemon)
        && true_p2_pokemon.equals(p2_pokemon) && p1Items.equals(p1ItemsOffer)
        && p2Items.equals(p2ItemsOffer);
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
