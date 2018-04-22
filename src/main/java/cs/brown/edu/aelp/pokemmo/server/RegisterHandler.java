package cs.brown.edu.aelp.pokemmo.server;

import com.google.gson.Gson;
import cs.brown.edu.aelp.pokemmo.data.DataSource;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemon.Main;
import java.util.HashMap;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

  private final Gson GSON = new Gson();

  @Override
  public Object handle(Request req, Response res) throws Exception {
    QueryParamsMap qm = req.queryMap();
    String user = qm.value("username");
    String pass = qm.value("password");
    String email = qm.value("email");
    String species = qm.value("species");
    String nickname = qm.value("nickname");
    Map<String, Object> vars = new HashMap<>();
    try {
      validateInput(user, pass, email, species, nickname);
      DataSource data = Main.getDataSource();
      User u = data.registerUser(user, email, pass);
      Pokemon p = data.addPokemonToUser(u, species, nickname);
      u.addPokemon(p);
      u.addPokemonToTeam(p);
      vars.put("success", true);
      vars.put("token", u.getToken());
      vars.put("id", u.getId());
    } catch (AuthException e) {
      vars.put("success", false);
      vars.put("message", e.getMessage());
    }
    return GSON.toJson(vars);
  }

  public boolean validateInput(String name, String pass, String email,
      String species, String nickname) throws AuthException {

    if (name.length() <= 3 || name.length() > 20) {
      throw new AuthException(
          "Username must have greater than 3 and fewer than 21 characters.");
    }

    if (!pass.matches(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}")) {
      throw new AuthException("Password must be at least 8 characters,"
          + " containing an upper case letter, a lower case letter, a number, and a symbol.");
    }

    if (!email.toUpperCase()
        .matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
      throw new AuthException("Email must be of a valid format.");
    }

    // TODO: put real starter species here
    if (!(species.equals("charizard") || species.equals("pikachu"))) {
      throw new AuthException("Invalid species selected.");
    }

    if (nickname.length() <= 3 || nickname.length() > 20) {
      throw new AuthException(
          "Pokemon nickname must be greater than 3 and fewer than 21 characters.");
    }

    return true;

  }

}
