/**
 * 
 */
package com.git.gdsbuilder.geogig;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author GIT
 *
 */
@SuppressWarnings("serial")
public class GeogigRepositoryTree extends JSONArray {

	private String server;

	/**
	 * @param server
	 */
	public void bulid(String server) {
		this.server = server;
		JSONObject geoserver = new JSONObject(); // baseURL
		geoserver.put("id", server);
		geoserver.put("parent", "#");
		geoserver.put("text", server);
		super.add(geoserver);
	}

	/**
	 * @param parent
	 * @param text
	 */
	public void add(String parent, String text) {
		JSONObject repoJson = new JSONObject();
		repoJson.put("id", text);
		repoJson.put("parent", parent);
		repoJson.put("text", text);
		super.add(repoJson);
	}

	/**
	 * @param parent
	 * @param text
	 * @param type
	 */
	public void addRepo(String parent, String text, String type) {
		JSONObject repoJson = new JSONObject();
		repoJson.put("id", text);
		repoJson.put("parent", parent);
		repoJson.put("text", text);
		repoJson.put("type", type);
		super.add(repoJson);
	}

	/**
	 * @param parent
	 * @param text
	 * @param status
	 */
	public void addBranch(String parent, String text, String status) {
		JSONObject repoJson = new JSONObject();
		repoJson.put("id", text);
		repoJson.put("parent", parent);
		repoJson.put("text", text);
		repoJson.put("status", status);
		super.add(repoJson);
	}
}
