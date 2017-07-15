package it.uniroma3.facade;

import it.uniroma3.model.*;
import it.uniroma3.postgres.PostgresRepository;


import java.sql.SQLException;
import java.util.List;

public class TVShowFacade {
	private PostgresRepository postgres;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";


	public TVShowFacade() {
		this.postgres=new PostgresRepository(dbUrl);
	}


	public List<TV> retrieveTvShow() throws SQLException {
		List<TV> tvList = postgres.retrieveTvShow();
		return tvList;
	}


	public List<TV> retrieveTvShow4Actor(String id_actor) throws SQLException {
		List<TV> tvList = postgres.retrieveTvShow4Actor(id_actor);
		return tvList;
	}
	
	
	
	

}
