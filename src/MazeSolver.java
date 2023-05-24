
import java.awt.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

interface IMazeSolver {

    int[][] solveDFS (File maze);
    int [][] solveBFS(File file);
    boolean isValid(int i,int j,char [][]maze);
}
public class MazeSolver implements IMazeSolver{

    public  int[][] solveDFS(File file)
    {
        int starti = -1 ,startj = -1;
        char[][] maze ={};

        try {
            Scanner in = new Scanner(file);
            int m = in.nextInt();
            int n = in.nextInt();
            maze = new char[m][n];


            for (int i = 0; i < m; i++) {
                String line = in.next();
                for (int j = 0; j < n; j++) {
                    maze[i][j] = line.charAt(j);
                    if(maze[i][j] == 'S' || maze[i][j] == 's') {
                        starti = i;
                        startj = j;
                    }
                }
            }
            if(starti == -1)
            {
                System.out.println("Starting point is not found");
                System.exit(0);
            }
            in.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            System.exit(0);
        }

        int i = starti;
        int j = startj;
        Point p = new Point(i,j);   // starting index 'S'
        Stack<Point> s = new Stack<>();   // stack of points to save every point in my path
        s.push(p);
        maze[i][j]='v';

        int [] di = {-1,1,0,0};
        int [] dj = {0,0,-1,1}; // up down left right
        int k ;

        while(!(s.isEmpty())) {

            for (k = 0; k < 4; k++) {

                if(isValid(i+di[k],j+dj[k] ,maze))   // search for a valid neighbour in the 4 directions
                {
                    i = i+di[k];
                    j = j+dj[k];
                    s.push(new Point(i,j));       // if found push its coordinates in the stack
                    if (maze[i][j] == 'E') break;
                    maze[i][j] = 'v';            // mark the cell as visited
                    break;       // break after founding a valid neighbour
                }

            }
            if (maze[i][j] == 'E') break;    // if we reach the target break the loop

            if(k == 4)   //start backtracking if we reach a dead end (the 4 neighbouring cells are invalid aka k =4)
            {
                s.pop();
                if(s.isEmpty())  // if we reach a dead end and stack is empty it means that there is no path
                {
                    System.out.println("no path found from S to E");
                    System.exit(0);
                }
                i = s.peek().x;
                j = s.peek().y;
            }
        }

        int [][] paths  = new int[s.size()][2];
        int l = 0;

        while(!(s.isEmpty()))
        {
            p = s.pop();            // the dfs path will be the points remaining in the stack
            paths[l][0] = (int) p.getX();
            paths[l++][1] = (int) p.getY();
        }

        return paths;
    }
    public  int [][] solveBFS(File file){

        int starti = -1 ,startj = -1;
        char[][] maze ={};

        try {
            Scanner in = new Scanner(file);
            int m = in.nextInt();
            int n = in.nextInt();
            maze = new char[m][n];


            for (int i = 0; i < m; i++) {
                String line = in.next();
                for (int j = 0; j < n; j++) {
                    maze[i][j] = line.charAt(j);
                    if(maze[i][j] == 'S' || maze[i][j] == 's') {
                        starti = i;
                        startj = j;
                    }
                }
            }
            if(starti == -1)
            {
                System.out.println("Starting point is not found");
                System.exit(0);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            System.exit(0);
        }

        int i = starti;
        int j = startj;
        Point p = new Point(i,j);   // starting index 'S'
        Queue<Point> q = new LinkedList<>();
        q.add(p);
        maze[i][j] = 'v';

        int [] di = {-1,1,0,0};
        int [] dj = {0,0,-1,1}; // up down left right
        Point [][] parent = new Point [1000][1000]; // to store the parent of each cell to be able to retrieve path
        int k , nx=0 ,ny = 0,last_i = starti , last_j = startj;
        boolean reached = false;


        while(!(q.isEmpty())){
            p = q.remove();                  // remove the head of the queue then add its neighbours
            i = p.x;          // i,j are coordinates of parent cell
            j = p.y;

            if(reached) break;

            for (k = 0; k < 4; k++) {                // check for the 4 neighbours of the dequeued point in all direction

                if(isValid(i+di[k],j+dj[k] ,maze)) {
                    nx = i+di[k];
                    ny = j+dj[k];                   // get neighbours' coordinates
                    q.add(new Point(nx,ny));           // add all valid neighbours in the queue
                    if (maze[nx][ny] == 'E') {
                        reached = true;
                        last_i = nx;
                        last_j = ny;
                    }
                    if (maze[nx][ny] != 'E') maze[nx][ny] = 'v';  // mark the cell as visited except E
                    parent[nx][ny] = new Point(i,j);    // mark the parent of each neighbour
                }
            }
        }
        //after ending the loop the coordinates of last visited cell aka E is last_i,last_j use it to find the parents so the path
        i = last_i;
        j = last_j;

        if(maze[i][j] != 'E')
        {
            System.out.println("No path found from S to E");
            System.exit(0);
        }

        ArrayList<Point> list = new ArrayList<>();
        list.add(new Point(i,j));  // add coordinates of 'E' at the end of the path

        while (true){
            try {
                p = parent[i][j];    // get the parent of each cell starting from 'E' till 's'
                list.add(p);
                i = p.x;
                j =  p.y;
            }
            catch(Exception e){
                break;
            }
        }
        if(list.get(list.size()-1) == null) list.remove(list.size()-1);

        int[][] path = new int[list.size()][2];
        for ( i = 0; i < list.size(); i++) {
            p = list.get(i);
            path[i][0] = p.x;
            path[i][1] = p.y;
        }

        return path;
    }

    public  boolean isValid(int i,int j,char [][]maze){
        return (i >= 0 && i < maze.length && j >= 0 && j < maze[0].length && maze[i][j] != 'v' && maze[i][j] != '#');
    }
    public static void main(String[] args) {

        File file = new File("0.txt");
        System.out.println("how do you want to solve the maze : 1)DFS  2)BFS");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        MazeSolver maze = new MazeSolver();
        int [][]path;   // array has path to 'E' in the maze

        if(choice == 1) {
            path = maze.solveDFS(file);
            for (int i = path.length-1 ; i >= 0; i--) {
                System.out.print("{"+ path[i][0] +"," + path[i][1] +"}");
                if(i >= 1) System.out.print(",");

            }
        } else if (choice == 2) {
            path = maze.solveBFS(file);

            for (int i = path.length - 1; i >= 0; i--) {
                System.out.print("{" + path[i][0] + "," + path[i][1] + "}");
                if (i >= 1) System.out.print(",");

            }
        }
    }
}
