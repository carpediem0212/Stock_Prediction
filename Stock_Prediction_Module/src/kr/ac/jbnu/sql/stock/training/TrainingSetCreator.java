package kr.ac.jbnu.sql.stock.training;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import kr.ac.jbnu.sql.stock.persistance.DBHandler;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class TrainingSetCreator {
	private DBHandler db = new DBHandler();
	private ArffFileGenerator arffGenerator = new ArffFileGenerator();
	
	public TrainingSetCreator() {
	}

	public void createTrainingSet(String stockCode, Vector<Integer> idSet) {
		try {
			FastVector allAttributes = createAttributes();	//1. Create attributes
			Instances trainingDataSet = new Instances(stockCode+"_TrainingData", allAttributes,
					allAttributes.size()); //2. Create test Set
			trainingDataSet.setClassIndex(allAttributes.size()-1);
			
			HashMap<String, Integer> wordsInfo = null;
			
			for(int i=0; i<idSet.size(); i++){
				wordsInfo = db.getWordsOfNews(idSet.get(i), stockCode);	// Get word frequency in each News
				String newsClassification = db.getNewsClassification(stockCode, idSet.get(i));
				if(newsClassification != null){
					createLearningDataSet(trainingDataSet, allAttributes, wordsInfo, newsClassification);	//3. Add test data
				}
			}
			
			arffGenerator.createArffFile(stockCode ,trainingDataSet);
			
			/*int newsNum = db.getNewsNum();
			
			for(int i=1; i<=newsNum; i++){
				wordsInfo = db.getWordsOfDoc(i);	// Get word frequency in each News
				String newsClassification = db.getNewsClassification(i);
				if(newsClassification != null){
					createLearningDataSet(trainingDataSet, allAttributes, wordsInfo, newsClassification);	//3. Add test data
				}
				
			}
			System.out.println(trainingDataSet.numInstances()+"::");
			//System.out.println(trainingDataSet.instance(0).value(225)+"::");
			 //4. Make a arff File
			System.out.println("finish");*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FastVector createAttributes() {
		Vector words = db.getWordsOfWholeWords(); // To get number of Words of all News
		
		FastVector attributes = new FastVector(words.size()+1);
		for (int i = 0; i < words.size(); i++) {
			attributes.addElement(new Attribute(words.get(i).toString()));
		}
		
		FastVector newsClassification = new FastVector(3);
		newsClassification.addElement("POSITIVE");
		newsClassification.addElement("NEGATIVE");
		newsClassification.addElement("PENDENCY");
		
		attributes.addElement(new Attribute("Classfication", newsClassification));
		
		return attributes;
	}

	private void createLearningDataSet(Instances _trainingDataSet, FastVector _allAttributes, HashMap<String, Integer> _wordsInfo, String _classification) {
		Instance trainingData = new Instance(_allAttributes.size());
		
		System.out.println(_classification);
		for (int i = 0; i < _allAttributes.size(); i++) {
			trainingData.setValue((Attribute) _allAttributes.elementAt(i), 0);
			
			if(i == (_allAttributes.size()-1)){
				trainingData.setValue((Attribute) _allAttributes.elementAt(i), _classification);
			}
		}

		for (String key : _wordsInfo.keySet()) {
			trainingData.setValue((Attribute) _allAttributes.elementAt(_trainingDataSet
					.attribute(key).index()), _wordsInfo.get(key));
		}
		
		_trainingDataSet.add(trainingData);
	}
}
