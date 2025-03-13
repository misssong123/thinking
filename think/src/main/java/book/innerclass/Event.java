package book.innerclass;

import java.time.Duration;
import java.time.Instant;

public abstract class Event {
    private Instant eventTime;
    protected final Duration delay;
    public Event(long delayTime){
        delay = Duration.ofMillis(delayTime);
        start();
    }
    public void start(){
        eventTime = Instant.now().plus(delay);
    }
    public boolean ready(){
        return Instant.now().isAfter(eventTime);
    }
    public abstract void action();
}
