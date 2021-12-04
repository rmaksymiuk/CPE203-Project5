import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Fairy extends MoverEntity{

    public Fairy(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod) {
        super(id, position,images,animationPeriod,actionPeriod);
        super.imageIndex = 0;
    }


    public void executeActorActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Stump stumpType = new Stump(null,null,null);
        Optional<Entity> fairyTarget = this.getPosition().findNearest(world, new ArrayList<>(Arrays.asList(stumpType)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveToEntity(world, fairyTarget.get(),scheduler)) {
                Entity sapling = Functions.createSapling("sapling_" + this.getId(), tgtPos,
                        imageStore.getImageList(Functions.SAPLING_KEY));
                world.addEntity(sapling);
                ((Sapling)sapling).scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
    }

@Override
    public  Point nextPositionEntity(WorldModel world, Point destPos, PathingStrategy strategy) {

    strategy = new AStarPathingStrategy();
    Point newPos = null;
    List<Point> points = null;

    points = strategy.computePath(this.getPosition(), destPos, (p) -> !world.isOccupied(p) && world.withinBounds(p), (p1, p2) -> p1.adjacent(p2) && world.withinBounds(p1) && world.withinBounds(p2), PathingStrategy.CARDINAL_NEIGHBORS);
    if (points.isEmpty()) {
        newPos = this.getPosition();
        return newPos;
    }
    newPos = new Point(points.get(1).getX(), points.get(1).getY());
    return newPos;
}


    public  boolean moveToEntity(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        PathingStrategy strategy = new SingleStepPathingStrategy();
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            //changed this line, for project 2
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
}
