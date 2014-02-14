package ss.rss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import ss.search.URLParser;
import ss.util.VariousUtils;

public class CrawlRSSForStories {
	
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(CrawlingUtils.class);

	
	public static void main(String[] args) {
		
		CrawlRSSForStories cr = new CrawlRSSForStories();
		
		cr.getAddressesFromDump();
		
		
	}
	
	
	
	public Vector<String> getAddressesFromDump() {
		
		Vector<String> allURLS = new Vector<String>();
		RSSParser parser = new RSSParser();
		
		File dir = new File("/Users/davidthomson/workspace/SupraBrowser/ss.core/text");
		int i = 0;
		File[] files = dir.listFiles();
		
				for (int j = 0; j < files.length; j++) {

			File f = files[j];

			try {
				FileReader fileReader = new FileReader(f);
				BufferedReader bufReader = new BufferedReader(fileReader);

				do {
					String line = bufReader.readLine();
					if (line == null)
						break;

					String oneURL = URLParser.getURLFromXML(line);
					
					
					if (oneURL!=null) {
						if (!VariousUtils.vectorContains(oneURL, allURLS)) {
							allURLS.add(oneURL);
							
							
							//if (i==50||i==51) {
								
								System.out.println("ONE: "+oneURL);
								
								SyndFeed feed = parser.checkForRSS(oneURL);

								if (feed==null) {
								Vector rssLinks = parser.findRSSURL(oneURL);
								
								System.out.println("It has one: "+rssLinks.size());
								for (int m=0;m<rssLinks.size();m++) {
									
									String one = (String)rssLinks.get(m);
									feed = parser.checkForRSS(one);
									
								}
								
								
								}
//								System.out.println("Feed: "+feed);
								if (feed!=null) {
								List stories = parser.getFeedItems(feed);
								
								System.out.println("Size; "+stories.size());
								for (int k=0;k<stories.size();k++) {
									
									SyndEntry entry = (SyndEntry) stories.get(k);

									// System.out.println("Entry Title:
									// "+entry.getTitle());
									String title = entry.getTitle();
									String url = entry.getLink();
									String desc = entry.getDescription().getValue();
									
									System.out.println("A Story: "+title+ " : "+url+"\n"+desc);
									
								}
								}
									
							//}
							i++;	
						}
						
						
					}
					
				} while (true);
				System.out.println("Total: "+i);
				bufReader.close();
			} catch (FileNotFoundException ex) {
				logger.error(ex);
			} catch (IOException ex) {
				logger.error(ex);
			}
		}
		return allURLS;
		
	}
	
	
	
	
	
	

}
