package com.ydjbuaa.npl.cqa.models;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

/**
 * this class is for loading train data for different models ,such as tf-idf for language model
 * @author Think
 *
 */
public class ModelsInitializer {

	private final static String tfidfMapPath="./library/tf-idf.map";
	private final static String trPath = "./library/tr/";
	public static void initializeModels()
	{
		//first load tf-idf map
		initTF_IDFMap();
		//load tr map
		TranslationModel.setTRPath(trPath);
	}


	public 	 static void initTF_IDFMap()
	{
        System.err.println("load tf-idf map ......");
        long startTime = System.currentTimeMillis();
		HashMap<String,Double> tfMap=new HashMap<String,Double>();
		HashMap<String,Double>idfMap=new HashMap<String,Double>();
		try {
			
			FileInputStream fi=new FileInputStream(tfidfMapPath);
			BufferedReader br=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
			String line=null;
			
			//read docs count
			line=br.readLine();
			long gTotalDocsCount=Long.parseLong(line.split("\t")[1]);
			//read words count
			line=br.readLine();
			long gTotalWordsCount=Long.parseLong(line.split("\t")[1]);
			
			//get avgdl
			double avgDL=gTotalWordsCount*1.0/gTotalDocsCount;
			
			//read tf count ,skip it
			line=br.readLine();
			long TFCount=Long.parseLong(line.split("\t")[1]);
			int i=0;
			while((line=br.readLine())!=null&&(i<TFCount))
			{
				i++;
				String [] items=line.split("\t");
				tfMap.put(items[0], Double.parseDouble(items[1]));
			}
			while((line=br.readLine())!=null)
			{
				String [] items=line.split("\t");
				idfMap.put(items[0], Double.parseDouble(items[1]));
			}
			br.close();
			fi.close();
			
			//set tf map for the language model	
			LanguageModel.setWordsTFMap(tfMap);
			
			//set idf map and avgdl
			BM25.setIDFMapandAvgDL(avgDL, idfMap);
			
			
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
		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("load tf-idf map over,use time:"+seconds+"s");
	}
}
