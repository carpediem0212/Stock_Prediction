package kr.ac.jbnu.sql.stock.control;

import java.util.HashMap;
import java.util.List;

import kr.ac.jbnu.sql.stock.Constants;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class MorphologicalAnalyzer {

	public MorphologicalAnalyzer() {
	}

	/*
	 * This Method analysis new contents and extracts the noun or verb
	 */
	public HashMap<String, Integer> AnalysisNewsContents(String newsContents) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		Komoran komoran = new Komoran(Constants.RESOURCE_DIR + "model");
		// To contain result
		List<List<Pair<String, String>>> result = komoran.analyze(newsContents); 

		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				// NNP is Common Nouns
				// NNG is Proper Nouns
				// VV is Verb
				if (wordMorph.getSecond().equals("NNG") || wordMorph.getSecond().equals("NNP")
						|| wordMorph.getSecond().equals("VV")) {
					if (wordMorph.getSecond().equals("VV")) {
						wordMorph.setFirst(wordMorph.getFirst() + "다");
					}

					if (map.get(wordMorph.getFirst()) == null) {
						// When word first found Setting the frequency to 1
						map.put(wordMorph.getFirst(), 1); 
					} else {
						int frequency = map.get(wordMorph.getFirst());
						frequency++;
						// When word not first found Increasing the frequency of words
						map.replace(wordMorph.getFirst(), frequency);
						
					}
				}
			}
		}

		return map;
	}
}
