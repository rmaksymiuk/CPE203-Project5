import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeFull extends MoverEntity{

    private int resourceLimit;

    public DudeFull(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int actionPeriod,
            int animationPeriod) {

        super(id,position,images,animationPeriod,actionPeriod);
        super.imageIndex = 0;
        this.resourceLimit = resourceLimit;
    }


    public void executeActorActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        House houseType = new House(null, null ,null);
        Optional<Entity> fullTarget = this.getPosition().findNearest(world, new ArrayList<>(Arrays.asList(houseType)));

        if (fullTarget.isPresent() && this.moveToEntity(world, fullTarget.get(),scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }


    public  boolean moveToEntity(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        PathingStrategy strategy = new AStarPathingStrategy();
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionEntity(world,target.getPosition(), strategy);

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                occupant.ifPresent(scheduler::unscheduleAllEvents);

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }


    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        Entity miner = Functions.createDudeNotFull(this.getId(),
                this.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(),
                this.resourceLimit,
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        ((DudeNotFull)miner).scheduleActions(scheduler, world, imageStore);
    }

}
