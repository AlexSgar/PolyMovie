package it.uniroma3.cassandra;

import java.io.IOException;


public class CassandraMain {

	public CassandraMain() {
	}

	public static void main(String args[]) throws IOException{
		 CassandraRepository client = new CassandraRepository();
		    client.connect("127.0.0.1");
		    client.createSchema();
		    //client.populateLanguageRelated();
		    //client.populateBestActor();
		    client.close();
	      System.out.println("Data created");
	   }
	
}
