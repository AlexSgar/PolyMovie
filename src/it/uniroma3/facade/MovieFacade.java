package it.uniroma3.facade;

import it.uniroma3.model.Movie;
import it.uniroma3.model.Review;
import it.uniroma3.mongodb.MongoDBRepository;
import it.uniroma3.neo4j.Neo4JRepository;
import it.uniroma3.postgres.PostgresRepository;
import it.uniroma3.redis.RedisRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieFacade {
	private PostgresRepository postgres;
	private RedisRepository redisRepo;
	private Neo4JRepository neo4Repo;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";
	private MongoDBRepository mongoRepo;

	public MovieFacade() {
		this.postgres=new PostgresRepository(dbUrl);
		this.mongoRepo= new  MongoDBRepository();
		this.redisRepo= new RedisRepository();
		this.neo4Repo= new Neo4JRepository();
	}

	public List<Movie> retrieveMovies() throws SQLException, JSONException {
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
		mv.retrieveMovies();

	}

	public List<Movie> retrieveMovies4Actor(String id_actor) throws SQLException, JSONException {
		
		List<String> movieList= postgres.retrieveMovies4Actor(id_actor);
		List<Movie> retrievedM=new LinkedList<Movie>();
		Document movie = null;
		JSONObject cur = null;
		for(String idMovie: movieList){
			movie = mongoRepo.getMovie(new Integer(idMovie));
			cur=new JSONObject(movie);
			Movie m= new Movie(cur);
			List<String> moviePosters = redisRepo.getMoviePosters(m.getId());
			setKeywords(m);
			if(moviePosters!=null&&moviePosters.size()!=0){
				m.setPoster(moviePosters.get(0));
			}
			retrievedM.add(m);
		}
		
		return orderMoviesListByPopularity(retrievedM);
	}

	public Movie getMovie(String id_movie) throws JSONException {
		Document movie = mongoRepo.getMovie(new Integer(id_movie));
		Movie m=new  Movie(new JSONObject(movie.toJson()));
		List<String> moviePosters = redisRepo.getMoviePosters(m.getId());
		List<String> movieTrailers = redisRepo.getMovieTrailer(m.getId());
		if(movieTrailers.size()!=0){
			m.setTrailer(movieTrailers.get(0));	
		}
		setKeywords(m);
		if(moviePosters!=null&&moviePosters.size()!=0){
			m.setPoster(moviePosters.get(0));
		}
		List<Review> retrieveReview = neo4Repo.retrieveReview(id_movie);
		if(retrieveReview!=null){
			m.setReview(retrieveReview);
		}
		return m;
	}

	public List<Movie> retrieveMoviesRelated(String id_movie) throws JSONException {
		List<String> movieList= neo4Repo.retrieveMovieRelated(id_movie);
		List<Movie> retrievedM=new LinkedList<Movie>();
		for(String idMovie: movieList){
			Document movie = mongoRepo.getMovie(new Integer(idMovie));
			if(movie!=null&&!movie.isEmpty()){
				JSONObject cur=new JSONObject(movie);
				Movie m= new Movie(cur);
				List<String> moviePosters = redisRepo.getMoviePosters(m.getId());
				setKeywords(m);
				if(moviePosters!=null&&moviePosters.size()!=0){
					m.setPoster(moviePosters.get(0));
				}
				retrievedM.add(m);
			}
		}
		return orderMoviesListByPopularity(retrievedM);
	}

	public List<Movie> searchMovieByTitle(String serch) throws JSONException {
		List<Movie> retrievedM=new LinkedList<Movie>();
		Iterable<Document> movies = this.mongoRepo.getMovieByTitle(serch);
	
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
	
	private List<Movie> orderMoviesListByPopularity(List<Movie> movies){
		
		Collections.sort(movies, new Comparator<Movie>(){

			@Override
			public int compare(Movie o1, Movie o2) {
				
				return Double.valueOf(o2.getPopularity()).intValue() -  Double.valueOf(o1.getPopularity()).intValue();
			}
			
		});
		
		return movies;
	}

}







