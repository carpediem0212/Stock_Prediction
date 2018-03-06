package kr.ac.jbnu.sql.stock;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class Analysis {
	public Analysis() {
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<String, Integer> AnalysisNewsContents(String newsContents){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String source = newsContents;
		
		try {

			KoreanAnalyzer analyzer = new KoreanAnalyzer();
			TokenStream stream = analyzer.tokenStream("s", new StringReader(
					source));
			
			while (stream.incrementToken()) {
				CharTermAttribute termAttr = stream
						.getAttribute(CharTermAttribute.class);
				OffsetAttribute offAttr = stream
						.getAttribute(OffsetAttribute.class);
				PositionIncrementAttribute posAttr = stream
						.getAttribute(PositionIncrementAttribute.class);
				TypeAttribute typeAttr = stream
						.getAttribute(TypeAttribute.class);

				//System.out.print(posAttr.getPositionIncrement() + "/");
				//System.out.println(new String(termAttr.buffer(), 0, termAttr
				//		.length()));
				
				String word = new String(termAttr.buffer(), 0,
						termAttr.length());
				System.out.println(termAttr+"");
				
				
				
				if (map.get(word) == null) {
					//System.out.println("first");
					map.put(new String(termAttr.buffer(), 0, termAttr.length()),
							1);
				} else {
					int frequence = map.get(word);
					map.replace(word, ++frequence);
				}

			}

			for (String key : map.keySet()) {
				//System.out.println(key + "/" + map.get(key));
			}

			//System.out.println(map.get("±∏¿‘"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			return map;
		}
			
	}
}
