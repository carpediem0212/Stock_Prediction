package kr.ac.jbnu.sql.stock.training;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;


public class CustomNaiveBayes extends NaiveBayes{
	private String code;
	public CustomNaiveBayes(String code) {
		this.code = code;
	}
	
	@Override
	public double classifyInstance(Instance instance) throws Exception {
	    double[] dist = distributionForInstance(instance);
	    if (dist == null) {
	      throw new Exception("Null distribution predicted");
	    }
	    
	    //RSI ���� ġ �ο� 
	    
	    RSICalculator rsiWeight = new RSICalculator();
	    double weightRSI[] = rsiWeight.getCloseGap(this.code);
	   
	    for(int i = 0; i<dist.length; i++){
	    	System.out.println("before >> "+i+" -> dist pro : " + dist[i] + "----weight : " + weightRSI[i]);
	    	dist[i] *= weightRSI[i];
	    	System.out.println("after >> "+i+" -> dist pro : " + dist[i] + "----weight : " + weightRSI[i]);
	    }
	    
	    switch (instance.classAttribute().type()) {
	    case Attribute.NOMINAL:
	      double max = 0;
	      int maxIndex = 0;

	      for (int i = 0; i < dist.length; i++) {
	        if (dist[i] > max) {
	          maxIndex = i;
	          max = dist[i];
	        }
	      }
	      if (max > 0) {
	        return maxIndex;
	      } else {
	        return Double.NaN;
	      }
	    case Attribute.NUMERIC:
	    case Attribute.DATE:
	      return dist[0];
	    default:
	      return Double.NaN;
	    }
	}
}