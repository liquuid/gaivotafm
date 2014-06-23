package net.liquuid.gaivotafm;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlHandler {

	private String title = "title";
	private String link = "link";
	private String description = "description";

	private String urlString = null;
	private XmlPullParserFactory xmlFactoryObject;
	public volatile boolean parsingComplete = true;
	public Map<String, String> posts = new HashMap<String, String>();
	private String postsRenderized = "";

	public XmlHandler(String url) {
		this.urlString = url;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getPosts() {
		return postsRenderized;
	}

	public void parseXMLAndStoreIt(XmlPullParser myParser) {
		int event;
		String text = null;

		try {
			event = myParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String name = myParser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					text = myParser.getText();
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("title")) {
						posts.put(text, null);
						title = text;
					} else if (name.equals("link")) {
						posts.put(title, text);
						link = text;
					} else {
					}
					break;
				}
				event = myParser.next();
			}
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fetchXML() {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();
			xmlFactoryObject = XmlPullParserFactory.newInstance();
			XmlPullParser myparser = xmlFactoryObject.newPullParser();
			myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			myparser.setInput(stream, null);
			parseXMLAndStoreIt(myparser);
			stream.close();

			Iterator iterator = posts.entrySet().iterator();
			String head = "<html><head><style type=\"text/css\">hr { clear: both; float: none; width: 100%; height: 1px; "
					+ "margin: 1.0em 0; border: none; background: #ddd; background-image: -webkit-gradient( "
					+ "linear, left bottom, right bottom,color-stop(0, rgb(255,255,255)),color-stop(0.1, rgb"
					+ "(221,221,221)),color-stop(0.9, rgb(221,221,221)),color-stop(1, rgb(255,255,255)));}"
					+ "</style></head><body>";

			String summary = "";
			while (iterator.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) iterator.next();
				summary = "<a href=\"" + mapEntry.getValue() + "\">"
						+ mapEntry.getKey() + "</a><br><hr> " + summary;
			}
			postsRenderized = head + summary + "</body></html>";

		} catch (Exception e) {
		}

		return postsRenderized;
	}

}
