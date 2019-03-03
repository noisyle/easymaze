package com.noisyle.bukkit.easymaze;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyMaze extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("EasyMaze被加载。");
    }

    @Override
    public void onDisable() {
        getLogger().info("EasyMaze被卸载。");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("easymaze")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
            } else {
                int d = 10;
                if (args.length > 0) {
                    try {
                        d = Integer.valueOf(args[0]);
                    } catch (NumberFormatException e) {
                        getLogger().warning("Illegal argument: " + args[0]);
                    }
                }
                Player player = (Player) sender;
                getLogger().info("输入命令easymaze");
                Location loc = player.getLocation();

                generateMaze(loc, d);

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

        Material floor = Material.GRASS_BLOCK;
        Material wall = Material.STONE;

        for (int tx = x - d; tx <= x + d; tx++) {
            for (int tz = z - d; tz <= z + d; tz++) {
                Node node = maze[tx - (x - d)][tz - (z - d)];
                world.getBlockAt(tx, y - 1, tz).setType(floor);
                if (node.wall) {
                    world.getBlockAt(tx, y, tz).setType(wall);
                    world.getBlockAt(tx, y + 1, tz).setType(wall);
                    world.getBlockAt(tx, y + 2, tz).setType(wall);
                } else {
                    world.getBlockAt(tx, y, tz).setType(Material.AIR);
                    world.getBlockAt(tx, y + 1, tz).setType(Material.AIR);
                    world.getBlockAt(tx, y + 2, tz).setType(Material.AIR);
                    torchList.add(world.getBlockAt(tx, y + 2, tz));
                }
            }
        }
        for (Block b : torchList) {
            b.setType(Material.WALL_TORCH);

            BlockData blockData = b.getBlockData();
            if (blockData instanceof Directional) {
                Directional directional = (Directional) blockData;
                directional.setFacing(BlockFace.NORTH);
            }

            b.getState().update();
        }
    }
}
