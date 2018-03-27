package cs.brown.edu.aelp.pokemon.battle;

import java.util.LinkedList;
import java.util.List;

import cs.brown.edu.aelp.pokemon.battle.events.BattleEvent;

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

  public <T extends BattleEvent> void handle(T event) {
    effects.forEach(e -> e.handle(event));
  }
}
