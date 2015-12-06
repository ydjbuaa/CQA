package com.ydjbuaa.npl.cqa_maven.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProbabilityTablePreprocess {
    //preprocess the word-to-word probablities table train by mgiza ,including remove stop words and low level probability
	public static void preprocess(String srcPath,String dstPath)
    {
		  long startTime = System.currentTimeMillis();
		   
		    try {
				
		    	BufferedReader br=new BufferedReader(
						new InputStreamReader(
								new FileInputStream(srcPath),"UTF-8"));
				
				BufferedWriter bw=new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(dstPath),"UTF-8"));
				
				String line=null;
				int writeCount=1;
				
			//	HashMap<String,Double> wstable=new HashMap<String,Double>();
			//	HashMap<String,Double> wtable=new HashMap<String,Double>();
				
				while((line=br.readLine())!=null)
				{
					//System.out.println(line);
					String [] items=line.split("\t");
					String w1=items[0];
					String w2=items[1];
					float p=Float.parseFloat(items[2]);
					if(!(AnsjWordSeg.isStopWord(w1)||AnsjWordSeg.isStopWord(w2)))
					{
						if(p>0.0001f)
						{
							bw.append(w1+"\t"+w2+"\t"+p+"\n");	
							writeCount++;
					     }
					}
					if(writeCount%10000==0)
					{
						System.out.println("write "+writeCount+"line.");
						bw.flush();
						writeCount=1;
					}
				}
				
				
				bw.flush();
				bw.close();
				br.close();
			}
		    catch (ArrayIndexOutOfBoundsException e)
		    {
		    	e.printStackTrace();
		    }
		    catch (UnsupportedEncodingException e) {
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
			System.err.println("read write over,use time:"+seconds+"s");
    }
	public static void preprocesswithNormalizaion(String srcPath,String dstPath)
     {
		    long startTime = System.currentTimeMillis();
		   
		    try {
				
		    	BufferedReader br=new BufferedReader(
						new InputStreamReader(
								new FileInputStream(srcPath),"UTF-8"));
				
				BufferedWriter bw=new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(dstPath),"UTF-8"));
				
				String line=null;
				int writeCount=1;
				
				HashMap<String,Double> wstable=new HashMap<String,Double>();
				HashMap<String,Double> wtable=new HashMap<String,Double>();
				
				while((line=br.readLine())!=null)
				{
					//System.out.println(line);
					String [] items=line.split(" ");
					String w1=items[0];
					String w2=items[2];
					double p=Float.parseFloat(items[4]);
					if(!(AnsjWordSeg.isStopWord(w1)||AnsjWordSeg.isStopWord(w2)))
					{
						if(p>0.00001)
						{
							wstable.put(w1+"\t"+w2, p);
							if(wtable.containsKey(w1))
							{
								wtable.put(w1, wtable.get(w1)+p);
							}
							else{
								wtable.put(w1,p);
							}
					     }
					}
				}
				//normalize
				//write to new_tr.dic
				Iterator<Map.Entry<String,Double> >it=wstable.entrySet().iterator();
				while(it.hasNext())
				{
					Map.Entry<String, Double> pit=it.next();
					String [] ww=pit.getKey().split("\t");
					double pw=wtable.get(ww[0]);
					bw.append(pit.getKey()+"\t"+pit.getValue()/pw+"\n");
					writeCount++;
					if(writeCount%10000==0)
					{
						System.out.println("write "+writeCount+"line.");
						bw.flush();
						writeCount=1;
					}
				}
				
				bw.flush();
				bw.close();
				br.close();
			}
		    catch (ArrayIndexOutOfBoundsException e)
		    {
		    	e.printStackTrace();
		    }
		    catch (UnsupportedEncodingException e) {
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
			System.err.println("read write over,use time:"+seconds+"s");
     }
}
