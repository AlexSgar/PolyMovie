package it.uniroma3.model;
import org.json.JSONException;
import org.json.JSONObject;


public class Movie{

	private String id;
	private String title;
	private String original_title;
	private String original_language;
	private String revenue;

	public Movie(JSONObject json){
		try{
			this.id= json.getString("id");
			this.title=json.getString("title");
			this.original_title=json.getString("original_title");
			this.original_language=json.getString("original_language");

		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString(){
		return "id: "+this.id+" title: "+this.title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	public String getOriginal_language() {
		return original_language;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}

	public String getRevenue() {
		return revenue;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}

}
