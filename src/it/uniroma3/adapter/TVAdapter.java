package it.uniroma3.adapter;

import org.json.JSONObject;

public class TVAdapter extends Adapter {
	private String resourcePath="/tv/";

	public TVAdapter() {
		super();
	}
	
	public JSONObject getDetails(String idTvShow){
		checkRequestRate();
		String url= resourcePath+idTvShow;
		JSONObject tvShowJson = this.client.get(url);
		return tvShowJson;

	}

}
