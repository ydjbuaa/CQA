package com.ydjbuaa.npl.cqa.models;

import java.util.HashMap;

public class BM25 {
	//parameters for model BM 25
	private static double avgDL=1d;
	private static double k1=1.2;
	private static double b=0.75;
	private static HashMap<String,Double> gWordsIDFMap=null;
	
	public static void setParams(float kk,float bb)
	{
		k1=kk;
		b=bb;
	}
	//set idf map and avgDL
	public static  void setIDFMapandAvgDL(Double avgdl,HashMap<String,Double>idfMap)
	{
	    avgDL=avgdl;
	    gWordsIDFMap=idfMap;
	}
	public static float getIDF(String word)
	{
		float idf=0.0f;
		if(gWordsIDFMap==null)
		{
			ModelsInitializer.initTF_IDFMap();
		}
		if(gWordsIDFMap.containsKey(word))
		{
			idf=(float) (gWordsIDFMap.get(word)*1.0);
		}
		return idf;
	}
	//compute the score of BM25 model
	public static Float  getBMScore(String[] querys,String[] qtexts)
	{
		if(gWordsIDFMap==null)
		{
			ModelsInitializer.initTF_IDFMap();
		}
		Double score=0d;
		HashMap<String,Integer>intTFMap=new HashMap<String,Integer>();
 		for(String word:qtexts)
 		{
 			if(intTFMap.containsKey(word))
 			{
 				intTFMap.put(word, intTFMap.get(word)+1);
 			}
 			else{
 				intTFMap.put(word,1);
 			}
 		}
		for(String q:querys)
		{
			Double idf=0.0;
			if(gWordsIDFMap.containsKey(q))
			{
				idf=gWordsIDFMap.get(q);
			}
			//int docDL=ds.length;
		    Double fwd=0d;
		    if(intTFMap.containsKey(q))
		    {
		    	fwd=intTFMap.get(q)*1.0/qtexts.length;
		    }
		    Double ss=(fwd*(k1+1))/(fwd+k1*(1-b+b*avgDL));
		    score+=ss*idf;
		}
		return (float) (score*1.0/2);
	}
}
