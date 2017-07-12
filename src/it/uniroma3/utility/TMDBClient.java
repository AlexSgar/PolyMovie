package it.uniroma3.utility;

import java.net.*;
import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TMDBClient {
	private String apiKey="?api_key=044d07c2be73d3ebc84afe8ff751e2f2";
	private  String baseUrl = "https://api.themoviedb.org/3";

	public TMDBClient(){}

	public JSONObject get(String resourcePath) {
		JSONObject response=null;
		String url = baseUrl+resourcePath+apiKey;
		try {
			response = readJsonFromUrl(url);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public JSONObject get(String resourcePath,String request2Append) {
		JSONObject response=null;
		
		String url = baseUrl+resourcePath+apiKey+"&append_to_response="+request2Append;
		try {
			response = readJsonFromUrl(url);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return response;
	} 

	
	public JSONArray getJSONArray(String resourcePath) {
		JSONArray response=null;
		String url = baseUrl+resourcePath+apiKey;
		try {
			response = readJsonArrayFromUrl(url);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return response;
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
	
	
	
	public JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String jsonText = readAll(rd);
			JSONArray json= new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}



}
