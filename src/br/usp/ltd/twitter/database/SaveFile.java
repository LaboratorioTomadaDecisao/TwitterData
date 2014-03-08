package br.usp.ltd.twitter.database;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.UserMentionEntity;
import br.usp.ltd.twitter.utils.FileType;
import br.usp.ltd.twitter.utils.TweetType;
import br.usp.ltd.twitter.utils.Utils;

public class SaveFile {

	private FileType fileType;
	private String title;
	private ArrayList<Status> tweets;
	private int i = 0;

	public SaveFile(FileType gexf, String title, ArrayList<Status> tweets) {
		fileType = gexf;
		this.title = title;
		this.tweets = tweets;
	}

	public void save(TweetType tweetType) throws Exception {

		if (fileType.equals(FileType.GEXF)) {
			saveGexf(tweetType);
		}
	}

	private void saveGexf(TweetType tweetType) throws Exception {
		FileOutputStream fileWriter = new java.io.FileOutputStream(title + " - " + tweetType.name() + " - "
				+ tweets.size() + FileType.GEXF.getExtension());
		PrintStream printStream = new java.io.PrintStream(fileWriter);

		createHeader(printStream);

		printStream.println("	<graph defaultedgetype=\"undirected\">");

		createAttributes(printStream);

		/*
		 * Preenchendo os campos node do .gml.
		 * 
		 * Não estou verificando se há ID repetido.
		 */
		createNodes(printStream, tweetType);

		createEdges(printStream, tweetType);

		printStream.println("	</graph>");
		printStream.println("</gexf>");

		printStream.close();
		fileWriter.close();

	}

	private void createEdges(PrintStream printStream, TweetType tweetType) {

		printStream.println("		<edges>");

		for (Status status : tweets) {
			UserMentionEntity[] mentions = status.getUserMentionEntities();

			if (hasMentions(mentions)) {
			} else {
				continue;
			}

			for (UserMentionEntity mention : mentions) {
				if (tweetType.equals(TweetType.Retweet)) {
					createRetweetEdges(status, printStream);
				}
				if (tweetType.equals(TweetType.Mention)) {
					createMentionEdges(status, mention, printStream);
				}
			}
		}

		printStream.println("		</edges>");

	}

	private void createMentionEdges(Status status, UserMentionEntity mention, PrintStream printStream) {
		printStream.println(" 	<edge id=\"" + i++ + "\" source=\"" + status.getUser().getId() + "\" target=\""
				+ mention.getId() + "\"/>");
	}

	/**
	 * @param mention
	 * @param status2
	 * @param printStream
	 */
	private void createRetweetEdges(Status status, PrintStream printStream) {
		if (status.isRetweet()) {
			printStream.println(" 	<edge id=\"" + i++ + "\" source=\"" + status.getUser().getId() + "\" target=\""
					+ status.getRetweetedStatus().getUser().getId() + "\"/>");
		}
	}

	/**
	 * @param tweets
	 * @param printStream
	 * @param tweetType
	 */
	private void createNodes(PrintStream printStream, TweetType tweetType) {
		printStream.println("		<nodes>");
		for (Status status : tweets) {
			UserMentionEntity[] mentions = status.getUserMentionEntities();
			/*
			 * Verifica se não há menção alguma, tanto de retweet como de "@".
			 */
			if (hasMentions(mentions)) {
			} else {
				continue;
			}

			if (tweetType.equals(TweetType.Retweet)) {
				createRetweetNode(printStream, status);
			}

			if (tweetType.equals(TweetType.Mention)) {
				createMentionedNode(printStream, status);
			}

		}
		printStream.println("		</nodes>");
	}

	/**
	 * @param mentions
	 * @return
	 */
	private boolean hasMentions(UserMentionEntity[] mentions) {
		return mentions == null || mentions.length > 0;
	}

