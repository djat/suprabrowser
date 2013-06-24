package ss.server.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.DateUtils;
import ss.common.XmlDocumentUtils;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.search.SearchEngine;
import ss.server.db.dataaccesscomponents.query.BynaryWhere;
import ss.server.db.dataaccesscomponents.query.IWhere;
import ss.server.db.dataaccesscomponents.query.InLaw;
import ss.server.db.dataaccesscomponents.query.LOp;
import ss.server.db.dataaccesscomponents.query.MomentLaw;
import ss.server.db.dataaccesscomponents.query.Order;
import ss.server.db.dataaccesscomponents.query.SelectQuery;
import ss.server.db.dataaccesscomponents.query.SimpleWhere;
import ss.server.db.dataaccesscomponents.query.SphereIdLaw;
import ss.server.db.dataaccesscomponents.query.TotalAccLaw;
import ss.server.db.dataaccesscomponents.query.UsedLaw;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.processing.keywords.ResearchWatcher;
import ss.util.SessionConstants;
import ss.util.SupraXMLConstants;

public class LightSphereMessagesSelector extends AbstractSphereMessagesSelector {

	/**
	 * 
	 */
	private static final int PER_PAGE = 50;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LightSphereMessagesSelector.class);

	private boolean getAll;

	/**
	 * @param xmldbOwner
	 */
	public LightSphereMessagesSelector(final XMLDB xmldbOwner) {
		super(xmldbOwner);
	}

	@SuppressWarnings("unchecked")
	public synchronized Hashtable getForSphereLight(Hashtable session,
			DialogsMainPeer handler, String sphere, Document sphere_document,
			Long existingStart, Long existingEnd) throws DocumentException {

		ResearchWatcher.INSTANCE.sphereOpened( (String)this.xmldbOwner.getSession().get(SessionConstants.USERNAME) , sphere );
		SelectQuery qOne_query = new SelectQuery();
		boolean topTwentyFivePercent = false;
		boolean bottomTwentyFivePercent = false;
		if (sphere_document.getRootElement().element("criteria") != null) {

			if (sphere_document.getRootElement().element("criteria")
					.attributeValue("value").equals("Most used by author")) {
				topTwentyFivePercent = true;
			} else if (sphere_document.getRootElement().element("criteria")
					.attributeValue("value").equals("Least used by author")) {
				bottomTwentyFivePercent = true;
			}
		}

		if (topTwentyFivePercent == false) {
			logger.info("top twenty five percent is false..");
		} else {
			logger.info("top twenty five percent is true...");
		}

		Vector<Document> allDocs = new Vector<Document>();
		Vector contactsOnly = new Vector();
		int totalCount = 0;
		String statement_types = "";
		Long relativeBeginMoment = null;
		Long finalEndMoment = null;
		int totalPages = 0;
		String statement_filter = "";
		boolean isKeywordSearchOn = false;
		String keywordSearch = "";
		Vector<SelectQuery> qQueries = new Vector<SelectQuery>();
		// createTable(sphere);
		SearchEngine se = new SearchEngine();
		Vector order = new Vector();
		Hashtable all = new Hashtable();
		Vector ranges = new Vector();
		boolean isKeywordKeywordSearch = false;
		Date sinceDate = null;

		boolean isUsed = false;

		if (sphere_document != null) {

			Element search = sphere_document.getRootElement().element("search");

			Element date = sphere_document.getRootElement().element(
					"date_criteria");
			if (date != null) {
				if (date.attributeValue("value").equals("used")) {
					isUsed = true;

				}

			}

			if (search != null) {
				if (search.element("keywords") != null) {

					String keywords = sphere_document.getRootElement().element(
							"search").element("keywords").attributeValue(
							"value");
					if (keywords.length() > 0) {
						isKeywordSearchOn = true;

						keywordSearch = keywords;
					}
				}
			}

			final Element threadTypes = sphere_document.getRootElement()
					.element("thread_types");
			Vector<Element> types = new Vector<Element>();
			if (threadTypes != null) {
				types.addAll(threadTypes.elements());
			} else {
				// TODO: dump this for other cases
				logger.warn("Thread types is null for "
						+ XmlDocumentUtils.toPrettyString(sphere_document));
			}

			logger.info("TYPES HERE: " + types.size());

			if (types.size() > 1) {
				statement_types = "(";
				for (int i = 0; i < types.size(); i++) {

					Element one = (Element) types.get(i);

					// if (!one.getName().equals("keywords")) {
					String modify = one.attributeValue("modify");
					boolean view = false;
					if ((modify.equals("any") || modify.equals("own") || modify
							.equals("view"))) {

						view = true;

					}

					/*
					 * if (one.getName().equals("sphere")) { //view = false; }
					 */
					if (i == 0) {
						statement_types = statement_types
								+ "type='reply'||type='comment'||type='edit'||"
								+ "type='"
								+ SupraXMLConstants.TYPE_VALUE_RESULT + "' || "
								+ "type='"
								+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE + "' || "
								+ "type='"
								+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
								+ "'||type='";
						if (view == true) {

							statement_types = statement_types + one.getName()
									+ "'";

						}
					} else {
						if (view == true) {
							statement_types = statement_types + "||type='"
									+ one.getName() + "'";
						}
					}
				}
				statement_types = statement_types + ")";

			} else {

				if (types.size() == 1) {

					Element one = (Element) types.elementAt(0);

					if (one.equals("keywords")) {
						isKeywordKeywordSearch = true;
					}

					statement_types = "type='" + one.getName() + "'";

					Element filter = one.element("filter");

					if (filter != null) {

						statement_filter = filter.attributeValue("value");

						String replace = statement_filter.replaceAll("~", "\"");
						statement_filter = replace;
					}
				}
			}

			SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER
					.getSpherePreferences(sphere);
			String exp = (search == null) ? preferences
					.getWorkflowConfiguration().getExpirationDate()
					: sphere_document.getRootElement().element("expiration")
							.attributeValue("value");
			exp = exp.toLowerCase();

			logger.info("EXPIRATION : " + exp);

			if ((!exp.equals("all") && !exp.equals("none"))) {

				long explong = System.currentTimeMillis();
				long expdays = 0;
				if (exp.lastIndexOf("hour") != -1) {

					StringTokenizer st = new StringTokenizer(exp, " ");
					String begin = st.nextToken();

					Integer conv = new Integer(begin);

					expdays = (conv.intValue() * 1000 * 60 * 60);

					long startMoment = explong;
					long endMoment = startMoment - expdays;
					finalEndMoment = new Long(endMoment);

					relativeBeginMoment = new Long(startMoment - endMoment);

					logger.info("ITS AN HOUR");

					finalEndMoment = new Long(endMoment);
					relativeBeginMoment = new Long(startMoment - expdays);
					ranges.add(new Long(startMoment));
					ranges.add(new Long(endMoment));

				} else if (exp.lastIndexOf("week") != -1) {

					StringTokenizer st = new StringTokenizer(exp, " ");
					String begin = st.nextToken();

					Long conv = new Long(begin);

					expdays = (conv.longValue() * 1000 * 60 * 60 * 24 * 7);

					long twoWeeks = (new Long(2).longValue() * 1000 * 60 * 60
							* 24 * 7);
					long startMoment = explong;
					// long days = 4 * 1000 * 60 * 60 * 24;
					long endMoment = startMoment - expdays;
					finalEndMoment = new Long(endMoment);
					relativeBeginMoment = new Long(startMoment - expdays);

					ranges.add(new Long(startMoment));

					while (true) {

						long nextRange = startMoment - twoWeeks;

						if (nextRange < endMoment) {
							ranges.add(new Long(endMoment));
							break;

						} else {
							ranges.add(new Long(nextRange));

						}
						startMoment = nextRange;
						totalPages += 1;

					}
				} else if (exp.equals("since local mark")) {

					logger.info("NEW SINCE LOCAL DO IT");

					String personalSphere = getUtils().getPersonalSphere(
							(String) session.get("username"),
							(String) session.get("real_name"));

					Document stats = getStatisticsDoc(personalSphere, sphere);
					Element mark = stats.getRootElement().element("last_mark");

					if (mark != null) {
						logger.info("MARK NOT NULL..."
								+ mark.attributeValue("moment"));
						explong = System.currentTimeMillis();

						try {
							SimpleDateFormat df = new SimpleDateFormat(
									"h:m:s a z MMM dd, yyyy");

							sinceDate = df.parse(mark.attributeValue("moment"));
						} catch (ParseException e) {
							logger.error("", e);
						}
					} else {
					}

				} else if (exp.equals("since global mark")) {

					String personalSphere = getUtils().getPersonalSphere(
							(String) session.get("username"),
							(String) session.get("real_name"));

					String sphereCore = getUtils().getSphereCore(session);

					Document stats = getStatisticsDoc(personalSphere,
							sphereCore);
					Element mark = stats.getRootElement().element(
							"last_global_mark");

					if (mark != null) {

						logger.info("MARK NOT NULL..."
								+ mark.attributeValue("moment"));
						explong = System.currentTimeMillis();

						try {

							SimpleDateFormat df = new SimpleDateFormat(
									"h:m:s a z MMM dd, yyyy");

							sinceDate = df.parse(mark.attributeValue("moment"));
						} catch (ParseException e) {
							logger.error("", e);
						}

					} else {
					}
				} else {

					logger.info("must be days");
					StringTokenizer st = new StringTokenizer(exp, " ");
					String begin = st.nextToken();

					Integer conv = new Integer(begin);

					expdays = (conv.intValue() * 1000 * 60 * 60 * 24);

					long startMoment = explong;
					long endMoment = startMoment - expdays;

					finalEndMoment = new Long(endMoment);
					relativeBeginMoment = new Long(startMoment - expdays);

					ranges.add(new Long(startMoment));
					ranges.add(new Long(endMoment));
				}
				long startMoment;
				if (existingStart == null) {
					startMoment = explong;
					existingStart = new Long(startMoment);
				} else {
					startMoment = existingStart.longValue();
					relativeBeginMoment = new Long(startMoment - expdays);
				}
				long endMoment;
				if (existingEnd == null) {
					endMoment = startMoment - expdays;
					existingEnd = new Long(endMoment);
				} else {
					finalEndMoment = existingEnd;
					endMoment = existingStart.longValue() - expdays;
					existingEnd = new Long(endMoment);

				}
				final java.sql.Timestamp startMomentTimeStamp = new java.sql.Timestamp(
						startMoment);
				final String startMomentString = DateUtils
						.dateToCanonicalString(startMomentTimeStamp);
				String endMomentString;
				if (sinceDate == null) {
					java.sql.Timestamp ts = new java.sql.Timestamp(endMoment);
					endMomentString = DateUtils.dateToCanonicalString(ts);
				} else {
					endMomentString = DateUtils
							.dateToCanonicalString(sinceDate);
				}

				logger.info("START MOMENT: " + startMomentString);
				logger.info("END MOMENT: " + endMomentString);

				if (isUsed == false) {
					if (topTwentyFivePercent == true) {

						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								startMomentString, endMomentString, isUsed,
								"top");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(
										new SphereIdLaw(sphere),
										new MomentLaw(startMomentString,
												LOp.less),
										new MomentLaw(endMomentString, LOp.more),
										new SimpleWhere(statement_types),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.emore));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued"));

					} else if (bottomTwentyFivePercent == true) {

						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								startMomentString, endMomentString, isUsed,
								"bottom");
						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(
										new SphereIdLaw(sphere),
										new MomentLaw(startMomentString,
												LOp.less),
										new MomentLaw(endMomentString, LOp.more),
										new SimpleWhere(statement_types),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.eless));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued", true));
					} else {
						logger.info("cused is false");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere.AndAll(new SphereIdLaw(
								sphere), new MomentLaw(startMomentString,
								LOp.less), new MomentLaw(endMomentString,
								LOp.more), new SimpleWhere(statement_types));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("create_ts"));
					}
					qQueries.add(qOne_query);
				} else {
					logger.info("cused is true");

					if (topTwentyFivePercent == true) {

						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								startMomentString, endMomentString, isUsed,
								"top");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(new SphereIdLaw(sphere), new UsedLaw(
										startMomentString, LOp.less),
										new UsedLaw(endMomentString, LOp.more),
										new SimpleWhere(statement_types),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.emore));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued"));
					} else if (bottomTwentyFivePercent == true) {
						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								startMomentString, endMomentString, isUsed,
								"bottom");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(new SphereIdLaw(sphere), new UsedLaw(
										startMomentString, LOp.less),
										new UsedLaw(endMomentString, LOp.more),
										new SimpleWhere(statement_types),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.eless));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued"));
					} else {
						logger.info("CUSED");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere.AndAll(new SphereIdLaw(
								sphere), new UsedLaw(startMomentString,
								LOp.less), new UsedLaw(endMomentString,
								LOp.more), new SimpleWhere(statement_types));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("create_ts"));
					}
					qQueries.add(qOne_query);
				}
			} else {

				if (exp.equals("none")) {
					qQueries.removeAllElements();
				} else {
					setGetAll(true);

					long explong = System.currentTimeMillis();
					long expdays = 0;

					Long conv = new Long("2");

					expdays = (conv.longValue() * 1000 * 60 * 60 * 24 * 7);

					long twoWeeks = (new Long(2).longValue() * 1000 * 60 * 60
							* 24 * 7);
					long startMoment = explong;
					long endMoment = startMoment - expdays;

					if (existingStart != null) {
						startMoment = existingStart.longValue();
						endMoment = startMoment - twoWeeks;
					}
					finalEndMoment = new Long(endMoment);

					relativeBeginMoment = new Long(startMoment - twoWeeks);
					ranges.add(new Long(startMoment));
					ranges.add(new Long(endMoment));

					totalPages += 1;

					java.sql.Timestamp t = new java.sql.Timestamp(startMoment);

					logger.info("statements: " + statement_types);

					if (topTwentyFivePercent == true) {

						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								"top");

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(new SphereIdLaw(sphere),
										new SimpleWhere(statement_types),
										new SimpleWhere("XMLDATA LIKE '%"
												+ statement_filter + "%'"),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.emore));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued"));
					} else if (bottomTwentyFivePercent == true) {

						int mostUsedThreshold = getMostUsedThresholdForStatement(
								sphere, statement_types, statement_filter,
								"bottom");
						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere
								.AndAll(new SphereIdLaw(sphere),
										new SimpleWhere(statement_types),
										new SimpleWhere("XMLDATA LIKE '%"
												+ statement_filter + "%'"),
										new TotalAccLaw(String
												.valueOf(mostUsedThreshold),
												LOp.eless));
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("total_accrued", true));

					} else {
						logger.info("Neither bottom nor top is true"); // can
						// be
						// supra
						// sphere

						qOne_query = new SelectQuery();
						IWhere where = BynaryWhere.AndAll(new SphereIdLaw(
								sphere), new SimpleWhere(statement_types));
						if (statement_filter != null
								&& !statement_filter.equals("")) {
							where = where.and(new SimpleWhere("XMLDATA LIKE '%"
									+ statement_filter + "%'"));
						}
						qOne_query.setWhere(where);
						qOne_query.setOrder(new Order("create_ts"));
					}

					qQueries.add(qOne_query);
				}
			}
		} else {
			long longnum = System.currentTimeMillis();
			long days = 4 * 1000 * 60 * 60 * 24;
			long newlong = longnum - days;

			java.sql.Timestamp ts = new java.sql.Timestamp(newlong);
			final String newdate = DateUtils.dateToCanonicalString(ts);

			if (!isKeywordKeywordSearch) {
				qOne_query = new SelectQuery();
				IWhere where = BynaryWhere.AndAll(new SphereIdLaw(sphere),
						new MomentLaw(newdate, LOp.more), new InLaw("type",
								SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL,
								SupraXMLConstants.TYPE_VALUE_RESULT,
								SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE,
								SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE,
								"message", "file", "bookmark", "comment",
								"terse", "edit", "reply", "library", "contact",
								"sphere", "rss"));
				qOne_query.setWhere(where);
				qOne_query.setOrder(new Order("create_ts"));

				qQueries.add(qOne_query);
			} else {
				qOne_query = new SelectQuery();
				IWhere where = BynaryWhere.AndAll(new SphereIdLaw(sphere),
						new MomentLaw(newdate, LOp.more), new InLaw("type",
								SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL,
								SupraXMLConstants.TYPE_VALUE_RESULT,
								SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE,
								SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE,
								"message", "file", "bookmark", "comment",
								"keywords", "terse", "edit", "reply",
								"library", "contact", "sphere", "rss"));

				qOne_query.setWhere(where);
				qOne_query.setOrder(new Order("create_ts"));
				qQueries.add(qOne_query);
			}
		}

		// String oldestRecord = null;
		//
		// if (this.isGetAll() == true) {
		// oldestRecord = this.getOldestRecordForSphere(sphere);
		// }

		if (this.isGetAll() == true) {
			String query;
			if (sphere.equals((String) session.get("supra_sphere"))
					&& session.get("query_id") == null) {
				query = "select count(recid) from supraspheres where sphere_id = '"
						+ sphere + "' and (type ='sphere'||type='contact')";
			} else {
				if (isKeywordKeywordSearch) {

					query = "select count(recid) from supraspheres where sphere_id = '"
							+ sphere + "' and type='keywords'";
				} else {
					query = "select count(recid) from supraspheres where sphere_id = '"
							+ sphere
							+ "' and type in ('message', '"
							+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
							+ "', '"
							+ SupraXMLConstants.TYPE_VALUE_RESULT
							+ "', '"
							+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
							+ "', "
							+ "'"
							+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
							+ "', 'file', 'bookmark', 'comment', "
							+ " 'terse', 'edit', 'reply', 'library', 'contact', 'sphere', 'rss')";
				}
			}
			totalCount = this.xmldbOwner.selectCount(query);
		}
		processSphereDoc(sphere_document, qQueries);
		qQueries = doPaging(qQueries, sphere_document);
		/*
		 * for (String stringQuery : queries) { List<Document>
		 * safeQueryDocumentList = this.xmldbOwner
		 * .safeQueryDocumentList(stringQuery); for (Document document :
		 * safeQueryDocumentList) { processDoc(session, sphere, stringQuery,
		 * supraSphereDoc, contactsOnly, isKeywordSearchOn, keywordSearch, se,
		 * order, all, allDocs, isKeywordKeywordSearch, document); } }
		 */
		
		List<Document> safeQueryDocumentList = null;
		for (SelectQuery onequery : qQueries) {
			String stringQuery = onequery.getQuery();
			safeQueryDocumentList = this.xmldbOwner
					.safeQueryDocumentList(stringQuery);
			for (Document document : safeQueryDocumentList) {
				processDoc(session, sphere, stringQuery,
						contactsOnly, isKeywordSearchOn, keywordSearch, se,
						order, all, isKeywordKeywordSearch, document, allDocs);
			}

		}		
		if (finalEndMoment != null || isGetAll() == true) {
			all.put("relativeBeginMoment", relativeBeginMoment);
			all.put("finalEndMoment", finalEndMoment);
			if (isGetAll() == true) {
				all.put("totalPagesType", "count");
				all.put("totalPages", new Integer(totalCount).toString());
			} else {
				all.put("totalPagesType", "page");
				all.put("totalPages", new Integer(totalPages).toString());
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Order: " + order);
		}
		
		all.put("docs_in_order", safeQueryDocumentList.toArray(new Document[]{}));
		all.put("order", order);
		all.put("contactsOnly", contactsOnly);

		setGetAll(false);
		return all;
	}

	/**
	 * @param sphere_document
	 * @param queries
	 */
	private void processSphereDoc(Document sphere_document,
			Vector<SelectQuery> queries) {		
		Element search = sphere_document.getRootElement().element("search");
		Element paging = sphere_document.getRootElement().element("paging");
		if ((search == null) && (paging != null)) {
			return;
		} else if (paging != null) {
			Element page = paging.element("page");
			if ((page != null)
					&& (Integer.parseInt(page.attributeValue("value")) != 1)) {
				return;
			}
			paging.detach();
		}		
		paging = sphere_document.getRootElement().addElement("paging");
		SelectQuery query = queries.get(0);
		SelectQuery noMomentQuery = query.getWithoutMoment();
		noMomentQuery.setSelect("count(XMLDATA)");
		int allCount = this.xmldbOwner.selectCount(noMomentQuery.getQuery());
		paging.addElement("allCount").addAttribute("value", "" + allCount);
		Element totalPages = paging.addElement("total_pages");
		int pages = (allCount / PER_PAGE)
				+ ((allCount % PER_PAGE == 0) ? 0 : 1);
		totalPages.addAttribute("value", "" + pages);
	}

	/**
	 * @param queries
	 * @param sphere_document
	 */
	private Vector<SelectQuery> doPaging(Vector<SelectQuery> queries,
			Document sphere_document) {
		Element paging = sphere_document.getRootElement().element("paging");
		if (paging != null) {
			Vector<SelectQuery> newQueries = new Vector<SelectQuery>();
			Element page = paging.element("page");
			if (page == null) {
				page = paging.addElement("page").addAttribute("value", "" + 1);
			}
			int requestedPage = Integer.parseInt(page.attributeValue("value"));
			int allCount = Integer.parseInt(paging.element("allCount")
					.attributeValue("value"));

			int totalPages = Integer.parseInt(paging.element("total_pages")
					.attributeValue("value"));
			for (SelectQuery query : queries) {
				newQueries.add(query.getWithoutMoment());
			}
			int end = allCount - (requestedPage - 1) * PER_PAGE;
			int start = ((end - PER_PAGE) < 0) ? 0 : (end - PER_PAGE);
			for (SelectQuery query : newQueries) {
				query.setLimit(start + "," + end);
			}
			return newQueries;
		}
		return queries;
	}

	@SuppressWarnings("unchecked")
	private void processDoc(Hashtable session, String sphere, String one_query,
			Vector contactsOnly,
			boolean isKeywordSearchOn, String keywordSearch, SearchEngine se,
			Vector order, Hashtable all, boolean isKeywordKeywordSearch,
			Document document, Vector<Document> allDocs) {
		if (logger.isDebugEnabled()) {
			logger.info("Processing document=" + document.asXML());
		}
		try {
			if (isShouldBeAdded(document, session, contactsOnly)) {

				order.add(document.getRootElement().element("message_id").attributeValue("value"));

				if ((isKeywordSearchOn || isKeywordKeywordSearch) ? (se
						.searchForKeywords(document, keywordSearch,
								isKeywordKeywordSearch)) : true) {					
					Document rootDoc = getUtils().getRootDoc(document, sphere);					
					// Vector<Document> allDocs = new Vector<Document>();
					if (rootDoc == null) { 
						return;
					}
					// logger.info("root doc not null");

					String rootDocId = rootDoc.getRootElement().element(
					"message_id").attributeValue("value");
					if (all.containsKey(rootDocId)) {
						return;
					}
					Hashtable response = getUtils().getAllOfThread(
							rootDoc, sphere);							
					Vector responses = (Vector) response
					.get("responses");
					Collections.reverse(responses);
					allDocs.addAll(responses);
					allDocs.add(rootDoc);

					for (Document doc : allDocs) {
						String messId = doc.getRootElement().element(
								"message_id").attributeValue("value");
						String subj = doc.getRootElement().element(
						"subject").attributeValue("value");
						logger.info(" Try to adding responces = "
								+ subj + " " + messId);
						if (!all.containsKey(messId)) {
							logger.info("Adding responces = " + subj
									+ " " + messId + "done");
							all.put(messId, doc);
							//order.add(messId);
						}
					}
					allDocs.removeAllElements();

				}
			}
		} catch (Exception exc) {
			logger.error("Exception in .. + query: " + one_query, exc);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isShouldBeAdded(Document document, Hashtable session,
			Vector contactsOnly) {
		String type = document.getRootElement().element("type").attributeValue(
				"value");

		if (type.equals("contact")) {
			logger.info("ADDING CONTACT TO CONTACTS ONLY: "
					+ document.getRootElement().element("login")
							.attributeValue("value"));
			contactsOnly.add(document);
		}
		boolean addSphere = false;
		if (type.equals("sphere")) {
			String sphereId = document.getRootElement().attributeValue(
					"system_name");
			ISupraSphereFacade supraSphere = getSupraSphere();
			String login = (String) session.get("username");
			if ( supraSphere.isSphereEnabledForMember(sphereId,login) ) {
				addSphere = true;
			}			
		} else {
			addSphere = true;
		}
		return addSphere;
	}

	@SuppressWarnings("unchecked")
	public synchronized Hashtable getForSphereLight(Vector sphereList) {

		logger.error(" -------------- get for sphere light in LSMS");
		logger.info("getforspherelight 11111...");

		long longnum = System.currentTimeMillis();

		long days = 4 * 1000 * 60 * 60 * 24;

		long newlong = longnum - days;

		java.sql.Timestamp ts = new java.sql.Timestamp(newlong);
		final String newdate = DateUtils.dateToCanonicalString(ts);
		String sphereString = "";

		logger.info("spherelist size: " + sphereList.size());
		Vector newSphereList = new Vector();

		for (int i = 0; i < sphereList.size(); i++) {
			newSphereList.add(sphereList.get(i));
		}

		for (int i = 0; i < newSphereList.size(); i++) {
			String oneSphere = (String) newSphereList.get(i);

			if (i == 0 && sphereList.size() == 1) {
				String oneStatement = "select XMLDATA, sphere_id, moment from "
						+ "supraspheres where sphere_id = '"
						+ oneSphere
						+ "' and moment > \""
						+ newdate
						+ "\" and type in ('message', '"
						+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_RESULT
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
						+ "', "
						+ "'"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
						+ "', 'file', 'bookmark', 'comment',"
						+ " 'terse', 'reply', 'contact', 'sphere') ORDER BY create_ts";
				sphereString = oneStatement;

			} else if (i == 0) {
				logger.info("case 2 ");
				String oneStatement = "select XMLDATA, sphere_id , moment from "
						+ "supraspheres where sphere_id = '"
						+ oneSphere
						+ "' and moment > \""
						+ newdate
						+ "\" and type in ('message', '"
						+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_RESULT
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
						+ "', 'file', 'bookmark', 'comment',"
						+ " 'terse', 'reply', 'contact', 'sphere')";

				sphereString = "(" + oneStatement + ")" + " UNION ";
			} else if (i <= newSphereList.size() - 2) {

				logger.info("case 3 ");

				String oneStatement = "select XMLDATA, sphere_id , moment from "
						+ "supraspheres where sphere_id = '"
						+ oneSphere
						+ "' and moment > \""
						+ newdate
						+ "\" and type in ('message', '"
						+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_RESULT
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
						+ "', 'file', 'bookmark', 'comment',"
						+ " 'terse', 'reply', 'contact', 'sphere')";

				sphereString = sphereString + "(" + oneStatement + ")"
						+ " UNION ";
			} else if (i == newSphereList.size() - 1) {

				logger.info("BREAK HERE!!!: " + i);
				String oneStatement = "select XMLDATA, sphere_id , moment from "
						+ "supraspheres where sphere_id = '"
						+ oneSphere
						+ "' and moment > \""
						+ newdate
						+ "\" and type in ('message', '"
						+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_RESULT
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
						+ "', '"
						+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
						+ "', 'file', 'bookmark', 'comment',"
						+ " 'terse', 'reply', 'contact', 'sphere')";

				sphereString = sphereString + "(" + oneStatement + ")"
						+ "ORDER BY MOMENT";
			} else {
				logger.warn("WHAT CASE WAS THIS:? ?" + i);
			}
		}
		logger.info("SPHERE STRING in getforsphereligth: " + sphereString);

		Vector contactsOnly = new Vector();
		int totalCount = 0;
		Long relativeBeginMoment = null;
		Long finalEndMoment = null;
		int totalPages = 0;
		boolean isKeywordSearchOn = false;

		Vector order = new Vector();
		Hashtable all = new Hashtable();
		Vector allDocs = new Vector();
		boolean isKeywordKeywordSearch = false;

		List<Document> docsInOrder = this.xmldbOwner.safeQueryDocumentList(sphereString); 
		for (Document document : docsInOrder) {
			try {
				String messageId = document.getRootElement().element(
						"message_id").attributeValue("value");

				if ((isKeywordSearchOn == true || isKeywordKeywordSearch)) {
					logger.info("keyword search on...prob wont work");

				} else {

					String type = (String) document.getRootElement().element(
							"type").attributeValue("value");

					if (type.equals("contact")) {
						contactsOnly.add(document);
					}

					all.put(messageId, document);
					order.add(messageId);

					allDocs.removeAllElements();

				}
			} catch (Exception e) {
			}

		}

		if (finalEndMoment != null || isGetAll() == true) {
			// logger.info("setting relative begina nd end:
			// "+relativeBeginMoment+ " : "+finalEndMoment);
			all.put("relativeBeginMoment", relativeBeginMoment);
			all.put("finalEndMoment", finalEndMoment);
			if (isGetAll() == true) {
				// logger.info("setting total pages, darnit");
				all.put("totalPagesType", "count");
				all.put("totalPages", new Integer(totalCount).toString());

			} else {
				// logger.info("how was get all false??? ");
				all.put("totalPagesType", "page");
				all.put("totalPages", new Integer(totalPages).toString());

			}
			// all.put("currentPage",currentPage);
		} else {

			// logger.info("when returning it was null!!!");
		}
		all.put("docs_in_order", docsInOrder);
		all.put("order", order);
		all.put("contactsOnly", contactsOnly);

		setGetAll(false);
		return all;

	}

	/**
	 * @param sphere
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getOldestRecordForSphere(String sphere) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param sphere
	 * @param statement_types
	 * @param statement_filter
	 * @param string
	 * @return
	 */
	private int getMostUsedThresholdForStatement(String sphere,
			String statement_types, String statement_filter, String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param sphere
	 * @param statement_types
	 * @param statement_filter
	 * @param startMomentString
	 * @param endMomentString
	 * @param isUsed
	 * @param string
	 * @return
	 */
	private int getMostUsedThresholdForStatement(String sphere,
			String statement_types, String statement_filter,
			String startMomentString, String endMomentString, boolean isUsed,
			String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param getAll
	 *            the getAll to set
	 */
	private void setGetAll(boolean getAll) {
		this.getAll = getAll;
	}

	/**
	 * @return the getAll
	 */
	private boolean isGetAll() {
		return this.getAll;
	}

}
