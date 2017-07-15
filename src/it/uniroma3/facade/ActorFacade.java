package it.uniroma3.facade;

import java.sql.SQLException;
import java.util.List;

import it.uniroma3.model.Actor;
import it.uniroma3.postgres.PostgresRepository;

public class ActorFacade {
	private PostgresRepository postgres;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";

	public ActorFacade() {
		this.postgres=new PostgresRepository(dbUrl);
	}

	public List<Actor> retrieveActors() throws SQLException {
		List<Actor> actors= postgres.retrieveActors();
		return actors;
	}

	public List<Actor> retrieveActors4Movie(String id_movie) throws SQLException {
		return postgres.retrieveActors4Movie(id_movie);
	}

	public List<Actor> retrieveActors4TV(String id_serie) throws SQLException {
		return postgres.retrieveActors4TV(id_serie);
	}

}
