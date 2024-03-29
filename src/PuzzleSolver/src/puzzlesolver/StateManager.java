/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzlesolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author kordusj
 */
public class StateManager {

    private State goal;
    private HashSet closedSet = new HashSet(); //Evaluated Nodes
    private HashSet openSet = new HashSet();   //Discovered but not evaluated nodes
    private int across;
    private int tall;
    private State start;
    private int movablePiece = 0;
    
    StateManager(){};

    StateManager(State init) {
        start = init;
        across = init.getWidth();
        tall = init.getHeight();
        movablePiece = across * tall;
    }

    public State getStart() {
        return start;
    }

    public void goalState(int width, int length) {
        tall = length;
        across = width;
        
        int start[][] = new int[width][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                start[j][i] = width * i + j + 1;
            }
        }
        
        movablePiece = width * length;
        
        goal = new State(start);
        goal.printCurrentState();
    }

    public State getGoal() {
        return goal;
    }

    public static State generateGoalState(int width, int length) {
        int start[][] = new int[width][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                start[j][i] = width * i + j + 1;
            }
        }

        return new State(start);
    }

    public boolean FoundGoal(State curState) {
        return curState == goal;
    }

    public State[] GetNextStates(State curState) {
        ArrayList<State> a = new ArrayList<>();
        int[][] raw = curState.getState();
        //Find the coordinates of the empty spot (0)
        Point p = findPoint(movablePiece, curState);
        State temp;
        if (across > p.x + 1) {
            //We can move right
            Point right = new Point(p.x + 1, p.y);
            temp = swapPoints(curState, p, right);
            if (isInOpenSet(temp)) {
                temp = getStateInSet(temp);
            }
            a.add(temp);
        }
        if (p.x > 0) {
            //We can move left
            Point right = new Point(p.x - 1, p.y);
            temp = swapPoints(curState, p, right);
            if (isInOpenSet(temp)) {
                temp = getStateInSet(temp);
            }
            a.add(temp);
        }
        if (tall > p.y + 1) {
            //We can move up.
            Point right = new Point(p.x, p.y + 1);
            temp = swapPoints(curState, p, right);
            if (isInOpenSet(temp)) {
                temp = getStateInSet(temp);
            }
            a.add(temp);
        }
        if (p.y > 0) {
            //We can move down.
            Point right = new Point(p.x, p.y - 1);
            temp = swapPoints(curState, p, right);
            if (isInOpenSet(temp)) {
                temp = getStateInSet(temp);
            }
            a.add(temp);
        }
        return a.toArray(new State[a.size()]);
    }

    public boolean addToClosedSet(State state) {

        boolean result = closedSet.add(state);

        return result;

    }

    public boolean addToOpenSet(State state) {
        boolean result = openSet.add(state);
        
        return result;
    }

    public boolean removeFromOpenSet(State state) {
        boolean result = openSet.remove(state);
        
        return result;

    }

    public boolean isInClosedSet(State state) {
        return closedSet.contains(state);
    }

    public boolean isInOpenSet(State state) {
        return openSet.contains(state);
    }

    public boolean updateOpenStateInstance(State state) {
        if (openSet.contains(state)) {
            openSet.remove(state);
            openSet.add(state);
            return true;
        } else {
            openSet.add(state);
        }

        return false;
    }

    public State getStateInSet(State state) {
        Iterator<State> it = openSet.iterator();

        while (it.hasNext()) {
            State s = it.next();
            if (s.equals(state)) {
                return s;
            }
        }
        return null;
    }

    public boolean openSetEmpty() {
        return openSet.isEmpty();
    }

    public State findLowestF() {
        Iterator<State> it = openSet.iterator();

        State lowest = null;

        while (it.hasNext()) {
            State s = it.next();

            if (lowest == null) {
                lowest = s;
            } else if (lowest.getFScore() > s.getFScore()) {
                lowest = s;
            }

        }

        return lowest;

    }

    public float getHeuristic(State state) {

        int[][] rawState = state.getState();

        float totalDist = 0;

        for (int i = 0; i < rawState.length; i++) {

            for (int j = 0; j < rawState[i].length; j++) {

                Point goalPoint = findPoint(rawState[i][j], goal);
                Point thisPoint = new Point(i, j);

                if (thisPoint != null && goalPoint != null) {
                    totalDist += Utils.distance(goalPoint, thisPoint);
                }

            }

        }

        return totalDist;
        //return 0;
    }

        public float getManhattanHeuristic(State state) {

        int[][] rawState = state.getState();

        float totalDist = 0;

        for (int i = 0; i < rawState.length; i++) {

            for (int j = 0; j < rawState[i].length; j++) {

                Point goalPoint = findPoint(rawState[i][j], goal);
                Point thisPoint = new Point(i, j);

                if (thisPoint != null && goalPoint != null) {
                    totalDist += Utils.manhattanDistance(goalPoint, thisPoint);
                }

            }

        }

        return totalDist;
        //return 0;
    }
    
    private State swapPoints(State state, Point a, Point b) {
       int width = state.getWidth();
       int height = state.getHeight();
       
       int[][] newRaw = new int[state.getWidth()][state.getHeight()];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newRaw[i][j] = state.getState()[i][j];
            }
        }
        State newState = new State(newRaw);
        int temp = newState.getState()[a.x][a.y];
        newState.getState()[a.x][a.y] = newState.getState()[b.x][b.y];
        newState.getState()[b.x][b.y] = temp;
        return newState;
    }

    private Point findPoint(int x, State state) {

        for (int i = 0; i < state.getState().length; i++) {
            for (int j = 0; j < state.getState()[i].length; j++) {
                if (state.getState()[i][j] == x) {
                    return new Point(i, j);
                }
            }

        }

        return null;

    }
    
    public int getMovablePiece()
    {
      return movablePiece;
    }

    //Needs: 
    //Heuristic Getter
    public static void main(String args[]) {

        int[][] raw;
        raw = new int[][]{{4, 1, 3},
        {2, 5, -1},
        {7, 6, 8}};

        int[][] rawGoal;
        rawGoal = new int[][]{{0, 3, 6},
        {1, 4, 7},
        {2, 5, 8}};

        State testState = new State(raw);

        StateManager sm = new StateManager(testState);

        sm.goalState(3, 3);

        sm.goal.printCurrentState();

        System.out.println(sm.goal.equals(new State(rawGoal)));

        System.out.println(sm.getHeuristic(testState));

    }

}
