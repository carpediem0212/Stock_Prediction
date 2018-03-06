package kr.ac.jbnu.sql.stock.crawling;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import kr.ac.jbnu.sql.stock.Constants;
import kr.ac.jbnu.sql.stock.model.StockInfo;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;

public class StockInfoCrawler {
	private Connection conn = null;
	private DBHandler db = new DBHandler();

	public StockInfoCrawler() {

	}
	/*
	 * This method crawls the Stock Infomation at Daum Finance Site
	 */
	public void crawlStockInfo(String stockCode, String date) {
		boolean state = true;
		int pageNum = 1;

		if(date == null){
			date = "01.01.01";
		}
		
		try {
			while (state) {
				// use Daum finance site
				String url = "http://finance.daum.net/item/quote_yyyymmdd_sub.daum?page=" + pageNum + "&code="
						+ stockCode + "&modify=null";
				conn = Jsoup.connect(url).header("User-Agent", Constants.USER_AGENT_INFO);

				Document doc = conn.get();
				Elements title = doc.select("table#bbsList tbody tr");

				String stockInfoContent = null;

				// BoilerPipe is used to extract meaningful contents
				// ArticleExtractor is one of BoilerPipe Extractor
				ArticleExtractor articleExtractor = new ArticleExtractor();
				stockInfoContent = articleExtractor.getText(title.toString());
				
				String[] str = stockInfoContent.split(" ");
				SimpleDateFormat stockDateFormat = new SimpleDateFormat("yy.MM.dd");

				// Stock information site has a 30-day stock quotes on a single page.
				// However, the first data of the crawled data is the name of each column.
				// So we must get the data after 1 index.
				// Column consist of the following: date, start, high, low, end, comparedToYesterday,-> 
				// fluctuation, tradingVolume > A total of eight columns.
				for(int i=1; i<=30; i++){
					Date crawledDate = stockDateFormat.parse(str[(8 * i) + 0]);
					Date settedDate = stockDateFormat.parse(date);

					//Compares the two dates 
					int resultOfDateCompare = crawledDate.compareTo(settedDate);
					
					if (resultOfDateCompare >= 0){
						String tempDate = str[(8 * i) + 0];
						int tempStartValue = Integer.parseInt(str[(8 * i) + 1].replace(",", "")); 
						int tempHighValue = Integer.parseInt(str[(8 * i) + 2].replace(",", "")); 
						int tempLowValue = Integer.parseInt(str[(8 * i) + 3].replace(",", "")); 
						int tempCloseValue = Integer.parseInt(str[(8 * i) + 4].replace(",", "")); 
						
						//Conversion process : (ex) ▲1,000 -> +1000
						str[(8 * i) + 5] = str[(8 * i) + 5].replace(",", "");
						if(str[(8 * i) + 5].charAt(0) == '▲'){
							str[(8 * i) + 5] = str[(8 * i) + 5].replaceAll("▲", "+");
						} else if(str[(8 * i) + 5].charAt(0) == '▼') {
							str[(8 * i) + 5] = str[(8 * i) + 5].replaceAll("▼", "-");
						} else if(str[(8 * i) + 5].charAt(0) == '-'){
							str[(8 * i) + 5] = str[(8 * i) + 5].replaceAll("-", "");
						}
						int tempComparedValue = Integer.parseInt(str[(8 * i) + 5]);
						
						//Conversion process : (ex) -5.23% -> -5.23
						str[(8 * i) + 6] = str[(8 * i) + 6].replace("%", "");
						double tempFluctuation = Double.parseDouble(str[(8 * i) + 6]);
						
						String temp = null;
						if(i==30){
							str[(8 * i) + 7] = str[(8 * i) + 7].replaceAll("\n", "");
						}
						
						int tradingVolume = Integer.parseInt(str[(8 * i) + 7].replaceAll(",", "")); 
						StockInfo stockInfo = new StockInfo(stockCode, tempDate, tempStartValue, tempHighValue, tempLowValue, 
								tempCloseValue, tempComparedValue, tempFluctuation, tradingVolume);
						
						if(!db.hasStockInfo(stockCode, tempDate)){
							db.insertStockInfo(stockInfo);
						}
						
					}else{
						state = false;
						break;
					}
				}
				pageNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
