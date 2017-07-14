package it.uniroma3.facade;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import it.uniroma3.model.Actor;
import it.uniroma3.mongodb.MongoDBRepository;
import it.uniroma3.postgres.PostgresRepository;
import it.uniroma3.redis.RedisRepository;

public class ActorFacade {
	private PostgresRepository postgres;
	private RedisRepository redisRepo;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";
	private MongoDBRepository mongoRepo;

	public ActorFacade() {
		this.postgres=new PostgresRepository(dbUrl);
		this.mongoRepo= new  MongoDBRepository();
		this.redisRepo= new RedisRepository();
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
