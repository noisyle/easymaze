package org.noisyle.bukkit.easymaze;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EasyMaze extends JavaPlugin {
	public void onEnable() {
		getLogger().info("EasyMaze被加载。");
	}

	public void onDisable() {
		getLogger().info("EasyMaze被卸载。");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("easymaze")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				int d = 10;
				if(args.length>0){
					try {
						d = Integer.valueOf(args[0]);
					} catch (Exception e) {
					}
				}
				Player player = (Player) sender;
				getLogger().info("输入命令easymaze");
				Location loc = player.getLocation();
				
				generateMaze(loc, d);

			}
			return true;
		}else if (cmd.getName().equalsIgnoreCase("easyhouse")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				Player player = (Player) sender;
				getLogger().info("输入命令easyhouse");
				Location loc = player.getLocation();
				
				generateHouse(loc);

			}
			return true;
		}
		return false;
	}

	private void generateMaze(Location loc, int d) {
		Node[][] maze = MazeGenerator.getMazeDFS(d);
		
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		int y = loc.getBlockY();

		World world = loc.getWorld();
		
		List<Block> torchList = new LinkedList<Block>();
		
		Material floor = Material.GRASS;
		Material wall = Material.STONE;
		
		for (int tx = x-d; tx <= x+d; tx++) {
			for (int tz = z-d; tz <= z+d; tz++) {
				Node node = maze[tx-(x-d)][tz-(z-d)];
				world.getBlockAt(tx, y-1, tz).setType(floor);
				if(node.wall){
					world.getBlockAt(tx, y, tz).setType(wall);
					world.getBlockAt(tx, y+1, tz).setType(wall);
					world.getBlockAt(tx, y+2, tz).setType(wall);
				}else{
					world.getBlockAt(tx, y, tz).setType(Material.AIR);
					world.getBlockAt(tx, y+1, tz).setType(Material.AIR);
					world.getBlockAt(tx, y+2, tz).setType(Material.AIR);
					torchList.add(world.getBlockAt(tx, y+2, tz));
				}
			}
		}
		for(Block b: torchList){
			b.setType(Material.TORCH);
			b.getState().update();
		}
	}
	
	private void generateHouse(Location loc) {
		try {
			World world = loc.getWorld();
			int x = loc.getBlockX();
			int z = loc.getBlockZ();
			int y = loc.getBlockY();
			List<Block> otherList = new LinkedList<Block>();

			JSONObject easyhouse = HouseGenerator.getHouse();
			JSONArray data = (JSONArray) easyhouse.get("data");
			for (int ty = 0; ty < data.size(); ty++) {
				JSONArray floor = (JSONArray) data.get(ty);
				for (int tx = 0; tx < floor.size(); tx++) {
					JSONArray line = (JSONArray) floor.get(tx);
					for (int tz = 0; tz < line.size(); tz++) {
						Long node = (Long) line.get(tz);
						Block b = world.getBlockAt(x-floor.size()/2+tx, y-1+ty, z-line.size()/2+tz);
						if(node==1){
							b.setType(Material.STONE);
						}else if(node==3){
							b.setType(Material.GLASS);
						}else{
							b.setType(Material.AIR);
						}
					}
				}
			}
			for (int ty = 0; ty < data.size(); ty++) {
				JSONArray floor = (JSONArray) data.get(ty);
				for (int tx = 0; tx < floor.size(); tx++) {
					JSONArray line = (JSONArray) floor.get(tx);
					for (int tz = 0; tz < line.size(); tz++) {
						Long node = (Long) line.get(tz);
						Block b = world.getBlockAt(x-floor.size()/2+tx, y-1+ty, z-line.size()/2+tz);
						if(node==2){
							b.setType(Material.TORCH);
						}else if(node==4){
							Block top = b.getRelative(BlockFace.UP, 1);
							top.setData((byte)0x1000);
							top.setType(Material.WOODEN_DOOR);
							top.getState().update();
							b.setData((byte)0x0000);
							b.setType(Material.WOODEN_DOOR);
						}
						b.getState().update();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
