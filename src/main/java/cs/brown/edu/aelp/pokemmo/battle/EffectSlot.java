package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.events.BattleEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EffectSlot {

  private Queue<Effect> effects = new ConcurrentLinkedQueue<>();

  public EffectSlot() {
  }

  public void register(Effect effect) {
    effect.setEffectSlot(this);
    effects.add(effect);

    System.out.println("registered effect!");
  }

  public void deregister(Effect effect) {
    effects.remove(effect);
  }

  public void clear() {
    effects.clear();
  }

  public Queue<Effect> getEffects() {
    return effects;
  }

  public void handle(BattleEvent event) {
    event.invokeOn(this);
  }
}
