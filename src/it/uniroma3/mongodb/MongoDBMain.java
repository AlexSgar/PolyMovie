package it.uniroma3.mongodb;

import java.io.IOException;

import org.bson.Document;

public class MongoDBMain {

	public MongoDBMain() {
		// TODO Auto-generated constructor stub
	}
	
	

	public static void main(String[] args) throws IOException{

		MongoDBRepository mongoDBAdapter = new MongoDBRepository();

		//mongoDBAdapter.deleteCollection("movies");
		//mongoDBAdapter.deleteCollection("movieKeywords");
		//mongoDBAdapter.populateMoviesAndMoviesKeywords();
		//mongoDBAdapter.printElements();

	}

}
