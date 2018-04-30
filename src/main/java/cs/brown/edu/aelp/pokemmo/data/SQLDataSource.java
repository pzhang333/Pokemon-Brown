package cs.brown.edu.aelp.pokemmo.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cs.brown.edu.aelp.pokemmo.data.Leaderboards.EloUser;
import cs.brown.edu.aelp.pokemmo.data.authentication.Password;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.PokemonLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
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
        try (PreparedStatement p = this.prepStatementFromFile(
            "src/main/resources/sql/create_tables.sql")) {
          p.execute();
        }
      }
    }
  }

  private Connection getConn() throws SQLException {
    if (this.conn == null || this.conn.isClosed()) {
      this.conn = DriverManager.getConnection(this.connString, this.user,
          this.pass);
    }
    this.conn.setAutoCommit(true);
    return this.conn;
  }

  private PreparedStatement prepStatementFromFile(String path)
      throws IOException, SQLException {
    File f = new File(path);
    if (!f.isFile() || !path.endsWith(".sql")) {
      throw new IllegalArgumentException(
          "ERROR: Bad .sql statement file path provided.");
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

  private List<Pokemon> loadPokemonForUser(User user)
      throws IOException, SQLException {
    List<Pokemon> pokemon = new ArrayList<>();
    try (PreparedStatement p = this.prepStatementFromFile(
        "src/main/resources/sql/get_pokemon_for_user.sql")) {
      p.setInt(1, user.getId());
      try (ResultSet rs = p.executeQuery()) {
        while (rs.next()) {
          Pokemon poke = PokemonLoader.load(rs.getString("species"),
              rs.getInt("experience"), rs.getInt("id"));
          List<Move> moves = poke.getMoves();
          for (int i = 0; i < moves.size(); i++) {
            int pp = rs.getInt("pp_" + i);
            if (!rs.wasNull()) {
              moves.get(i).setPP(pp);
            }
          }
          int hp = rs.getInt("cur_health");
          if (!rs.wasNull()) {
            poke.setHealth(hp);
          }
          poke.setStored(rs.getBoolean("stored"));
          poke.changeNickname(rs.getString("nickname"));
          pokemon.add(poke);
        }
        return pokemon;
      }
    }
  }

  private User loadUser(ResultSet rs) throws SQLException, IOException {
    User user = new User(rs.getInt("id"), rs.getString("username"),
        rs.getString("email"), rs.getString("session_token"));
    user.setCurrency(rs.getInt("currency"));
    Chunk c = Main.getWorld().getChunk(rs.getInt("chunk"));
    Location loc = new Location(c, rs.getInt("row"), rs.getInt("col"));
    user.setLocation(loc);

    System.out.println(
        "User: " + user.getUsername() + " : " + this.loadPokemonForUser(user));
    /*
     * for (Pokemon pokemon : this.loadPokemonForUser(user)) {
     * user.addPokemonToTeam(pokemon); }
     */

    Pokemon pokemon = PokemonLoader.load("bulbasaur", Pokemon.calcXpByLevel(5));
    pokemon.setOwner(user);
    user.addPokemonToTeam(pokemon);

    return user;
  }

  @Override
  public User authenticateUser(int id, String token) throws AuthException {
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/get_user_by_id.sql")) {
      p.setInt(1, id);
      try (ResultSet rs = p.executeQuery()) {
        if (rs.next() && token.equals(rs.getString("session_token"))) {
          return loadUser(rs);
        } else {
          throw new AuthException("Invalid token.");
        }
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      throw new AuthException();
    }

  }

  @Override
  public User authenticateUser(String username, String password)
      throws AuthException {
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/get_user_by_name.sql")) {
      p.setString(1, username);
      try (ResultSet rs = p.executeQuery()) {
        if (rs.next() && Password.authenticate(password,
            Base64.getDecoder().decode(rs.getString("hashed_pw")),
            Base64.getDecoder().decode(rs.getString("salt")))) {
          User u = this.loadUser(rs);
          String token = this.generateToken();
          this.insertTokenForUser(u.getId(), token);
          u.setToken(token);
          return u;
        } else {
          throw new AuthException("Invalid login credentials.");
        }
      }
    } catch (SQLException | IOException | InvalidKeySpecException
        | NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new AuthException();
    }
  }

  @Override
  public User registerUser(String username, String email, String password)
      throws AuthException {
    // first check if the username is taken
    try {
      try (PreparedStatement p = this.prepStatementFromFile(
          "src/main/resources/sql/check_username_taken.sql")) {
        p.setString(1, username);
        try (ResultSet rs = p.executeQuery()) {
          if (rs.next()) {
            if (rs.getInt(1) > 0) {
              throw new AuthException("That username is already in use.");
            }
          }
        }
      }
      // then check if email is taken
      try (PreparedStatement p = this.prepStatementFromFile(
          "src/main/resources/sql/check_email_taken.sql")) {
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
        String token = this.generateToken();
        String hash = Base64.getEncoder()
            .encodeToString(Password.hashPassword(password, salt));
        p.setString(1, username);
        p.setString(2, email);
        p.setString(3, hash);
        p.setString(4, Base64.getEncoder().encodeToString(salt));
        p.setString(5, token);

        Location spawn = Main.getWorld().getSpawn();

        p.setInt(6, spawn.getChunk().getId());
        p.setInt(7, spawn.getRow());
        p.setInt(8, spawn.getCol());

        try (ResultSet rs = p.executeQuery()) {
          if (rs.next()) {
            return new User(rs.getInt("id"), username, email, token);
          } else {
            throw new AuthException();
          }
        }
      }
    } catch (IOException | SQLException | NoSuchAlgorithmException
        | InvalidKeySpecException e) {
      e.printStackTrace();
      throw new AuthException();
    }
  }

  /**
   * Attempt to save a collection of SQLBatchSavables to the database.
   *
   * @param objects
   *          the objects to save
   * @return the number of objects saved
   * @throws SaveException
   *           if something goes wrong
   */
  public <E extends SQLBatchSavable> int save(Collection<E> objects)
      throws SaveException {
    List<E> toSave = objects.stream().filter(E::hasUpdates)
        .collect(Collectors.toList());
    if (toSave.isEmpty()) {
      return 0;
    }
    try {
      Connection conn = this.getConn();
      try (PreparedStatement p = this.getPStatementForClass(toSave.get(0))) {
        for (E obj : toSave) {
          obj.bindValues(p);
        }
        conn.setAutoCommit(false);
        int i = IntStream.of(p.executeBatch()).sum();
        conn.commit();
        return i;
      }
    } catch (SQLException e) {
      try {
        System.out.println("CRITICAL: Batch save failed:");
        e.printStackTrace();
        conn.rollback();
      } catch (SQLException e1) {
        System.out.println("EVEN MORE CRITICAL: Rollback failed:");
        e1.printStackTrace();
      }
      throw new SaveException();
    }
  }

  private void insertTokenForUser(int userId, String token)
      throws AuthException {
    try {
      try (PreparedStatement p = this.prepStatementFromFile(
          "src/main/resources/sql/insert_user_token.sql")) {
        p.setString(1, token);
        p.setInt(2, userId);
        int updated = p.executeUpdate();
        if (updated == 0) {
          throw new AuthException();
        }
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      throw new AuthException();
    }
  }

  private String generateToken() {
    byte[] token = new byte[8];
    new SecureRandom().nextBytes(token);
    return Base64.getEncoder().encodeToString(token);
  }

  @Override
  public Pokemon addPokemonToUser(User u, String species, String nickname)
      throws SaveException {
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/insert_pokemon.sql")) {
      p.setInt(1, u.getId());
      p.setString(2, nickname);
      p.setString(3, species);
      p.setInt(4, Pokemon.calcXpByLevel(5));
      try (ResultSet rs = p.executeQuery()) {
        if (rs.next()) {
          Pokemon poke = PokemonLoader.load(species, Pokemon.calcXpByLevel(5),
              rs.getInt("id"));
          poke.setStored(rs.getBoolean("stored"));
          return poke;
        } else {
          throw new SaveException();
        }
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      throw new SaveException();
    }
  }

  private PreparedStatement getPStatementForClass(SQLBatchSavable object) {
    // inelegant, but oh well
    try {
      Connection conn = this.getConn();
      StringBuilder q = new StringBuilder();
      q.append("UPDATE " + object.getTableName() + " SET ");
      for (String col : object.getUpdatableColumns()) {
        q.append(col + " = ?, ");
      }
      q.replace(q.length() - 2, q.length(), " ");
      q.append("WHERE ");
      for (String col : object.getIdentifyingColumns()) {
        q.append(col + " = ? AND");
      }
      q.replace(q.length() - 4, q.length(), ";");
      return conn.prepareStatement(q.toString());
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void loadLeaderboards() throws LoadException {
    try (PreparedStatement p = this
        .prepStatementFromFile("src/main/resources/sql/get_top_50_elos.sql")) {
      try (ResultSet rs = p.executeQuery()) {
        while (rs.next()) {
          EloUser eu = new EloUser(rs.getInt("id"), rs.getString("username"),
              rs.getInt("elo"));
          Leaderboards.tryInsertTop50(eu);
        }
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      throw new LoadException(
          "Something went wrong while loading top 50 elos.");
    }
  }
}
