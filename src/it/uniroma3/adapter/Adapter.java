package it.uniroma3.adapter;

import it.uniroma3.tmdb.TMDBClient;

public abstract class Adapter {
	protected TMDBClient client;
	protected int request;

	public Adapter() {
		this.client= new TMDBClient();
		this.request=0;
	}
	
	
	protected void checkRequestRate() {
		this.request++;
		if(request==20){
			request=0;
			try {
				System.out.println("Pausa per TMDBClient ... ");
				Thread.sleep(3000);
			} catch (InterruptedException e) {e.printStackTrace();}
		}

	}

}
