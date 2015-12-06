package com.ydjbuaa.npl.xgboost;

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
import java.util.ArrayList;
import java.util.Iterator;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.LCS;
import com.ydjbuaa.npl.cqa.models.LD;
import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.TransLM;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa.models.VSM;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;
import com.ydjbuaa.npl.cqa_maven.util.BoosterParams;
import com.ydjbuaa.npl.cqa_maven.util.LabelItem;

public class XgBoost {

	private final static String XGBOOST="./xgboost/xgboost";
	
	
	public static float[] predict(float[][] features) throws IOException
	{
		String modelPath="./xgboost/baidu.model";
		String predPath="./xgboost/pred.txt";
		String fetPath="./xgboost/predict";
		
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fetPath),"UTF-8"));
		//write dmat to the file
		for(int i=0;i<features.length;i++)
		{
			float [] it=features[i];
			String line="1 ";
			for(int j=0;j<it.length;j++)
			{
				if(it[j]!=0f)
				line+=(j+1)+":"+it[j]+" ";
			}
			bw.write(line+"\n");
		}
		bw.close();
		BufferedWriter gbw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fetPath+".group"),"UTF-8"));
		gbw.write(features.length+"\n");
		gbw.close();
		
		BoosterParams param = new BoosterParams();
		param.put("eta", 0.14f);
		param.put("max_depth", 3);
		param.put("gamma", 100f);
		param.put("min_child_weight", 1f);
		param.put("objective", "rank:pairwise");
		param.put("num_round", 4);
		
		String confPath="./xgboost/baidu.conf";
		
		param.genConfFile(confPath, fetPath, modelPath, true, predPath);
		deleteBufferFile(fetPath+".buffer");
		
		String[] execString = new String[] { XGBOOST, confPath };
		//run xgboost 
		Process process = Runtime.getRuntime().exec(execString);
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader(ir);
		String line = null;
		while ((line = input.readLine()) != null) {
			System.err.println(line);
		}
		InputStreamReader eir = new InputStreamReader(process.getErrorStream());
		BufferedReader einput = new BufferedReader(eir);

		String eline = null;

		while ((eline = einput.readLine()) != null) {

			System.err.println(eline);
		}

		
		BufferedReader preBr=new BufferedReader(
				new InputStreamReader(
						new FileInputStream(predPath),"UTF-8"));
		String pline=null;
		ArrayList<Float> pList=new ArrayList<Float>();
		while((pline=preBr.readLine())!=null)
		{
		      String [] pps=pline.split(" ");
		    	Float p=Float.parseFloat(pps[0]);
		    	pList.add(p);
		
		}
		float [] pres=new float[pList.size()];
		for(int i=0;i<pList.size();i++)
		{
			pres[i]=pList.get(i);
		}
		return pres;
	}
	public static void train(String xgPath, String dataPath, String modelPath,
			BoosterParams params) throws IOException {
		System.err.println("xgboost train run......");

		long startTime = System.currentTimeMillis();
		// generate config file
		String confPath = xgPath + "baidu.train.conf";
		//delete buffered file
		deleteBufferFile(dataPath+".buffer");
		
		params.genConfFile(confPath, dataPath, modelPath, false,null);
		String[] execString = new String[] { XGBOOST, confPath };
		Process process = Runtime.getRuntime().exec(execString);

		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader(ir);
		String line = null;
		while ((line = input.readLine()) != null) {
			System.err.println(line);
		}
		InputStreamReader eir = new InputStreamReader(process.getErrorStream());
		BufferedReader einput = new BufferedReader(eir);

		String eline = null;

		while ((eline = einput.readLine()) != null) {

			System.err.println(eline);
		}

		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("xgboost train over, use time:" + seconds + "s");

	}

	public static float[] predict(String xgPath, String dataPath,
			String modelPath, BoosterParams params) throws IOException {
		
		System.err.println("xgboost predict run......");
		long startTime = System.currentTimeMillis();
		// generate config file
		String confPath = xgPath + "baidu.test.conf";
		//delete buffered file
		deleteBufferFile(dataPath+".buffer");
		
		params.genConfFile(confPath, dataPath, modelPath, true,xgPath+"pred.txt");
		String[] execString = new String[] { XGBOOST, confPath };
		Process process = Runtime.getRuntime().exec(execString);
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader(ir);
		String line = null;
		while ((line = input.readLine()) != null) {
			System.err.println(line);
		}
		InputStreamReader eir = new InputStreamReader(process.getErrorStream());
		BufferedReader einput = new BufferedReader(eir);

		String eline = null;

		while ((eline = einput.readLine()) != null) {

			System.err.println(eline);
		}

		//read pred.txt
		
		BufferedReader preBr=new BufferedReader(
				new InputStreamReader(
						new FileInputStream(xgPath+"pred.txt"),"UTF-8"));
		String pline=null;
		ArrayList<Float> pList=new ArrayList<Float>();
		while((pline=preBr.readLine())!=null)
		{
		      String [] pps=pline.split(" ");
		    	Float p=Float.parseFloat(pps[0]);
		    	pList.add(p);
		
		}
		float [] pres=new float[pList.size()];
		for(int i=0;i<pList.size();i++)
		{
			pres[i]=pList.get(i);
		}
		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("xgboost predict over, use time:" + seconds + "s");
       //Float[] pres=pList.toArray(new Float[0]);
        return pres;
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
					 // bwt.append(" qid:"+qid);
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
				     //  Float s5=TranslationModel.getTRScorewithNormalization(querys, candidates);
				       Float s5=TranslationModel.getTRScore(querys, candidates);
				       if(!s5.equals(0d))
				       {
				    	   bwt.append(" 5:"+s5);
				       }
				       //transLM
				      // Float s6=TransLM.getTransLMScorewithNormalization(querys, candidates);
				       Float s6=TransLM.getTransLMScore(querys, candidates);
				       if(!s6.equals(0d))
				       {
				    	   bwt.append(" 6:"+s6);
				       }
				       // LCS String 
				       Float s7=LCS.getLCSStrScore(querys, candidates);
				       if(!s7.equals(0d))
				       {
				    	   bwt.append(" 7:"+s7);
				       }
				       Float s8=VSM.getVSMScore(querys, candidates);
						if(!s8.equals(0d))
						{
							bwt.append(" 8:"+s8);
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
		private static void deleteBufferFile(String filePath)
		{
			System.err.println(filePath);
			File file=new File(filePath);
			if(file.isFile()&&file.exists())
			{
				file.delete();
			}
		}
}
