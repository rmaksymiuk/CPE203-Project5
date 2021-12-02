import java.util.List;

/**
 * An event is made up of an Entity that is taking an
 * Action a specified time.
 */
public final class Event
{
    private Action action;
    private long time;
    private Entity entity;

    public Event(Action action, long time, Entity entity) {
        this.action = action;
        this.time = time;
        this.entity = entity;
    }

    public Action getAction()
    {
        return this.action;
    }
    public long getTime()
    {
        return this.time;
    }
    public  void removePendingEvent(EventScheduler scheduler)
    {
        List<Event> pending = scheduler.getPendingEvents().get(this.entity);

        if (pending != null) {
            pending.remove(this);
        }
    }
}
