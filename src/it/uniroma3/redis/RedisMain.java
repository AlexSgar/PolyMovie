package it.uniroma3.redis;

import java.io.IOException;
import java.util.LinkedList;

public class RedisMain {

	public RedisMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException{
		RedisRepository ra = new RedisRepository();
		
		//ra.getMovieTrailer("119450").forEach(e->System.out.println(e));
		//movieImages.forEach(e->System.out.println(e));
		//ra.deleteDatabase();
		//ra.populateMovieImagesAndTrailers();
		//ra.populateActorImages();
	} 
	
	
}
