import processing.core.PImage;

import java.util.List;

public abstract class Animator extends Entity {

    private int animationPeriod;


    public Animator(String id, Point position, List<PImage> images, int animationPeriod)
    {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }


    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createAnimationAction(0), this.getAnimationPeriod());
    }
    protected int getAnimationPeriod(){return this.animationPeriod;}
    protected void nextImage() {this.imageIndex = (this.imageIndex + 1) % this.getImages().size();}
    protected Action createAnimationAction(int repeatCount) {return new AnimationAction(this, repeatCount);}
}
