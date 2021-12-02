import processing.core.PImage;

import java.util.List;

public abstract class Plant extends EntityActivity{

    private int health;
    //will not be defined here

    public Plant(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod, int health)
    {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }
    abstract boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    //will be defined here
    protected int getHealth() {return this.health;}
    protected void setDecreseRHealth(int numToDecrease) {this.health = this.health - numToDecrease;}
    protected void setIncreaseHealth(int numToIncrease) {this.health = this.health + numToIncrease;}
}
