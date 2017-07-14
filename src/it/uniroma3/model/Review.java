package it.uniroma3.model;

public class Review {
	private String id;
	private String user;
	private String collapse;
	private String content;

	public Review() {
	}

	public Review(String id, String user, String content) {
		super();
		this.id = id;
		this.user = user;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCollapse() {
		return collapse;
	}

	public void setCollapse(String collapse) {
		this.collapse = collapse;
	}

}
