/**
 * 
 */
package com.git.gdsbuilder.geogig.type;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Geogig LsTree Command 응답 xml 객체.
 * 
 * @author GIT
 *
 */
@XmlRootElement(name = "response")
public class GeogigRevisionTree {

	/**
	 * Geogig Command 응답 성공 여부
	 */
	private String success;

	private List<Node> nodes;

	@XmlElement(name = "success")
	public String getSuccess() {
		return success;
	}

	@XmlElement(name = "node")
	public List<Node> getNodes() {
		return nodes;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	@XmlRootElement(name = "node")
	public static class Node {

		private String path;

		@XmlElement(name = "path")
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

}
