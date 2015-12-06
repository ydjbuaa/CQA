package com.ydjbuaa.npl.cqa.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class Word2Vec {
	private final static String vecBinPath = "./library/baidu.vec";
	private static int VEC_SIZE = 200;
	private static HashMap<String, Float[]> vecHashMap = null;

	// load word vector map
	private static void loadVecMap() throws IOException, FileNotFoundException {
		long startTime = System.currentTimeMillis();
		System.err.println("load words vectors......");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(vecBinPath), "UTF-8"));
		vecHashMap = new HashMap<String, Float[]>();
		// read words size and vector size
		// String s=br.readLine();
		String[] items = br.readLine().split(" ");
		int word_size = Integer.parseInt(items[0]);
		VEC_SIZE = Integer.parseInt(items[1]);
		System.err.println("Vector Size:\t" + VEC_SIZE);
		for (int i = 0; i < word_size; i++) {
			String line = br.readLine();
			String[] vStrings = line.split(" ");
			Float[] fs = new Float[VEC_SIZE];
			for (int k = 1; k <= VEC_SIZE; k++) {
				fs[k - 1] = Float.parseFloat(vStrings[k]);
			}
			vecHashMap.put(vStrings[0], fs);
		}
		br.close();
		// cul use time
		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("load words vectors,use time:" + seconds + "s");
	}

	public static Float[] getWordVector(String word) throws IOException {
		// if vectors map has not loaded, load it
		if (vecHashMap == null)
			loadVecMap();
		if (vecHashMap.containsKey(word))
			return vecHashMap.get(word);
		//System.err.println("Do not find word:\t" + word);
		return null;
	}

	public static float getW2CScore(String[] querys, String[] questions) throws IOException {
		// if vectors map has not loaded, load it
		if (vecHashMap == null)
			loadVecMap();
		Float[] sQueryVecs = new Float[VEC_SIZE];
		for (int i = 0; i < sQueryVecs.length; i++)
			sQueryVecs[i] = 0f;
		Float[] sQuestionVecs = new Float[VEC_SIZE];
		for (int i = 0; i < sQuestionVecs.length; i++)
			sQuestionVecs[i] = 0f;
		for (String q : querys) {
			Float[] fVecs = getWordVector(q);
			if(fVecs==null) continue;
			for (int i = 0; i < sQueryVecs.length; i++)
				sQueryVecs[i] += fVecs[i];
		}
		for (String q : questions) {
			Float[] fVecs = getWordVector(q);
			if(fVecs==null) continue;
			for (int i = 0; i < sQuestionVecs.length; i++)
				sQuestionVecs[i] += fVecs[i];
		}
		double xx = 0;
		double yy = 0;
		double xy = 0;
		for (int i = 0; i < VEC_SIZE; i++) {
			xx += sQueryVecs[i]*sQueryVecs[i];
			yy += sQuestionVecs[i]*sQuestionVecs[i];
			xy += sQueryVecs[i] * sQuestionVecs[i];
		}
		if (xx == 0 || yy == 0 || xy == 0)
			return 0f;
		float s=(float) ((xy) / (Math.sqrt(xx) * Math.sqrt(yy)));
		//System.err.println(AnsjWordSeg.mergeIntoString(querys)+"\t"+AnsjWordSeg.mergeIntoString(questions)+"\n"+s);
		return s;
	}
}
