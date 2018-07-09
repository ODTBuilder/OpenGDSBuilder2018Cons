package com.git.gdsbuilder.geogig.type;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.git.gdsbuilder.geogig.type.GeogigRepositoryLog.Commit;

/**
 * Geogig Blame Command 응답 xml 객체.
 * 
 * @author GIT
 *
 */
@XmlRootElement(name = "response")
public class GeogigBlame {
	/**
	 * Geogig Command 응답 성공 여부
	 */
	private String success;

	@XmlElementWrapper(name = "Blame")
	@XmlElement(name = "Attribute")
	private List<Attribute> attributes;

	@XmlElement(name = "success")
	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	@XmlRootElement(name = "Attribute")
	public static class Attribute {

		private String name;
		private String value;
		private List<Commit> commits;

		@XmlElement(name = "name")
		public String getName() {
			return name;
		}

		@XmlElement(name = "value")
		public String getValue() {
			return value;
		}

		@XmlElement(name = "commit")
		public List<Commit> getCommits() {
			return commits;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void setCommits(List<Commit> commits) {
			this.commits = commits;
		}

	}

}
