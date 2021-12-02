import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldNode {

    private Point point;
    private int distanceFromStart;
    private int distanceToEnd;
    private WorldNode priorNode;


    WorldNode(Point point, int distanceFromStart, int distanceToEnd, WorldNode priorNode)
    {
        this.point = point;
        this.distanceFromStart = distanceFromStart;
        this.distanceToEnd = distanceToEnd;
        this.priorNode = priorNode;
    }

    public Point getPoint()
    {
        return this.point;
    }
    public WorldNode getPriorNode(){return this.priorNode;}
    public void setPoint(Point point)
    {
        this.point = point;
    }
    //implement the functions
    public void setDistanceFromStart(Point start)
    {
        distanceFromStart = Math.abs(point.getX() - start.getX()) + Math.abs(point.getY() - start.getY());
    }
    public void setDistanceToEnd(Point end)
    {
        distanceToEnd = Math.abs(end.getX() - point.getX()) + Math.abs(end.getY() - point.getY());
    }
    public int getDistanceFromStart()
    {
        return distanceFromStart;
    }

    public int getDistanceToEnd()
    {
        return distanceToEnd;
    }

    public int totalManhattanDistance()
    {
        return getDistanceFromStart() + getDistanceToEnd();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldNode worldNode = (WorldNode) o;
        return Objects.equals(point, worldNode.point); // distanceFromStart == worldNode.distanceFromStart && distanceToEnd == worldNode.distanceToEnd &&
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, distanceFromStart, distanceToEnd, priorNode);
    }
}
