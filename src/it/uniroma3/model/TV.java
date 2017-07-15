package it.uniroma3.model;

import org.json.JSONException;
public class TV {
	private String id;
	private String title;
	private String status;
	private String overview;
	private String poster;
	private String popularity;
	private String originalLang;
	private String voteAvg;
	private String episodes;
	private String seasons;

	public TV() {}


	public TV(String id,String title,String overview,String popularity,String poster) throws JSONException {
		this.id = id;
		this.title = title;
		this.overview = overview;
		this.popularity=popularity;
		if(poster!=null){
			this.poster=poster;
		}
		else
			this.poster="http://i64.tinypic.com/2mx341i.png";
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getOverview() {
		return overview;
	}


	public void setOverview(String overview) {
		this.overview = overview;
	}


	public String getPoster() {
		return poster;
	}


	public void setPoster(String poster) {
		this.poster = poster;
	}


	public String getPopularity() {
		return popularity;
	}


	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}


	public String getOriginalLang() {
		return originalLang;
	}


	public void setOriginalLang(String originalLang) {
		this.originalLang = originalLang;
	}


	public String getVoteAvg() {
		return voteAvg;
	}


	public void setVoteAvg(String voteAvg) {
		this.voteAvg = voteAvg;
	}


	public String getEpisodes() {
		return episodes;
	}


	public void setEpisodes(String episodes) {
		this.episodes = episodes;
	}


	public String getSeasons() {
		return seasons;
	}


	public void setSeasons(String seasons) {
		this.seasons = seasons;
	}
}
