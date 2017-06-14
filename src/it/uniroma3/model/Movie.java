package it.uniroma3.model;
import org.json.JSONException;
import org.json.JSONObject;


public class Movie{

	private int id;
	private String title;

	public Movie(JSONObject json){
		try{
			this.id= json.getInt("id");
			this.title=json.getString("title");
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString(){
		return "id: "+this.id+" title: "+this.title;
	}

}
