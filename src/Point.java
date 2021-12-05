import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point
{
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX()
    {
        return this.x;
    }
    public int getY()
    {
        return this.y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point)other).x == this.x
                && ((Point)other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public boolean adjacent(Point p2) {
        return (this.x == p2.x && Math.abs(this.y - p2.y) == 1) || (this.y == p2.y
                && Math.abs(this.x - p2.x) == 1);
    }

    public boolean proximity(Point p2) {
        return (Math.abs(this.y - p2.y) <= 2) && (Math.abs(this.x - p2.x) <= 2);
    }

    public  boolean contains(Viewport viewport) {
        return this.y >= viewport.getRow() && this.y < viewport.getRow() + viewport.getNumRows()
                && this.x >= viewport.getCol() && this.x < viewport.getCol() + viewport.getNumCols();
    }



    public  Optional<Entity> findNearest(
            WorldModel world,List<Entity> types)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity type: types)
        {
            for (Entity entity : world.getEntities()) {
                if (entity.getClass() == type.getClass()) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType);
    }


    private  int distanceSquared(Point p2) {
        int deltaX = this.x - p2.x;
        int deltaY = this.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }


    private  Optional<Entity> nearestEntity(
            List<Entity> entities)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(this);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(this);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

}
