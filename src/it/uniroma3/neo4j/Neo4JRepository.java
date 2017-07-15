package it.uniroma3.neo4j;
import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.model.Review;
import it.uniroma3.utility.Movie;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import  static org.neo4j.driver.v1.Values.parameters;

public class Neo4JRepository {
	private Driver driver;
	private Session session;


	public Neo4JRepository(){
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "root" ) );
		session = driver.session();

	}

	public void populateDB()
			throws FileNotFoundException, IOException {
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

			addReviews(idToAsk, reviews);


			addRelatedMovie(idToAsk, related);


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

	public  void addReviews(String idToAsk,
			JSONArray reviews) throws JSONException {
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
	}

	public  void addRelatedMovie(String idToAsk,
			JSONArray related) throws JSONException {
		for(int j=0; j<related.length() && j<5; j++){
			Movie movieRelated= new Movie(related.getJSONObject(j));
			session.run( "MATCH (a:Movie) WHERE a.id_movie={idA}"
					+ " MERGE (m:Movie {id_movie: {id}})"
					+ "CREATE (a)-[:RELATED]->(m)",
					parameters("idA",idToAsk,"id", movieRelated.getId()));
		}
	}

	public List<Review> retrieveReview(String id_toAsk) {
		StatementResult run = session.run( "MATCH(m:Movie)-[:HASREVIEW]-(r:Review)"
				+ "-[:WRITTENBY]-(u:User) where m.id_movie={idM} return r,u",
				parameters("idM",id_toAsk));

		List<Review> reviews= new LinkedList<Review>();
		int i=1;
		while(run.hasNext()){
			Record record = run.next();
			Node review = (Node) record.asMap().get("r");
			Node user = (Node) record.asMap().get("u");
			String content = review.get("content").asString();
			String id = review.get("id_review").asString();
			String nick = user.get("nick").asString();
			Review r= new Review(id,nick,content);
			r.setCollapse(""+i);
			reviews.add(r);
			i++;
		}

		return reviews;


	}

	public List<String> retrieveMovieRelated(String id_movie) {
		StatementResult run = session.run( "MATCH(m:Movie)-[:RELATED]->(n:Movie) "
				+ "where m.id_movie={idM} return distinct n",
				parameters("idM",id_movie));

		List<String> related= new LinkedList<String>();

		while(run.hasNext()){
			Record record = run.next();
			Node movie = (Node) record.asMap().get("n");
			String id = movie.get("id_movie").asString();
			related.add(id);
		}

		return related;
	}

}
