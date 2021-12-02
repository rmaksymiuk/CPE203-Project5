import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleStepPathingStrategy implements PathingStrategy {

    @Override
    public List<Point> computePath(Point start,
                                   Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        return potentialNeighbors.apply(start)
                .filter(canPassThrough)
                .filter(pt ->
                        !pt.equals(start) &&
                                !pt.equals(end) &&
                                Math.abs(end.getX() - pt.getX()) <= Math.abs(end.getX() - start.getX()) &&
                                Math.abs(end.getY() - pt.getY()) <= Math.abs(end.getY() - start.getY())
                )
                .limit(1)
                .collect(Collectors.toList());
    }
}