	private void createMentionedNode(PrintStream printStream, Status status) {
		printStream.println(" 		<node id=\"" + status.getUser().getId() + "\" label=\"@"
				+ status.getUser().getScreenName() + "\">");

		printStream.println("		<attvalues>");
		if (status.isRetweet()) {
			printStream.println("			<attvalue for=\"0\" value=\""
					+ Utils.replaceSpecialChars(status.getRetweetedStatus().getText()) + "\"/>");
			printStream.println("			<attvalue for=\"1\" value=\"" + status.isRetweet() + "\"/>");
		} else {
			printStream.println("			<attvalue for=\"0\" value=\"" + Utils.replaceSpecialChars(status.getText())
					+ "\"/>");
			printStream.println("			<attvalue for=\"1\" value=\"" + status.isRetweet() + "\"/>");
		}
		printStream.println("		</attvalues>");

		printStream.println("		</node>");

		createMentionedNode(printStream, status.getUserMentionEntities());

	}

	private void createMentionedNode(PrintStream printStream, UserMentionEntity[] mentions) {
		for (UserMentionEntity mention : mentions) {
			printStream.println(" 		<node id=\"" + mention.getId() + "\" label=\"@" + mention.getScreenName() + "\">");

			printStream.println("		<attvalues>");
			printStream.println("			<attvalue for=\"0\" value=\"" + Utils.replaceSpecialChars(mention.getText())
					+ "\"/>");
			printStream.println("			<attvalue for=\"1\" value=\"" + false + "\"/>");
			printStream.println("		</attvalues>");

			printStream.println("		</node>");
		}
	}

	/**
	 * @param printStream
	 * @param status
	 */
	private static void createRetweetNode(PrintStream printStream, Status status) {
		/*
		 * Se for o retweet de alguém cria o nó e este alguém.
		 */
		if (status.isRetweet()) {
			Status someone = status.getRetweetedStatus();
			/*
			 * Retweet.
			 */
			printStream.println(" 		<node id=\"" + status.getUser().getId() + "\" label=\"@"
					+ status.getUser().getScreenName() + "\">");

			printStream.println("		<attvalues>");
			printStream.println("			<attvalue for=\"0\" value=\""
					+ Utils.replaceSpecialChars(someone.getText()) + "\"/>");
			printStream.println("			<attvalue for=\"1\" value=\"" + status.isRetweet() + "\"/>");

			printStream.println("		</attvalues>");
			printStream.println("		</node>");
			/*
			 * Alguém.
			 */
			printStream.println(" 		<node id=\"" + someone.getUser().getId() + "\" label=\"@"
					+ status.getUser().getScreenName() + "\">");

			printStream.println("		<attvalues>");
			printStream.println("			<attvalue for=\"0\" value=\""
					+ Utils.replaceSpecialChars(someone.getText()) + "\"/>");
			printStream.println("			<attvalue for=\"1\" value=\"" + status.isRetweet() + "\"/>");

			printStream.println("		</attvalues>");
			printStream.println("		</node>");


		}
	}

	/**
	 * Create the attributes of the node.
	 * 
	 * @param gexf
	 */
	private static void createAttributes(PrintStream gexf) {
		gexf.println("		<attributes class=\"node\">");
		gexf.println("			<attribute id=\"0\" title=\"text\" type=\"string\"/>");
		gexf.println("			<attribute id=\"1\" title=\"isRetweet\" type=\"boolean\"/>");
		gexf.println("				<default>false</default>");
		gexf.println("		</attributes>");
	}

	/**
	 * Cria o cabeçalho do .gexf.
	 * 
	 * @param title
	 * @param gexf
	 */
	private void createHeader(PrintStream gexf) {
		gexf.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		gexf.println("<gexf xmlns=\"http://www.gexf.net/1.2draft\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\">");

		createMeta(gexf);
	}

	/**
	 * @param title
	 * @param gexf
	 */
	private void createMeta(PrintStream gexf) {
		gexf.println("	<meta lastmodifieddate=\"" + Utils.getCurrentDate() + "\">");
		gexf.println("		<creator>LFDI</creator>");
		gexf.println("		<description>" + title + "</description>");
		gexf.println("	</meta>");
	}
}
