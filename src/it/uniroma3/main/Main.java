package it.uniroma3.main;

import it.uniroma3.adapter.MovieAdapter;


public class Main {

	public static void main(String[] args) throws Exception {
		MovieAdapter mvAd= new MovieAdapter();
		String string = mvAd.getMovieDetails("862").toString();
		System.out.println(string);
	}

}