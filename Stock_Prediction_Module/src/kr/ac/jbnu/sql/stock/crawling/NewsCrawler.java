package kr.ac.jbnu.sql.stock.crawling;

import static kr.ac.jbnu.sql.stock.Constants.NUM_OF_NEWS_IN_A_PAGE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import kr.ac.jbnu.sql.stock.Constants;
import kr.ac.jbnu.sql.stock.model.News;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;

public class NewsCrawler {
	private Connection conn;
	private String stockCode;
	private String startDate;
	private String endDate;
	private String keyWord;
	private String basicURL;
	private DBHandler db;

	public NewsCrawler(String _code, String _startDate, String _endDate, String _keyWord) {
		this.stockCode = _code;
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.db = new DBHandler();
		
		try {
			this.keyWord = URLEncoder.encode(_keyWord, "euc-kr");
			this.basicURL = "http://finance.naver.com/news/news_search.nhn?rcdate=&q=" + keyWord
					+ "&x=24&y=11&sm=title.basic&pd=4&stDateStart=" + startDate + "&stDateEnd=" + endDate + "&page=";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
	}
	
	/*
	 * This Method crawl the news
	 */
	public Vector<Integer> crawlTheNews() {
		Vector<Integer> crawaledNewsID = new Vector<>();
		News news = null;
		
 		int numOfTotalPages = (int) Math.ceil(getNumOfSearchedNews() / NUM_OF_NEWS_IN_A_PAGE);
		int numOfNewsInLastPage = getNumOfSearchedNews() % (int)NUM_OF_NEWS_IN_A_PAGE;
		

		for (int i = 1; i <= numOfTotalPages; i++) {
			for (int j = 0; j < NUM_OF_NEWS_IN_A_PAGE; j++) {
				if(i==numOfTotalPages && j == numOfNewsInLastPage){
					break;
				}
				String eachNewsURL = getNewsURL(i, j);
				news = getNewsData(eachNewsURL);
				System.out.println("수집 뉴스 :");
				System.out.println("      제목 >>>> " +news.getTitle());
				System.out.println("      본문 >>>> " +news.getContents());
				System.out.println("      주소 >>>> " +news.getURL());
				System.out.println("      날짜 >>>> " +news.getDate());
				
				int hasNewsDataID = db.hasNewsData(stockCode, news.getTitle());
				
				if(news != null && hasNewsDataID == 0){
					db.insertNewsData(news);
					crawaledNewsID.add(db.hasNewsData(stockCode, news.getTitle()));
				}
			}
		}
		
		return crawaledNewsID;
	}

	/*
	 * This Method check the number of news that is searched
	 */
	private int getNumOfSearchedNews() {
		int numOfSearchedNews = 0;

		try {
			String url = basicURL+1;
			conn = Jsoup.connect(url).timeout(0).header("User-Agent", Constants.USER_AGENT_INFO);
			numOfSearchedNews = 0;

			Document doc = conn.get();

			// Including bringing news headlines on the Web
			Elements title = doc.select("p.resultCount strong");
			// To change to the integer, delete ',' (ex)4,000->4000
			String str = title.get(1).text().replace(",", "");
			// converted value

			numOfSearchedNews = Integer.parseInt(str);
			return numOfSearchedNews;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return numOfSearchedNews;
	}

	/*
	 * This method gets news URLs on each page
	 */
	private String getNewsURL(int pageNum, int newsNum) {
		try {
			String url = basicURL+pageNum;
			conn = Jsoup.connect(url).timeout(0).header("User-Agent", Constants.USER_AGENT_INFO);

			Document doc = conn.get();
			Elements title = doc.select("*.articleSubject a");
			return title.get(newsNum).attr("href").toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/*
	 * This method gets a news Data and puts news data in News Model Object
	 */
	private News getNewsData(String _newsURL){
		News newsData = null;
		String url = "http://finance.naver.com";
		url += _newsURL;
		
		conn = Jsoup.connect(url).timeout(0).header("User-Agent", Constants.USER_AGENT_INFO);
		
		try {
			Document doc = conn.get();
			Elements title = doc.select("p.articleTitle");
			Elements contents = doc.select("div.articleCont");
			Elements date = doc.select("span.wdate");
			
			// BoilerPipe is used to extract meaningful contents
			// ArticleExtractor is one of BoilerPipe Extractor
			String extractContents = null;
			ArticleExtractor articleExtractor = new ArticleExtractor();
			extractContents = articleExtractor.getText(contents.toString());
			
			newsData = new News(stockCode, title.text(), extractContents, _newsURL, date.text());
			
			return newsData;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
