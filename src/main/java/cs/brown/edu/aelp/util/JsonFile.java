package cs.brown.edu.aelp.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonFile {

  private static final Gson GSON = new Gson();
  private Map<String, Object> vals = new HashMap<>();

  public JsonFile(String path) throws IOException {
    try (FileReader fr = new FileReader(path);
        JsonReader reader = new JsonReader(fr)) {
      this.vals = GSON.fromJson(reader, this.vals.getClass());
    }
  }

  @SuppressWarnings("unchecked")
  private Object getObject(String... keys) {
    Map<String, Object> jsonObj = vals;
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      if (i == keys.length - 1) {
        if (jsonObj.containsKey(key)) {
          return jsonObj.get(key);
        }
      } else {
        jsonObj = (Map<String, Object>) jsonObj.get(key);
      }
    }
    return null;
  }

  public Integer getInt(String... keys) {
    Object o = this.getObject(keys);
    return o == null ? null : (int) (double) o;
  }

  public String getString(String... keys) {
    Object o = this.getObject(keys);
    return o == null ? null : (String) o;
  }

  public Double getDouble(String... keys) {
    Object o = this.getObject(keys);
    return o == null ? null : (double) o;
  }

}
