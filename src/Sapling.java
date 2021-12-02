import processing.core.PImage;

import java.util.List;

public class Sapling extends Plant{

    private int healthLimit;

    public Sapling(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod, health);
        super.imageIndex = 0;
        this.healthLimit = healthLimit;
    }


    public void executeActorActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        this.setIncreaseHealth(1);
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }


    public  boolean transformPlant(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Functions.createStump(this.getId(),
                    this.getPosition(),
                    imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);
            //stump.scheduleActions(scheduler, world, imageStore);

            return true;
        } else if (this.getHealth() >= this.healthLimit) {
            Entity tree = Functions.createTree("tree_" + this.getId(),
                    this.getPosition(),
                    Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
                    Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
                    Functions.getNumFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN),
                    imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(tree);
            ((Tree)tree).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

}
