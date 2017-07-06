package it.uniroma3.populater;

import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.adapter.PeopleAdapter;
import it.uniroma3.adapter.TVAdapter;
import it.uniroma3.utility.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PostgressPopulater {
	private Set<String> actorsRetrieved;
	private Set<String> showsRetrieved;
	private MovieAdapter mvAd;
	private List<Pair<String, JSONArray>> movie2credits;
	private FileReader input;
	private BufferedReader lines ;
	private Connection conn;
	private String dbUrl;
	private PeopleAdapter peAdapter;
	private TVAdapter tvAdapter;


	public PostgressPopulater(String url, String dbName) throws FileNotFoundException, SQLException {
		actorsRetrieved=new HashSet<String>();
		showsRetrieved=new HashSet<String>();
		mvAd= new MovieAdapter();
		peAdapter= new PeopleAdapter();
		tvAdapter= new TVAdapter();
		movie2credits= new LinkedList<Pair<String,JSONArray>>();
		input = new FileReader("ml-latest/links.csv");
		lines = new BufferedReader(input);
		dbUrl=(url+dbName);

	}

	public void populateDB() throws IOException, SQLException {

		conn=DriverManager.getConnection(dbUrl);

		System.out.println("inizio l'aggiunta dei film");
		try {
			populateMovieTables();
		} catch (SQLException e) {
			e.printStackTrace();}

		System.out.println("inizio l'aggiunta degli attori e dei ruoli");
		try {
			populateActorTables();
		} catch (SQLException | JSONException e) {
			e.printStackTrace();}

		System.out.println("inizio l'aggiunta delle serieTv");
		try {
			populateTvShowTables();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		conn.close();



	}


	private void populateMovieTables() throws SQLException, IOException {
		String currentLine="";
		int i=0;

		String q1="INSERT INTO MovieCredits(id_movie,id_credit)"+ "VALUES(?,?)";
		String q2="INSERT INTO Credits(id_credit,id_cast,id_actor,character)"+ "VALUES(?,?,?,?)";
		PreparedStatement ps1 = conn.prepareStatement(q1);
		PreparedStatement ps2 = conn.prepareStatement(q2);
		conn.setAutoCommit(false);

		while((currentLine = lines.readLine())!=null&&i<1000) {
			try{
				i++;
				String[] splitted = currentLine.split(",");
				String idToAsk = splitted[2];	
				JSONArray movieCredits = mvAd.getMovieCredits(idToAsk);
				if(movieCredits!=null){
					Pair<String, JSONArray> pair = new Pair<String, JSONArray>(idToAsk, movieCredits);
					movie2credits.add(pair);
				}

				//List<Pair<String, String>> movieAndCredits= new LinkedList<Pair<String,String>>();
				for(Pair<String, JSONArray> mc :movie2credits){
					String idMovie = mc.getLeft();
					JSONArray credits = mc.getRight();
					for(int k=0; k<credits.length(); k++){
						JSONObject jsonC = (JSONObject)credits.get(k);
						String idCredit = jsonC.getString("credit_id");
						String id_actor = jsonC.getString("id");
						ps1.setString(1,idMovie);
						ps1.setString(2,idCredit);
						ps1.addBatch();

						ps2.setString(1,idCredit);
						ps2.setString(2,jsonC.getString("cast_id"));
						ps2.setString(3,id_actor);
						ps2.setString(4,jsonC.getString("character"));
						ps2.addBatch();
						actorsRetrieved.add(id_actor);
						//movieAndCredits.add(new Pair<String, String>(idMovie, idCredit));
					}

				}
				movie2credits.clear();

				System.out.println("sono ad "+ i);
				System.out.println(idToAsk);
				if(i%1000==0){
					try{
						ps1.executeBatch();
						ps2.executeBatch();
						conn.commit();
					}catch(Exception e){e.printStackTrace();}

				}

			}catch(Exception e){
				e.printStackTrace();
				System.out.println("problemi di malformattazione sulla riga: "+i);
			}
		}
		
		try{
			ps1.executeBatch();
			ps2.executeBatch();
			conn.commit();
		}catch(Exception e){e.printStackTrace();}
		

	}

	private void populateActorTables() throws SQLException, IOException, JSONException{

		String q3="INSERT INTO Actors(id_actor,name,gender,profile_path,popularity,birthday)"+ "VALUES(?,?,?,?,?,?)";
		String q4="INSERT INTO TVRoles(id_actor,id_credit_Tv,id_serie,character,episode_count)"+ "VALUES(?,?,?,?,?)";
		PreparedStatement ps3 = conn.prepareStatement(q3);
		PreparedStatement ps4 = conn.prepareStatement(q4);
		conn.setAutoCommit(false);
		int i =0;

		for(String idActor: actorsRetrieved){
			i++;
			JSONObject actorInfo=peAdapter.getDetails(idActor);
			JSONArray castJSarray = peAdapter.getTVCredits(idActor);

			ps3.setString(1,idActor);
			ps3.setString(2,actorInfo.getString("name"));
			ps3.setInt(3,actorInfo.getInt("gender"));
			ps3.setString(4,actorInfo.getString("profile_path"));
			ps3.setFloat(5,new Float(actorInfo.getDouble("popularity")));
			ps3.setString(6,actorInfo.getString("birthday"));
			ps3.addBatch();

			for(int h=0; h<castJSarray.length(); h++){
				JSONObject casting = (JSONObject) castJSarray.get(h);
				String showId = casting.getString("id");

				ps4.setString(1,idActor);
				ps4.setString(2,casting.getString("credit_id"));
				ps4.setString(3,showId);
				ps4.setString(4,casting.getString("character"));
				ps4.setInt(5,casting.getInt("episode_count"));
				ps4.addBatch();
				showsRetrieved.add(showId);
			}

			if(i%1000==0){
				try{
					ps3.executeBatch();
					ps4.executeBatch();
					conn.commit();
				}catch(Exception e){e.printStackTrace();}	
			}
		}
		try{
			ps3.executeBatch();
			ps4.executeBatch();
			conn.commit();
		}catch(Exception e){e.printStackTrace();}


	}


	private void populateTvShowTables() throws SQLException, JSONException {
		String q5="INSERT INTO "
				+ "TvShow(id_serie,name,original_name,seasons_number,episodes_number,status,original_language,"
				+ "vote_average,popularity,poster_path) "+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps5 = conn.prepareStatement(q5);
		conn.setAutoCommit(false);
		int i =0;
		for(String idShow: showsRetrieved){
			i++;
			JSONObject tvJson = tvAdapter.getDetails(idShow);
			ps5.setString(1,idShow);
			ps5.setString(2,tvJson.getString("name"));
			ps5.setString(3,tvJson.getString("original_name"));
			ps5.setInt(4,tvJson.getInt("number_of_seasons"));
			ps5.setInt(5,tvJson.getInt("number_of_episodes"));
			ps5.setString(6,tvJson.getString("status"));
			ps5.setString(7,tvJson.getString("original_language"));
			ps5.setDouble(8,tvJson.getDouble("vote_average"));
			ps5.setDouble(9,tvJson.getDouble("popularity"));
			ps5.setString(10,tvJson.getString("poster_path"));
			ps5.addBatch();

			if(i%1000==0){
				ps5.executeBatch();
				conn.commit();}

		}
		ps5.executeBatch();
		conn.commit();

	}


}
