package cs.brown.edu.aelp.pokemon.data;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDataSource implements DataSource {

  private String connString;
  private String user;
  private String pass;
  private Connection conn;

  public SQLDataSource(String ip, int port, String db, String user, String pass)
      throws Exception {
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
    return this.conn;
  }

  private PreparedStatement prepStatementFromFile(String path)
      throws Exception {
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

  @Override
  public void authenticateUser(String email, String password) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void registerUser(String username, String email, String password)
      throws Exception {
    // TODO Auto-generated method stub

  }

}
