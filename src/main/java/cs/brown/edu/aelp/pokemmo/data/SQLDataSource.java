package cs.brown.edu.aelp.pokemmo.data;

import cs.brown.edu.aelp.pokemmo.data.authentication.Password;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveLoader;
import cs.brown.edu.aelp.pokemon.Main;
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

  private List<Pokemon> loadPokemonForUser(int userId)
      throws IOException, SQLException {
    List<Pokemon> pokemon = new ArrayList<>();
    try (PreparedStatement p = this.prepStatementFromFile(
        "src/main/resources/sql/get_pokemon_for_user.sql")) {
      p.setInt(1, userId);
      try (ResultSet rs = p.executeQuery()) {
        while (rs.next()) {
          Pokemon.Builder b = new Pokemon.Builder(rs.getInt("id"));
          Integer id1 = rs.getInt("move_1");
          Integer id2 = rs.getInt("move_2");
          Integer id3 = rs.getInt("move_3");
          Integer id4 = rs.getInt("move_4");
          if (id1 != null) {
            Move m = MoveLoader.getMoveById(id1);
            m.setPP(rs.getInt("pp_1"));
            b.withMove(m);
          }
          if (id2 != null) {
            Move m = MoveLoader.getMoveById(id2);
            m.setPP(rs.getInt("pp_2"));
            b.withMove(m);
          }
          if (id3 != null) {
            Move m = MoveLoader.getMoveById(id3);
            m.setPP(rs.getInt("pp_3"));
            b.withMove(m);
          }
          if (id4 != null) {
            Move m = MoveLoader.getMoveById(id4);
            m.setPP(rs.getInt("pp_4"));
            b.withMove(m);
          }
          b.withNickName(rs.getString("nickname"))
              .withGender(rs.getInt("gender")).withExp(rs.getInt("experience"))
              .asStored(rs.getBoolean("stored"))
              .withHp(rs.getInt("cur_health"));

          // TODO: ADD CORRECT TYPE
          b.withType(PokeTypes.NORMAL);

          pokemon.add(b.build());
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
    /*for (Pokemon pokemon : this.loadPokemonForUser(user.getId())) {
      user.addPokemonToTeam(pokemon);
    }*/
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
    } catch (Exception e) {
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
      conn.setAutoCommit(false);
      try (PreparedStatement p = this.getPStatementForClass(toSave.get(0))) {
        for (E obj : toSave) {
          obj.bindValues(p);
          p.addBatch();
        }
        int i = IntStream.of(p.executeBatch()).sum();
        conn.commit();
        return i;
      }
    } catch (SQLException e) {
      try {
        conn.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
        throw new SaveException("ERROR: Failed to rollback failed commit...");
      }
      e.printStackTrace();
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
      try (ResultSet rs = p.executeQuery()) {
        if (rs.next()) {
          Pokemon.Builder b = new Pokemon.Builder(rs.getInt("id"));
          b.withGender(rs.getInt("gender")).withExp(rs.getInt("experience"))
              .asStored(rs.getBoolean("stored"));
          return b.build();
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
      Class<? extends SQLBatchSavable> c = object.getClass();
      if (c.equals(User.class)) {
        return conn.prepareStatement(
            "UPDATE users SET chunk = ?, row = ?, col = ?, currency = ?, "
                + "session_token = ? WHERE id = ?");
      } else if (c.equals(Pokemon.class)) {
        return conn.prepareStatement(
            "UPDATE pokemon SET user_id = ?, nickname = ?, gender = ?, "
                + "experience = ?, stored = ?, cur_health = ?, species = ?, "
                + "move_1 = ?, move_2 = ?, move_3 = ?, move_4 = ?, pp_1 = ?, "
                + "pp_2 = ?, pp_3 = ?, pp_4 = ? WHERE id = ?");
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
