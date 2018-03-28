package cs.brown.edu.aelp.pokemmo.battle;

import java.util.LinkedList;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.events.BattleEvent;

public class EffectSlot {

  private List<Effect> effects = new LinkedList<>();

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

  public List<Effect> getEffects() {
    return effects;
  }

  public void handle(BattleEvent event) {
    event.invokeOn(this);
  }
}
