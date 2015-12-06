package com.ydjbuaa.npl.cqa.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
public class NCross {
	private static  int TOP_N=3;
	public static void setTopN(int n)
	{
		TOP_N=n;
	}
	public static float getTopNCrossScore(String [] querys,String [] questions)
	{
		HashMap<String, Float> queryHashMap=new HashMap<String,Float>();
		HashMap<String,Float> questionHashMap=new HashMap<String,Float>();
		for(String q:querys)
		{
			float w=LanguageModel.getPWC(q)*BM25.getIDF(q);
			queryHashMap.put(q, w);
		}
		for(String q:questions)
		{
			//float w=
			float w=LanguageModel.getPWC(q)*BM25.getIDF(q);
			questionHashMap.put(q, w);
		}
/**
		Iterator<Entry<String, Float>> sit=queryHashMap.entrySet().iterator();
		while(sit.hasNext())
		{
			Entry<String, Float> e=sit.next();
			System.out.println("Str:\t"+e.getKey()+"\tWeight:\t"+e.getValue());
		}
		
		Iterator<Entry<String, Float>> tit=queryHashMap.entrySet().iterator();
		while(tit.hasNext())
		{
			Entry<String, Float> e=tit.next();
			System.out.println("Str:\t"+e.getKey()+"\tWeight:\t"+e.getValue());
		}
		*/
		ArrayList<String> querylist=new ArrayList<String>();
		ArrayList<String> questionlist=new ArrayList<String>();
		//get top N query String
		for(int i=0;i<TOP_N;i++)
		{
			Iterator<Entry<String, Float>> it=queryHashMap.entrySet().iterator();
			if(!it.hasNext()) break;    // size is less than N
			float f=0f;
			String maxString="";
			while(it.hasNext())
			{
				Entry<String, Float> entry=it.next();
				if(entry.getValue()>f)
				{
					maxString=entry.getKey();
					f=entry.getValue();
				}
			}
			querylist.add(maxString);
			queryHashMap.remove(maxString);			
		}
		//get top N question string
		for(int i=0;i<TOP_N;i++)
		{
			Iterator<Entry<String, Float>> it=questionHashMap.entrySet().iterator();
			if(!it.hasNext()) break;    // size is less than N
			float f=0f;
			String maxString="";
			while(it.hasNext())
			{
				Entry<String, Float> entry=it.next();
				if(entry.getValue()>f)
				{
					maxString=entry.getKey();
					f=entry.getValue();
				}
			}
			questionlist.add(maxString);
			questionHashMap.remove(maxString);			
		}
		int mix_num=0;
		for(int i=0;i<querylist.size();i++)
		{
			for(int j=0;j<questionlist.size();j++)
			{
				if(querylist.get(i).equals(questionlist.get(j)))
				{
					mix_num++;
					break;
				}
			}
		}
		int add_num=querylist.size()+questionlist.size()-mix_num;
		/**
		for(String q:querylist)
		{
			System.out.print("@"+q+"@");
		}
		System.out.println("");
		for(String q:questionlist)
		{
			System.out.print("@"+q+"@");
		}
		System.out.println("");
		*/
		if(add_num==0) return 0f;
		float  s=(float) (mix_num*1.0/add_num);
		//System.out.println("Mix Num:\t"+mix_num+"Score:\t"+s);
		return s;		
	}
}
