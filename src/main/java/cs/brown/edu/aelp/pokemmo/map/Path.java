package cs.brown.edu.aelp.pokemmo.map;

import java.util.List;

public class Path {

  /*
   * Recognize that this class depends on client latency being roughly less
   * than 200 ms. Expect client and server understandings of location to
   * differ by as much as 2 tiles in any direction.
   */

  private final long start_time = System.currentTimeMillis();
  private final int step_time = 200; // milliseconds
  private final List<Location> steps;
  private final Location start;
  private final Location end;

  public Path(List<Location> steps) {
    assert !steps.isEmpty();
    this.steps = steps;
    this.start = steps.get(0);
    this.end = steps.get(steps.size() - 1);
  }

  public Location getStart() {
    return this.start;
  }

  public Location getEnd() {
    return this.end;
  }

  public Location getStepByIndex(int i) {
    assert i >= 0;
    assert i < this.steps.size();
    return this.steps.get(i);
  }

  public Location getCurrentStep() {
    long diff = System.currentTimeMillis() - this.start_time;
    int index = (int) diff / (int) this.step_time;
    if (index < 0) {
      index = 0;
    } else if (index >= this.steps.size()) {
      index = this.steps.size() - 1;
    }
    return this.getStepByIndex((int) index);
  }

}
