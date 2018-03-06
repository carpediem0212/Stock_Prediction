package kr.ac.jbnu.sql.stock;

import java.sql.Date;
import java.util.HashMap;
import java.util.Vector;

import weka.classifiers.evaluation.Prediction;
import kr.ac.jbnu.sql.stock.control.MorphologicalAnalyzer;
import kr.ac.jbnu.sql.stock.control.TrainingDataClassfier;
import kr.ac.jbnu.sql.stock.crawling.NewsCrawler;
import kr.ac.jbnu.sql.stock.crawling.NewsCrawler_r;
import kr.ac.jbnu.sql.stock.crawling.PredictionNewsCrawler;
import kr.ac.jbnu.sql.stock.crawling.StockInfoCrawler;
import kr.ac.jbnu.sql.stock.crawling.StockInfoCrawler_r;
import kr.ac.jbnu.sql.stock.crawling.TrainingNewsCrawler_r;
import kr.ac.jbnu.sql.stock.model.News;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;
import kr.ac.jbnu.sql.stock.training.NewsClassifier;
import kr.ac.jbnu.sql.stock.training.TrainingSetCreator;

public class StockPredictor {
	private NewsCrawler_r newsCrawler1;
	private NewsCrawler newsCrawler;
	private StockInfoCrawler stockInfoCrawler;
	private DBHandler db; 
	public StockPredictor() {
		db = new DBHandler();
	}
	/*
	 * This Method is crawling news for training at stock news in NAVER
	 */
	private void crawlStockInfo(String stockCode) {
		stockInfoCrawler = new StockInfoCrawler();
		stockInfoCrawler.crawlStockInfo("005380", "01.01.01");
		
	}
	
	/*
	 * This Method is to run training of news data
	 */
	public void train() {
		String startDateforTraining = "2015-09-01";
		String endDateforTraining = "2015-09-01";
		String stockCode = "005380";

		Vector<Integer> crawledNewsId = crawlNews(startDateforTraining, endDateforTraining, stockCode);
		extractWordInNews(crawledNewsId, stockCode);
		TrainingDataClassfier classfier = new TrainingDataClassfier();
		classfier.classifyForTrainingData(stockCode, crawledNewsId);
		
		TrainingSetCreator b = new TrainingSetCreator();
		b.createTrainingSet(stockCode, crawledNewsId);
	}

	/*
	 * This Method crawls the news for training
	 */
	private Vector<Integer> crawlNews(String startDateforTraining, String endDateforTraining,
			String stockCode) {
		newsCrawler = new NewsCrawler("005380", startDateforTraining, endDateforTraining, "현대차");
		return newsCrawler.crawlTheNews();
	}

	/*
	 * This Method extracts word in a crawled news
	 */
	private void extractWordInNews(Vector<Integer> crawledNewsId, String stockCode) {
		MorphologicalAnalyzer koreanAnalyzer = new MorphologicalAnalyzer();
		
		News news = null;

		HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
		for (int i = 0; i < crawledNewsId.size(); i++) {
			news = db.selectNewsDataForID(crawledNewsId.get(i), stockCode);
			bagOfWords = koreanAnalyzer.AnalysisNewsContents(news.getContents());
			db.insertBagOfWord(bagOfWords, crawledNewsId.get(i), stockCode);
		}
	}

	

	/*
	 * This Method is to run predicting of stock
	 */
	public void predict() {
		String targetDate = "2015-01-04";
		String stockCode = "005380";
		HashMap<String, Integer> result = new HashMap<>();
		result.put(ClassficationType.POSITIVE.toString(), 0);
		result.put(ClassficationType.NEGATIVE.toString(), 0);
		result.put(ClassficationType.PENDENCY.toString(), 0);
		
		Vector<Integer> crawledNewsId = crawlNews(targetDate, targetDate, stockCode);
		extractWordInNews(crawledNewsId, stockCode);
		
		NewsClassifier classfier = new NewsClassifier();
		classfier.prepareClassification(stockCode);
		
		for(int i=0; i<crawledNewsId.size(); i++){
			String temp = classfier.runClassification(db.getWordsOfNews(crawledNewsId.get(i), stockCode));
			
			if(temp != null){
				int frequency = result.get(temp);
				result.replace(temp, ++frequency);
			}
			
			System.out.println(temp);
		}
		
		int max = 0;
		String maxString = null;
		
		for (String key : result.keySet()) {
			System.out.println(key+"/"+result.get(key));
			if(result.get(key) > max){
				max = result.get(key);
				maxString = key;
			}
		}
		
		System.out.println("최종 값 : " + maxString);
		/*newsCrawler1 = new PredictionNewsCrawler(targetDate, targetDate, stockCode);
		newsCrawler1.getNews();*/

	}
	
	public static void main(String[] args) throws Exception {
		StockPredictor stockPredictor = new StockPredictor();
		//stockPredictor.train();
		stockPredictor.predict();
	}

}