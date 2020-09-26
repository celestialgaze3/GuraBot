package net.celestialgaze.GuraBot.json;

import org.json.simple.JSONObject;

public class ServerInfo {
	public static ServerInfo getServerInfo(long id) {
		return new ServerInfo(id);
	}
	public static String getFilename(long id) {
		return System.getProperty("user.dir") + "\\data\\server\\" + id + ".json";
	}
	long id;
	
	public ServerInfo(long id) {
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	public void setPrefix(String newPrefix) {
		// creating JSONObject 
        JSONObject jo = JSON.readFile(getFilename(id));
          
        // putting data to JSONObject 
        jo.put("prefix", newPrefix);
        
        JSON.writeToFile(jo, getFilename(id));
	}
	@SuppressWarnings("unchecked")
	public String getPrefix() {
		JSONObject jo = JSON.readFile(getFilename(id));
		String prefix = (String)jo.getOrDefault("prefix", "a!");
		return prefix.toLowerCase();
	}
}
