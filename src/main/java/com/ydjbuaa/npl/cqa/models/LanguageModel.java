package com.ydjbuaa.npl.cqa.models;

import java.util.HashMap;

public class LanguageModel {
	
	 //smoothing paramater
	 // final static float paraSmoothing=0.1f;
     private static float paraSmoothing=0.6f;
    //words tf table in the collection
     private static HashMap<String,Double>gWordsTFMap=null;
     public static void setPara(float pa)
     {
    	 paraSmoothing=pa;
     }
     public static float getPWC(String word)
     {
    	 if(gWordsTFMap==null)
    	 {
    		 ModelsInitializer.initTF_IDFMap();
    	 }
    	    Float pWC=0.0f;
			if(gWordsTFMap.containsKey(word))
			{
				pWC=(float) (gWordsTFMap.get(word)*1.0);
			}
			return pWC;
     }
     //use language mode to get relevance score
     public static float getLMScore(String[] querys,String[] qtexts)
     {
    	 float score=0f;
    	 if(gWordsTFMap==null)
    	 {
    		 ModelsInitializer.initTF_IDFMap();
    	 }
 		int wordCount=0;
 		HashMap<String,Integer>intTFMap=new HashMap<String,Integer>();
 		for(String word:qtexts)
 		{
 			if(word=="")
 			{
 				continue;
 			}
 			wordCount++;
 			if(intTFMap.containsKey(word))
 			{
 				intTFMap.put(word, intTFMap.get(word)+1);
 			}
 			else{
 				intTFMap.put(word,1);
 			}
 		}
 		for(String word:querys)
 		{
 			Double pWD=0.0;
 			if(intTFMap.containsKey(word))
 			{
 				pWD=intTFMap.get(word)*1.0/(wordCount);
 			}
 			Double pWC=0.0;
 			if(gWordsTFMap.containsKey(word))
 			{
 				pWC=gWordsTFMap.get(word);
 			}
 			Double p=(1-paraSmoothing)*pWD+paraSmoothing*pWC;
 	        if(p==0f)
 	        {
 	        	p=Double.MIN_VALUE;
 	        }
 	        //System.err.println(word+":\t"+p);
 	        score+=Math.log(p);
 		}
 		//System.err.println("Score:"+score);
 		return score;
     }
     public static float getLMScorewithNormalization(String[] querys,String[] qtexts)
     {
    	 double pqq=getLMScore(querys,querys);  //-1
    	 double pqd=getLMScore(querys,qtexts);   //-15
    	 //double pmax=Math.log(Float.MIN_VALUE);//- 100
    	 //System.out.println("(LM)pqq:\t"+pqq+"\tpqd:\t"+pqd+"\tpmin:\t"+pmax);
    	 //return (float) (pqd-pqq);
    	 //return (float) Math.exp((pqd-pqq)); 
    	 //return (float) ((pqd-max)/(pqq-pmax));  //(-15+100)/(-1+100)
    	 //return (float) (pqq/pqd);
    	double p=pqd-pqq;
    	return (float) p;
    	//return (float) Math.exp(p);
     }
     public static void setWordsTFMap(HashMap<String,Double>tfMap)
     {
    	 gWordsTFMap=tfMap;
     }
}
