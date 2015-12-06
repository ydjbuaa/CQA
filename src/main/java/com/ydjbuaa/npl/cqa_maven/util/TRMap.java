package com.ydjbuaa.npl.cqa_maven.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class TRMap {
	
	private String path=null;
	// src words strword to integer map
     private HashMap<String, Integer> srcVcbMap=null;
     //trg words string word to integer map
     private HashMap<String, Integer> trgVcbMap=null;
     // word to word translation probabilities
     private HashMap<Integer,HashMap<Integer,Float>> trMap=null;
     // read Vcb file and get string-to-int map
     private HashMap<String, Integer> getStringtoIntMap(String path)
     {
    	 HashMap<String,Integer> map=new HashMap<String,Integer>();
    	 try {
			BufferedReader br=new BufferedReader(
					 new InputStreamReader(
							 new FileInputStream(path), "UTF-8"));
			String line=null;
			while((line=br.readLine())!=null)
			{
			    String [] items =line.split(" ");
			    if(items.length!=3)
			    {
			    	System.err.println("Vcb line err:"+line);
			    	continue;
			    }
			    Integer num=Integer.parseInt(items[0]);
			    map.put(items[1], num);
			}
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
    	 return map;
     }
     //init all kinds of map by reading file
     public void initTRMap()
     {
    	 long startTime = System.currentTimeMillis();
    	 System.err.println("load word-to-word translation probabilities......");
    	 //set  path
    	 String srcPath=this.path+"src.vcb";
    	 String trgPath=this.path+"trg.vcb";
    	 String wtwPath=this.path+"tr.final.dic";
    	 //get map
    	 this.srcVcbMap=getStringtoIntMap(srcPath);
    	 this.trgVcbMap=getStringtoIntMap(trgPath);
    	 this.trMap=getWordtoWordTRMap(wtwPath);
    	//cul use time
    	 long endTime = System.currentTimeMillis();
 	   	float seconds = (endTime - startTime) / 1000F;
 		//System.out.println("Training End! Training Time:"+seconds+" s");
 		//System.out.println("use time:"+seconds+"s");
 		System.err.println("load mgiza word-to-wod translation map over,use time:"+seconds+"s");
     }
     public TRMap(String path)
     {
 		this.path=path;
     }
     
     public Float getWordtoWordTranslationProbability(String srcWord,String trgWord)
     {
    	 if(trMap==null)
		 {
			 //load tr map
			 initTRMap();
		 }
    	 //if(srcWord.equals(trgWord)) return 1f;
    	 if(srcVcbMap.containsKey(srcWord)&&trgVcbMap.containsKey(trgWord))
    	 {
    		 int srcInt=srcVcbMap.get(srcWord);
    		 int trgInt=trgVcbMap.get(trgWord);
    		 if(srcInt==trgInt) return 1f;
    		 if(trMap.containsKey(srcInt))
    		 {
    			 HashMap<Integer, Float> wordMap=trMap.get(srcInt);
    			 if(wordMap.containsKey(trgInt))
    			 {
    				 return wordMap.get(trgInt);
    			 }
    		 }
    	 }
    	 return 0f;
     }
     private HashMap<Integer, HashMap<Integer, Float>> getWordtoWordTRMap(String path)
     {
    	 //鍥炴敹绯荤粺鍨冨溇
    	 System.gc();
    	 HashMap<Integer, HashMap<Integer,Float>> map=new 	 HashMap<Integer, HashMap<Integer,Float>>();
    		try {
    			
    			BufferedReader br=new BufferedReader(
    					new InputStreamReader(
    							new FileInputStream(path),"UTF-8"));
    			
    			String line=null;
    			int  curItem=-1;
    			HashMap<Integer,Float> wordMap=null;
    			
    			while((line=br.readLine())!=null)
    			{
    				String [] items=line.split("\t");
    				if(items.length!=3)
    				{
    					System.err.println("Tr line err:"+line);
    				}
    				Float p=Float.parseFloat(items[2]);
    				Integer w1=Integer.parseInt(items[0]);
    				Integer w2=Integer.parseInt(items[1]);
    				if(curItem!=w1) //new one
    				{
    					if(curItem!=-1)  //not none before
    					{
    						map.put(curItem, wordMap);
    					}
    					curItem=w1;
    					wordMap=new HashMap<Integer,Float>();
    				}
    				wordMap.put(w2, p);
    			}
    			//put the last one
    			map.put(curItem, wordMap);
    			
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
    	 return map;
     }
}
