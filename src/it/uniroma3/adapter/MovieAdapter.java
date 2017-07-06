package it.uniroma3.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.model.Movie;
import it.uniroma3.utility.TMDBClient;

public class MovieAdapter {
	private TMDBClient client;
	private String resourcePath="/movie/";
	private int request;

	public MovieAdapter(){
		this.client= new TMDBClient();
		this.request=0;
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
	

	private void checkRequestRate() {
		this.request++;
		if(request==15){
			request=0;
			try {
				System.out.println("pausa richieste ... ");
				Thread.sleep(3000);
			} catch (InterruptedException e) {e.printStackTrace();}
		}

	}

}
