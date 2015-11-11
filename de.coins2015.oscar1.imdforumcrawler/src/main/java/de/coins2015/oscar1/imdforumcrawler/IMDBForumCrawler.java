package de.coins2015.oscar1.imdforumcrawler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IMDBForumCrawler {

    private ArrayList<Integer> visitedThreads;

    public IMDBForumCrawler() {
	visitedThreads = new ArrayList<Integer>();
    }

    public List<JSONObject> visitPage(String url) {
	ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
	try {
	    String baseUrl = "http://www.imdb.com";

	    Document doc = Jsoup.connect(url).timeout(10000)
		    .userAgent("Mozilla").get();

	    Elements threads = doc.getElementsByAttributeValueMatching("class",
		    "thread [odd|even]");

	    for (Element thread : threads) {
		boolean deleted = thread.attr("class").contains("deleted");

		if (!deleted) {
		    String threadUrl = baseUrl
			    + thread.getElementsByAttributeValue("class",
				    "title").get(0).getElementsByTag("a")
				    .get(0).attr("href");
		    String threadId = threadUrl.substring(threadUrl
			    .lastIndexOf("/") + 1);
		    String threadTitle = thread
			    .getElementsByAttributeValue("class", "title")
			    .get(0).getElementsByTag("a").get(0).html();
		    String threadAuthorId = thread
			    .getElementsByAttributeValue("class", "nickname")
			    .get(0).attr("href");
		    threadAuthorId = threadAuthorId.substring(6,
			    threadAuthorId.length() - 1);
		    String threadAuthorName = thread
			    .getElementsByAttributeValue("class", "nickname")
			    .get(0).html();
		    int threadReplies = Integer.parseInt(thread
			    .getElementsByAttributeValue("class", "replies")
			    .get(0).getElementsByTag("a").get(0).html());

		    boolean alreadyVisited = false;

		    for (int i = visitedThreads.size() - 1; i >= 0; i--) {
			if (visitedThreads.get(i).intValue() == Integer
				.parseInt(threadId)) {
			    alreadyVisited = true;
			    break;
			}
		    }

		    if (!alreadyVisited) {
			JSONObject threadJSON = new JSONObject();
			threadJSON.put("_id", threadId);
			threadJSON.put("title", threadTitle);
			threadJSON.put("replies", threadReplies);
			threadJSON.put("authorId", threadAuthorId);
			threadJSON.put("authorName", threadAuthorName);
			threadJSON.put("comments", new JSONArray());

			visitThread(threadJSON, threadUrl, 1);
			jsonObjects.add(threadJSON);
			System.out.println(threadId + " " + threadTitle);
		    }

		    visitedThreads.add(Integer.parseInt(threadId));
		} else {
		    System.out.println("deleted thread");
		}

	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return jsonObjects;
    }

    public void visitThread(JSONObject threadJSON, String url, int page) {
	try {

	    if (pageHasContent(url + "?p=" + page)) {
		Document doc = Jsoup.connect(url + "?p=" + page).timeout(10000)
			.userAgent("Mozilla").get();

		Element container = doc.getElementsByAttributeValueMatching(
			"class", "thread mode-thread").get(0);

		Elements comments = doc.getElementsByAttributeValueMatching(
			"name", "[0-9]{9}");

		for (Element comment : comments) {

		    String commentId = comment.attr("name");

		    Element commentSummary = container
			    .getElementsByAttributeValue("data-comment-id",
				    commentId).get(0);

		    Element commentBody = container.getElementById("comment-"
			    + commentId);

		    boolean deleted = commentSummary.attr("class").contains(
			    "deleted");

		    if (!deleted) {
			String commentTitle = commentSummary
				.getElementsByTag("a").get(0).html();

			String commentPosterId = commentSummary
				.getElementsByTag("a").get(2).attr("href");
			commentPosterId = commentPosterId.substring(6,
				commentPosterId.length() - 1);

			String commentPosterName = commentSummary
				.getElementsByTag("a").get(2).html();

			String commentDateString = "";

			try {
			    commentDateString = commentSummary
				    .getElementsByClass("timestamp").get(0)
				    .getElementsByTag("span").get(0)
				    .attr("title");
			} catch (IndexOutOfBoundsException e) {
			    commentDateString = commentSummary
				    .getElementsByClass("timestamp").get(0)
				    .html().trim();
			}

			DateFormat df = new SimpleDateFormat(
				"EEE MMM dd yyyy kk:mm", Locale.ENGLISH);
			Date date = df.parse(commentDateString);

			long commentTimeStamp = date.getTime();

			String commentContent = commentBody
				.getElementsByClass("body").get(0).html();
			commentContent = Jsoup.parse(commentContent).text();

			JSONObject commentJSON = new JSONObject();

			commentJSON.put("id", commentId);
			commentJSON.put("title", commentTitle);
			commentJSON.put("posterId", commentPosterId);
			commentJSON.put("posterName", commentPosterName);
			commentJSON.put("timestamp", commentTimeStamp);
			commentJSON.put("content", commentContent);

			JSONArray commentsArray = (JSONArray) threadJSON
				.get("comments");
			commentsArray.add(commentJSON);

		    }

		}

		page++;
		visitThread(threadJSON, url, page);
	    }
	} catch (IOException | ParseException e) {
	    e.printStackTrace();
	}

    }

    public boolean pageHasContent(String url) {
	boolean result = true;

	Document doc;
	try {
	    doc = Jsoup.connect(url).timeout(10000).userAgent("Mozilla").get();

	    Element pageContent = doc.getElementById("pagecontent");

	    Elements messageBox = pageContent
		    .getElementsByAttributeValueStarting("class", "message_box");

	    for (Element e : messageBox) {
		for (Element e1 : e.getElementsByTag("h2")) {
		    String test = e1.html();
		    if (e1.html().equals("No messages!")) {
			result = false;
			break;
		    }
		}
	    }
	} catch (HttpStatusException e) {
	    if (e.getStatusCode() == 404) {

		result = false;
		return result;
	    }
	} catch (IOException e) {

	}

	return result;
    }

}
