package com.ydjbuaa.npl.cqa_maven.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ModelDataList {
	// language model 、bm25、LCS、LD、TR、TransLm、7:TopNCross、8:Word2Vector
	// 9 NerLM ,10 NerCross
	private final int MODEL_SIZE=10;
	private int data_size;
	private float [] [] modelValueMatrix=null;
	private String  [] queryList=null;
	private String [] questionList=null;
	private String [] keyList=null;
	private int []labelList=null;
	private int []qidList=null;
	private ArrayList<Q2Item> trainList=null;
	private ArrayList<Q2Item> testList=null;
	public int getDataSize()
	{
		return this.data_size;
	}
	public ModelDataList(String dataPath) throws IOException, FileNotFoundException
	{
		System.err.println("read file"+dataPath+".....");
		BufferedReader br=new BufferedReader(
				new InputStreamReader(
						new FileInputStream(dataPath),"UTF-8"));
		String line=null;
		//set data size and init model value matrix
		data_size=Integer.parseInt(br.readLine());
		System.err.println("data size:\t"+data_size);
		modelValueMatrix=new float[data_size][MODEL_SIZE];
		labelList=new int[data_size];
		qidList=new int[data_size];
		queryList=new String [data_size];
		questionList=new String[data_size];
		keyList=new String[data_size];
		int index=0;
		while(((line=br.readLine())!=null)&&(index<data_size))
		{
			String [] valueStrings=line.split("\t");
			labelList[index]=Integer.parseInt(valueStrings[0]);
			queryList[index]=valueStrings[1];
			questionList[index]=valueStrings[2];
			keyList[index]=valueStrings[3];
			qidList[index]=Integer.parseInt(valueStrings[4]);
			for(int i=5;i<valueStrings.length;i++)
			{
				float f=Float.parseFloat(valueStrings[i]);
				modelValueMatrix[index][i-5]=f;
			}
			index++;
		}
		br.close();
	}
	public void genTrainAndTestData(String path,int block_size,int flag) throws IOException
	{
		BufferedWriter trainBWriter=new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(path+"/baidu.train"), "UTF-8"));
		BufferedWriter gtrainBWriter=new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(path+"/baidu.train.group"), "UTF-8"));
		BufferedWriter testBWriter=new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(path+"/baidu.test"), "UTF-8"));
		BufferedWriter gtestBWriter=new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(path+"/baidu.test.group"), "UTF-8"));
		if(trainList==null)  trainList=new ArrayList<Q2Item>(); 
		else trainList.clear();
		if(testList==null)  testList=new ArrayList<Q2Item>();
		else testList.clear();
		int trainGroupID=-1;
		int trainGroupLen=0;
		int testGroupID=-1;
		int testGroupLen=0;
		
		for(int i=0;i<data_size;i++)
		{
			
			/**
			for(int k=0;k<modelValueMatrix[i].length;k++)
			{
				//skip small feature
				if(modelValueMatrix[i][k]<0.001) modelValueMatrix[i][k]=0f;
			}
			*/
			
			Q2Item iItem=new Q2Item(labelList[i], queryList[i], questionList[i], qidList[i], modelValueMatrix[i]);
			//LabelItem iItem=new LabelItem(keyList[i], queryList[i], questionList[i], labelList[i]);
			//test data set
			if(i<block_size*(flag+1)&&i>=block_size*flag)
			{		
				testList.add(iItem);
				testBWriter.write(labelList[i]+"");
				if(testGroupID!=qidList[i])
				{
					//System.err.println(testGroupID+"\t"+qidList[i]);
					if(testGroupLen>0)  gtestBWriter.write(testGroupLen+"\n");
					testGroupID=qidList[i];
					testGroupLen=1;
				}
				else{testGroupLen++;}
				//testBWriter.write(labelList[i]+" qid:"+qidList[i]);
				for(int k=0;k<MODEL_SIZE;k++)
				{
					if(k>=8) continue;
					//if(k==6) continue; //ignore bm 25 model and edit distance
					//skip to small feature
					//if(modelValueMatrix[i][k]<0.001) continue;
					testBWriter.write(" "+(k+1)+":"+modelValueMatrix[i][k]);
				}
				testBWriter.write("\n");
			}
			else{
				trainList.add(iItem);
				trainBWriter.write(labelList[i]+"");
				if(trainGroupID!=qidList[i])  
				{
					if(trainGroupLen>0) gtrainBWriter.write(trainGroupLen+"\n");
					trainGroupID=qidList[i];
					trainGroupLen=1;
				}
				else trainGroupLen++;
				//trainBWriter.write(labelList[i]+" qid:"+qidList[i]);
				for(int k=0;k<MODEL_SIZE;k++)
				{
					//if(k==1||k==3) continue; //ignore bm 25 model and edit distance
					if(k>=8) continue;
					//skip to small feature
					//if(modelValueMatrix[i][k]<0.001) continue;
					trainBWriter.write(" "+(k+1)+":"+modelValueMatrix[i][k]);
				}
				trainBWriter.write("\n");
			}
		}
		//flush the last group length
		if(trainGroupLen>0) gtrainBWriter.write(trainGroupLen+"\n");
		if(testGroupLen>0)  gtestBWriter.write(testGroupLen+"\n");
		System.err.println("train list size\t"+trainList.size()+"\ttest list size:\t"+testList.size());
		//close writer stream
		trainBWriter.close();
		testBWriter.close();
		gtrainBWriter.close();
		gtestBWriter.close();
	}
	public ArrayList<Q2Item> getTrainList()
	{
		return trainList;
	}
	public ArrayList<Q2Item> getTestList()
	{
		return testList;
	}
}
