package kr.ac.jbnu.sql.stock.training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import kr.ac.jbnu.sql.stock.ClassficationType;
import kr.ac.jbnu.sql.stock.Constants;
import kr.ac.jbnu.sql.stock.persistance.DBHandler;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class NewsClassifier
{
	private BufferedReader breader = null;
	private Instances trainingSet = null;
	private Classifier classifier = null;
	private Evaluation eTest = null;
	private int[] predictNum;
	
	public NewsClassifier()
	{
	}

	public void prepareClassification(String code)
	{
		try
		{
			String filePath = Constants.RESOURCE_DIR+"/data/"+code+".arff";
			
			breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "UTF-8"));
			
			trainingSet = new Instances(breader);
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

			classifier = (Classifier) new CustomNaiveBayes(code);
			// classifier = (Classifier) new NaiveBayes();
			classifier.buildClassifier(trainingSet);
			// Bring Attribute and DataSet
			breader.close();
			eTest = new Evaluation(trainingSet);
			eTest.evaluateModel(classifier, trainingSet);
			System.out.println(eTest.toSummaryString());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String runClassification(HashMap<String, Integer> _wordInfo)
	{

		try
		{
			Instance test = createInstance(trainingSet, _wordInfo);
			double pre = classifier.classifyInstance(test);
			System.out.println(pre);

			if (pre == 0.0)
			{
				System.out.println("positive");
				return ClassficationType.POSITIVE.toString();
			} else if (pre == 1.0)
			{
				System.out.println("negative");
				return ClassficationType.NEGATIVE.toString();
			} else
			{
				System.out.println("pendency");
				return ClassficationType.PENDENCY.toString();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;

	}

	private Instance createInstance(Instances associatedDataSet, HashMap<String, Integer> _wordInfo)
	{
		Instance instance = new Instance(associatedDataSet.numAttributes());
		instance.setDataset(associatedDataSet);

		DBHandler db = new DBHandler();

		HashMap<String, Integer> wordsInfo = _wordInfo;

		for (int i = 0; i < (associatedDataSet.numAttributes() - 1); i++)
		{
			instance.setValue(i, 0);
		}

		for (String key : wordsInfo.keySet())
		{
			try
			{
				instance.setValue((Attribute) associatedDataSet.attribute(key), wordsInfo.get(key));
			} catch (NullPointerException e)
			{

			}

		}

		return instance;
	}

}
