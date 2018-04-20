package cs.brown.edu.aelp.pokemmo.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.data.authentication.Password;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType.PokeRawType;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveHandler;
import cs.brown.edu.aelp.pokemon.Main;

public class SQLDataSource implements DataSource {

  private String connString;
  private String user;
  private String pass;
  private Connection conn;

  public SQLDataSource(String ip, int port, String db, String user, String pass)
      throws SQLException, IOException {
    String s = String.format("jdbc:postgresql://%s:%d/%s", ip, port, db);
    this.connString = s;
    this.user = user;
    this.pass = pass;
    // check if "users" table exists already, if not, assume no tables exist
    // attempt to create them all
    Connection c = this.getConn();
    DatabaseMetaData md = c.getMetaData();
    try (ResultSet rs = md.getTables(null, null, "users", null)) {
      if (!rs.next()) {
        try (PreparedStatement p = this
            .prepStatementFromFile("src/main/resources/sql/create_tables.sql")) {
          p.execute();
        }
      }
    }
  }

  private Connection getConn() throws SQLException {
    if (this.conn == null || this.conn.isClosed()) {
      this.conn = DriverManager.getConnection(this.connString, this.user, this.pass);
    }
    return this.conn;
  }

  private PreparedStatement prepStatementFromFile(String path) throws IOException, SQLException {
    File f = new File(path);
    if (!f.isFile() || !path.endsWith(".sql")) {
      throw new IllegalArgumentException("ERROR: Bad .sql statement file path provided.");
    }
    PreparedStatement p = null;
    try (FileInputStream fis = new FileInputStream(f)) {
      byte[] data = new byte[(int) f.length()];
      fis.read(data);
      String query = new String(data, "UTF-8");
      p = this.getConn().prepareStatement(query);
    }
    return p;
  }

  private List<Pokemon> loadPokemonForUser(String username) throws IOException, SQLException {
    List<Pokemon> pokemon = new ArrayList<>();
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/get_pokemon_for_user.sql")) {
      p.setString(1, username);
      try (ResultSet rs = p.executeQuery()) {
        while (rs.next()) {
          Pokemon.Builder b = new Pokemon.Builder();
          String id1 = rs.getString("move_1");
          String id2 = rs.getString("move_2");
          String id3 = rs.getString("move_3");
          String id4 = rs.getString("move_4");
          if (id1 != null) {
            Move m = MoveHandler.getMoveById(id1);
            m.setPP(rs.getInt("pp_1"));
            b.withMove(m);
          }
          if (id2 != null) {
            Move m = MoveHandler.getMoveById(id2);
            m.setPP(rs.getInt("pp_2"));
            b.withMove(m);
          }
          if (id3 != null) {
            Move m = MoveHandler.getMoveById(id3);
            m.setPP(rs.getInt("pp_3"));
            b.withMove(m);
          }
          if (id4 != null) {
            Move m = MoveHandler.getMoveById(id4);
            m.setPP(rs.getInt("pp_4"));
            b.withMove(m);
          }
          b.withId(rs.getInt("id")).withNickName(rs.getString("nickname"))
              .withGender(rs.getInt("gender")).withExp(rs.getInt("experience"))
              .asStored(rs.getBoolean("stored")).withHp(rs.getInt("cur_health"))
              .withMaxHp(rs.getInt("max_health"));

          // TODO: ADD CORRECT TYPE
          b.withType(PokeType.getType(PokeRawType.NORMAL));

          pokemon.add(b.build());
        }
        return pokemon;
      }
    }
  }

  @Override
  public User authenticateUser(String username, String password) throws AuthException {
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/get_user_by_name.sql")) {
      p.setString(1, username);
      try (ResultSet rs = p.executeQuery()) {
        if (rs.next()) {
          boolean auth = false;
          String token = rs.getString("session_token");
          // first see if they gave us a valid token
          if (token != null) {
            auth = password == token;
          }
          // if not, see if they gave us a valid password
          if (!auth) {
            auth = Password.authenticate(password, rs.getString("hashed_pw").getBytes(),
                rs.getString("salt").getBytes());
          }
          if (auth) {
            // id, username, email, token
            User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"),
                rs.getString("session_token"));
            user.setCurrency(rs.getInt("currency"));
            Chunk c = Main.getWorld().getChunk(rs.getString("chunk"));
            Location loc = new Location(c, rs.getInt("row"), rs.getInt("col"));
            user.setLocation(loc);
            for (Pokemon pokemon : this.loadPokemonForUser(user.getUsername())) {
              user.addPokemonToTeam(pokemon);
            }
            return user;
          } else {
            // I think this is typically a bad idea, but this is hardly a
            // high-security project
            throw new AuthException("Incorrect password.");
          }
        } else {
          throw new AuthException("Invalid username.");
        }
      }
    } catch (Exception e) {
      // we can differentiate here between SQLException and IOException for
      // debugging, if needed
      throw new AuthException();
    }
  }

  @Override
  public User registerUser(String username, String email, String password) throws AuthException {
    // first check if the username is taken
    try {
      try (PreparedStatement p = this
          .prepStatementFromFile("src/main/resources/sql/check_username_taken.sql")) {
        p.setString(1, username);
        try (ResultSet rs = p.executeQuery()) {
          if (rs.next() && rs.getInt(1) > 0) {
            throw new AuthException("That username is already in use.");
          }
        }
      }
      // then check if email is taken
      try (PreparedStatement p = this
          .prepStatementFromFile("src/main/resources/sql/check_email_taken.sql")) {
        p.setString(1, email);
        try (ResultSet rs = p.executeQuery()) {
          if (rs.next() && rs.getInt(1) > 0) {
            throw new AuthException("That email is already in use.");
          }
        }
      }
      // if neither failed, we can hope we are good to insert
      // race conditions are possible, but worst case scenario we fail
      // generically
      try (PreparedStatement p = this
          .prepStatementFromFile("src/main/resources/sql/register_user.sql")) {
        byte[] salt = Password.generateSalt();
        byte[] token = new byte[32];
        new SecureRandom().nextBytes(token);
        p.setString(1, username);
        p.setString(2, email);
        p.setString(3, Password.hashPassword(password, salt).toString());
        p.setString(4, salt.toString());
        p.setString(5, token.toString());
        try (ResultSet rs = p.executeQuery()) {
          if (rs.next()) {
            return new User(rs.getInt("id"), username, email, token.toString());
          } else {
            throw new AuthException();
          }
        }
      }
    } catch (Exception e) {
      throw new AuthException();
    }
  }
}
