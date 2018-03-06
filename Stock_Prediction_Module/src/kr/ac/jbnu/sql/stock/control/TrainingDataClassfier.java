package kr.ac.jbnu.sql.stock.control;

import java.util.Vector;

import kr.ac.jbnu.sql.stock.ClassficationType;
import kr.ac.jbnu.sql.stock.model.News;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;

public class TrainingDataClassfier {
	private DBHandler db;
	
	public TrainingDataClassfier() {
		db = new DBHandler();
	}
	
	public void classifyForTrainingData(String stockCode, Vector<Integer> idSet){
				
		for (int i = 0; i < idSet.size(); i++) {
			News news = db.selectNewsDataForID(idSet.get(i), stockCode);
			String classification = newsClassify(news);
			db.updateClassfication(stockCode, idSet.get(i), classification);
			System.out.println(classification);
		}
	}
	
	private String newsClassify(News news){
		int writeTime = Integer.parseInt(news.getDate().split("")[1].split(":")[0]);
		String writeDate = news.getDate().split("")[0];
		
		double valueOfOffage = 0;
		
		if (writeTime >= 9 && writeTime < 15 && db.hasStockInfo(news.getStockCode(), writeDate)) {
			valueOfOffage = db.getOffageAtOpen(news.getStockCode(), writeDate);
		} else if (writeTime < 9 && db.hasStockInfo(news.getStockCode(), writeDate)) {
			valueOfOffage = db.getOffageBeforeOpen(news.getStockCode(), writeDate);
		} else if (writeTime >= 15 && db.hasStockInfo(news.getStockCode(), writeDate)) {
			valueOfOffage = db.getOffageAfterClose(news.getStockCode(), writeDate);
		} else {
			valueOfOffage = db.getOffageCloseDay(news.getStockCode(), writeDate);
		}

		String classfication = null;
		
		if (valueOfOffage >= +2) {
			classfication = ClassficationType.POSITIVE.toString();
		} else if (valueOfOffage <= -2) {
			classfication = ClassficationType.NEGATIVE.toString();
		} else {
			classfication = ClassficationType.PENDENCY.toString();
		}
		
		return classfication;
	}	
}
