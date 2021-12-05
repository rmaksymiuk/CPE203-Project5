import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Entity> listOfKnights= world.getEntities().stream().filter(p -> (p.getClass() == Knight.class)).collect(Collectors.toSet());
        for (Entity f: listOfKnights)
        {
            if(this.getPosition().adjacent(f.getPosition()))
            {
                Point fairyPos = this.getPosition();
                world.removeEntity(this);
                //scheduler.unscheduleAllEvents(this);
                Animator fairy = new Fairy("fairy",fairyPos,imageStore.getImageList("fairy"), 51,51);
                world.addEntity(fairy);
                fairy.scheduleActions(scheduler, world,imageStore);
                break;
            }
        }
        if (fullTarget.isPresent() && this.moveToEntity(world, fullTarget.get(),scheduler)) {
            Entity dude = Functions.createDudeNotFull("dude", this.getPosition(),787,100,0,imageStore.getImageList("dude"));
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            ((DudeNotFull)dude).scheduleActions(scheduler, world, imageStore);
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
            world.removeEntity(target);
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
