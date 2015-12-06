package com.ydjbuaa.npl.xgboost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.LCS;
import com.ydjbuaa.npl.cqa.models.LD;
import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.ModelsInitializer;
import com.ydjbuaa.npl.cqa.models.TransLM;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class GenTrainingData {
 /**
  * generate the training data and group data for xgboost rank
  * xx.train format: label featrure1:value 1 feature2:value2 ...featureN:valueN
  * 
  */
	/**
	 * read the baidu label data
	 * @param srcpath:the label data path
	 * @param dstpath:the dst file path 
	 */
	public static  void genTrainingData(String srcPath,String dstPath)
	{
		try {
			//init file stream reader
			FileInputStream fi=new FileInputStream(srcPath);
			BufferedReader br=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
			
			FileOutputStream fol=new FileOutputStream(dstPath+"baidu.train");
			FileOutputStream fog=new FileOutputStream(dstPath+"baidu.train.group");
			
			BufferedWriter bwl=new BufferedWriter(new OutputStreamWriter(fol,"UTF-8"));
			BufferedWriter bwg=new BufferedWriter(new OutputStreamWriter(fog,"UTF-8"));
			
			String line=null;
			int groupLen=0;
			String groupStr="";
			
			//init all the models
			ModelsInitializer.initializeModels();
			
			//read every record and generate group information and extract every model feature as train data
		    while((line=br.readLine())!=null)
		    {
		       String [] items=line.split("\t");
		       if(groupStr.equals(items[0]))
		       {
		    	   groupLen++;
		       }
		       else{

		    	   if(groupLen>0)
		    	   {
		    		   bwg.write(groupLen+"\n");
		    	   }
		    	   groupLen=1;
		    	   groupStr = items[0];
		       }
		       String [] querys=AnsjWordSeg.getWordsSegStrs(items[0]);
		       String [] candidates=AnsjWordSeg.getWordsSegStrs(items[1]);
		       String label=items[2];
		       bwl.append(label);
		       //language model feature
		       Float s1=LanguageModel.getLMScorewithNormalization(querys, candidates);
		       if(!s1.equals(0d))
		       {
		    	   bwl.append(" 1:"+s1);
		       }
		       
		       //BM 25 model feature
		       Float s2=BM25.getBMScore(querys, candidates);
		       if(!s2.equals(0d))
		       {
		    	   bwl.append(" 2:"+s2);
		       }
		       
		       //LCS feature
		       Float s3=LCS.getLCSScore(querys, candidates);
		       if(!s3.equals(0d))
		       {
		    	   bwl.append(" 3:"+s3);
		       }
		       //LD feature
		       Float s4=LD.getLDScore(querys, candidates);
		       if(!s4.equals(0d))
		       {
		    	   bwl.append(" 4:"+s4);
		       }
		       //TR feature
		       Float s5=TranslationModel.getTRScorewithNormalization(querys, candidates);
		       if(!s5.equals(0d))
		       {
		    	   bwl.append(" 5:"+s5);
		       }
		       Float s6=TransLM.getTransLMScore(querys, candidates);
		       if(!s6.equals(0f))
		       {
		    	   bwl.append(" 6:"+s6);
		       }
		       //write \n
		       bwl.append("\n");
		       bwl.flush();
		       //System.out.println(items[0]+"\t"+items[1]+"\t"+items[2]+"\t"+s1+"\t"+s2+"\t"+s3+"\t"+s4);
		    }
		    //write the last group information
		    bwg.write(groupLen+"\n");
			System.out.println("Over!");
		    
			//colse all the file pointer
			bwl.close();
			bwg.close();
			fol.close();
			fog.close();
			br.close();
			fi.close();
			
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
}
