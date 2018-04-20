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

  public Object getKey(String key) {
    if (vals.containsKey(key)) {
      return vals.get(key);
    }
    return null;
  }

}
