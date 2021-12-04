import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Monster extends MoverEntity{

    public Monster(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod) {
        super(id,position,images, animationPeriod, actionPeriod);
        super.imageIndex = 0;
    }

    public void executeActorActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Fairy fairyType = new Fairy(null, null ,null,0,0);
        Optional<Entity> fullTarget = this.getPosition().findNearest(world, new ArrayList<>(List.of(fairyType)));

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
    }


    public  boolean moveToEntity(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        PathingStrategy strategy = new AStarPathingStrategy();
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            //Stump stump = Functions.createStump("stump",target.getPosition(), )
            //world.addEntity(new Stump("stump", target.getPosition(), ));
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
}
