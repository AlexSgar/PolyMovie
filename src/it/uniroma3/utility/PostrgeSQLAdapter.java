package it.uniroma3.utility;

import it.uniroma3.populater.PostgressPopulater;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONException;
import org.postgresql.util.PSQLException;

public class PostrgeSQLAdapter {


	public static void main(String[] args) throws SQLException, IOException, JSONException{
		String url = "jdbc:postgresql://localhost/";
		String dbName="moviedb";
		PostgressPopulater populater = new PostgressPopulater(url, dbName);


		System.out.println("Creazione Database e tabelle in corso...");
		try{
			buildDataBase(url, dbName);//diventera una classe che crea il DB
		}
		catch(PSQLException p){System.out.println("database gia esistente");}
	
		//populater.populateActorsPart();
		//populater.populateDB();
		populater.populateTVPart();


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
