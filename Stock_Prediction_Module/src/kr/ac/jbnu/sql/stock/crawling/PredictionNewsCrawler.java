package kr.ac.jbnu.sql.stock.crawling;

import java.util.HashMap;

import kr.ac.jbnu.sql.stock.control.MorphologicalAnalyzer;
import kr.ac.jbnu.sql.stock.training.NewsClassifier;

import static kr.ac.jbnu.sql.stock.Constants.NUM_OF_NEWS_IN_A_PAGE;

/*
 * This class is for news gathering data for prediction. 
 */
public class PredictionNewsCrawler extends NewsCrawler_r {
	private int predictResult[] = {0,0,0};
	public PredictionNewsCrawler(String _startDate, String _endDate,
			String _code) {
		super(_startDate, _endDate, _code);
	}

	@Override
	public void getNews() {
		
		MorphologicalAnalyzer koreanAnalysis = new MorphologicalAnalyzer();

		HashMap<String, String> newsData = new HashMap<String, String>();
		// This is value to contain news data
		HashMap<String, Integer> analysisData = new HashMap<String, Integer>();
		// This is value for bag of word 
		int totalPage = getNumOfSearchedNews() / (int)NUM_OF_NEWS_IN_A_PAGE;
		// Counting number of page

		String eachNewsURL = "";
		int newsId = 1;
		NewsClassifier classfier = new NewsClassifier();
		//classfier.prepareClassification();
		for (int i = 1; i <= totalPage; i++) {
			for (int j = 0; j < 20; j++) {
				int index;
				eachNewsURL = getEachNewsURL(i, j);
				// Get URL of Each News
				newsData = getEachNewsData(eachNewsURL);
				analysisData = koreanAnalysis.AnalysisNewsContents(newsData
						.get("contents"));

				//index = classfier.runClassification(analysisData);
//				if(index != -1){
//					predictResult[index]++;
//				}
				
			}
		}
		
		System.out.println("���� �ְ��� ����� : " + finalPredict());
	}
	
	public int finalPredict(){
		int maxIndex = 0;
		if(predictResult[0] < predictResult[1]){
			maxIndex = 1;
		}
		
		if(predictResult[maxIndex] < predictResult[2]){
			maxIndex =2;
		}
		
		return maxIndex;
	}
}
