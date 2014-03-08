package br.usp.ltd.twitter.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import twitter4j.Status;

@SuppressWarnings("unused")
public class Utils {

	/**
	 * Returns current date in format yyyy-MM-dd.
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public static String replaceSpecialChars(String string) {
		return string.replace("\"", "'").replace("&", "E").replace(">", "MAIOR").replace("<", "MENOR");
	}

	public static String constructTwitterQuery(String... elements) {
		StringBuffer buffer = new StringBuffer();

		for (String element : elements) {
			buffer.append(upperCase(element));
			buffer.append(" OR ");
			buffer.append(lowerCase(element));
			buffer.append(" OR ");
			buffer.append("#");
			buffer.append(upperCase(element));
			buffer.append(" OR ");
			buffer.append("#");
			buffer.append(lowerCase(element));
			buffer.append(" OR ");
		}

		String string = removeLastOR(buffer.toString());

		System.out.println("Query: " + string);

		return string.toString();
	}

	private static String removeLastOR(String element) {
		StringBuilder sb = new StringBuilder(element);
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	private static String lowerCase(String element) {
		StringBuilder sb = new StringBuilder(element);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));

		return sb.toString();
	}

	private static String upperCase(String element) {
		StringBuilder sb = new StringBuilder(element);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

		return sb.toString();
	}
	
	/**
	 * Sysout for the ArrayList<Status> tweets.
	 * @param tweets
	 */
	private static void printTweets(ArrayList<Status> tweets) {
		for (int i = 0; i < tweets.size(); i++) {
			Status t = (Status) tweets.get(i);

			String user = t.getUser().getScreenName();
			String msg = t.getText();
			if (t.isRetweet()) {
				System.out.println(t.getId() + " @" + user + " - " + msg + "\nRetweet: " + t.isRetweet()
						+ " : ScreenName: " + t.getRetweetedStatus().getUser().getScreenName());
			} else {
				System.out.println(t.getId() + " @" + user + " - " + msg);
			}
			System.out.println();
		}
		System.out.println("Size: " + tweets.size());
	}

}
