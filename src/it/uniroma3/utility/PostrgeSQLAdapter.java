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
			populater.buildDataBase(url, dbName);
		}
		catch(PSQLException p){System.out.println("database gia esistente");}
		//populater.populateDB();

		//populater.fixError();
		//populater.populateActorsPart();
		//populater.populateTVPart();


	}


}
