package cs.brown.edu.aelp.commands;

import java.util.List;
import java.util.Locale;

/**
 * This class represents a command which can be invoked by the user at the
 * command line in order to perform some task. A class extending this class must
 * override the method call(), which will be called whenever the user types the
 * name of this command and an appropriate number of arguments.
 *
 * @author Louis Kilfoyle
 */
public abstract class Command {

  private String description;
  private String name;
  // allow usage that doesn't correlate to an accepted args length?
  private boolean varArgs;
  private List<List<String>> params;
  private boolean disableParsing;

  /**
   * A Command object represents a command which can be invoked by the user at
   * the command line in order to perform some task.
   *
   * @param name
   *          the string typed to invoke the command
   * @param params
   *          a list of lists, one for each possible parameter combination, with
   *          strings as parameter names to be printed to the user
   * @param description
   *          a brief description of the command
   * @param varArgs
   *          whether or not this command accepts a variable number of arguments
   * @param disableParsing
   *          whether this Command should attempt to parse out user input into
   *          distinct arguments, or just treat the entire input as a single
   *          string
   */
  public Command(String name, List<List<String>> params, String description,
      boolean varArgs, boolean disableParsing) {
    this.name = name.toLowerCase(Locale.ENGLISH);
    this.params = params;
    this.varArgs = varArgs;
    this.description = description;
    this.disableParsing = disableParsing;
  }

  /**
   * Get the name of this Command.
   *
   * @return the name of this Command
   */
  public String getName() {
    return this.name;
  }

  /**
   * Prints out the usage details for this command.
   */
  public void printUsage() {
    System.out.println(description);
    System.out.println("Usage:");
    for (int i = 0; i < this.params.size(); i++) {
      String usage = String.format("\t %s", this.name);
      List<String> paramsSet = this.params.get(i);
      for (int j = 0; j < paramsSet.size(); j++) {
        usage = String.format("%s <%s>", usage, paramsSet.get(j));
      }
      System.out.println(usage.substring(0, usage.length()));
    }
    System.out.println("To see this dialog:");
    System.out.println(String.format("\t %s help", this.name));
  }

  /**
   * Determines whether a command was invoked with an acceptable number of
   * arguments. If so, call() is called. If not, printUsage() is called.
   *
   * @param args
   *          the arguments the user passed
   */
  public void onCall(List<String> args) {
    if (!this.isParsingDisabled()) {
      if (args.size() > 0 && args.get(0).equalsIgnoreCase("help")) {
        this.printUsage();
        return;
      }
    }
    boolean match = false;
    for (int i = 0; i < this.params.size(); i++) {
      if (args.size() == this.params.get(i).size()) {
        match = true;
        break;
      }
    }
    if (!match && !varArgs) {
      if (!this.isParsingDisabled()) {
        System.out.printf(
            "ERROR: Invalid arguments. Use '%s help' to see usage details.%n",
            this.name);
      } else {
        System.out.println("ERROR: Invalid arguments.");
      }
      return;
    }
    this.call(args);
  }

  /**
   * Get whether or not this Command wants arguments to be parsed.
   *
   * @return whether or not this Command wants arguments to be parsed
   */
  public boolean isParsingDisabled() {
    return this.disableParsing;
  }

  /**
   * This method is called when the Command is invoked by the user with an
   * acceptable number of arguments.
   *
   * @param args
   *          the arguments the user provided
   */
  protected abstract void call(List<String> args);

}
