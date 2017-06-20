package it.uniroma3.utility;
import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.model.Movie;

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

public class Neo4JAdapter {


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
			JSONArray related = mvAd.getMovieReccomandations(idToAsk);
			JSONArray reviews = mvAd.getMovieReview(idToAsk);

			session.run( "MERGE (a:Movie {id: {id}, title: {title}, original_title: {original},"
					+ " original_language: {original_language}})",
					parameters( "id", currMovie.getId(), "title",
							currMovie.getTitle(),"original",currMovie.getOriginal_title(),"original_language",
							currMovie.getOriginal_language()));

			for(int j=0; j<reviews.length(); j++){
				JSONObject currRev = reviews.getJSONObject(j);
				String author = currRev.getString("author");
				String idReview = currRev.getString("id");
				String reviewContent = currRev.getString("content");
				session.run( "MATCH (a:Movie) WHERE a.id={id} MERGE (u:User {nickname: {name}}) "
						+ " CREATE (r:Review {id: {idR}, content: {content}}),"
						+ "(a)-[:HASREVIEW]->(r), (r)-[:WRITTENBY]->(u)",
						parameters( "name", author,"idR", idReview,"content",reviewContent,"id",idToAsk));
			}

			
			for(int j=0; j<related.length() && j<5; j++){
				Movie movieRelated= new Movie(related.getJSONObject(j));
				session.run( "MATCH (a:Movie) WHERE a.id={idA}"
						+ " MERGE (m:Movie {id: {id}, title: {title}, original_title: {original},original_language: {original_language}})"
						+ "CREATE (a)-[:RELATED]->(m)",
						parameters("idA",idToAsk,"id", movieRelated.getId(), "title",
								movieRelated.getTitle(),"original",movieRelated.getOriginal_title(),"original_language",
								movieRelated.getOriginal_language()));
			}

			
			if(i==1000){
				break;
			}
			}catch(Exception e){System.out.println("malformattato riga i: "+i);}
		}




		session.close();
		lines.close();
		driver.close();

	}

}
