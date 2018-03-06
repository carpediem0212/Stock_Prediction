package kr.ac.jbnu.sql.stock.crawling;

import java.util.HashMap;

import org.jsoup.Connection;

import kr.ac.jbnu.sql.stock.ClassficationType;
import kr.ac.jbnu.sql.stock.control.MorphologicalAnalyzer;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;
import static kr.ac.jbnu.sql.stock.Constants.NUM_OF_NEWS_IN_A_PAGE;

/*
 * This class is for news gathering data for training. 
 */
public class TrainingNewsCrawler_r extends NewsCrawler_r {
	DBHandler db = new DBHandler();
	private String startDate;
	private String endDate;
	private String code;
	
	public TrainingNewsCrawler_r(String _startDate, String _endDate, String _code) {
		super(_startDate, _endDate, _code);
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.code = _code;
	}

	@Override
	public void getNews() {
		MorphologicalAnalyzer koreanAnalysis = new MorphologicalAnalyzer();
		
		HashMap<String, String> newsData = new HashMap<String, String>();
		HashMap<String, Integer> analysisData = new HashMap<String, Integer>();

		// counting a number of page
		int totalPage = (int) Math.ceil(getNumOfSearchedNews() / NUM_OF_NEWS_IN_A_PAGE);

		String eachNewsURL = "";
		int newsId = 1;

		for (int i = 1; i <= totalPage; i++) {
			for (int j = 0; j < 20; j++) {

				eachNewsURL = getEachNewsURL(i, j); // Get URL of Each News
				newsData = getEachNewsData(eachNewsURL);
				analysisData = koreanAnalysis.AnalysisNewsContents(newsData.get("contents"));

				System.out.println("title : " + newsData.get("title") + " / date : " + newsData.get("date"));
				if (newsData.get("title") == null || newsData.get("title").equals("")) {
					continue;
				}

				int writeTime = Integer.parseInt(newsData.get("date").split(" ")[1].split(":")[0]);
				// extract hour
				double valueOfOffage = 0;

				String classfication = null;
				String writeDate = newsData.get("date").split(" ")[0];

				/*if (writeTime >= 9 && writeTime < 15 && db.isStockInfo(writeDate)) {
					valueOfOffage = db.getOffageAtOpen(writeDate);
				} else if (writeTime < 9 && db.isStockInfo(writeDate)) {
					// �ð� - ���� ����
					valueOfOffage = db.getOffageBeforeOpen(writeDate);
				} else if (writeTime >= 15 && db.isStockInfo(writeDate)) {
					valueOfOffage = db.getOffageAfterClose(writeDate);
				} else {
					valueOfOffage = db.getOffageCloseDay(writeDate);
				}

				if (valueOfOffage >= +2) {
					classfication = ClassficationType.POSITIVE.toString();
				} else if (valueOfOffage <= -2) {
					classfication = ClassficationType.NEGATIVE.toString();
				} else {
					classfication = ClassficationType.PENDENCY.toString();
				}
				db.insertNewsData1(newsId, newsData.get("title"), analysisData, newsData.get("date"), classfication);*/
				newsId++;
			}
		}
	}
}
