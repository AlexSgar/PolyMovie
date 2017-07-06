package it.uniroma3.utility;

import it.uniroma3.adapter.MovieAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PSQLException;

public class PostrgeSQLAdapter {


	public static void main(String[] args) throws SQLException, IOException{
		String url = "jdbc:postgresql://localhost/";
		String dbName="moviedb";


		System.out.println("Creazione Database e tabelle in corso...");

		try{
			buildDataBase(url, dbName);//diventera una classe che crea il DB
		}
		catch(PSQLException p){System.out.println("database gia esistente");}

		try{
			populateCredits(url, dbName);}// diventera una classe che popola
		catch(PSQLException p){System.out.println("elementi gia inseriti");}

		
		
		
		
		

	}

	public static void populateCredits(String url, String dbName)
			throws FileNotFoundException, SQLException, IOException {
		Set<String> actorsRetrieved=new HashSet<String>();
		MovieAdapter mvAd= new MovieAdapter();
		List<Pair<String, JSONArray>> movie2credits= new LinkedList<Pair<String,JSONArray>>();
		FileReader input = new FileReader("ml-latest/links.csv");
		BufferedReader lines = new BufferedReader(input);
		Connection conn=DriverManager.getConnection(url+dbName);

		String currentLine="";
		int i=0;

		String q1="INSERT INTO MovieCredits(id_movie,id_credit)"+ "VALUES(?,?)";
		String q2="INSERT INTO Credits(id_credit,id_cast,id_actor,character)"+ "VALUES(?,?,?,?)";
		PreparedStatement ps1 = conn.prepareStatement(q1);
		PreparedStatement ps2 = conn.prepareStatement(q2);
		conn.setAutoCommit(false);

		while((currentLine = lines.readLine())!=null) {
			try{i++;
			String[] splitted = currentLine.split(",");
			String idToAsk = splitted[2];	
			JSONArray movieCredits = mvAd.getMovieCredits(idToAsk);
			if(movieCredits!=null){
				Pair<String, JSONArray> pair = new Pair<String, JSONArray>(idToAsk, movieCredits);
				movie2credits.add(pair);
			}

			if(i<5){
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

			}
			System.out.println("sono ad "+ i);
			if(i==5){
				try{
					ps1.executeBatch();
					ps2.executeBatch();
					conn.commit();
				}catch(Exception e){e.printStackTrace();}
				break;
			}}catch(Exception e){
				e.printStackTrace();
				System.out.println("problemi di malformattazione sulla riga: "+i);
			}
		}
		conn.close();
	}

	public static void buildDataBase(String url, String dbName) throws SQLException {

		Connection conn = DriverManager.getConnection(url);
		Statement statementDB = conn.createStatement();
		String query = "CREATE DATABASE "+dbName;
		statementDB.executeUpdate(query);
		conn=DriverManager.getConnection(url+dbName);
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


}
