package it.uniroma3.model;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
	private String id;
	private String title;
	private String overview;
	private String keywords;
	private List<String> keywordTag;
	private String trailer;
	private String poster;
	private String year;
	private String popularity;
	private String voteAvg;
	private List<Review> review;
	public Movie() {}

	public Movie(String id, String name, String description, String poster,
			String year) {
		super();
		this.id = id;
		this.title = name;
		this.overview = description;
		this.poster = poster;
		this.year = year;
	}

	public Movie(JSONObject cur) throws JSONException {
		this.id = cur.getString("_id");
		this.title = cur.getString("title");
		this.overview = cur.getString("overview");
		this.year =cur.getString("release_date");
		this.setKeywords("movie");
		this.popularity=cur.getString("popularity");
		this.setVoteAvg(cur.getString("vote_average"));
		this.poster="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIjBLiqDUUSRDCtHnCMiAuaa1X54cT_Qt7P2pY32gwaoK_ix7R";
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

	public void setTitle(String name) {
		this.title = name;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String description) {
		this.overview = description;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getVoteAvg() {
		return voteAvg;
	}

	public void setVoteAvg(String voteAvg) {
		this.voteAvg = voteAvg;
	}

	public List<String> getKeywordTag() {
		String[] split = keywords.split(",");
		LinkedList<String> tags = new LinkedList<String>();
		for(int i=0; i<split.length; i++){
			tags.add(split[i]);
		}
		return tags;
	}

	public void setKeywordTag(List<String> keywordTag) {
		this.keywordTag = keywordTag;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}
	
	public List<Review> getReview(){
		return this.review;
	}

	public void setReview(List<Review> retrieveReview) {
		this.review= retrieveReview;
		
	}
	
	
	

}
