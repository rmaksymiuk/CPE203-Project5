import processing.core.PImage;

import java.util.List;

public abstract class EntityActivity extends Animator {

    private int actionPeriod;

    public EntityActivity(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod)
    {
        super(id, position, images,animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    protected abstract void executeActorActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    protected Action createActivityAction(WorldModel world, ImageStore imageStore) {return new ActivityAction(this, world, imageStore, 0);}
    protected int getActionPeriod(){return actionPeriod;}

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, this.createAnimationAction(0), this.getAnimationPeriod());
    }
}
