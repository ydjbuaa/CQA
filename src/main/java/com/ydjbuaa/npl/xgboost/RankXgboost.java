package com.ydjbuaa.npl.xgboost;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dmlc.xgboost4j.Booster;
import org.dmlc.xgboost4j.DMatrix;
import org.dmlc.xgboost4j.util.Trainer;
import org.dmlc.xgboost4j.util.XGBoostError;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.LCS;
import com.ydjbuaa.npl.cqa.models.LD;
import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.TransLM;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;
import com.ydjbuaa.npl.cqa_maven.util.BoosterParams;
import com.ydjbuaa.npl.cqa_maven.util.LabelItem;

public class RankXgboost {

	//booster
	private static Booster rankBooster=null;
	//booster param
	private static BoosterParams param=null;
	
   private static void loadBooster(String modelPath)
   {
	   Map<String, Object> paramMap = new HashMap<String, Object>() {
           {

           	  put("eta", 0.1);
                 put("max_depth", 6);
                 put("silent", 1);
                 put("gamma",1);
                 put("min_child_weight",0.1);
                 put("objective", "rank:pairwise");
           }
       };
       Iterable<Entry<String, Object>> param = paramMap.entrySet();
	try {
		rankBooster=new Booster(param, modelPath);
		
	} catch (XGBoostError e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   public static float[][] rank(float[] features,int nrow,int ncol)
   {
	   //load booster
	   loadBooster("./data/baidu/baidu.model");
	   
	   //create DMatitix
	   try {
		DMatrix dtest=new DMatrix(features,nrow,ncol);
	
		float[][] predicts=rankBooster.predict(dtest);
		
		//System.out.println(predicts.length);
		return predicts;
	} catch (XGBoostError e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   return null;
   }
	public static void test() throws XGBoostError
	{
		System.out.println("Xgboost Rank Test:");
		  //set params
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {

            	  put("eta", 0.1);
                  put("max_depth", 6);
                  put("silent", 1);
                  put("gamma",1);
                  put("min_child_weight",0.1);
                  put("objective", "rank:pairwise");
            }
        };
        Iterable<Entry<String, Object>> param = paramMap.entrySet();
		Booster booster=new Booster(param, "./data/mq2008/2008.model");
	    DMatrix testMat = new DMatrix("./data/mq2008/mq2008.test");
	  //predict
	    float[][] predicts = booster.predict(testMat);
	    System.out.println("Predict Result:");
	    for(int i=0;i<predicts.length;i++)
	    {
	    	float[] predict=predicts[i];
	    	for(int j=0;j<predict.length;++j)
	    	{
	    		System.out.println(predict[j]);
	    	}
	    }
	}
	public static float [] predict(DMatrix dmat,String modelPath) throws XGBoostError
	{
		//load model
		 loadBooster(modelPath);
		 float [][] press=rankBooster.predict(dmat);
		 float [] pres=new float[press.length];
		 for(int i=0;i<pres.length;i++)
		 {
			 pres[i]=press[i][0];
		 }
		 return pres;
		// return rankBooster.predict(dmat);
	}
	public static void genTrainAndGroupFilewithNormalization(ArrayList<LabelItem>list,String path)
	{
		Iterator<LabelItem> it=list.iterator();
		//travel the list
		
	     ArrayList<float []>scoreList=new ArrayList<float []>();
	     ArrayList<Integer>labelList=new ArrayList<Integer>();
	     
		try {
		
			FileOutputStream fot=new FileOutputStream(path);
			FileOutputStream fog=new FileOutputStream(path+".group");
			
			BufferedWriter bwt=new BufferedWriter(new OutputStreamWriter(fot,"UTF-8"));
			BufferedWriter bwg=new BufferedWriter(new OutputStreamWriter(fog,"UTF-8"));
			
			String groupStr="";
			int groupLen=0;
			
			float s1Min=Float.MAX_VALUE;
			float s1Max=Float.MAX_VALUE*-1;
			float s2Min=Float.MAX_VALUE;
			float s2Max=Float.MAX_VALUE*-1;
			float s5Min=Float.MAX_VALUE;
			float s5Max=Float.MAX_VALUE*-1;
			float s6Min=Float.MAX_VALUE;
			float s6Max=Float.MAX_VALUE*-1;
			
			while(it.hasNext())
			{
				LabelItem lit=it.next();
				String query=lit.getQueryStr();
				String question=lit.getQuestionStr();
				
				String[] querys=AnsjWordSeg.getWordsSegStrs(query);
				String [] candidates=AnsjWordSeg.getWordsSegStrs(question);
				
				if(!groupStr.equals(query))
				{
					if(groupLen>0)
					{
						bwg.write(groupLen+"\n");
					}
					groupLen=1;
					groupStr=query;
				}
				else{
				groupLen++;
				}
				
				int label=lit.getLabel();
				labelList.add(label);
				
		     	  float [] scores=new float[6];
				 //language model feature
			       Float s1=LanguageModel.getLMScorewithNormalization(querys, candidates);
			       if(s1==0f)
			       {
			    	   s1=Float.MIN_VALUE;
			    	   //System.out.println("query:"+query+"\tquestions:"+question);
			       }
			   		float ss1=(float) Math.log(s1);
			   		if(ss1>s1Max)
			   		{
			   			s1Max=ss1;
			   		//	System.out.println("s1MAx:"+s1Max+"\ts1:"+s1);
			   		}
			   		if(ss1<s1Min)
			   		{
			   			s1Min=ss1;
			   			//System.out.println("s1Min:"+s1Min+"\ts1:"+s1);
			   		}
			       //BM 25 model feature
			       Float s2=BM25.getBMScore(querys, candidates);
			       if(s2==0f)
			       {
			    	   s2=Float.MIN_VALUE;
			       }
			       float ss2=(float) Math.log(s2);
			   		if(ss2>s2Max)
			   		{
			   			s2Max=ss2;
			   		}
			   		if(ss2<s2Min)
			   		{
			   			s2Min=ss2;
			   		}
			       //LCS feature
			       Float s3=LCS.getLCSScore(querys, candidates);
			      
			       //LD feature
			       Float s4=LD.getLDScore(querys, candidates);
			     
			       //TR feature
			       Float s5=TranslationModel.getTRScore(querys, candidates);
			       if(s5==0f)
			       {
			    	   s5=Float.MIN_VALUE;
			       }
			       float ss5=(float) Math.log(s5);
			   		if(ss5>s5Max)
			   		{
			   			s5Max=ss5;
			   		}
			   		if(ss5<s5Min)
			   		{
			   			s5Min=ss5;
			   		}
			       //transLM
			       Float s6=TransLM.getTransLMScore(querys, candidates);
			       if(s6==0f)
			       {
			    	   s6=Float.MIN_VALUE;
			       }
			       float ss6=(float) Math.log(s6);
			   		if(ss6>s6Max)
			   		{
			   			s6Max=ss6;
			   		}
			   		if(ss6<s6Min)
			   		{
			   			s6Min=ss6;
			   		}
			        scores[0]=ss1;
			        scores[1]=ss2;
			        scores[2]=s3;
			        scores[3]=s4;
			        scores[4]=ss5;
			        scores[5]=ss6;
			        
			        scoreList.add(scores);
			     //   System.out.println("s1MAX:"+s1Max+"\ts1Min:"+s1Min);
			}
			  bwg.write(groupLen+"\n");
			  bwg.close();
			  fog.close();
			Iterator<float[]> sit=scoreList.iterator();
			Iterator<Integer> iit=labelList.iterator();
			
			while((sit.hasNext())&&(iit.hasNext()))
			{
			    float[] score=sit.next();
			    int label=iit.next();
			    
			    
			    float s1=score[0];
			    float ns1=(s1-s1Min)/(s1Max-s1Min);
			    assert(ns1<=1&&ns1>=0);
			    float s2=score[1];
			    float ns2=(s2-s2Min)/(s2Max-s2Min);
			    assert(ns2<=1&&ns2>=0);
			    float s5=score[4];
			    float ns5=(s5-s5Min)/(s5Max-s5Min);
			    assert(ns5<=1&&ns5>=0);
			    float s6=score[5];
			    float ns6=(s6-s6Min)/(s6Max-s6Min);
			    assert(ns6<=1&&ns6>=0);
			    bwt.append(label+"");
			    if(ns1!=0f)
			    {
			    	bwt.append(" 1:"+ns1);
			    }
			    if(ns2!=0f)
			    {
			    	bwt.append(" 2:"+ns2);
			    }
			    if(score[2]!=0f)
			    {
			    	bwt.append(" 3:"+score[2]);
			
			    }
			    if(score[3]!=0f)
			    {
			    	bwt.append(" 4:"+score[3]);
			    }
			    if(ns5!=0f)
			    {
			    	bwt.append(" 5:"+ns5);
			    }
			    if(ns6!=0f)
			    {
			    	bwt.append(" 6:"+ns6);
			    }
			    bwt.append("\n");
			    bwt.flush();
			}
		
		    bwt.flush();
		    bwt.close();
		    fot.close();
		    
		   // System.out.println("Wirte "+path+" and group file over!");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//generate the train data file and group file from List
	public static void genTrainAndGroupFile(ArrayList<LabelItem>list,String path)
	{
		Iterator<LabelItem> it=list.iterator();
		//travel the list
		
		try {
		
			FileOutputStream fot=new FileOutputStream(path);
			FileOutputStream fog=new FileOutputStream(path+".group");
			
			BufferedWriter bwt=new BufferedWriter(new OutputStreamWriter(fot,"UTF-8"));
			BufferedWriter bwg=new BufferedWriter(new OutputStreamWriter(fog,"UTF-8"));
			
			String groupStr="";
			int groupLen=0;
			int qid=0;
			while(it.hasNext())
			{
				LabelItem lit=it.next();
				String query=lit.getQueryStr();
				String question=lit.getQuestionStr();
				
				String[] querys=AnsjWordSeg.getWordsSegStrs(query);
				String [] candidates=AnsjWordSeg.getWordsSegStrs(question);
				
				if(!groupStr.equals(query))
				{
					if(groupLen>0)
					{
						bwg.write(groupLen+"\n");
					}
					qid++;
					groupLen=1;
					groupStr=query;
				}
				else{
				groupLen++;
				}
				  //add label
				  bwt.append(lit.getLabel()+" ");
				//  bwt.append(" qid:"+qid);
				 //language model feature
			       Float s1=LanguageModel.getLMScorewithNormalization(querys, candidates);
			       if(!s1.equals(0d))
			       {
			    	   bwt.append(" 1:"+s1);
			       }
			       
			       //BM 25 model feature
			       Float s2=BM25.getBMScore(querys, candidates);
			       if(!s2.equals(0d))
			       {
			    	   bwt.append(" 2:"+s2);
			       }
			       
			       //LCS feature
			       Float s3=LCS.getLCSScore(querys, candidates);
			       if(!s3.equals(0d))
			       {
			    	   bwt.append(" 3:"+s3);
			       }
			       //LD feature
			       Float s4=LD.getLDScore(querys, candidates);
			       if(!s4.equals(0d))
			       {
			    	   bwt.append(" 4:"+s4);
			       }
			       //TR feature
			       Float s5=TranslationModel.getTRScore(querys, candidates);
			       if(!s5.equals(0d))
			       {
			    	   bwt.append(" 5:"+s5);
			       }
			       //transLM
			       Float s6=TransLM.getTransLMScore(querys, candidates);
			       if(!s6.equals(0d))
			       {
			    	   bwt.append(" 6:"+s6);
			       }
			       //write \n
			       bwt.append("\n");
			       bwt.flush();
			}
		    bwg.write(groupLen+"\n");
		    bwt.close();
		    bwg.close();
		    fot.close();
		    fog.close();
		   // System.out.println("Wirte "+path+" and group file over!");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void train(DMatrix trainMat,String modelPath,BoosterParams param,int round) throws XGBoostError
	{
		 //System.out.println("Xgboost Rank Training:");
		long startTime=System.currentTimeMillis();
		
		
		 
		 //load the trainMat from  trianfile
		 //DMatrix trainMat=new DMatrix(trainPath);
		 //DMatrix testMat=new DMatrix(testPath);
		 
		 //set watchList
	        List<Entry<String, DMatrix>> watchs =  new ArrayList<>();
	       watchs.add(new AbstractMap.SimpleEntry<>("train", trainMat));
	      //  watchs.add(new AbstractMap.SimpleEntry("test", testMat));
	      
	        
	        //set round
	        //int round = 4;
	        
	        Booster booster=Trainer.train(param, trainMat, round, watchs, null, null);
	        
	        //saved model
	        
	        //save train model
	        booster.saveModel(modelPath);
	        
			long endTime=System.currentTimeMillis();
			float seconds = (endTime - startTime) / 1000F;
			System.out.println("Train Time:"+seconds+" s");
	}
	public static void train(String trainPath,String savedPath) throws XGBoostError
	{
		System.err.println("Xgboost Rank Training:");
		long startTime=System.currentTimeMillis();
		
		DMatrix trainMat=new DMatrix(trainPath);
		  //set params
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {
                //put("eta", 0.1);
                put("max_depth", 6);
                put("gamma",1);
                put("min_child_weight",0.1);
                put("objective", "rank:pairwise");
            }
        };
       Iterable<Entry<String, Object>> pa = paramMap.entrySet();
       
        //set watchList
        List<Entry<String, DMatrix>> watchs =  new ArrayList<>();
        watchs.add(new AbstractMap.SimpleEntry<>("test", trainMat));
      
        //set round
        int round = 4;
        //train a boost model
        Booster booster = Trainer.train(pa, trainMat, round, watchs, null, null);
        
        //save train model
        booster.saveModel(savedPath);
        
        
		long endTime=System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		//System.out.println("Train Time:"+seconds+" s");
	}
	
}
