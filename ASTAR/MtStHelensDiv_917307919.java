import java.awt.*;
import java.util.*;
import java.util.List;

public class MtStHelensDiv_917307919 implements AIModule{

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

    private double getHeuristic(final TerrainMap map, final Point pt1, final Point goalPt) {
        double xx = Math.pow(pt1.x-goalPt.x,2);
        double yy = Math.pow(pt1.y-goalPt.y,2);
        double zz = Math.pow(map.getTile(pt1)-map.getTile(goalPt),2);

        if (pt1.equals(goalPt)) {
            return 0;
        }

        return Math.sqrt(xx+zz+yy)/1000;
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
            currentPoint = fringe.poll();

            if (visited.contains(currentPoint)) {
                System.out.println("Duplicate");
                return null;
            }

            visited.add(currentPoint);

            if (currentPoint.equals(goal)) {
                break;
            }

            for (Point neighbor : map.getNeighbors(currentPoint)) {
                if (!visited.contains(neighbor)) {
                    double currentCost = pathCosts.getOrDefault(neighbor, Double.MAX_VALUE);
                    double altCost = pathCosts.get(currentPoint) + map.getCost(currentPoint, neighbor);

                    altCost += getHeuristic(map, currentPoint, neighbor);

                    if (altCost < currentCost) {
                        pathCosts.put(neighbor, altCost);
                        history.put(neighbor, currentPoint);
                    }
                    fringe.remove(neighbor);
                    fringe.add(neighbor);
                }
            }
        }

        while (currentPoint != null) {
            path.add(0, currentPoint);
            currentPoint = history.get(currentPoint);
        }

        return path;
    }
}



