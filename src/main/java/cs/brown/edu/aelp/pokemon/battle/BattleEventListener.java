package cs.brown.edu.aelp.pokemon.battle;

public abstract class BattleEventListener {

  public enum EventListenerPriority {
    HIGH, NORMAL, LOW
  };

  private final EventListenerPriority priority;

  private final String id;

  protected BattleEventListener(String id) {
    this(id, EventListenerPriority.NORMAL);
  }

  protected BattleEventListener(String id, EventListenerPriority priority) {
    this.id = id;
    this.priority = priority;
  }

  public abstract void onEvent(BattleEvent e);

  public EventListenerPriority getPriority() {
    return priority;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((priority == null) ? 0 : priority.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BattleEventListener other = (BattleEventListener) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (priority != other.priority) {
      return false;
    }
    return true;
  }

}
