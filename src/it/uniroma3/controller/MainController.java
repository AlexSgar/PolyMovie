package it.uniroma3.controller;

import it.uniroma3.facade.MovieFacade;
import it.uniroma3.facade.TVShowFacade;
import it.uniroma3.model.Movie;
import it.uniroma3.model.TV;
import it.uniroma3.postgres.PostgresRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MainController {
	private  List<String> actorsRetrieved;
	private  List<String> movieRetrived;
	private  List<String> tvShowRetrived;
	private  List<String> movieList;
	private PostgresRepository postgres;
	private String dbUrl="jdbc:postgresql://localhost/moviedb";
	private MovieFacade movieFacade;
	private TVShowFacade tvFacade;


	public MainController() {
		this.postgres=new PostgresRepository(dbUrl);
		movieFacade= new MovieFacade();
		tvFacade= new TVShowFacade();
		

	}


	@RequestMapping(value="movie",method = RequestMethod.GET)
	public String getMovies(Model model) throws SQLException, JSONException {
		List<Movie> retrieveMovie = movieFacade.retrieveMovie();
		model.addAttribute("message", "film trovati: "+retrieveMovie.size());
		model.addAttribute("movies",retrieveMovie);
		return  "movie-list";
	}



	@RequestMapping(value="actor",method = RequestMethod.GET)
	public String getActors(Model model) throws SQLException {
		this.actorsRetrieved=postgres.retrieveActorsID();
		int size = this.actorsRetrieved.size();
		model.addAttribute("actors",new LinkedList<String>(this.actorsRetrieved).subList(0, 100));
		return  "actor-list";
	}



	@RequestMapping(value="tv", method = RequestMethod.GET)
	public String getTVShows(Model model) throws SQLException {
		List<TV> tvShowRetrived= tvFacade.retrieveTvShow();
		model.addAttribute("tvShowList",tvShowRetrived);
		return  "tvshow-list";
	}



	@RequestMapping(value="actor/{id}", method = RequestMethod.GET)
	public String getSingleActor(@PathVariable("id") String id_attore ,Model model) throws SQLException {
		model.addAttribute("tvShow","dafare");
		return  "tvshow-list";
	}



	@RequestMapping(value="movie/{id}/actors",method = RequestMethod.GET)
	public String getActorsInMovies(@PathVariable("id") String id_movie ,Model model) throws SQLException {
		this.actorsRetrieved=postgres.retrieveActors4Movie(id_movie);
		int size = this.actorsRetrieved.size();
		model.addAttribute("actors",new LinkedList<String>(this.actorsRetrieved));
		return  "actor-list";
	}


	@RequestMapping(value="actor/{id}/movies",method = RequestMethod.GET)
	public String getMoviesWithActor(@PathVariable("id") String id_actor ,Model model) throws SQLException, JSONException {
		List<Movie> movieList = movieFacade.retrieveMovies4Actor(id_actor);
		int size = movieList.size();
		model.addAttribute("movies",(movieList));
		return  "movie-list";
	}



	@RequestMapping(value="actor/{id}/tvshows",method = RequestMethod.GET)
	public String getTVShowWithActor(@PathVariable("id") String id_actor ,Model model) throws SQLException {
		this.tvShowRetrived=postgres.retrieveTvShow4Actor(id_actor);
		int size = this.tvShowRetrived.size();
		model.addAttribute("tvShowList",new LinkedList<String>(this.tvShowRetrived));
		return  "tvshow-list";
	}


	@RequestMapping(value="tv/{id}/actors",method = RequestMethod.GET)
	public String getActors4TVShow(@PathVariable("id") String id_serie ,Model model) throws SQLException {
		this.actorsRetrieved=this.postgres.retrieveActors4TV(id_serie);
		int size = this.actorsRetrieved.size();
		model.addAttribute("actors",new LinkedList<String>(this.actorsRetrieved));
		return  "actor-list";
	}


}
