package it.uniroma3.neo4j;
import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.utility.Movie;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import  static org.neo4j.driver.v1.Values.parameters;

public class Neo4JRepository {


	public static void main(String[] args) throws IOException, JSONException, InterruptedException {
		Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "root" ) );
		Session session = driver.session();
		

		MovieAdapter mvAd= new MovieAdapter();
		FileReader input = new FileReader("ml-latest/links.csv");
		BufferedReader lines = new BufferedReader(input);

		String currentLine= null;
		int i=0;
		while((currentLine = lines.readLine())!=null) {
			try{i++;
			String[] splitted = currentLine.split(",");
			String idToAsk = splitted[2];
			Movie currMovie = mvAd.getMovieDetails(idToAsk);
			JSONObject movieAppendedRequest = mvAd.getMovieAppendedRequest(idToAsk);
			JSONArray related =movieAppendedRequest.getJSONObject("recommendations").getJSONArray("results");
			JSONArray reviews = movieAppendedRequest.getJSONObject("reviews").getJSONArray("results");
	
			session.run( "MERGE (a:Movie {id_movie: {id}})",
					parameters( "id", currMovie.getId()));

			for(int j=0; j<reviews.length(); j++){
				JSONObject currRev = reviews.getJSONObject(j);
				String user = currRev.getString("author");
				String idReview = currRev.getString("id");
				String reviewContent = currRev.getString("content");
				session.run( "MATCH (a:Movie) WHERE a.id_movie={id} MERGE (u:User {nick: {name}}) "
						+ " CREATE (r:Review {id_review: {idR}, id_movie:{idM}, content: {content}}),"
						+ "(a)-[:HASREVIEW]->(r), (r)-[:WRITTENBY]->(u)",
						parameters( "name", user,"idR",idReview, "idM",idToAsk,"content",reviewContent,"id",idToAsk));
			}
			
			
			for(int j=0; j<related.length() && j<5; j++){// da togliere il 10
				Movie movieRelated= new Movie(related.getJSONObject(j));
				session.run( "MATCH (a:Movie) WHERE a.id_movie={idA}"
						+ " MERGE (m:Movie {id_movie: {id}})"
						+ "CREATE (a)-[:RELATED]->(m)",
						parameters("idA",idToAsk,"id", movieRelated.getId()));
			}

			
			if(i%300==0){
				System.out.println("inseriti fin'ora: "+i);
				System.out.println("id riga corrente "+ idToAsk);
			}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("malformattato riga i: "+i);}
		}




		session.close();
		lines.close();
		driver.close();

	}

}
