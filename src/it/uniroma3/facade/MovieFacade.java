package it.uniroma3.facade;

import it.uniroma3.model.Movie;
import it.uniroma3.mongodb.MongoDBRepository;
import it.uniroma3.postgres.PostgresRepository;
import it.uniroma3.redis.RedisRepository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieFacade {
	private PostgresRepository postgres;
	private RedisRepository redisRepo;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";
	private MongoDBRepository mongoRepo;

	public MovieFacade() {
		this.postgres=new PostgresRepository(dbUrl);
		this.mongoRepo= new  MongoDBRepository();
		this.redisRepo= new RedisRepository();
	}

	public List<Movie> retrieveMovie() throws SQLException, JSONException {
		List<Movie> retrievedM=new LinkedList<Movie>();
		Iterable<Document> movies = this.mongoRepo.getMovies("popularity");
		//Iterable<Document> movies = this.mongoRepo.getMovies("release_date");

		for(Document d: movies){
			JSONObject cur=new JSONObject(d.toJson());
			Movie m= new Movie(cur);
			List<String> moviePosters = redisRepo.getMoviePosters(m.getId());
			setKeywords(m);
			if(moviePosters!=null&&moviePosters.size()!=0){
				m.setPoster(moviePosters.get(0));
			}
			retrievedM.add(m);
		}
		return retrievedM;
	}

	public void setKeywords(Movie m) throws JSONException {
		Document movieKeywords = mongoRepo.getMovieKeywords(new Integer(m.getId()));
		JSONObject jsonKeywords = new JSONObject(movieKeywords.toJson());
		JSONArray keyWordsArray = jsonKeywords.getJSONArray("keywords");
		String toSetKeywords="";
		int size = keyWordsArray.length();
		System.out.println(size);
		for(int i=0; i<size; i++){
			String keyword =((JSONObject) keyWordsArray.get(i)).getString("name");
			if(i!=(size-1)){
				toSetKeywords+=keyword+", ";
			}else{
				toSetKeywords+=keyword;
			}
		}
		m.setKeywords(toSetKeywords);
	}



	public static void main(String[] arg) throws SQLException, JSONException{
		MovieFacade mv= new MovieFacade();
		mv.retrieveMovie();

	}

	public List<Movie> retrieveMovies4Actor(String id_actor) throws SQLException, JSONException {
		List<String> movieList= postgres.retrieveMovies4Actor(id_actor);
		List<Movie> retrievedM=new LinkedList<Movie>();
		for(String idMovie: movieList){
			Document movie = mongoRepo.getMovie(new Integer(idMovie));
			JSONObject cur=new JSONObject(movie);
			Movie m= new Movie(cur);
			List<String> moviePosters = redisRepo.getMoviePosters(m.getId());
			setKeywords(m);
			if(moviePosters!=null&&moviePosters.size()!=0){
				m.setPoster(moviePosters.get(0));
			}
			retrievedM.add(m);
		}
		return retrievedM;
	}

}







