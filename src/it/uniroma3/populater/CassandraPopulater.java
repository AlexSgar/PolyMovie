package it.uniroma3.populater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma3.adapter.MovieAdapter;
import it.uniroma3.adapter.PeopleAdapter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class CassandraPopulater {
	private Session session;
	private Cluster cluster;
	private  MovieAdapter mvAdapter;
	private PeopleAdapter pplAdapter;
	private FileReader input;
	private BufferedReader lines ;


	public CassandraPopulater() throws FileNotFoundException {
		mvAdapter = new MovieAdapter();
		pplAdapter= new PeopleAdapter();
		input = new FileReader("ml-latest/links_clear.csv");
		lines = new BufferedReader(input);
	}

	public void populateLanguageRelated() throws IOException{
		String currentLine="";
		int i=0;
		session.execute("use movieDB");
		while((currentLine = lines.readLine())!=null&&i<100) {
			/*while( 27616>i){
				currentLine = lines.readLine();
				i++;
			}*/

			try{
				String[] splitted = currentLine.split(",");
				String idToAsk = splitted[2];
				JSONObject movie = mvAdapter.getMovieLanguagesAppendedRequest(idToAsk);
				JSONArray translationAvailable=movie.getJSONObject("translations").getJSONArray("translations");
				JSONArray titlesAvailable=movie.getJSONObject("alternative_titles").getJSONArray("titles");

				insertOfTraslations(idToAsk, translationAvailable);
				insertOfAlternativeTitles(idToAsk, titlesAvailable);

			}catch(Exception e){
				e.printStackTrace();
				System.out.println("problemi alla riga i: "+ i);

			}
			System.out.println("inserisco "+i);
			i++;
		}

	}

	public void insertOfAlternativeTitles(String idToAsk,
			JSONArray titlesAvailable) throws JSONException {
		String queryTitles="INSERT INTO MovieAlternativeTitles (id_Movie, iso_3166, alternative_titles)";
		Map<String,String> iso3166Map=new HashMap<>();
		for(int j=0; j<titlesAvailable.length();j++){
			JSONObject title = (JSONObject) titlesAvailable.get(j);
			String escapedTitle = title.getString("title").replaceAll("'", "''");
			String titles;
			if(iso3166Map.containsKey(title.getString("iso_3166_1"))){
				titles = iso3166Map.get(title.getString("iso_3166_1"));
				titles+=("'"+escapedTitle+"',");
			}else{titles="['"+escapedTitle+"',";}

			iso3166Map.put(title.getString("iso_3166_1"), titles);
		}
		for(String iso:iso3166Map.keySet()){
			String alternatives = iso3166Map.get(iso)+"]";
			alternatives=alternatives.replaceAll(",]", "]");
			String inQuery=queryTitles+" VALUES('"+idToAsk+"','"+iso+"',"+alternatives+")";
			session.execute(inQuery);
		}
	}

	public void insertOfTraslations(String idToAsk,
			JSONArray translationAvailable) throws JSONException {
		String queryTraslation="INSERT INTO MovieTranslations(id_Movie,translation_languages,translation_Json)";
		StringBuilder languagesList= new StringBuilder();
		StringBuilder languageType= new StringBuilder();

		for(int j=0; j<translationAvailable.length();j++){	
			JSONObject lng = (JSONObject) translationAvailable.get(j);
			languagesList.append("'"+lng.getString("english_name")+"',");
			languageType.append("{iso_3166:'"+lng.getString("iso_3166_1")+"',iso_639:'"+lng.getString("iso_639_1")+"',name:'"
					+lng.getString("name")+"',english_name:'"+lng.getString("english_name")+"'},");
		}

		String jsonLanguages="["+languageType.toString()+"]";
		String enNameList="["+languagesList+"]";
		jsonLanguages=jsonLanguages.replaceAll(",]", "]");
		enNameList=enNameList.replaceAll(",]", "]");

		queryTraslation+=" VALUES('"+idToAsk+"',"+enNameList+","+jsonLanguages+")";
		session.execute(queryTraslation);
	}




	public void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).build();
		Metadata metadata = cluster.getMetadata();
		System.out.println("Cassandra connection established");
		System.out.printf("Connected to cluster: %s\n",metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datatacenter: %s; Host: %s; Rack: %s \n",
					host.getDatacenter(), host.getAddress(), host.getRack());
			session = cluster.connect();
		}
	}


	public void close() {
		cluster.close();
	}


	public void createSchema() {
		try{session.execute("CREATE KEYSPACE movieDB WITH replication "
				+ "= {'class':'SimpleStrategy', 'replication_factor':3};");}
		catch(AlreadyExistsException ex){
			System.out.println("keyspace gia creato");
			cluster.connect("movieDB");
		}

		String query1 = "create TYPE translation_info (iso_3166 varchar, iso_639 varchar , "
				+ "name varchar, english_name varchar)" ;
		String query3 = "create TYPE known_for_info (id varchar, media_type varchar);" ;
		String query4= "create COLUMNFAMILY  MovieTranslations (id_Movie varchar primary Key , "
				+ "translation_languages list<varchar>, translation_Json list<frozen<translation_info>>)";
		String query5 = "create COLUMNFAMILY  MovieAlternativeTitles (id_Movie varchar, iso_3166 varchar,"
				+ " alternative_titles list<varchar>,  PRIMARY KEY(id_Movie, iso_3166))" ;
		String query6 = "create COLUMNFAMILY ActorDailyRanking (timestamp varchar, rank int, "
				+ "popularity_score double, id_actor varchar,	name varchar, known_for_json list<frozen<known_for_info>>, "
				+ " PRIMARY KEY(timestamp, rank))";

		try{
			session.execute("use movieDB");
			session.execute(query1);
			session.execute(query3);
			session.execute(query4);
			session.execute(query5);
			session.execute(query6);
		}catch(InvalidQueryException ex){
			System.out.println("columnfamilies gia create");
		}
	}

	public void populateBestActor() {
		LocalDate localDate = LocalDate.now();
		String todayDate = DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate);
		session.execute("use movieDB");
		try {
			JSONArray popularActors = pplAdapter.getPopular();
			String queryActor="INSERT INTO actordailyranking(timestamp,rank,id_actor,known_for_json,name,popularity_score)";
			for(int j=0; j<popularActors.length();j++){
				JSONObject currentActor =(JSONObject)popularActors.get(j);
				JSONArray known4Array = currentActor.getJSONArray("known_for");
				String known4="[";
				for(int k=0; k<known4Array.length();k++ ){
					JSONObject role =(JSONObject)known4Array.get(k);
					known4+="{id:'"+role.getString("id")+"',media_type:'"+role.getString("media_type")+"'},";
				}
				known4+="]";
				known4=known4.replaceAll(",]", "]");
				PreparedStatement ps= session.prepare(queryActor+" VALUES(?,?,?,"+known4+",?,?)");
				BoundStatement bind = ps.bind(todayDate,j+1,currentActor.getString("id"),
						currentActor.getString("name"),currentActor.getDouble("popularity"));
				session.execute(bind);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("errore con il json");
		}



	}



}
