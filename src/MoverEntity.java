import processing.core.PImage;

import java.util.List;

public abstract class MoverEntity extends EntityActivity{


    public MoverEntity(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod)
    {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public Point nextPositionEntity(WorldModel world, Point destPos, PathingStrategy strategy) {
        strategy = new AStarPathingStrategy();
        Point newPos = null;
        List<Point> points = null;

        if (strategy.getClass() == AStarPathingStrategy.class) {
            points = strategy.computePath(this.getPosition(), destPos, (p) -> world.withinBounds(p) && (!world.isOccupied(p) || (world.getOccupancyCell(p).getClass() == Stump.class)), (p1, p2) -> p1.adjacent(p2) && world.withinBounds(p1) && world.withinBounds(p2), PathingStrategy.CARDINAL_NEIGHBORS);
            if (points.isEmpty()) {
                newPos = this.getPosition();
                return newPos;
            }
            //newPos = new Point(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY());
            newPos = new Point(points.get(1).getX(), points.get(1).getY());
            return newPos;


        }
        else
        {
            points = strategy.computePath(this.getPosition(), destPos, (p) -> !world.isOccupied(p) || (world.getOccupancyCell(p).getClass() == Stump.class), (p1, p2) -> p1.adjacent(p2) && world.withinBounds(p1) && world.withinBounds(p2), PathingStrategy.CARDINAL_NEIGHBORS);
            if (points.isEmpty()) {
                newPos = this.getPosition();
                return newPos;
            }
            //newPos = new Point(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY());
            newPos = new Point(points.get(0).getX(), points.get(0).getY());
            return newPos;
        }
    }
    protected abstract boolean moveToEntity(WorldModel world, Entity target, EventScheduler scheduler);


}
