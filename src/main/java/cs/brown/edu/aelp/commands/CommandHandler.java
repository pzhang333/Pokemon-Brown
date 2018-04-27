package cs.brown.edu.aelp.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A CommandHandler tracks Commands and listens to input on System.in to execute
 * Commands as instructed. After instantiation, Commands need to be registered
 * using .registerCommand() and .start() needs to be called to tell the handler
 * to start listening. Obviously this blocks the thread while listening.
 *
 * @author Louis Kilfoyle
 *
 */
public class CommandHandler {

  private HashMap<String, Command> commands = new HashMap<String, Command>();

  /**
   * Register a new Command to be listened for.
   *
   * @param comm
   *          the Command object
   */
  public void registerCommand(Command comm) {
    commands.put(comm.getName(), comm);
  }

  /**
   * Begins listening for command input from the user.
   */
  public void start() {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"))) {
      String text;
      while ((text = br.readLine()) != null) {
        List<String> words = parseArgs(text);
        if (words.size() == 0) {
          continue;
        }
        String cmd = words.remove(0).toLowerCase(Locale.ENGLISH);
        if (this.commands.containsKey(cmd)) {
          Command c = this.commands.get(cmd);
          if (c.isParsingDisabled()) {
            List<String> a = new ArrayList<>();
            if (text.length() > cmd.length()) {
              a.add(text.substring(cmd.length() + 1, text.length()));
            }
            c.onCall(a);
          } else {
            c.onCall(words);
          }
        } else {
          System.out.println("ERROR: Unknown command.");
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: Failed to read user input.");
    }
  }

  /**
   * Accepts a string and returns a list of strings, having parsed out
   * individual parameters. Anything separated by one or more space characters
   * is considered a new parameter, unless surrounded by double quotes. No kind
   * of escaping is performed, so a literal " character is not possible.
   *
   * @param s
   *          the input string from a user
   * @return a list of the parsed out parameters
   */
  public static List<String> parseArgs(String s) {
    List<String> args = new ArrayList<>();
    StringBuilder build = new StringBuilder();
    boolean quoting = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '"' || c == ' ') {
        if (c == '"') {
          quoting = !quoting;
        }
        if (quoting) {
          if (c == ' ') {
            build.append(c);
          }
        } else if (build.length() > 0) {
          args.add(build.toString());
          build.delete(0, build.length());
        }
      } else {
        build.append(c);
      }
    }
    if (build.length() > 0) {
      args.add(build.toString());
    }
    return args;
  }

}
