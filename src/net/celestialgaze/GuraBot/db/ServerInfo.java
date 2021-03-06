package net.celestialgaze.GuraBot.db;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.celestialgaze.GuraBot.GuraBot;
import net.celestialgaze.GuraBot.commands.Commands;
import net.dv8tion.jda.api.entities.Guild;

public class ServerInfo {
	public static Map<Long, ServerInfo> serverInfos = new HashMap<Long, ServerInfo>();
	public final Guild guild;
	public static ServerInfo getServerInfo(long id) {
		if (serverInfos.containsKey(id)) return serverInfos.get(id);
		return new ServerInfo(id);
	}
	public Document cachedProperties = new Document();
	public Document getDocument() {
		Document result = GuraBot.servers.find(documentFilter).first();
		if (result == null) {
			GuraBot.servers.insertOne(new Document().append("id", id));
			result = new Document().append("id", id);
		}
		return result;
	}
	long id;
	Bson documentFilter;
	
	public ServerInfo(long id) {
		this.id = id;
		this.documentFilter = Filters.eq("id", id);
		this.guild = GuraBot.jda.getGuildById(id);
		serverInfos.put(this.id, this);
	}
	
	public void setProperty(ServerProperty property, Object value) {
        Bson updateOperation = Updates.set(property.toString().toLowerCase(), value);
        GuraBot.servers.updateOne(documentFilter, updateOperation);
		cachedProperties.put(property.toString().toLowerCase(), value);
	}
	public Document getModuleDocument(String moduleName) {
		Document moduleDoc = getProperty(ServerProperty.MODULE, new Document());
		if (moduleDoc.get(moduleName) == null) {
			moduleDoc.append(moduleName, new Document());
		}
		return (Document) moduleDoc.get(moduleName);
		
	}
	public void updateModuleDocument(String moduleName, Document newDoc) {
		Document moduleDoc = getProperty(ServerProperty.MODULE, new Document());
		moduleDoc.put(moduleName, newDoc);
		setProperty(ServerProperty.MODULE, moduleDoc);
	}
	@SuppressWarnings("unchecked")
	public <T> T getProperty(ServerProperty property) {
		String name = property.toString().toLowerCase();
		if (cachedProperties.containsKey(name)) {
			return (T) cachedProperties.get(name);
		} else {
			T object = (T) getDocument().get(name);
			cachedProperties.put(name, object);
			return object;
		}
	}
	public <T> T getProperty(ServerProperty property, T def) {
		T result = getProperty(property);
		return (result != null ? result : def);
	}
	public String getPrefix() {
		return getProperty(ServerProperty.PREFIX, Commands.defaultPrefix);
	}
	private void setXP(long userId, int value, Document xpDoc) {
			updateModuleDocument("xp",
				new DocBuilder(xpDoc)
					.getSubDoc("experience")
					.put(Long.toString(userId), value)
					.build());
	}
	public void setXP(long userId, int value) {
		setXP(userId, value, getModuleDocument("xp"));
	}
	public void addXP(long userId, int value) {
		Document xpDoc = getModuleDocument("xp");
		setXP(userId, getXP(userId, xpDoc)+value, xpDoc);
	}
	public int getXP(long userId) {
		return getXP(userId, getModuleDocument("xp"));
	}
	public int getXP(long userId, Document xpDoc) {
		return new DocBuilder(xpDoc)
				.getSubDoc("experience")
				.get(Long.toString(userId), Integer.parseInt("0"));
	}
	public Map<String, Integer> getXpMap() {
		return getXpMap(getModuleDocument("xp"));
	}
	public Map<String, Integer> getXpMap(Document xpDoc) {
		Map<String, Integer> result = new HashMap<>();
		Document expDoc = new DocBuilder(xpDoc)
				.getSubDoc("experience")
				.buildThis();
		expDoc.forEach((key, value) -> {
			result.put((String)key, (int)value);
		});
		return result;
	}
}
