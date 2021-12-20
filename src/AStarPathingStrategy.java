import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {

        //Know the start and end
        int counter = 0;
        //Creating Open(Priority Queue) and Closed List(Hash Set )
        Queue<WorldNode> openList = new PriorityQueue<>(Comparator.comparing(WorldNode::totalManhattanDistance).thenComparing(WorldNode::getDistanceToEnd)); //(Integer.compare(p1.totalManhattanDistance(), p2.totalManhattanDistance()))
        Set<Point> closedList = new HashSet<>();
        boolean targetReached = false;
        List<Point> path = new LinkedList<>();

        //adding startNode to the priority queue
        WorldNode startNode = new WorldNode(start,0,0,null);
        startNode.setDistanceToEnd(end);
        openList.add(startNode);
        WorldNode current = startNode;
        while(!openList.isEmpty() && !withinReach.test(current.getPoint(), end))
        {
            //remove current node from the priority queue and add it to the closedList
            closedList.add(current.getPoint());
            openList.remove(current);
            //adding neighbors to priority queue
            WorldNode finalCurrent = current;
            List<WorldNode> potenialNeighborsList = potentialNeighbors.apply(current.getPoint()).filter(canPassThrough).filter(p -> !closedList.contains(p)).map(p -> {
                    WorldNode n = new WorldNode(p, 0, 0, null);
                    finalCurrent.setDistanceFromStart(start);
                    n.setDistanceToEnd(end);
                    return new WorldNode(p, finalCurrent.getDistanceFromStart() + 1, n.getDistanceToEnd(), finalCurrent);}).collect(Collectors.toList());

            for(WorldNode node : potenialNeighborsList)
            {


                if(openList.contains(node))
                {
                    Predicate<WorldNode> closer = p -> p.getDistanceFromStart() >= node.getDistanceFromStart();
                    Predicate<WorldNode> equalPoint = p -> p.equals(node);
                    if(closer.test(node))
                    {
                        openList.removeIf(equalPoint);
                        openList.add(node);
                    }
                }
                else
                {
                    openList.add(node);
                }
            }
            //remove current from the priority queue and add it to closed list
            WorldNode removedNode = openList.peek();
            current = removedNode;

            if(current != null && current.getPoint().adjacent(end) && withinReach.test(current.getPoint(), end))
            {
                targetReached = true;
                break;

            }
        }

        if(targetReached)
        {
            while(current != null)
            {
                path.add(0, current.getPoint());
                current = current.getPriorNode();

            }
            return path;
        }
        else{
            return path;
        }


    }
}
