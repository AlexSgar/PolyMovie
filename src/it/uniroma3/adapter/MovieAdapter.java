package it.uniroma3.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.model.Movie;
import it.uniroma3.utility.TMDBClient;

public class MovieAdapter  extends Adapter{
	private String resourcePath="/movie/";

	public MovieAdapter(){
		super();
	}


	public JSONObject getMovie(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie;
		JSONObject movieJson = this.client.get(url);
		return movieJson;
	}
	
	public JSONObject getMovieKeywords(String idMovie){
		checkRequestRateAlex();
		String url = resourcePath + idMovie +"/keywords";
		JSONObject movieKeywords = this.client.get(url);
		return movieKeywords;
	}
	
	
	public Movie getMovieDetails(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie;
		JSONObject movieJson = this.client.get(url);
		return new Movie(movieJson);
	}


	public JSONArray getMovieReview(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie+"/reviews";
		JSONObject reviewJson = this.client.get(url);
		JSONArray reviews=null;
		try {
			reviews = reviewJson.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();}
		return reviews;
	}
	

	public JSONArray getMovieCredits(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie+"/credits";
		JSONObject creditsJson = this.client.get(url);
		JSONArray credits=null;
		try {
			credits = creditsJson.getJSONArray("cast");
		} catch (JSONException e) {
			e.printStackTrace();}
		return credits;
	}

	
	public JSONArray getMovieReccomandations(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie+"/recommendations";
		JSONObject reviewJson = this.client.get(url);
		JSONArray reviews=null;
		try {
			reviews = reviewJson.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();}
		return reviews;
	}

	public JSONObject getMovieAppendedRequest(String idMovie) {
		checkRequestRate();
		String url= resourcePath+idMovie;
		JSONObject movieJson = this.client.get(url,"reviews,recommendations");
		return movieJson;
		
	}
	
	
	public JSONObject getMovieLanguagesAppendedRequest(String idMovie) {
		checkRequestRate();
		String url= resourcePath+idMovie;
		JSONObject movieJson = this.client.get(url,"alternative_titles,translations");
		return movieJson;
		
	}
	
	
	

}
