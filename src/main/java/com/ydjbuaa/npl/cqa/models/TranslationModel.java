package com.ydjbuaa.npl.cqa.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;

import com.ydjbuaa.npl.cqa_maven.util.TRMap;

public class TranslationModel {
	//final static float paraSmoothing=0.1f;
	private static float paraSmoothing=0.80f;
	//private static HashMap<String,Float> trMap=null;
	private static final String trPath = "./library/tr/";
	//private static String mgizaTRPath="./library/final.tr.dic";
	//private static HashMap<String,HashMap<String,Float>> mgizaTrMap=null;
	private static TRMap trMap=null;
	
	/**
	 * load the word-to-word translation probability train by mgiza++
	 */
	public static  void setTRPath(String path)
	{
		//mgizaTRPath=path;
		trMap=new TRMap(path);
	}
	/**
	private static void loadMgizaTrMap()
	{
		System.err.println("load word-to-word tr map......");
		long startTime = System.currentTimeMillis();
		
		mgizaTrMap=new HashMap<String,HashMap<String,Float>>();
		
		try {
			
			BufferedReader br=new BufferedReader(
					new InputStreamReader(
							new FileInputStream(mgizaTRPath),"UTF-8"));
			
			String line=null;
			String curStr="";
			HashMap<String,Float> wordMap=null;
			
			while((line=br.readLine())!=null)
			{
				String [] items=line.split("\t");
				Float p=Float.parseFloat(items[2]);
				
				/**
				if(!curStr.equals(items[0]))  //new one
				{
					if(!curStr.equals(""))  // not none before 
					{
						mgizaTrMap.put(curStr, wordMap);
					}
					curStr=items[0];
					wordMap=new HashMap<String,Float>();
				}
				wordMap.put(items[1], p);
				
			}
*/
			//put the last one
/**
			mgizaTrMap.put(curStr, wordMap);
			
			br.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		//System.out.println("Training End! Training Time:"+seconds+" s");
		System.err.println("load mgiza word-to-wod translation map over,use time:"+seconds+"s");
	}
    /**
     * get Translation Model Relevance Score
     * @param querys
     * @param qtexts
     */
	public static float getWordtoWordTRMap(String word1,String word2)
	{
		if(trMap==null) 		trMap=new TRMap(trPath);
		return trMap.getWordtoWordTranslationProbability(word1, word2);
		/**
		if(mgizaTrMap==null)
		{
		   loadMgizaTrMap();
		}
		if(mgizaTrMap.containsKey(word1))
		{
			HashMap<String,Float>map=mgizaTrMap.get(word1);
			if(map.containsKey(word2))
			{
				return map.get(word2);
			}
		}*/
		}
	public static float getTRScore(String[]querys,String []qtexts)
    {
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
 	/**
 		System.out.println("Querys:");
 		for(String query:querys)
 		{
 			System.out.print(query+",");
 		}
 		System.out.println("\nQuestions:");
 		for(String qtext:qtexts)
 		{
 			System.out.print(qtext+",");
 		}
 		System.out.println("");
*/
 		double score=0d;;
    	for(String query:querys)
    	{
    		float ptr=0f;
    		 HashSet<String> qSet=new HashSet<String>();
    		for(String qtext:qtexts)
    		{
    			if(qSet.contains(qtext))
    			{
    				continue;
    			}
    			qSet.add(qtext);
    			float pwd=0f;
    			if(intTFMap.containsKey(qtext))
    			{
    				pwd=(float) (intTFMap.get(qtext)*1.0/(qtexts.length));
    			}
    			double wwp=getWordtoWordTRMap(query,qtext);
    		    //System.out.println("query-to-word:\t"+query+":"+qtext+"\tp:"+wwp+";pwd:"+pwd);
    			ptr+=wwp*pwd;
    		}
    		
    		float pwc=LanguageModel.getPWC(query);
    		
    		//System.out.println("query pwc:"+pwc);
    		float p=((1-paraSmoothing)*ptr+paraSmoothing*pwc);
    		if(p==0f)
    		{
    			p=Float.MIN_VALUE;
    		}
    		double pe=Math.log(p);
    		//System.out.println("Pe:"+pe);
    		//System.err.println("Query:"+query+"\tPtr:"+ptr+"\tpwc:"+pwc+"\tp:"+p+"\tpe:"+pe);
    		score+=pe;
    	}
    	//System.out.println("Score:"+score);
    	return (float) score;
    }
	public static float getTRScorewithNormalization(String[]querys,String []qtexts)
	{
		double pqq=getTRScore(querys,querys);
		double pqd=getTRScore(querys,qtexts);
		// return (float) (pqd-pqq);
   	 	//return (float) Math.exp((pqd-pqq)); 

		//double pmax=Math.log(Float.MIN_VALUE);
   	 	//System.out.println("(TR)pqq:\t"+pqq+"\tpqd:\t"+pqd+"\tpmin:\t"+pmax);
		//return (float) ((pqd-pmax)/(pqq-pmax));
		//return (float) ((pqd-pqq)/(pmax-pqq));
		//return (float) (pqq/pqd);
		double p=pqd-pqq;
		//System.out.println("pqq:"+pqq+"\tpqd:"+pqd);
    	//return (float) p;
		return (float) p;
		//return (float) Math.exp(p);
	}
	/**
	public static void setTRMap(HashMap<String,Float>tr)
	{
		trMap=tr;
	}
	*/
	public static void setRTPara(float pa)
	{
		paraSmoothing=pa;
	}
}
