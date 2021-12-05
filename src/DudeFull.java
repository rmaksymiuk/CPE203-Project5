import processing.core.PImage;

import java.util.*;
import java.util.stream.Collectors;

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
        Set<Entity> listOfFences= world.getEntities().stream().filter(p -> (p.getClass() == Fence.class)).collect(Collectors.toSet());
        for (Entity f: listOfFences)
        {
            if(this.getPosition().proximity(f.getPosition()))
            {
                Point monsterPosition = this.getPosition();
                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);
                Animator monster = new Monster("monster",monsterPosition,imageStore.getImageList("monster"), 500,51);
                world.addEntity(monster);
                monster.scheduleActions(scheduler, world,imageStore);
                break;
            }
        }
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
        PathingStrategy strategy = new SingleStepPathingStrategy();

        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionEntity(world,target.getPosition(), strategy);

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

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
