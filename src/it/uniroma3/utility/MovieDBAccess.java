package it.uniroma3.utility;

import java.net.*;
import java.io.*;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBAccess {
	private String apiKey="?api_key=044d07c2be73d3ebc84afe8ff751e2f2";
	private  String url = "https://api.themoviedb.org/3";

	public MovieDBAccess(){}


	public  JSONObject getMovie(String idMovie) throws Exception {
		String tag="/movie/";
		tag.concat(idMovie);
		String request= url+tag+apiKey;
		JSONObject movieJson = readJsonFromUrl(request);
		System.out.println(movieJson.toString());
		return movieJson;
	}
	
	public  JSONObject getTVShow(String idShow) throws Exception {
		String tag="/tv/";
		tag.concat(idShow);
		String request= url+tag+apiKey;
		JSONObject tvShowJson = readJsonFromUrl(request);
		System.out.println(tvShowJson.toString());
		return tvShowJson;
	}

	
	
	
	
	
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}


}
