import processing.core.PImage;

import java.util.*;
import java.util.stream.Collectors;

public class DudeNotFull extends MoverEntity{


    private int resourceLimit;
    private int resourceCount;



    public DudeNotFull(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod) {
        super(id, position,images,animationPeriod,actionPeriod);
        super.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    public void setIncreaseResourceCount(int numToIncrease){this.resourceCount += numToIncrease;}



    public void executeActorActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Tree treeType = new Tree(null,null,null,0,0,0);
        Sapling saplingType = new Sapling(null,null,null,0,0,0,0);
        Optional<Entity> target = this.getPosition().findNearest(world, new ArrayList<>(Arrays.asList(treeType,saplingType)));
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
        if (!target.isPresent() || !this.moveToEntity(world, target.get(),scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
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
            //increase the resourseCount by 1
            this.setIncreaseResourceCount(1);
            //check for correctness
            ((Plant)target).setDecreseRHealth(1);
            return true;
        }
        else {
            //changed this line, make sure it's correct
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


    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = Functions.createDudeFull(this.getId(),
                    this.getPosition(), this.getActionPeriod(),
                    this.getAnimationPeriod(),
                    this.resourceLimit,
                    this.getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            ((DudeFull)miner).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

}
