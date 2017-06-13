package it.uniroma3.main;

import it.uniroma3.utility.MovieDBAccess;

public class Main {

	public static void main(String[] args) throws Exception {
		MovieDBAccess db= new MovieDBAccess();
		db.getMovie("862");
	}

}