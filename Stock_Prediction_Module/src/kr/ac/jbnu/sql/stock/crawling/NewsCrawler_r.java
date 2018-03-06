package kr.ac.jbnu.sql.stock.crawling;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;

/*
 *  This class is an abstract class of news crawl classes.
 */
public abstract class NewsCrawler_r {
	protected String startDate;
	protected String endDate;
	protected String stockCode;

	public NewsCrawler_r(String _startDate, String _endDate, String _code) {
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.stockCode = _code;
	}

	public abstract void getNews();

	/*
	 * This Method check the number of news that is searched
	 */
	protected int getNumOfSearchedNews() {
		int numOfSearchedNews = 0;
		try {
			String keyWord = URLEncoder.encode("기아차", "euc-kr");
			String url = "http://finance.naver.com/news/news_search.nhn?rcdate=&q=" + keyWord
					+ "&x=24&y=11&sm=title.basic&pd=4&stDateStart=" + startDate + "&stDateEnd=" + endDate + "&page=1";
			Connection conn = Jsoup.connect(url).timeout(0).header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1 CoolNovo/2.0.4.16");
			System.out.println(url);
			numOfSearchedNews = 0;

			Document doc = conn.get();
			Elements title = doc.select("p.resultCount strong");
			// Including bringing news headlines on the Web
			String str = title.get(1).text().replace(",", "");
			// To change to the integer, delete ',' (ex)4,000->4000
			numOfSearchedNews = Integer.parseInt(str);
			// converted value
			return numOfSearchedNews;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return numOfSearchedNews;
	}

	/*
	 * This method gets news URLs on each page
	 */
	protected String getEachNewsURL(int pageNum, int newsNum) {
		Document doc;
		Elements title = new Elements();

		String url = "http://finance.naver.com/news/news_search.nhn?rcdate=&q=������&x=24&y=11&sm=title.basic&pd=4&stDateStart="
				+ startDate + "&stDateEnd=" + endDate + "&page=";
		url += pageNum;
		Connection conn = Jsoup.connect(url).timeout(0).header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1 CoolNovo/2.0.4.16");

		try {
			doc = conn.get();
			title = doc.select("*.articleSubject a");

			System.out.println(title.get(newsNum).attr("href").toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return title.get(newsNum).attr("href").toString();
	}

	/*
	 * This Method gets news data on each URL
	 */
	protected HashMap<String, String> getEachNewsData(String detailURL) {
		HashMap<String, String> newsDataContainer = new HashMap<String, String>();
		String basicURL = "http://finance.naver.com";
		basicURL += detailURL;

		Connection conn = Jsoup.connect(basicURL).timeout(0).header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1 CoolNovo/2.0.4.16");
		try {
			Document doc = conn.get();
			Elements title = doc.select("p.articleTitle");
			// Get Title
			Elements contents = doc.select("div.articleCont");
			// Get contents
			Elements date = doc.select("span.wdate");
			// Get date

			String content = null;
			ArticleExtractor ae = new ArticleExtractor();
			content = ae.getText(contents.toString());
			// BoilerPipe is used to extract meaningful contents
			// ArticleExtractor is one of BoilerPipe Extractor
			// String writeDate = date.text().split(" ")[0];
			// String writeTime = date.text().split(" ")[1];
			newsDataContainer.put("title", title.text());
			newsDataContainer.put("contents", content);
			newsDataContainer.put("date", date.text());
			// newsDataContainer.put("datetime", writeTime);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		}

		return newsDataContainer;
	}
}