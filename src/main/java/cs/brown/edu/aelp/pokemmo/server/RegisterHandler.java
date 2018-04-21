package cs.brown.edu.aelp.pokemmo.server;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import cs.brown.edu.aelp.pokemmo.data.DataSource;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

  private DataSource datasrc;
  private final Gson GSON = new Gson();

  public RegisterHandler(DataSource datasrc) {
    this.datasrc = datasrc;
  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    QueryParamsMap qm = req.queryMap();
    String user = qm.value("username");
    String pass = qm.value("password");
    String email = qm.value("email");
    Map<String, Object> vars = new HashMap<>();
    try {
      validateInput(user, pass, email);
      User u = datasrc.registerUser(user, email, pass);
      vars.put("success", true);
      vars.put("token", u.getToken());
      vars.put("id", u.getId());
    } catch (AuthException e) {
      vars.put("success", false);
      vars.put("message", e.getMessage());
    }
    return GSON.toJson(vars);
  }

  public boolean validateInput(String name, String pass, String email) throws AuthException {

    if (name.length() <= 3 || name.length() > 20) {
      throw new AuthException("Username must have greater than 3 and fewer than 21 characters.");
    }

    if (!pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}")) {
      throw new AuthException("Password must be at least 8 characters,"
          + " containing an upper case letter, a lower case letter, a number, and a symbol.");
    }

    if (!email.toUpperCase().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
      throw new AuthException("Email must be of a valid format.");
    }

    return true;

  }

}
