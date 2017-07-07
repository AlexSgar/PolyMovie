package it.uniroma3.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import it.uniroma3.adapter.MovieAdapter;

public class MongoDBAdapter{
	
	private MongoClient mongoClient;
	private MovieAdapter movieAdapter;
	private MongoDatabase mongoDatabase;
	
	public MongoDBAdapter (){
		super();
		this.mongoClient = new MongoClient();  //use default ("localhost" , 27017)
		this.movieAdapter = new MovieAdapter();
		this.mongoDatabase = this.mongoClient.getDatabase("tmdb");
	}
	
	public static void main(String[] args) throws IOException{
		
		MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();
		mongoDBAdapter.deleteCollection();
		mongoDBAdapter.populateMovies();
		//mongoDBAdapter.populateMovieKeywords();
		//mongoDBAdapter.printElements();
		
	}
	
	public void populateMovies() throws IOException{
	
		int nOfEntryes = 21;
		MongoCollection<Document> movies = this.mongoDatabase.getCollection("movies");
		
		BufferedReader br = new BufferedReader(new FileReader("ml-latest/links.csv"));
		String currentLine = "";
		List<Document> moviesToAdd = new LinkedList<Document>();
		br.readLine();
		int i=0;
		JSONObject movieJson = new JSONObject();
		
		while((currentLine = br.readLine()) != null){
			
			try{
				String movieId = currentLine.split(",")[2];
				
				movieJson = this.movieAdapter.getMovie(movieId);
				System.out.println("movie id: "+ movieJson.getString("id")+" title:"+ movieJson.getString("title"));
				
				moviesToAdd.add(Document.parse(movieJson.toString()));
				i++;
				
				if(moviesToAdd.size() == 20){
					movies.insertMany(moviesToAdd);
					moviesToAdd = new LinkedList<Document>();
					System.out.println("inserisco 20 entryes,totali " + i);
				}
				
				if(nOfEntryes == i){
					break;
				}
			}
			catch(ArrayIndexOutOfBoundsException e ){
				System.out.println("failed parse moviedid at "+i);
				e.printStackTrace();
			} catch (JSONException e) {
				System.out.println("failed parse json movie at "+i);
				System.out.println("json movie: "+movieJson);
				e.printStackTrace();
			}
		}
		
		if(moviesToAdd.size()>0){
			movies.insertMany(moviesToAdd);
			System.out.println("inserisco "+moviesToAdd.size()+" entryes,totali " + i);
		}
		
		br.close();
		
		
	}
	
	public void populateMovieKeywords() throws IOException{
		
		int nOfEntryes = 40;
		MongoCollection<Document> movieKeywords = this.mongoDatabase.getCollection("movieKeywords");
		
		BufferedReader br = new BufferedReader(new FileReader("ml-latest/links.csv"));
		String currentLine = "";
		List<Document> movieKeywordsToAdd = new LinkedList<Document>();
		br.readLine();
		int i=0;
		JSONObject movieKeywordsJson = new JSONObject();
		while((currentLine = br.readLine()) != null){
			
			try{
				String movieId = currentLine.split(",")[2];
				
				movieKeywordsJson = this.movieAdapter.getMovieKeywords(movieId);
				System.out.println("movie id: "+ movieKeywordsJson.getString("id")+" keywords:"+ movieKeywordsJson.getJSONArray("keywords"));
				
				movieKeywordsToAdd.add(Document.parse(movieKeywordsJson.toString()));
				i++;
				
				if(movieKeywordsToAdd.size() == 20){
					movieKeywords.insertMany(movieKeywordsToAdd);
					movieKeywordsToAdd = new LinkedList<Document>();
					System.out.println("inserisco 20 entryes,totali " + i);
				}
				
				if(nOfEntryes == i){
					break;
				}
			}
			catch(ArrayIndexOutOfBoundsException e ){
				System.out.println("failed parse moviedid at "+i);
				e.printStackTrace();
			} catch (JSONException e) {
				System.out.println("failed parse json movieKeywords at "+i);
				System.out.println("json movieKeywords: "+movieKeywordsJson);
				e.printStackTrace();
			}
		}
		
		if(movieKeywordsToAdd.size()>0){
			movieKeywords.insertMany(movieKeywordsToAdd);
			System.out.println("inserisco "+movieKeywordsToAdd.size()+" entryes,totali " + i);
		}
		
		br.close();
		
		
	}
	
	public void printElements(){
		MongoCollection<Document> movies = this.mongoDatabase.getCollection("movies");
		
		System.out.println(movies.count());
		
		MongoCursor<Document> cursor = movies.find().iterator();
		
		try{
			while(cursor.hasNext()){
				System.out.println(cursor.next().toJson());
			}
		}
		finally{
			cursor.close();
		}
	}
	
	public void deleteCollection(){
		MongoCollection<Document> movies = this.mongoDatabase.getCollection("movies");
		movies.deleteMany(new Document());
	}
}
