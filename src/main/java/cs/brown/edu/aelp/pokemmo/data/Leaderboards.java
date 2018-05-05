package cs.brown.edu.aelp.pokemmo.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.util.Identifiable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboards {

  private static List<EloUser> scores = new ArrayList<>();

  public static void setTop5(List<EloUser> users) {
    assert users.size() <= 5;
    scores = users;
  }

  public static List<EloUser> getTop5() {
    return Collections.unmodifiableList(scores);
  }

  public static class EloUser extends Identifiable {

    private final String username;
    private final int elo;

    public EloUser(int id, String username, int elo) {
      super(id);
      this.username = username;
      this.elo = elo;
    }

    public int getElo() {
      return this.elo;
    }

    public String getUsername() {
      return this.username;
    }

    public static class EloUserAdapter implements JsonSerializer<EloUser> {

      @Override
      public JsonElement serialize(EloUser src, Type typeOfSrc,
          JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("id", src.getId());
        o.addProperty("username", src.getUsername());
        o.addProperty("elo", src.getElo());
        return o;
      }

    }

  }

}
