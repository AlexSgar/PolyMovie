package it.uniroma3.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.utility.TMDBClient;

public class PeopleAdapter extends Adapter {
	private String resourcePath="/person/";

	public PeopleAdapter() {
		super();
	}
	
	public JSONObject getDetails(String idPerson){
		checkRequestRate();
		String url= resourcePath+idPerson;
		JSONObject personJson = this.client.get(url);
		return personJson;

	}
	
	public JSONArray getTVCredits(String idPerson) throws JSONException{
		checkRequestRate();
		String url= resourcePath+idPerson+"/tv_credits";
		JSONObject creditsJson = this.client.get(url);
		JSONArray castingJson= creditsJson.getJSONArray("cast");
		return castingJson;

	}

}
