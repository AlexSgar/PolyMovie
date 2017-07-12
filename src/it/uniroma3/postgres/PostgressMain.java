package it.uniroma3.postgres;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.print.DocFlavor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PostgressMain {
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
	final  String username="postgres";
	final  String password="";


	public PostgressMain(String url, String dbName) throws FileNotFoundException, SQLException {
		actorsRetrieved=new HashSet<String>();
		showsRetrieved=new HashSet<String>();
		mvAd= new MovieAdapter();
		peAdapter= new PeopleAdapter();
		tvAdapter= new TVAdapter();
		movie2credits= new LinkedList<Pair<String,JSONArray>>();
		input = new FileReader("ml-latest/links_clear.csv");
		lines = new BufferedReader(input);
		dbUrl=(url+dbName);
	}


	public Connection getConnection(String url) throws SQLException{
		Connection conn = DriverManager.getConnection(url,username,password);
		return conn;
	}


	public void buildDataBase(String url, String dbName) throws SQLException {

		Connection conn = getConnection(url);
		Statement statementDB = conn.createStatement();
		String query = "CREATE DATABASE "+dbName;
		statementDB.executeUpdate(query);
		conn=this.getConnection(url+dbName);
		Statement statement = conn.createStatement();
		query = "create table MovieCredits (id_movie TEXT, id_credit TEXT, PRIMARY KEY (id_movie, id_credit))"; 
		statement.addBatch(query);
		query = "create table Credits (id_credit TEXT, id_cast TEXT, id_actor TEXT, character TEXT, PRIMARY KEY (id_credit, id_cast))"; 
		statement.addBatch(query); 
		query = "create table Actors (id_actor TEXT,name TEXT ,gender INT, profile_path TEXT, popularity FLOAT, birthday TEXT, PRIMARY KEY (id_actor))"; 
		statement.addBatch(query); 
		query = "create table TVRoles (id_actor TEXT,id_credit_Tv TEXT,id_serie TEXT,character TEXT,episode_count INT, PRIMARY KEY (id_actor,id_credit_Tv))"; 
		statement.addBatch(query); 
		query = "create table TvShow (id_serie TEXT,name TEXT, original_name TEXT, seasons_number INT, episodes_number INT,status TEXT, original_language TEXT,vote_average FLOAT, popularity FLOAT, poster_path TEXT, PRIMARY KEY (id_serie))"; 
		statement.addBatch(query); 	
		statement.executeBatch();
		statement.close();
		conn.close();
		System.out.println("Database creato correttamente!");
	}


	/**
	 * popola solamente la parte di database riguardante i film con dei crediti:
	 * @throws SQLException
	 * @throws IOException
	 */
	public void populateMoviePart() throws SQLException, IOException {
		conn=this.getConnection(dbUrl);
		populateMovieTables();
		conn.close();
	}



	/**
	 *  popola solamente la parte di database riguardante gli attori e i loro ruoli:
	 *   ci devono essere nel DB i dati relativi ai film
	 * @throws SQLException
	 */
	public void populateActorsPart() throws SQLException{

		retrieveActorID();
		conn=this.getConnection(dbUrl);
		System.out.println("attori presi"+this.actorsRetrieved.size());

		try {
			populateActorTables();
			
		} catch (SQLException | JSONException | IOException e) {
			e.printStackTrace();}
		conn.close();

	}


	/**
	 * popola solamente la parte di database riguardante le serie tv: 
	 * ci devono essere nel DB i dati relativi agli attori
	 * @throws SQLException
	 * @throws JSONException
	 */
	public void populateTVPart() throws SQLException, JSONException{
		retrieveTVID();
		conn=this.getConnection(dbUrl);
		System.out.println("show TV presi"+this.showsRetrieved.size());
		populateTvShowTables();
		conn.close();

	}


	/**
	 * esegue sequenzialmente la popolazione delle tabelle
	 * dei film, degli attori e delle serie TV.
	 * Viene sfruttata la memoria volatile
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException 
	 */
	public void populateDB() throws IOException, SQLException, JSONException {
		conn=this.getConnection(dbUrl);

		System.out.println("inizio l'aggiunta dei film");
		populateMovieTables();

		System.out.println("inizio l'aggiunta degli attori e dei ruoli");
		populateActorTables();

		System.out.println("inizio l'aggiunta delle serieTv");
		populateTvShowTables();

		conn.close();



	}


	/*
	 * Metodi privati per prendere dal DB gli attori e le serie presenti
	 */

	private void retrieveActorID() throws SQLException {
		conn=this.getConnection(dbUrl);
		Statement stmt = null;
		String query = " select distinct id_actor  from credits order by id_actor";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			this.actorsRetrieved.add(rs.getString("id_actor"));}
		conn.close();
	}

	private void retrieveTVID() throws SQLException {
		conn=this.getConnection(dbUrl);
		Statement stmt = null;
		String query = "select distinct id_serie  from tvroles";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			this.showsRetrieved.add(rs.getString("id_serie"));}
		conn.close();

	}

	/*
	 * Metodi privati per popolare uno ad uno i database:
	 */

	private void populateMovieTables() throws SQLException, IOException {

		String currentLine="";
		int i=0;
		int nullCredit=0;
		List<String> idNulli= new LinkedList<String>();
		String q1="INSERT INTO MovieCredits(id_movie,id_credit)"+ "VALUES(?,?) ON CONFLICT DO NOTHING";
		String q2="INSERT INTO Credits(id_credit,id_cast,id_actor,character)"+ "VALUES(?,?,?,?) ON CONFLICT DO NOTHING";
		PreparedStatement ps1 = conn.prepareStatement(q1);
		PreparedStatement ps2 = conn.prepareStatement(q2);
		conn.setAutoCommit(false);

		while((currentLine = lines.readLine())!=null) {
			try{
				i++;
				String[] splitted = currentLine.split(",");
				String idToAsk = splitted[2];	
				JSONArray movieCredits = mvAd.getMovieCredits(idToAsk);

				if(movieCredits!=null){
					Pair<String, JSONArray> pair = new Pair<String, JSONArray>(idToAsk, movieCredits);
					movie2credits.add(pair);
				}else{
					nullCredit++;
					idNulli.add(idToAsk);
				}

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
					}
				}
				movie2credits.clear();

				System.out.println("riga numero:"+ i);
				//System.out.println(idToAsk);
				if(i%100==0){
					try{
						System.out.println("movie credit scirtti "+ i);
						System.out.println("crediti nulli fin'ora: "+nullCredit);
						System.out.println("id film problematici: "+idNulli.toString());
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
		List<String> errors=new LinkedList<String>();
		List<Integer> tryToinsert=new LinkedList<Integer>();
		String q3="INSERT INTO Actors(id_actor,name,gender,profile_path,popularity,birthday)"+ "VALUES(?,?,?,?,?,?)  ON CONFLICT DO NOTHING";
		String q4="INSERT INTO TVRoles(id_actor,id_credit_Tv,id_serie,character,episode_count)"+ "VALUES(?,?,?,?,?)  ON CONFLICT DO NOTHING";
		PreparedStatement ps3 = conn.prepareStatement(q3);
		PreparedStatement ps4 = conn.prepareStatement(q4);
		conn.setAutoCommit(false);
		int i =0;
		int numberOfActors = actorsRetrieved.size();

		for(String idActor: actorsRetrieved){
			if(i>117099){

				tryToinsert.add(i);
				JSONObject actorInfo=peAdapter.getDetailsAppendedRequest(idActor);

				try{
					JSONArray castJSarray = actorInfo.getJSONObject("tv_credits").getJSONArray("cast");


					ps3.setString(1,idActor);
					ps3.setString(2,actorInfo.getString("name"));
					ps3.setInt(3,actorInfo.getInt("gender"));
					ps3.setString(4,actorInfo.getString("profile_path"));
					ps3.setFloat(5,new Float(actorInfo.getDouble("popularity")));
					ps3.setString(6,actorInfo.getString("birthday"));
					ps3.addBatch();
					System.out.println("sono all'attore i:"+i+" con tot serieTV:"+castJSarray.length());
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

					if(i%100==0){
						try{
							System.out.println("attori e ruoli inseriti su "+numberOfActors+": "+i);
							System.out.println("percentuale completamento: "+(new Double(i)/numberOfActors)*100+" %");
							ps3.executeBatch();
							ps4.executeBatch();
							conn.commit();
							tryToinsert=new LinkedList<Integer>();
						}catch(Exception e){
							e.printStackTrace();
							errors.add(tryToinsert.toString());
							tryToinsert=new LinkedList<Integer>();

						}	
					}
					i++;
				}catch(JSONException j){
					j.printStackTrace();
					System.out.println("problemi con l'attore "+ idActor);

				}	
			}else{
				i++;}
		}
		try{
			System.out.println("attori e serie inseriti "+i);
			ps3.executeBatch();
			ps4.executeBatch();
			conn.commit();
			tryToinsert=new LinkedList<Integer>();
		}catch(Exception e){
			e.printStackTrace();
			errors.add(tryToinsert.toString());
			tryToinsert=new LinkedList<Integer>();}


	}

	/*
	public void fixError() throws SQLException, IOException, JSONException{
		conn= this.getConnection(dbUrl);
		input = new FileReader("ml-latest/movieDaRifare.txt");
		lines = new BufferedReader(input);
		String currentLine="";
		conn.setAutoCommit(false);
		String q2="INSERT INTO Credits(id_credit,id_cast,id_actor,character)"+ "VALUES(?,?,?,?) ON CONFLICT DO NOTHING";
		PreparedStatement ps2 = conn.prepareStatement(q2);
		int i=0;
		while((currentLine = lines.readLine())!=null) {

			JSONArray credits = mvAd.getMovieCredits(currentLine);
			for(int k=0; k<credits.length(); k++){
				JSONObject jsonC = (JSONObject)credits.get(k);
				String castid = jsonC.getString("cast_id");
				if(castid.equals("1011")||castid.equals("1009")){
					String idCredit = jsonC.getString("credit_id");
					String id_actor = jsonC.getString("id");
					ps2.setString(1,idCredit);
					ps2.setString(2,castid);
					ps2.setString(3,id_actor);
					ps2.setString(4,jsonC.getString("character"));
					ps2.addBatch();
					i++;
				}
			}
		}

		ps2.executeBatch();
		conn.commit();
		System.out.println(i);
		conn.close();





	}*/


	private void populateTvShowTables() throws SQLException, JSONException {

		String q5="INSERT INTO "
				+ "TvShow(id_serie,name,original_name,seasons_number,episodes_number,status,original_language,"
				+ "vote_average,popularity,poster_path) "+ "VALUES(?,?,?,?,?,?,?,?,?,?)  ON CONFLICT DO NOTHING";
		PreparedStatement ps5 = conn.prepareStatement(q5);
		conn.setAutoCommit(false);
		int i =0;

		int numberOfShows = showsRetrieved.size();

		for(String idShow: showsRetrieved){

			if(true){

				JSONObject tvJson = tvAdapter.getDetails(idShow);
				try{
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
				}catch(JSONException j){
					System.out.println(tvJson.toString());
					System.out.println("errori alla riga"+ i);
				}
				System.out.println("serie inserite su "+numberOfShows+": "+i);
				if(i%100==0){
					System.out.println("serie inserite su "+numberOfShows+": "+i);
					System.out.println("percentuale completamento: "+(new Double(i)/numberOfShows)*100+" %");
					ps5.executeBatch();
					conn.commit();}
				i++;
			}else
				i++;
		}
		ps5.executeBatch();
		conn.commit();


	}


}
