package org.noisyle.bukkit.easymaze;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HouseGenerator {
	public static JSONObject getHouse() {
		JSONObject easyhouse = null;
		try {
			InputStream in = null;
			File f = new File("plugins/easymaze/house.json");
			if(f.exists()){
				in = new FileInputStream(f);
			}else{
				f.createNewFile();
				in = HouseGenerator.class.getResourceAsStream("/easymaze/house.json");
			}
			byte b[] = new byte[10240];
			int len = 0, temp = 0;
			while ((temp = in.read()) != -1) {
				b[len] = (byte) temp;
				len++;
			}

			JSONParser parser = new JSONParser();
			JSONObject parseObject = (JSONObject) parser.parse(new String(b,0,len));
			easyhouse = (JSONObject) parseObject.get("easyhouse");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return easyhouse;
	}

	public static void main(String[] args) {
		try {
			JSONObject easyhouse = getHouse();
			JSONArray data = (JSONArray) easyhouse.get("data");
			for (int i = 0; i < data.size(); i++) {
				JSONArray floor = (JSONArray) data.get(i);
				for (int j = 0; j < floor.size(); j++) {
					JSONArray line = (JSONArray) floor.get(j);
					for (int k = 0; k < line.size(); k++) {
						Long node = (Long) line.get(k);
						System.out.print(node);
					}
					System.out.println();
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
