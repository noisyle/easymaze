package com.noisyle.bukkit.easymaze;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Node {
    int x, y;
    boolean wall = false;
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class MazeGenerator {

    public static Node[][] getMazeDFS(int d) {
        int width = 2 * d + 1;
        Map<String, Node> nodes = new HashMap<String, Node>();
        Node[][] maze = new Node[width][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = new Node(x, y);
                if (x % 2 == 1 && y % 2 == 1) {
                    nodes.put(x + ":" + y, maze[y][x]);
                } else {
                    maze[y][x].wall = true;
                }
            }
        }

        Map<String, Node> branch = new HashMap<String, Node>();
        Random r = new Random(new Date().getTime());
        int sx = (2 * r.nextInt(d) + 1), sy = (2 * r.nextInt(d) + 1);
        Node node = nodes.get(sx + ":" + sy);// 随机起点
        do {
            nodes.remove(node.x + ":" + node.y);
            List<Node> l = new LinkedList<Node>();
            if (node.x > 1 && nodes.containsKey((node.x - 2) + ":" + node.y))
                l.add(maze[node.y][node.x - 2]);
            if (node.y > 1 && nodes.containsKey(node.x + ":" + (node.y - 2)))
                l.add(maze[node.y - 2][node.x]);
            if (node.x < width - 2 && nodes.containsKey((node.x + 2) + ":" + node.y))
                l.add(maze[node.y][node.x + 2]);
            if (node.y < width - 2 && nodes.containsKey(node.x + ":" + (node.y + 2)))
                l.add(maze[node.y + 2][node.x]);

            if (!l.isEmpty()) {
                Node next = l.get(r.nextInt(l.size()));
                l.remove(next);
                if (!l.isEmpty())
                    branch.put(node.x + ":" + node.y, node);
                maze[(node.y + next.y) / 2][(node.x + next.x) / 2].wall = false;
                node = next;
            } else if (!branch.isEmpty()) {
                node = branch.values().iterator().next();
                branch.remove(node.x + ":" + node.y);
            } else {
                node = nodes.values().iterator().next();
            }
        } while (!nodes.isEmpty());

        return maze;
    }

    public static Node[][] getMazeBFS(int d, int seed) {
        int width = 2 * d + 1;
        Map<String, Node> nodes = new HashMap<String, Node>();
        Node[][] maze = new Node[width][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = new Node(x, y);
                if (x % 2 == 1 && y % 2 == 1) {
                    nodes.put(x + ":" + y, maze[y][x]);
                } else {
                    maze[y][x].wall = true;
                }
            }
        }

        List<Node> branch = new LinkedList<Node>();
        Random r = new Random(new Date().getTime());
        int sx = (2 * r.nextInt(d) + 1), sy = (2 * r.nextInt(d) + 1);
        Node node = nodes.get(sx + ":" + sy);// 随机起点
        branch.add(node);
        nodes.remove(node.x + ":" + node.y);
        do {
            if (branch.isEmpty()) {
                List<Node> l = new ArrayList<Node>(nodes.values());
                node = l.get(r.nextInt(l.size()));
                nodes.remove(node.x + ":" + node.y);
                branch.add(node);
            } else {
                List<Node> new_branch = new LinkedList<Node>();
                while (!branch.isEmpty()) {
                    Node n = branch.get(r.nextInt(branch.size()));
                    branch.remove(n);
                    List<Node> l = new LinkedList<Node>();
                    if (n.x > 1 && nodes.containsKey((n.x - 2) + ":" + n.y))
                        l.add(maze[n.y][n.x - 2]);
                    if (n.y > 1 && nodes.containsKey(n.x + ":" + (n.y - 2)))
                        l.add(maze[n.y - 2][n.x]);
                    if (n.x < width - 2 && nodes.containsKey((n.x + 2) + ":" + n.y))
                        l.add(maze[n.y][n.x + 2]);
                    if (n.y < width - 2 && nodes.containsKey(n.x + ":" + (n.y + 2)))
                        l.add(maze[n.y + 2][n.x]);

                    for (Node ne : l) {
                        if (new_branch.size() >= seed)
                            break;
                        maze[(n.y + ne.y) / 2][(n.x + ne.x) / 2].wall = false;
                        nodes.remove(ne.x + ":" + ne.y);
                        new_branch.add(ne);
                    }
                }
                branch = new_branch;
            }
        } while (!nodes.isEmpty());

        return maze;
    }

    public static void main(String[] args) {
        Date time1 = new Date();
        Node[][] maze1 = MazeGenerator.getMazeDFS(20);
        System.out.println("耗时：" + (new Date().getTime() - time1.getTime()) + "毫秒");
        printMaze(maze1);

        Date time2 = new Date();
        Node[][] maze2 = MazeGenerator.getMazeBFS(20, 5);
        System.out.println("耗时：" + (new Date().getTime() - time2.getTime()) + "毫秒");
        printMaze(maze2);
    }

    private static void printMaze(Node[][] maze) {
        for (Node[] row : maze) {
            for (Node node : row) {
                if (node.wall)
                    System.out.print("○");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
    }

}
