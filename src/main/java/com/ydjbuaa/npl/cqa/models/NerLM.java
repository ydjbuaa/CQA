package com.ydjbuaa.npl.cqa.models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

import edu.stanford.nlp.ie.AbstractSequenceClassifier; 
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel; 
public class NerLM {
	private static float pa=0.6f;
	private static AbstractSequenceClassifier<CoreLabel> nerClassifier=null;
	final static String serializedClassifier = "./library/classifiers/chinese.misc.distsim.crf.ser.gz"; // chinese.misc.distsim.crf.ser.gz
	private static HashMap<String, Float> weightMap=null;
	private static void initNerClassfiler() throws Exception
	{
		nerClassifier=CRFClassifier.getClassifier(serializedClassifier);
		weightMap=new HashMap<String,Float>();
		weightMap.put("PERSON", 1.3f);
		weightMap.put("GPE", 1.8f);
		weightMap.put("LOC", 1.6f);
		weightMap.put("ORG", 1.5f);
		weightMap.put("MISC", 1.1f);
	}
	public static float getNerLMScore(String  querys,String questions) throws Exception
	{
		String querySegStr=AnsjWordSeg.getWordsSegString(querys);
		String questionSegStr=AnsjWordSeg.getWordsSegString(questions);
		
		ArrayList<WeightWord> queryList=getNerWeight(querySegStr);
		ArrayList<WeightWord> questionList=getNerWeight(questionSegStr);
		
		HashMap<String, Integer> tfMap=new HashMap<String,Integer>();
		int wordCount=0;
		for(int i=0;i<questionList.size();i++){
			String word=questionList.get(i).getWord();
			if(!AnsjWordSeg.isStopWord(word)) wordCount++;
			if(tfMap.containsKey(word)) tfMap.put(word, tfMap.get(word)+1);
			else tfMap.put(word, 1);
		}
		double score=0;
		for(int i=0;i<queryList.size();i++)
		{
			String word=queryList.get(i).getWord();
			float weight=queryList.get(i).getWeight();
			if(AnsjWordSeg.isStopWord(word)) continue;
			double pwd=0;
			if(tfMap.containsKey(word)) pwd=(weight*tfMap.get(word))/wordCount;///queryList.size();
			double pwc=LanguageModel.getPWC(word);
			double p=(1-pa)*pwd+pa*pwc;
			//punish item
			//if(pwd==0) p/=(weight*10);
			if(p==0f)
 	        {
 	        	p=Double.MIN_VALUE;
 	        }
			//System.out.println("word:\t"+word+"\tpwd:\t"+pwd+"\tpwc:\t"+pwc);
			//score+=Math.log(p);
			
			//if(pwd==0) score+=Math.log(p)*weight;
			score+=Math.log(p);

		}
		return (float) score;
	}
	public static void setParam(float a)
	{
		pa=a;
	}
	public static float getNerLMScorewithNormalization(String query,String question) throws Exception
	{
		float pqq=getNerLMScore(query, query);
		float pqd=getNerLMScore(query, question);
    	float p=pqd-pqq;

//		/System.out.println(p+"\t"+query+"\t"+question);
    	//return p;
    	return (float) Math.exp(p); 
	}
	public static ArrayList<WeightWord> getNerWeight(String sentence) throws Exception
	{
	     ArrayList<WeightWord> wwList=new ArrayList<WeightWord>();
//	     /System.out.println(sentence);
		//init the ner classifiler
		if(nerClassifier==null) initNerClassfiler();
		List<List<CoreLabel>> out = nerClassifier.classify(sentence);
		for (List<CoreLabel> s : out) {
			for (CoreLabel word : s) {
				String label=word.get(CoreAnnotations.AnswerAnnotation.class);
				float weight=1f;
				if(!label.equals("O"))
				{
					//named entry 
					weight=1.1f;
					if(weightMap.containsKey(label)) weight=weightMap.get(label);
					//System.out.print(word.word() + '/' +label + '/'+weight+" ");
				}
				wwList.add(new WeightWord(word.word(),weight));
				//System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
			}
			//System.out.println();
		}
	   //System.out.println("------");
	   return wwList;
	}
     public static float getNerCrossScore(String query,String question) throws Exception
     {
    	 String querySegStr=AnsjWordSeg.getWordsSegString(query);
 		String questionSegStr=AnsjWordSeg.getWordsSegString(question);
 		
 		ArrayList<WeightWord> queryList=getNerWeight(querySegStr);
 		ArrayList<WeightWord> questionList=getNerWeight(questionSegStr);
 		
 		HashSet<String> querySet=new HashSet<String>();
 		for(int i=0;i<queryList.size();i++) 
 		{
 			if(queryList.get(i).getWeight()<=1.0f) continue;
 				querySet.add(queryList.get(i).getWord());
 			}
 		
 		int mix_num=0;
 		HashSet<String> questionSet=new HashSet<String>();
 		for(int i=0;i<questionList.size();i++) 
 		{
 			if(questionList.get(i).getWeight()<=1.0f) continue;
 			if(querySet.contains(questionList.get(i).getWord())) mix_num++;
 			questionSet.add(questionList.get(i).getWord());
 		}
 		
 		int add_num=querySet.size()+questionSet.size()-mix_num;
 		
 		float f=0f;
 		if(add_num>0)f=(mix_num*1.0f)/add_num;
 		/**
 		for(String s:querySet) System.out.print(s+"\t");
 		System.out.println("");
 		for(String s:questionSet) System.out.print(s+"\t");
 		System.out.println("\n"+add_num+"\tScore"+f+"-------------------------");
 		*/
 		return f;
 		
     }
}
class WeightWord
{
	private String word;
	private float  weight;
	public WeightWord(String s,float w)
	{
		this.weight=w;
		this.word=s;
	}
	public String getWord()
	{
		return this.word;
	}
	public float getWeight()
	{
		return this.weight;
	}
}