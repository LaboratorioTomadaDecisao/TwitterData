package br.usp.ltd.twitter.main;

import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import br.usp.ltd.twitter.database.SaveFile;
import br.usp.ltd.twitter.utils.FileType;
import br.usp.ltd.twitter.utils.TweetType;
import br.usp.ltd.twitter.utils.Utils;

/**
 * Hello world!
 * 
 */
public class Main {

	public static void main(String[] args) {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("ZunhRgKf4TAYI2UJMih1g")
				.setOAuthConsumerSecret("oio04s4wsLuM3q4mjWsnvtYQrX6kquWtPOoEEssT4")
				.setOAuthAccessToken("2350219574-C6C7gc7zyGnIegz70TSkpGQ4MDRL5Pt9du1XtfU")
				.setOAuthAccessTokenSecret("mzI7Uh8DBjbdEkkoUuyzHjRPTT1T1RuMTphZvBYGEfqgi");

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		// String query = Utils.constructTwitterQuery("Dilma", "AÃ©cio",
		// "Aecio");
		String query = Utils.constructTwitterQuery("mensalão");
		ArrayList<Status> tweets = new ArrayList<Status>();
		int tweetsNumber = 100;

		retrieveTweets(twitter, tweets, query, tweetsNumber);

		// printTweets(tweets);

		System.out.println("Size: " + tweets.size());

		try {
			SaveFile file = new SaveFile(FileType.GEXF, Utils.replaceSpecialChars(query), tweets);
			file.save(TweetType.Mention);
			file.save(TweetType.Retweet);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 */
	private static void retrieveTweets(Twitter twitter, ArrayList<Status> tweets, String query, int tweetsNumber) {

		Query tQuery;
		int numberOfTweets = tweetsNumber;
		long lastID = Long.MAX_VALUE;

		while (tweets.size() < numberOfTweets) {

			tQuery = new Query(query);
			tQuery.setMaxId(lastID - 1);

			if (numberOfTweets - tweets.size() > 100)
				tQuery.setCount(100);
			else {
				tQuery.setCount(numberOfTweets - tweets.size());
			}

			try {
				QueryResult result;

				do {
					if (tweets.size() >= numberOfTweets) {
						break;
					}

					result = twitter.search(tQuery);
					tweets.addAll(result.getTweets());

					System.out.println("Gathered " + tweets.size() + " tweets");

					for (Status t : tweets) {
						if (t.getId() < lastID) {
							lastID = t.getId();
						}
					}

				} while ((tQuery = result.nextQuery()) != null);

			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

			System.out.println("Nova busca!");

		}
	}
}
