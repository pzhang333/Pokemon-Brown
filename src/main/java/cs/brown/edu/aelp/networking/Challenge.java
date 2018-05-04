package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.Timer;
import java.util.TimerTask;

public class Challenge {

  private final static int EXPIRE_SEC = 30;
  private final User from;
  private final User to;
  private final long timestamp;
  private final Timer timer;

  public Challenge(User from, User to) {
    this.from = from;
    this.to = to;
    this.timestamp = System.currentTimeMillis();
    from.setChallenge(this);
    to.setChallenge(this);
    this.timer = new Timer();
    this.timer.schedule(new TimerTask() {
      @Override
      public void run() {
        from.setChallenge(null);
        to.setChallenge(null);
        PacketSender.sendChallengeResponse(from, "expired");
        PacketSender.sendChallengeResponse(to, "expired");
      }
    }, EXPIRE_SEC * 1000);
  }

  public User getFrom() {
    return this.from;
  }

  public User getTo() {
    return this.to;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void cancel() {
    this.from.setChallenge(null);
    this.to.setChallenge(null);
    this.timer.cancel();
  }

  public User other(User u) {
    return u == this.from ? this.to : this.from;
  }

}
