package it.uniroma3.cassandra;

import java.io.FileNotFoundException;
import java.io.IOException;


public class CassandraRepository {

	public CassandraRepository() {
	}

	public static void main(String args[]) throws IOException{
		 CassandraMain client = new CassandraMain();
		    client.connect("127.0.0.1");
		    client.createSchema();
		    client.populateLanguageRelated();
		    //client.populateBestActor();
		    client.close();
	      System.out.println("Data created");
	   }
	
}
