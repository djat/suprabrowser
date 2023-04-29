package ss.rss;

import java.io.IOException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author david
 * 
 */
public class RSSParser {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RSSParser.class);

	public RSSParser() {
	}
	
	private static InputStream getIS(String urlString) {
		
		 try {
		URL url = new URL(urlString);
		java.net.HttpURLConnection httpURL = ( java.net.HttpURLConnection )  url.openConnection();
	      
	      httpURL.setRequestProperty ( "User-Agent", "Mozilla/5.0  ( compatible ) " );
	     
			InputStream inputStream =  httpURL.getInputStream();
			return inputStream;
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	      
		
	}

	public static void main(String[] args) {
		//String title = getTitleFromURL("http://www.digg.com");
		//ogger.warn("Title: "+title);
		
		Vector rssLinks = findRSSURL("http://yoavs.blogspot.com/");
		System.out.println("It has one: "+rssLinks.size());
		for (int m=0;m<rssLinks.size();m++) {
			
			String one = (String)rssLinks.get(m);
			System.out.println("And it is: "+one);
			
		}
		
		
	}

	public static Vector findAllRSSLinks(String baseURL) {
		Vector<String> allLinks = new Vector<String>();
		try {
			InputStream urlStream = getIS(baseURL);

			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {

				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}

			String lowerCaseContent = content.toLowerCase();
			int index = 0;
			while ((index = lowerCaseContent.indexOf("<a href", index)) != -1) {
				index++;
				String remaining = content.substring(index - 1);

				StringTokenizer st = new StringTokenizer(remaining, ">");
				String strLink = st.nextToken();

				String endURL = strLink + ">";
				String lowerURL = endURL.toLowerCase();

				int href = 0;

				href = lowerURL.indexOf("href=\"");
				boolean isSingle = false;

				if (href == -1) {
					href = lowerURL.indexOf("href='");
				}
				String rest = endURL.substring(href + 5);
				StringTokenizer st1 = null;

				if (!isSingle) {
					st1 = new StringTokenizer(rest, "\t\n\r\">#");
				} else {
					st1 = new StringTokenizer(rest, "\t\n\r\'>#");
				}

				String real = null;

				try {
					real = st1.nextToken();
					real = real.replace("//", "/");

					real = real.replace("http:/", "http://");
					SyndFeed feed = checkForRSS(real);

					if (feed != null) {
						allLinks.add(real);
					} else {
						// real = real.replace("'", "");
						// findSubLinks(real);
					}
				} catch (Exception e) {
				}
			}
		} catch (MalformedURLException ex) {
			logger.error(ex);
		} catch (IOException ex) {
			logger.error(ex);
		}

		return allLinks;

	}

	public static Vector findSubLinks(String baseURL) {
		Vector allLinks = new Vector();
		
		try {
			InputStream urlStream = getIS(baseURL);

			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {

				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;

				}
			}

			String lowerCaseContent = content.toLowerCase();
			int index = 0;
			while ((index = lowerCaseContent.indexOf("<a href", index)) != -1) {

				index++;
				String remaining = content.substring(index - 1);

				StringTokenizer st = new StringTokenizer(remaining, ">");
				String strLink = st.nextToken();

				String endURL = strLink + ">";
				String lowerURL = endURL.toLowerCase();

				int href = 0;
				href = lowerURL.indexOf("href=\"");
				boolean isSingle = false;
				if (href == -1) {
					href = lowerURL.indexOf("href='");
				}

				String rest = endURL.substring(href + 5);
				StringTokenizer st1 = null;

				if (!isSingle) {
					st1 = new StringTokenizer(rest, "\t\n\r\">#");
				} else {
					st1 = new StringTokenizer(rest, "\t\n\r\'>#");
				}

				try {
					String real = st1.nextToken();

					SyndFeed feed = checkForRSS(real);
					if (feed != null) {
						logger.info("these is an rss in sub: " + real);
					}

					else {
						real = real.replace("'", "");
					}
				} catch (Exception e) {
				}
			}
		} catch (MalformedURLException e) {
		} catch (IOException ex) {
			logger.error(ex);
		}

		return allLinks;

	}

	public static String findOneRSSURL(String URL) {

		SyndFeed feed = checkForRSS(URL);

		if (feed == null) {
			java.util.List items = findRSSURL(URL);

			for (int i = 0; i < items.size(); i++) {
				String url = (String) items.get(i);

				String title = getTitleFromURL(url);
				URL = title;

			}
		}

		return URL;

	}

	public static SyndFeed checkForRSS(String URL) {

		SyndFeed feed = null;

		boolean ok = false;

		try {
			URL feedUrl = new URL(URL);

			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(feedUrl));
			if (feed.getEntries().size() != 0) {
				ok = true;
			}

		}

		catch (Exception ex) {

		}

		if (!ok) {
			return null;
		} else {
			return feed;
		}

	}

	public static List getFeedItems(SyndFeed feed) {
		if (feed == null)
			return null;
		List list = feed.getEntries();
		return list;
	}

	private static String getRealBaseURL(String URL) {

		StringTokenizer st = new StringTokenizer(URL, ".");

		int time = 0;
		String baseURL = "";
		while (st.hasMoreTokens()) {

			String token = st.nextToken();
			if (!st.hasMoreTokens()) {
				StringTokenizer innerST = new StringTokenizer(token, "/");
				String end = innerST.nextToken();
				baseURL = baseURL + "." + end + "/";
				break;
			}
			if (time != 0) {
				baseURL = baseURL + "." + token;
			} else {
				baseURL = token;
			}

			time++;
		}
		return baseURL;

	}

	public static String getDomainOnly(String domain) {
		String newDomain = "";
		StringTokenizer st = new StringTokenizer(domain, ".");
		int time = 0;

		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (time == 0) {
				newDomain = newDomain + token + ".";
			}

			if (time != 0) {

				int index = token.lastIndexOf("/");

				if (index != -1) {
					String begin = token.substring(0, index);
					newDomain = newDomain + begin;
					break;
				}

				newDomain = newDomain + token + ".";
			}

			time++;

		}
		return newDomain;

	}

	public static Vector findRSSURL(String URL) {

		Vector<String> urls = new Vector<String>();
		String rssURL = null;
		try {

			InputStream urlStream = getIS(URL);
			
			if (urlStream!=null) {
			
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {

				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;

				}
			}

			String lowerCaseContent = content.toLowerCase();
			int index = 0;
			while ((index = lowerCaseContent.indexOf("<link rel=\"alternate", index)) != -1) {

				index++;
				String remaining = content.substring(index - 1);

				StringTokenizer st = new StringTokenizer(remaining, ">");
				String strLink = st.nextToken();

				rssURL = strLink + ">";
				
				URL urlLink;
				
				String domain = URL;

				System.out.println("rssURL : "+rssURL);
				
				domain = getRealBaseURL(domain);

				if (rssURL.toLowerCase().lastIndexOf("application/rss+xml") != -1) {
					logger.info("URL: " + rssURL);

					String lowerURL = rssURL.toLowerCase();

					int href = 0;

					boolean isSSL = false;

					href = lowerURL.indexOf("href=\"");
					String rest = rssURL.substring(href + 5);

					StringTokenizer st1 = new StringTokenizer(rest,
							"\t\n\r\">#");

					String real = st1.nextToken();

					if (!real.toLowerCase().startsWith("http")) {
						if (real.startsWith("//")) {
							real = "http:" + real;

						} else {

							if (real.toLowerCase().startsWith("https")) {
								isSSL = true;
							}
							logger.info("DOmain: " + domain);
							String begin = "//";

							StringTokenizer stoken = new StringTokenizer(
									domain, begin);
							stoken.nextToken();
							String restToken = stoken.nextToken();
							stoken = new StringTokenizer(restToken, "/");

							if (!domain.endsWith("/")) {
								if (!domain.startsWith("http")) {
									if (!isSSL) {
										real = "http://" + domain + "/" + real;
									} else {
										real = "https://" + domain + "/" + real;
									}
								} else {
									real = domain + "/" + real;
								}
							} else {
								if (!domain.startsWith(("http"))) {
									if (!isSSL) {
										real = "http://" + domain + real;
									} else {
										real = "https://" + domain + real;
									}
								} else {
									real = domain + real;
								}
							}
						}
					}
					real = real.replace("//", "/");

					real = real.replace("http:/", "http://");
					urls.add(real);
				} else if (rssURL.toLowerCase().lastIndexOf(
						"application/atom+xml") != -1) {

					
					String lowerURL = rssURL.toLowerCase();

					int href = 0;

					logger.info("lowerURL: " + lowerURL.indexOf("href=\""));
					href = lowerURL.indexOf("href=\"");
					String rest = lowerURL.substring(href + 5);
					logger.info("rest: " + rest);

					StringTokenizer st1 = new StringTokenizer(rest,
							"\t\n\r\">#");

					String real = st1.nextToken();

					if (!real.toLowerCase().startsWith("http")) {
						if (real.startsWith("//")) {
							real = "http:" + real;

						} else {
							String begin = "//";

							StringTokenizer stoken = new StringTokenizer(
									domain, begin);
							stoken.nextToken();
							String restToken = stoken.nextToken();
							stoken = new StringTokenizer(restToken, "/");
							logger.info("Before: " + real);
							if (real.startsWith("/")) {
								real = real.replace("/", "");
							}

							if (!domain.endsWith("/")) {
								logger.info("this was it!!!!: " + domain
										+ " : " + real);
								if (!domain.startsWith("http")) {

									real = "http://" + domain + "/" + real;
								} else {
									real = domain + "/" + real;
								}
							} else {
								if (!domain.startsWith(("http"))) {
									real = "http://" + domain + real;
								} else {
									real = domain + real;
								}
							}
						}
					}
					real = real.replace("//", "/");

					real = real.replace("http:/", "http://");
					urls.add(real);
				}

				try {
					urlLink = new URL(strLink);
					
				} catch (MalformedURLException e) {
					break;
				}
			}
			}

		} catch (IOException ex) {
			logger.error(ex);
		}

		return urls;
	}

	public static String getTitleFromURL(String URL) {
		String title = null;

		try {
			

			InputStream urlStream = getIS(URL);
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {

				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;

			}
			}

			String lowerCaseContent = content.toLowerCase();

			int startIndex = lowerCaseContent.indexOf("<title>");

			if (startIndex == -1) {
				startIndex = lowerCaseContent.indexOf("<title") + 1;
			}

			String remaining = content.substring(startIndex - 1);
			StringTokenizer st = new StringTokenizer(remaining.toLowerCase(),
					">");
			String strLink = st.nextToken();

			startIndex = lowerCaseContent.indexOf(strLink);
			int endIndex = lowerCaseContent.indexOf("</title>");

			remaining = content.substring(startIndex + strLink.length() + 1,
					endIndex);
			title = remaining;
		} catch (Exception ex) {
			logger.error("No title in url "+URL, ex);
		}

		return title;
	}
	
	public static String getHtmlFromUrl(final String URL) {
		String content = "";

		try {
			InputStream urlStream = getIS(URL);
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			content = new String(b, 0, numRead);
			while (numRead != -1) {
				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}
		} catch(IOException ex) {
			logger.error("Exception occurs during extracting html text from "+URL, ex);
		}
		return content;
	}
}
