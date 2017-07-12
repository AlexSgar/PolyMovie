package it.uniroma3.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

	public JSONObject getDetailsAppendedRequest(String idPerson){
		checkRequestRate();
		String url= resourcePath+idPerson;
		JSONObject personJson = this.client.get(url,"tv_credits");
		return personJson;
	}

	public JSONArray getTVCredits(String idPerson) throws JSONException{
		checkRequestRate();
		String url= resourcePath+idPerson+"/tv_credits";
		JSONObject creditsJson = this.client.get(url);
		JSONArray castingJson= creditsJson.getJSONArray("cast");
		return castingJson;

	}

	public JSONArray getPopular() throws JSONException{
		checkRequestRate();
		String url= resourcePath+"popular";
		JSONObject popularJson = this.client.get(url);
		JSONArray acotrsJson= popularJson.getJSONArray("results");
		return acotrsJson;

	}


	public JSONObject getImages(String idPerson) throws JSONException{
		checkRequestRate();
		String url= resourcePath+idPerson+"/images";
		return this.client.get(url);
	}

	public JSONObject getMoviecredits(String id_credit){
		checkRequestRate();
		String url= resourcePath+id_credit;
		JSONObject creditPerson = this.client.get(url,"movie_credits");
		return creditPerson;
	}

}