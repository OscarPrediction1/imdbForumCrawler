package de.coins2015.oscar1.imdbforumcrawler;

import java.util.List;

import org.json.simple.JSONObject;

public class Controller {

    public static void main(String[] args) throws Exception {

	String host = args[0];
	String port = args[1];
	String userName = args[2];
	String password = args[3];
	String database = args[4];

	String pageBase = "http://www.imdb.com/board/bd0000005/threads/?p=";
	int pageIndex = 1;

	IMDBForumCrawler imdbfc = new IMDBForumCrawler();

	MongoDBHandler mdbh = new MongoDBHandler(host, port, userName,
		database, password);
	while (imdbfc.pageHasContent(pageBase + pageIndex)) {
	    System.out.println("\nPAGE " + pageIndex + "\n");

	    List<JSONObject> jsonObjects = imdbfc.visitPage(pageBase
		    + pageIndex);

	    for (JSONObject jsonObject : jsonObjects) {
		mdbh.storeData(jsonObject, "imdb_forum");
	    }

	    pageIndex++;
	}

    }
}
