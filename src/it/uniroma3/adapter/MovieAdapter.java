package it.uniroma3.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.model.Movie;

public class MovieAdapter  extends Adapter{
	private String resourcePath="/movie/";

	public MovieAdapter(){
		super();
	}


	public JSONObject getMovie(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie;
		return this.client.get(url);
	}
	
	public JSONObject getMovieImages(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie+"/images";
		return this.client.get(url);
	}
	
	public JSONObject getMovieAppendImagesAndVideos(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie;
		return this.client.get(url,"images,videos");
	}
	
	public JSONObject getMovieKeywords(String idMovie){
		checkRequestRate();
		String url = resourcePath + idMovie +"/keywords";
		return this.client.get(url);
	}
	
	public JSONObject getMovieAppendKeywords(String idMovie){
		checkRequestRate();
		String url= resourcePath+idMovie;
		return this.client.get(url,"keywords");
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
			if(credits.length()==0){
				credits=null;
			}
		} catch (Exception e) {
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
