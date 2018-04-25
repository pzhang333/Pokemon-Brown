package cs.brown.edu.aelp.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonFile {

  private static final Gson GSON = new Gson();
  private Map<String, Object> vals = new HashMap<>();

  private JsonFile(Map<String, Object> obj) {
    this.vals = obj;
  }

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
      if (jsonObj.containsKey(key)) {
        if (i == keys.length - 1) {
          return jsonObj.get(key);
        } else {
          jsonObj = (Map<String, Object>) jsonObj.get(key);
        }
      } else {
        return null;
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

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(String... keys) {
    Object o = this.getObject(keys);
    return o == null ? null : (List<T>) o;
  }

  @SuppressWarnings("unchecked")
  public List<JsonFile> getJsonList(String... keys) {
    Object o = this.getObject(keys);
    if (o == null) {
      return null;
    }
    List<JsonFile> results = new ArrayList<>();
    for (Map<String, Object> ele : (List<Map<String, Object>>) o) {
      results.add(new JsonFile(ele));
    }
    return results;
  }

  @SuppressWarnings("unchecked")
  public JsonFile getMap(String... keys) {
    Object o = this.getObject(keys);
    assert Map.class.isAssignableFrom(o.getClass());
    return new JsonFile((Map<String, Object>) o);
  }

}
