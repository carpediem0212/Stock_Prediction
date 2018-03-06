package kr.ac.jbnu.sql.stock.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import kr.ac.jbnu.sql.stock.Constants;
import weka.core.Instances;


public class ArffFileGenerator {
	public ArffFileGenerator() {
	}
	
	public void createArffFile(String code,Instances a) throws IOException{
		Instances temp = a;
		String filePath = Constants.RESOURCE_DIR+"/data/"+code+".arff";
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePath), "UTF-8"));
		
		writer.write(temp.toString());
		writer.flush();
		writer.close();
	}
}
