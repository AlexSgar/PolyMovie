package it.uniroma3.populater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniroma3.adapter.MovieAdapter;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class CassandraPopulater {
	private Session session;
	private Cluster cluster;
	private  MovieAdapter mvAdapter;
	private FileReader input;
	private BufferedReader lines ;


	public CassandraPopulater() throws FileNotFoundException {
		mvAdapter = new MovieAdapter();
		input = new FileReader("ml-latest/links_clear.csv");
		lines = new BufferedReader(input);
	}

	public void populateLanguageRelated() throws IOException{
		String currentLine="";
		int i=0;
		session.execute("use movieDB");
		while((currentLine = lines.readLine())!=null&&i<40000) {
			while( 27616>i){
				currentLine = lines.readLine();
				i++;
			}
			
			
			try{
				String[] splitted = currentLine.split(",");
				String idToAsk = splitted[2];	
				JSONObject movie = mvAdapter.getMovieLanguagesAppendedRequest(idToAsk);
				JSONArray translationAvailable=movie.getJSONObject("translations").getJSONArray("translations");
				JSONArray titlesAvailable=movie.getJSONObject("alternative_titles").getJSONArray("titles");


				String queryTraslation="INSERT INTO MovieTranslations(id_Movie,translation_languages,translation_Json)";
				String jsonLanguages="[";
				String enNameList="[";
				StringBuilder languagesList= new StringBuilder();
				StringBuilder languageType= new StringBuilder();

				for(int j=0; j<translationAvailable.length();j++){	
					JSONObject lng = (JSONObject) translationAvailable.get(j);
					languagesList.append("'"+lng.getString("english_name")+"',");
					languageType.append("{iso_3166:'"+lng.getString("iso_3166_1")+"',iso_639:'"+lng.getString("iso_639_1")+"',name:'"
							+lng.getString("name")+"',english_name:'"+lng.getString("english_name")+"'},");
				}
				jsonLanguages+=languageType.toString()+"]";
				enNameList+=languagesList+"]";
				jsonLanguages=jsonLanguages.replaceAll(",]", "]");
				enNameList=enNameList.replaceAll(",]", "]");
				
				queryTraslation+=" VALUES('"+idToAsk+"',"+enNameList+","+jsonLanguages+")";
				session.execute(queryTraslation);

				String queryTitles="INSERT INTO MovieAlternativeTitles (id_Movie, iso_3166, alternative_titles)";
				Map<String,String> alternativeMap=new HashMap<>();
				for(int j=0; j<titlesAvailable.length();j++){
					JSONObject title = (JSONObject) titlesAvailable.get(j);
					if(alternativeMap.containsKey(title.getString("iso_3166_1"))){
						alternativeMap.get("'"+title.getString("iso_3166_1")+"'")
						.concat("'"+title.getString("title").replaceAll("'", "''")+"',");
					}else{
						alternativeMap.put("'"+title.getString("iso_3166_1")+"'",
								"['"+title.getString("title").replaceAll("'", "''")+"',");
					}
				}
				for(String s:alternativeMap.keySet()){
					String alternatives = alternativeMap.get(s)+"]";
					alternatives=alternatives.replaceAll(",]", "]");
					String inQuery=queryTitles+" VALUES('"+idToAsk+"',"+s+","+alternatives+")";
					session.execute(inQuery);
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("problemi alla riga i: "+ i);

			}
			System.out.println("inserisco "+i);
			i++;
		}



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



}
