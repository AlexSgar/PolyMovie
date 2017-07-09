package it.uniroma3.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
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
	private MongoCollection<Document> movies;
	private MongoCollection<Document> moviesKeywords;
	
	public MongoDBAdapter (){
		this.mongoClient = new MongoClient();  //use default ("localhost" , 27017)
		this.movieAdapter = new MovieAdapter();
		this.mongoDatabase = this.mongoClient.getDatabase("tmdb");
	}
	
	public static void main(String[] args) throws IOException{
		
		MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();
		//mongoDBAdapter.deleteCollection("movies");
		//mongoDBAdapter.deleteCollection("movieKeywords");
		//mongoDBAdapter.populateMoviesAndMoviesKeywords();
		//mongoDBAdapter.printElements();
		
	}
	
	public void populateMoviesAndMoviesKeywords() throws IOException{
		
		long startTime = System.currentTimeMillis();
		System.out.println("started at: "+new Date(startTime));
		
		int maxEntriesToInsert = 50000;
		int nOfCurrentGoodRequest = 0;
		int nOfInsertedEntries = 0;
		int batchSize = 19;
		int nOfLinesToSkip = 0;
		
		BufferedReader br = new BufferedReader(new FileReader("ml-latest/links_clear.csv"));
		this.movies = this.mongoDatabase.getCollection("movies");
		this.moviesKeywords = this.mongoDatabase.getCollection("movieKeywords");
		
		List<Document> moviesToAdd = new LinkedList<Document>();
		List<Document> moviesKeywordsToAdd = new LinkedList<Document>();
		JSONObject movieJson = null;
		String movieId=null;
		String currentLine = "";
		int currentLineNumber=0;
		
		while(nOfLinesToSkip >0){
			br.readLine();
			currentLineNumber++;
			nOfLinesToSkip--;
		}
		
		while((currentLine = br.readLine()) != null){
			try{
				currentLineNumber++;
				movieId = currentLine.split(",")[2];
				movieJson = this.movieAdapter.getMovieAndKeywords(movieId);
				//System.out.println("movie id: "+ movieJson.getString("id")+" title:"+ movieJson.getString("title"));
				
				if(movieJson!=null){
					
					//add movieKeywords to moviesKeywordsToAdd collection
					moviesKeywordsToAdd.add(Document.parse(movieJson.getJSONObject("keywords").toString())
													.append("id_movie", Integer.parseInt(movieId)));
					
					//add movie to moviesToAdd collection
					movieJson.remove("id");
					movieJson.remove("keywords");
					moviesToAdd.add(Document.parse(movieJson.toString())
											.append("_id", Integer.parseInt(movieId)));
					nOfCurrentGoodRequest++;
					if(nOfCurrentGoodRequest == batchSize){
						
						insertMovies(moviesToAdd);
						moviesToAdd = new LinkedList<Document>();
						insertMoviesKeywords(moviesKeywordsToAdd);
						moviesKeywordsToAdd = new LinkedList<Document>();
						
						nOfInsertedEntries+=nOfCurrentGoodRequest;
						nOfCurrentGoodRequest=0;
						System.out.println("Insert "+batchSize+" entries,total " + nOfInsertedEntries);
					}
					
					if (currentLineNumber == maxEntriesToInsert){
						break;
					}
				}
				else{
					System.out.println("Error TMDBClient movie doesn't exists,id: "+ movieId);
				}
			}
			catch(ArrayIndexOutOfBoundsException e ){
				e.printStackTrace();
				System.out.println("Failed split file line at "+currentLineNumber);
				System.out.println("currentLine: "+currentLine);
			}
			catch (JSONException e) { 
				e.printStackTrace();
				System.out.println("Failed parse movieJson at line"+ currentLineNumber);
				System.out.println("movieId: "+movieId);
				System.out.println("movieJson: "+movieJson);
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("Exception at movieJson,id: "+ movieId);
			}
		}
		
		if(nOfCurrentGoodRequest>0){
			insertMovies(moviesToAdd);
			insertMoviesKeywords(moviesKeywordsToAdd);
			nOfInsertedEntries+=nOfCurrentGoodRequest;
			System.out.println("Insert "+nOfCurrentGoodRequest+" entries,total " + nOfInsertedEntries);
		}
		
		br.close();
		
		long endTime = System.currentTimeMillis();
		System.out.println("end time: "+new Date(endTime));
		System.out.println("ended in: "+((endTime-startTime)/1000)/60.0+" minutes");
		
	}
	
	private void insertMovies(List<Document> moviesToAdd){
		movies.insertMany(moviesToAdd);
	}
	
	private void insertMoviesKeywords(List<Document> moviesKeywordsToAdd){
		moviesKeywords.insertMany(moviesKeywordsToAdd);
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
	
	public void deleteCollection(String collectionName){
		MongoCollection<Document> movies = this.mongoDatabase.getCollection(collectionName);
		movies.deleteMany(new Document());
	}
}