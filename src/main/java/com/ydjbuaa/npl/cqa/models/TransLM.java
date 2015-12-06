package com.ydjbuaa.npl.cqa.models;

import java.util.HashMap;

public class TransLM {
    //  an implement for translation-based language model
	
	// Translation-based language model parameters
	
	private static float paraA=0.44f;
	private static float paraB=0.5f;
	
	public static float getTransLMScore(String []querys,String [] questions)
	{
	
		HashMap<String,Integer>intTFMap=new HashMap<String,Integer>();
 		for(String word:questions)
 		{
 			if(intTFMap.containsKey(word))
 			{
 				intTFMap.put(word, intTFMap.get(word)+1);
 			}
 			else{
 				intTFMap.put(word,1);
 			}
 		}
 		double score=0d;
 		for(String query:querys)
 		{
 			double Ptr=0d;
 			for(String question:questions)
 			{
 				double p=0d;
 				if(intTFMap.containsKey(question))
 				{
 					p=intTFMap.get(question)*1.0d/questions.length;
 				}
 				Ptr+=TranslationModel.getWordtoWordTRMap(query, question)*p;
 			}
 			double Pml=0d;
 			if(intTFMap.containsKey(query))
				{
					Pml=intTFMap.get(query)*1.0d/questions.length;
				}
 			double Pwc=LanguageModel.getPWC(query);
 			double pqd=(1-paraA)*((1-paraB)*Pml+paraB*Ptr)+paraA*Pwc;
 			//System.err.println("pml:\t"+Pml+"\tptr:\t"+Ptr+"\tpqd:\t"+pqd);
 			if(pqd==0d)
 			{
 				pqd=Double.MIN_VALUE;
 			}
 			double pe=Math.log(pqd);
 			score+=pe;
 		}
		return (float) score;
	}
	public static float getTransLMScorewithNormalization(String []querys,String [] questions)
	{
		float pqq=getTransLMScore(querys, querys);
		float pqd=getTransLMScore(querys, questions);
   	 	double p=pqd-pqq;
    	return (float) p;
    	//return (float) Math.exp((pqd-pqq)); 	
	
	}
	public static void setParams(float a,float b)
	{
		paraA=a;
		paraB=b;
	}
}
