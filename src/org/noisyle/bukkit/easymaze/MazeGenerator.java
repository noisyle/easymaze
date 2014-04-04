package org.noisyle.bukkit.easymaze;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Node{
	int x,y;
	boolean wall = false;
	public Node(int x,int y){
		this.x=x;
		this.y=y;
	}
}

public class MazeGenerator {
	
	public static Node[][] getMazeDFS(int d){
		int width = 2*d+1;
		Map<String, Node> nodes = new HashMap<String, Node>();
		Node[][] maze = new Node[width][width];
		for(int y=0;y<width;y++){
			for(int x=0;x<width;x++){
				maze[y][x] = new Node(x,y);
				if(x%2==1 && y%2==1){
					nodes.put(x+":"+y, maze[y][x]);
				}else{
					maze[y][x].wall = true;
				}
			}
		}
		
		Map<String, Node> branch = new HashMap<String, Node>();
		Random r = new Random(new Date().getTime());
		int sx=(2*r.nextInt(d)+1),sy=(2*r.nextInt(d)+1);
		Node node = nodes.get(sx+":"+sy);//随机起点
		do{
			nodes.remove(node.x+":"+node.y);
			List<Node> l = new LinkedList<Node>();
			if(node.x>1 && nodes.containsKey((node.x-2)+":"+node.y)) l.add(maze[node.y][node.x-2]);
			if(node.y>1 && nodes.containsKey(node.x+":"+(node.y-2))) l.add(maze[node.y-2][node.x]);
			if(node.x<width-2 && nodes.containsKey((node.x+2)+":"+node.y)) l.add(maze[node.y][node.x+2]);
			if(node.y<width-2 && nodes.containsKey(node.x+":"+(node.y+2))) l.add(maze[node.y+2][node.x]);
			
			if(!l.isEmpty()){
				Node next = l.get(r.nextInt(l.size()));
				l.remove(next);
				if(!l.isEmpty()) branch.put(node.x+":"+node.y, node);
				maze[(node.y+next.y)/2][(node.x+next.x)/2].wall = false;
				node = next;
			}else if(!branch.isEmpty()){
				node = branch.values().iterator().next();
				branch.remove(node.x+":"+node.y);
			}else{
				node = nodes.values().iterator().next();
			}
		}while(!nodes.isEmpty());
		
		return maze;
	}
	
	public static Node[][] getMazeBFS(int d, int seed){
		int width = 2*d+1;
		Map<String, Node> nodes = new HashMap<String, Node>();
		Node[][] maze = new Node[width][width];
		for(int y=0;y<width;y++){
			for(int x=0;x<width;x++){
				maze[y][x] = new Node(x,y);
				if(x%2==1 && y%2==1){
					nodes.put(x+":"+y, maze[y][x]);
				}else{
					maze[y][x].wall = true;
				}
			}
		}
		
		List<Node> branch = new LinkedList<Node>();
		Random r = new Random(new Date().getTime());
		Node node = null;
		for(int i=0;i<seed;i++){
			int sx=(2*r.nextInt(d)+1),sy=(2*r.nextInt(d)+1);
			if(nodes.containsKey(sx+":"+sy)){
				node = nodes.get(sx+":"+sy);//随机起点
				branch.add(node);
				nodes.remove(node.x+":"+node.y);
			}else{
				i--;
			}
		}
		do{
			if(branch.isEmpty()){
				List<Node> l = new ArrayList<Node>(nodes.values());
				node = l.get(r.nextInt(l.size()));
				nodes.remove(node.x+":"+node.y);
				branch.add(node);
			}else{
				List<Node> new_branch = new LinkedList<Node>();
				while(!branch.isEmpty()){
					Node n = branch.get(r.nextInt(branch.size()));
					branch.remove(n);
					List<Node> l = new LinkedList<Node>();
					if(n.x>1 && nodes.containsKey((n.x-2)+":"+n.y)) l.add(maze[n.y][n.x-2]);
					if(n.y>1 && nodes.containsKey(n.x+":"+(n.y-2))) l.add(maze[n.y-2][n.x]);
					if(n.x<width-2 && nodes.containsKey((n.x+2)+":"+n.y)) l.add(maze[n.y][n.x+2]);
					if(n.y<width-2 && nodes.containsKey(n.x+":"+(n.y+2))) l.add(maze[n.y+2][n.x]);
					
					for(Node ne : l){
						maze[(n.y+ne.y)/2][(n.x+ne.x)/2].wall = false;
						nodes.remove(ne.x+":"+ne.y);
						new_branch.add(ne);
					}
				}
				branch = new_branch;
			}
		}while(!nodes.isEmpty());
		
		return maze;
	}
	
	public static Node[][] getMaze111(int d){
		int width = 2*d+1;
		Node[][] maze = new Node[width][width];
		for(int y=0;y<width;y++){
			for(int x=0;x<width;x++){
				maze[y][x] = new Node(x,y);
				if(x==0 || y==0 || x==width-1 || y==width-1){
					maze[y][x].wall = true;
				}
			}
		}
		process(maze, 1, 1, width-2, width-2);
		return maze;
	}
	
	private static void process(Node[][] maze, int x, int y, int w, int h){
		Random r = new Random(new Date().getTime());
		if(w>=3 && h>=3){
			int wx=1+r.nextInt(w-2);
			int wy=1+r.nextInt(h-2);
			int py1=r.nextInt(wy);
			int py2=r.nextInt(h-wy-1)+wy+1;
			for(int i=0;i<h;i++){
				if(i!=py1 && i!=py2){
					maze[y+i][x+wx].wall = true;
				}
			}
			int px1=r.nextInt(wx);
			int px2=r.nextInt(w-wx-1)+wx+1;
			for(int i=0;i<w;i++){
				if(i!=px1 && i!=px2){
					maze[y+wy][x+i].wall = true;
				}
			}
			process(maze, x, y, wx, wy);
			process(maze, x+wx+1, y, w-wx-1, wy);
			process(maze, x, y+wy+1, wy, h-wy-1);
			process(maze, x+wx+1, y+wy+1, w-wx-1, h-wy-1);
		}else if(w>=3){
			int wx=1+r.nextInt(w-2);
			int py=r.nextInt(h);
			for(int i=0;i<h;i++){
				if(i!=py){
					maze[y+i][x+wx].wall = true;
				}
			}
			process(maze, x, y, wx, h);
			process(maze, x+wx+1, y, w-wx-1, h);
		}else if(h>=3){
			int wy=1+r.nextInt(h-2);
			int px=r.nextInt(w);
			for(int i=0;i<w;i++){
				if(i!=px){
					maze[y+wy][x+i].wall = true;
				}
			}
			process(maze, x, y, w, wy);
			process(maze, x, y+wy+1, w, h-wy-1);
		}
	}
	
	public static void main(String[] args) {
//		Date time1 = new Date();
//		Node[][] maze1 = MazeGenerator.getMazeDFS(200);
//		System.out.println("耗时："+(new Date().getTime() - time1.getTime())+"毫秒");
//		printMaze(maze1);

//		Date time2 = new Date();
//		Node[][] maze2 = MazeGenerator.getMazeBFS(200, 5);
//		System.out.println("耗时："+(new Date().getTime() - time2.getTime())+"毫秒");
//		printMaze(maze2);
		
		Node[][] maze3 = MazeGenerator.getMaze111(4);
		printMaze(maze3);
	}
	
	private static void printMaze(Node[][] maze){
		for(Node[] row : maze){
			for(Node node : row){
				if(node.wall) System.out.print("* ");
				else System.out.print("  ");
			}
			System.out.println();
		}
	}

}
