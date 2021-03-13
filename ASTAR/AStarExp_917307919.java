import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarExp_917307919 implements AIModule{

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
        double absXDist = Math.abs(pt1.x - goalPt.x);
        double absYDist = Math.abs(pt1.y - goalPt.y);
        double currentHeight = map.getTile(pt1);
        double goalHeight = map.getTile(goalPt);

        double absHeightDiff = Math.abs(goalHeight-currentHeight);
        double tileDist = Math.max(absXDist, absYDist);

        double cost = 0;

        if (pt1.equals(goalPt)) {
            return cost;
        }

        if (currentHeight > goalHeight) { // above

            if (absHeightDiff > tileDist) { // more height than tile dist
                double smallPow = Math.floor(absHeightDiff/tileDist);
                double largePow = smallPow+1;
                double dhModDist = absHeightDiff % tileDist;
                cost = (tileDist-dhModDist)*Math.exp(-1*smallPow) + (dhModDist)*Math.exp(-1*largePow);
            }
            else { // less height than tile dist
                cost = absHeightDiff*Math.exp(-1);
                cost +=  tileDist - absHeightDiff;
            }
        }
        else if (currentHeight < goalHeight) { //below

            if (absHeightDiff > tileDist) { // more height than tile dist
                double smallPow = Math.floor(absHeightDiff/tileDist);
                double largePow = smallPow+1;
                double dhModDist = absHeightDiff % tileDist;
                cost = (tileDist-dhModDist)*Math.exp(smallPow) + (dhModDist)*Math.exp(largePow);
            }
            else { // less height than tile dist
                cost = absHeightDiff*Math.exp(1);
                cost += tileDist - absHeightDiff;
            }
        }
        else { // leveled
            cost = tileDist; // this is correct
        }

        return cost/2;
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

            visited.add(currentPoint);

            if (currentPoint.equals(goal)) {
                break;
            }

            for (Point neighbor : map.getNeighbors(currentPoint)) {
                if (!visited.contains(neighbor)) {
                    double currentCost = pathCosts.getOrDefault(neighbor, Double.MAX_VALUE);
                    double altCost = pathCosts.get(currentPoint) + map.getCost(currentPoint, neighbor);

                    altCost += getHeuristic(map, neighbor, goal);

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



