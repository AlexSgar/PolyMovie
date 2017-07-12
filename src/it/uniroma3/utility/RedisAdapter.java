package it.uniroma3.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.adapter.PeopleAdapter;
import it.uniroma3.model.Image;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisAdapter {

	private MovieAdapter movieAdapter;
	private Jedis jedis;
	private PeopleAdapter peopleAdapter;

	public RedisAdapter(){
		this.movieAdapter = new MovieAdapter();
		this.peopleAdapter = new PeopleAdapter();
		this.jedis = new Jedis();//use default for connection
	}
	
	public static void main(String[] args) throws IOException{
		RedisAdapter ra = new RedisAdapter();
		//ra.deleteDatabase();
		//ra.populateMovieImagesAndTrailers();
		ra.populateActorImages();
	} 
	
	public void populateMovieImagesAndTrailers() throws IOException{
		
		long startTime = System.currentTimeMillis();
		System.out.println("started at: "+new Date(startTime));

		int maxEntriesToInsert = 50000;
		int nOfCurrentGoodRequest = 0;
		int nOfInsertedEntries = 0;
		int nOfMaxImages = 5;
		int nOfLinesToSkip = 0;
		int batchSize = 19;
		
		BufferedReader br = new BufferedReader(new FileReader("ml-latest/links_clear.csv"));
		String baseImageUrl= "https://image.tmdb.org/t/p/original";
		String baseTrailerUrl = "https://www.youtube.com/watch?v=";
		JSONObject movieJson = null;
		String movieId=null;
		String currentLine = "";
		int currentLineNumber=0;

		while(nOfLinesToSkip >0){
			br.readLine();
			currentLineNumber++;
			nOfLinesToSkip--;
		}
		
		Map<String,String> movieImageToAdd = new HashMap<>();
		JSONObject movieImages;
		JSONArray movieBackdrops;
		JSONArray moviePosters;
		JSONObject currentBackdrop;
		JSONObject currentPoster;
		Map<String,String> movieTrailersToAdd = new HashMap<>();
		JSONArray movieVideos;
		JSONObject currentVideo;
		Pipeline p = this.jedis.pipelined();

		try{
			while((currentLine = br.readLine()) != null){

				currentLineNumber++;
				movieId = currentLine.split(",")[2];
				movieJson = this.movieAdapter.getMovieAppendImagesAndVideos(movieId);
				//System.out.println("movie id: "+ movieJson.getString("id")+" title:"+ movieJson.getString("title"));

				if(movieJson!=null){
					
					nOfCurrentGoodRequest++;
					movieImages = movieJson.getJSONObject("images");
					movieBackdrops= movieImages.getJSONArray("backdrops");
					moviePosters = movieImages.getJSONArray("posters");
					movieVideos = (movieJson.getJSONObject("videos")).getJSONArray("results");
					
					for(int i=0;i<nOfMaxImages;i++){
						try{
							currentBackdrop = (JSONObject)movieBackdrops.get(i);
							movieImageToAdd.put("backdrop#" + i,baseImageUrl + currentBackdrop.getString("file_path"));
						}
						catch(JSONException e ){
							//e.printStackTrace();
						}
						try{
							currentPoster = (JSONObject)moviePosters.get(i);
							movieImageToAdd.put("poster#" + i, baseImageUrl + currentPoster.getString("file_path"));
						}
						catch(JSONException e ){
							//e.printStackTrace();
						}
						
					}
					if(movieImageToAdd.size()>0){
						p.hmset("movieImages:" + movieId, movieImageToAdd);
						movieImageToAdd.clear();
					}
					
					for(int j=0;j<movieVideos.length();j++){
						currentVideo = (JSONObject)movieVideos.get(j);
						if(currentVideo.getString("site").toLowerCase().equals("youtube") && currentVideo.getString("type").toLowerCase().equals("trailer")){
							movieTrailersToAdd.put("name#" + j,currentVideo.getString("name"));
							movieTrailersToAdd.put("link#"  + j,baseTrailerUrl + currentVideo.getString("key"));
						}
						
					}
					if(movieTrailersToAdd.size()>0){
						p.hmset("movieTrailers:" + movieId, movieTrailersToAdd);
						movieTrailersToAdd.clear();;
					}
					
					if(nOfCurrentGoodRequest == batchSize){
						p.sync();
						nOfInsertedEntries+=nOfCurrentGoodRequest;
						nOfCurrentGoodRequest=0;
						movieImageToAdd.clear();
						movieTrailersToAdd.clear();
						System.out.println("Insert entries,total " + nOfInsertedEntries);
					}
					
					if (currentLineNumber == maxEntriesToInsert){
						break;
					}
				}
				else{
					System.out.println("Error TMDBClient: movie doesn't exists,id: "+ movieId);
				}
			}
			if(movieImageToAdd.size()>0){
				p.sync();
				nOfInsertedEntries+=nOfCurrentGoodRequest;
				movieImageToAdd.clear();
				movieTrailersToAdd.clear();
				System.out.println("Insert entries,total " + nOfInsertedEntries);
			}
		}
		catch(ArrayIndexOutOfBoundsException e ){
			System.out.println("Failed split file line at "+currentLineNumber);
			System.out.println("currentLine: "+currentLine);
			e.printStackTrace();
		}
		catch (JSONException e) { 
			System.out.println("Failed parse movieJson at line"+ currentLineNumber);
			System.out.println("movieId: "+movieId);
			System.out.println("movieJson: "+movieJson);
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Exception at movieJson,id: "+ movieId);
			e.printStackTrace();
		}
		finally{
			br.close();
			this.jedis.close();//maybe not close cause close only in single mode
			System.out.println("Read "+currentLineNumber+" lines from file");
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("end time: "+new Date(endTime));
		System.out.println("ended in: "+((endTime-startTime)/1000)/60.0+" minutes");
	}
	
	public void populateActorImages() throws IOException{
		
		long startTime = System.currentTimeMillis();
		System.out.println("started at: "+new Date(startTime));

		int maxEntriesToInsert = 200000;
		int nOfCurrentGoodRequest = 0;
		int nOfInsertedEntries = 0;
		int nOfMaxImages = 5;
		int nOfLinesToSkip = 0;
		int batchSize = 19;
		
		BufferedReader br = new BufferedReader(new FileReader("ml-latest/lista_actors.txt"));
		String baseImageUrl= "https://image.tmdb.org/t/p/original";
		JSONObject actorJson = null;
		String actorId=null;
		String currentLine = "";
		int currentLineNumber=0;

		while(nOfLinesToSkip >0){
			br.readLine();
			currentLineNumber++;
			nOfLinesToSkip--;
		}
		
		Map<String,String> actorImagesToAdd = new HashMap<>();
		JSONArray actorImages;
		JSONObject currentImage = null;
		TreeSet<Image> actorImagesOrdered = new TreeSet<Image>();
		int nOfActorWithNoImages = 0;
		Pipeline p = this.jedis.pipelined();

		try{
			while((currentLine = br.readLine()) != null){

				currentLineNumber++;
				actorId = currentLine;//note not ,,
				actorJson = this.peopleAdapter.getImages(actorId);
				//System.out.println("movie id: "+ movieJson.getString("id")+" title:"+ movieJson.getString("title"));

				if(actorJson!=null){
			
					nOfCurrentGoodRequest++;
					actorImages = actorJson.getJSONArray("profiles");
					
					if(actorImages.length()==0){
						nOfActorWithNoImages++;
					}
					//order actor images by score
					for(int i=0;i<actorImages.length();i++){
						currentImage = (JSONObject)actorImages.get(i);
						actorImagesOrdered.add(new Image(currentImage.getDouble("vote_average"), currentImage.getString("file_path")));
					}
					//take best 5 actor images
					int nOfActorImages = 1;
					for(Image img: actorImagesOrdered){
						
						if(nOfActorImages <= nOfMaxImages){
							actorImagesToAdd.put("image#" + nOfActorImages , baseImageUrl + img.getImagePath());
							nOfActorImages++;
						}
						else
							break;
						
					}
					actorImagesOrdered.clear();
					
					if(actorImagesToAdd.size()>0){
						p.hmset("actorImages:" + actorId , actorImagesToAdd);
						actorImagesToAdd.clear();
					}
					
					if(currentLineNumber % 100 ==0){
						System.out.println("Current line: "+currentLineNumber);
					}
					
					if(nOfCurrentGoodRequest == batchSize){
						p.sync();
						nOfInsertedEntries+=nOfCurrentGoodRequest;
						nOfCurrentGoodRequest=0;
						actorImagesToAdd.clear();
						System.out.println("Insert entries,total " + nOfInsertedEntries);
						System.out.println("Actor with no images " + nOfActorWithNoImages);
					}
					
					if (currentLineNumber == maxEntriesToInsert){
						break;
					}
					
				}
				else{
					System.out.println("Error TMDBClient: actor doesn't exists,id: "+ actorId);
				}
			}
			
			if(actorImagesToAdd.size()>0){
				p.sync();
				nOfInsertedEntries+=nOfCurrentGoodRequest;
				actorImagesToAdd.clear();
				System.out.println("Insert entries,total " + nOfInsertedEntries);
			}
			System.out.println("Actor with no images " + nOfActorWithNoImages);
		}
		catch(ArrayIndexOutOfBoundsException e ){
			System.out.println("Failed split file line at "+currentLineNumber);
			System.out.println("currentLine: "+currentLine);
			e.printStackTrace();
		}
		catch (JSONException e) { 
			System.out.println("Failed parse movieJson at line"+ currentLineNumber);
			System.out.println("actorId: "+actorId);
			System.out.println("actorJson: "+actorJson);
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Exception at actorJson,id: "+ actorId);
			e.printStackTrace();
		}
		finally{
			br.close();
			this.jedis.close();//maybe not close cause close only in single mode
			System.out.println("Read "+currentLineNumber+" lines from file");
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("end time: "+new Date(endTime));
		System.out.println("ended in: "+((endTime-startTime)/1000)/60.0+" minutes");
	}
	
	private void deleteDatabase(){
		this.jedis.flushDB();
	}
}