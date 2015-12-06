package com.ydjbuaa.npl.cqa.models;

import java.util.HashMap;
import java.util.HashSet;

import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class VSM {
    // An implement to vector space model
	
	public static float getVSMScore(String [] querys,String [] questions)
	{
		HashMap<String,Integer>map=new HashMap<String,Integer>();
		int totalLen=0;
		for(String q:querys)
		{
			if(map.containsKey(q)) continue;
			map.put(q, totalLen);
			totalLen++;
		}
		for(String q:questions)
		{
			if(map.containsKey(q)) continue;
			map.put(q, totalLen);
			totalLen++;
		}
		float [] queryVct=new float[totalLen];
		float [] questionVct=new float[totalLen];
		for(String q:querys)
		{
			float w=LanguageModel.getPWC(q)*BM25.getIDF(q);
			queryVct[map.get(q)]=w;
		}
		for(String q:questions)
		{
			float w=LanguageModel.getPWC(q)*BM25.getIDF(q);
			questionVct[map.get(q)]=w;
		}
		float f=culCosValue(queryVct, questionVct);
		System.err.println(AnsjWordSeg.mergeIntoString(querys)+"\n"+AnsjWordSeg.mergeIntoString(questions)+"\n"+f);
		return f;
	}
     private static float culCosValue(float [] x,float [] y)
     {
    	 double xy=0d;
    	 double xx=0d;
    	 double yy=0d;
    	 for(int i=0;i<x.length;i++)
    	 {
    		 xy+=x[i]*y[i];
    		 xx+=x[i]*x[i];
    		 yy+=y[i]*y[i];
    	 }
    	 if(xy==0||xx==0||yy==0)
    		 return 0f;
    	 double f=(xy)/(Math.sqrt(xx)*Math.sqrt(yy));
    	 return (float) f;
     }
}
