package com.ydjbuaa.npl.cqa.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class TF_IDFTrain {

	private static HashMap<String,Integer >gWordCountMap=null;
	private static HashMap<String,Integer> gWordDocsMap=null;
	private static long gTotalWordCount=0;
	private static long gTotalDocsCount=0;
	/**
	 * train tf idf map for language model and BM 25 model
	 * @param srcPath:train data
	 * @param dstPath: generated tf idf map path
	 */
	public static void train(String srcPath,String dstPath)
	{
		long startTime = System.currentTimeMillis();
		System.out.println("Training Start!");
        //init wordCountMap and totalWordCount
		gWordCountMap=new HashMap<String,Integer>();
		gWordDocsMap=new HashMap<String,Integer>();
		
		gTotalWordCount=0;
		gTotalDocsCount=0;
		
		readDirs(srcPath);
		
		//write the tf(w,coll) and tf(w,d) and idf(w)
				try {
					
					FileOutputStream fo=new FileOutputStream(dstPath);
					BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fo,"UTF-8"));
					bw.write("TotalDocsCount\t"+gTotalDocsCount+"\n");
					bw.write("TotalWordsCount\t"+gTotalWordCount+"\n");
					bw.write("WordTFinCollection\t"+gWordCountMap.size()+"\n");

					java.util.Iterator docsiter=gWordCountMap.entrySet().iterator();
					while(docsiter.hasNext())
					{
						Map.Entry<String, Integer> entry=(Entry<String, Integer>)docsiter.next();
						bw.write(entry.getKey().toString()+"\t"+Float.parseFloat(entry.getValue().toString())/gTotalWordCount+"\n");
					}
					Iterator it=gWordDocsMap.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry<String, Integer> entry=(Entry<String, Integer>) it.next();
						Double idf=Math.log((gTotalDocsCount-entry.getValue()+0.5)/(entry.getValue()+0.5));
						bw.write(entry.getKey().toString()+"\t"+idf+"\n");
					}
					bw.close();
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
				System.out.println("Training End! Training Time:"+seconds+" s");
	}
	private static void readDirs(String path)
	{
		 
				//list path
				File curfile=new File(path);
				File [] subfiles=curfile.listFiles();
				for (File subfile:subfiles)
				{
					if(subfile.isDirectory())
					{
						readDirs(subfile.getAbsolutePath());
					}
					else if(subfile.getName().endsWith("Question.dat")) //question content
					{
						
						try {
							//read the question file
							readFile(subfile.getAbsolutePath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
	}
	private static void readFile(String path) throws IOException
	{
		 System.out.println("Reading Training Data from:"+path);
		 FileInputStream fi=new FileInputStream(path);
		 BufferedReader qbr=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
		 String qline=null;
		 while((qline=qbr.readLine())!=null)
		 {
			 String [] qitems=qline.split("\t");
			 gTotalDocsCount+=1;
			 
			 String [] qwords=AnsjWordSeg.getWordsSegStrs(qitems[2]);
			 HashSet<String> docSet=new HashSet<String>();
			 for(String word:qwords)
			 {
				 if(word=="")
					 continue;
				 //add the word to the set 
				 docSet.add(word);
				 if(gWordCountMap.containsKey(word))
				 {
					 gWordCountMap.put(word, gWordCountMap.get(word)+1);
				 }
				 else{
					 gWordCountMap.put(word,  1);
				 }
				 gTotalWordCount+=1;
			 }
			 Iterator<String> it=docSet.iterator();
			 while(it.hasNext())
			 {
				 String word=it.next().toString();
				 if(gWordDocsMap.containsKey(word))
				 {
					 gWordDocsMap.put(word,gWordDocsMap.get(word)+1);
				 }
				 else{
					 gWordDocsMap.put(word, 1);
				 }
			 }
		 }
		 qbr.close();
	     fi.close();
	}
}
