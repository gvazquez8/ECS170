import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraAI implements AIModule{

    private class PointCompartor implements Comparator<Point> {

        HashMap<Point, Double> costs;
        public PointCompartor(HashMap<Point, Double> costs) {
            this.costs = costs;
        }

        @Override
        public int compare(Point o1, Point o2) {
            double c1 = costs.get(o1);
            double c2 = costs.get(o2);

            if (c1 < c2) {
                return -1;
            }
            else if (c1 > c2) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }
    @Override
    public List<Point> createPath(TerrainMap map) {

        // init variables
        final ArrayList<Point> path = new ArrayList<>();

        HashSet<Point> visited = new HashSet<>();
        HashMap<Point, Double> pathCosts = new HashMap<>();
        HashMap<Point, Point> history = new HashMap<>();
        PriorityQueue<Point> fringe = new PriorityQueue<>(11, new PointCompartor(pathCosts));

        // set up the start/goal point
        Point currentPoint = map.getStartPoint();
        Point goal = map.getEndPoint();

        pathCosts.put(currentPoint, 0.0);
        history.put(currentPoint, null);
        fringe.add(currentPoint);

        while (!fringe.isEmpty()) {
            currentPoint = fringe.peek();

            //System.out.println(String.format("%f @ (%d,%d)", pathCosts.get(currentPoint), currentPoint.x, currentPoint.y));

            if (visited.contains(currentPoint)) {
                System.out.println("Duplicate");
                return null;
            }

            visited.add(currentPoint);

            if (currentPoint.x == goal.x && currentPoint.y == goal.y) {
                break;
            }

            for (Point neighbor : map.getNeighbors(currentPoint)) {
                if (!visited.contains(neighbor)) {
                    double currentCost = pathCosts.getOrDefault(neighbor, Double.MAX_VALUE);
                    double altCost = pathCosts.get(currentPoint) + map.getCost(currentPoint, neighbor);

                    if (altCost < currentCost) {
                        pathCosts.put(neighbor, altCost);
                        history.put(neighbor, currentPoint);
                    }

                    fringe.remove(neighbor);
                    fringe.add(neighbor);
                }
            }

            fringe.poll();
        }

        while (currentPoint != null) {
            path.add(0, currentPoint);
            currentPoint = history.get(currentPoint);
        }

        return path;
    }
}
