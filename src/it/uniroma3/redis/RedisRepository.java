package it.uniroma3.redis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.adapter.PeopleAdapter;
import it.uniroma3.utility.Image;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisRepository {

	private MovieAdapter movieAdapter;
	private Jedis jedis;
	private PeopleAdapter peopleAdapter;

	public RedisRepository(){
		this.movieAdapter = new MovieAdapter();
		this.peopleAdapter = new PeopleAdapter();
		this.jedis = new Jedis();//use default for connection
	}

	public List<String> getMoviePosters(String id_movie){
		List<String> posters = this.jedis.hgetAll("movieImages:"+id_movie)
				.entrySet()
				.stream()
				.filter(e->e.getKey().contains("poster"))
				.map(e->e.getValue())
				.collect(Collectors.toList());
		return posters;

	}

	public List<String> getMovieTrailer(String id) {
		List<String> trailers=new LinkedList<String>();
		Map<String, String> hgetAll = this.jedis.hgetAll("movieTrailers:"+id);
		List<String> toTake=new LinkedList<String>();

		for(Entry<String, String> e:hgetAll.entrySet()){
			if(e.getValue().contains("Official Trailer")||e.getValue().contains("Official US Trailer")){
				toTake.add(e.getKey());
			}
		}

		for(String s: toTake){
			trailers.add(hgetAll.get("link"+s.substring(s.indexOf("#"))));
		}
		return trailers;
	}

	public List<String> getMovieBackdrops(String id_movie){
		List<String> backs = this.jedis.hgetAll("movieImages:"+id_movie)
				.entrySet()
				.stream()
				.filter(e->e.getKey().contains("backdrop"))
				.map(e->e.getValue())
				.collect(Collectors.toList());
		return backs;

	}

	public List<String> getActorImages(String id_actor) throws IOException {

		return (List<String>)this.jedis.hgetAll("actorImages:"+id_actor).values();

	}

	public void populateMovieImagesAndTrailers() throws IOException{

		long startTime = System.currentTimeMillis();
		System.out.println("started at: "+new Date(startTime));

		int maxEntriesToInsert = 50000;//max entries in link.csv is ~40K
		int nOfCurrentGoodRequest = 0;
		int nOfInsertedEntries = 0;
		int nOfMaxImages = 5;//n of max images stored for each movie image type (poster|backdrop)
		int nOfLinesToSkip = 0;//n of line to skip if some during exception goes wrong
		int batchSize = 19;//n of item for batch insert,19 cause of API requests limitations

		BufferedReader br = new BufferedReader(new FileReader("ml-latest/links_clear.csv"));
		String baseImageUrl= "https://image.tmdb.org/t/p/original";
		String baseTrailerUrl = "https://www.youtube.com/watch?v=";
		JSONObject movieJson = null;
		String movieId=null;
		String currentLine = "";
		int currentLineNumber=0;

		//code for skipping some lines
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

					//store best 5 images per each movie image type (images were ordered by API)
					for(int i=0;i<nOfMaxImages;i++){
						try{
							currentBackdrop = (JSONObject)movieBackdrops.get(i);
							movieImageToAdd.put("backdrop#" + (i+1), baseImageUrl + currentBackdrop.getString("file_path"));
						}
						catch(JSONException e ){
							//e.printStackTrace();
						}
						try{
							currentPoster = (JSONObject)moviePosters.get(i);
							movieImageToAdd.put("poster#" + (i+1), baseImageUrl + currentPoster.getString("file_path"));
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
							movieTrailersToAdd.put("name#" + (j+1), currentVideo.getString("name"));
							movieTrailersToAdd.put("link#"  + (j+1), baseTrailerUrl + currentVideo.getString("key"));
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
			if(movieImageToAdd.size()>0 || movieTrailersToAdd.size()>0){
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

		int maxEntriesToInsert = 200000;//max entries in lista_actors.txt is ~180K
		int nOfCurrentGoodRequest = 0;
		int nOfInsertedEntries = 0;
		int nOfMaxImages = 5;//n of max images stored for each actor
		int nOfLinesToSkip = 0;//n of line to skip if some during exception goes wrong
		int batchSize = 19; //n of item for batch insert,19 cause of API requests limitations

		BufferedReader br = new BufferedReader(new FileReader("ml-latest/lista_actors.txt"));
		String baseImageUrl= "https://image.tmdb.org/t/p/original";
		JSONObject actorJson = null;
		String actorId=null;
		String currentLine = "";
		int currentLineNumber=0;

		//code for skipping some lines
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

					if(nOfCurrentGoodRequest == batchSize){
						p.sync();
						nOfInsertedEntries+=nOfCurrentGoodRequest;
						nOfCurrentGoodRequest=0;
						actorImagesToAdd.clear();
						System.out.println("Insert entries,total " + nOfInsertedEntries);
						System.out.println("Actor with no images " + nOfActorWithNoImages);
					}

					if(currentLineNumber % 100 ==0){
						System.out.println("Current line: "+currentLineNumber +", current actorId: " + actorId);
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
