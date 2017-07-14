package it.uniroma3.facade;

import it.uniroma3.model.*;
import it.uniroma3.mongodb.MongoDBRepository;
import it.uniroma3.postgres.PostgresRepository;
import it.uniroma3.redis.RedisRepository;

import java.sql.SQLException;
import java.util.List;

public class TVShowFacade {
	private PostgresRepository postgres;
	private RedisRepository redisRepo;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";
	private MongoDBRepository mongoRepo;


	public TVShowFacade() {
		this.postgres=new PostgresRepository(dbUrl);
		this.mongoRepo= new  MongoDBRepository();
		this.redisRepo= new RedisRepository();
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
