package it.uniroma3.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.postgresql.util.PSQLException;

public class PostrgesMain {


	public static void main(String[] args) throws SQLException, IOException, JSONException{
		
		String url = "jdbc:postgresql://localhost/";
		String dbName="moviedb";
		PostgresRepository populater = new PostgresRepository(url, dbName);

		System.out.println("Creazione Database e tabelle in corso...");
		
		
		 try{
			populater.buildDataBase(url, dbName);
		}catch(PSQLException p){
			System.out.println("database gia esistente");
		}
		 
		populater.populateDB();
		

	}


}
