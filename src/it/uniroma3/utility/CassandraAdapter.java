package it.uniroma3.utility;

import java.io.FileNotFoundException;
import java.io.IOException;

import it.uniroma3.populater.CassandraPopulater;


public class CassandraAdapter {

	public CassandraAdapter() {
	}

	public static void main(String args[]) throws IOException{
		 CassandraPopulater client = new CassandraPopulater();
		    client.connect("127.0.0.1");
		    client.createSchema();
		    //client.populateLanguageRelated();
		    client.populateBestActor();
		    client.close();
	      System.out.println("Data created");
	   }
	
}
