package it.uniroma3.adapter;

import org.json.JSONObject;

import it.uniroma3.model.Movie;
import it.uniroma3.utility.TMDBClient;

public class MovieAdapter {
	private TMDBClient client;
	private String resourcePath="/movie/";
	
	public MovieAdapter(){
		this.client= new TMDBClient();
	}
	
	public Movie getMovieDetails(String idMovie){
		String url= resourcePath+idMovie;
		JSONObject movieJson = this.client.get(url);
		return new Movie(movieJson);
		
	}

}
