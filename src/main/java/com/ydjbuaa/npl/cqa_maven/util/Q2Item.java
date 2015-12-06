package com.ydjbuaa.npl.cqa_maven.util;
/**
 * query-question item including basic information ,model values,predict Value 
 * @author ydj
 *
 */
public class Q2Item implements Comparable<Q2Item>{
	private int label;
	private String queryStr;
	private String questionStr;
	private String answerStr;
	private int groupID;
	private float [] modelValues=null;
	private float preValue;
	
	public void setPredictValue(float fPre)
	{
		preValue=fPre;
	}
	public float getPredictValue()
	{
		return preValue;
	}
	public Q2Item(int l,String query,String question,int gid,float [] mvalues)
	{
		this.label=l;
		this.queryStr=query;
		this.questionStr=question;
		this.groupID=gid;
		this.modelValues=mvalues;
	}
	public void setAnswerStr(String aStr)
	{
		this.answerStr=aStr;
	}
	public float [] getModelValues()
	{
		return this.modelValues;
	}
	public String getQueryStr() {
		// TODO Auto-generated method stub
		return queryStr;
	}
	public String getAnswerStr()
	{
		return answerStr;
	}
	public String getQuestionStr()
	{
		return questionStr;
	}
	public int getGroupID()
	{
		return this.groupID;
	}
	public int getLabel()
	{
		return label;
	}
	public float getLMValue()
	{
		return modelValues[0];
	}
	public float getBM25Value()
	{
		return modelValues[1];
	}
	public float getLCSValue()
	{
		return modelValues[2];
	}
	public float getEDValue()
	{
		return modelValues[3];
	}
	public float getTRValue()
	{
		return modelValues[4];
	}
	public float getTransLMValue()
	{
		return modelValues[5];
	}
	public float getTopNCrossValue()
	{
		return modelValues[6];
	}
	public float getW2VValue()
	{
		return modelValues[7];
	}
	public float getNerLMValue()
	{
		return modelValues[8];
	}
	public float getNerCrossValue()
	{
		return modelValues[9];
	}
	public int compareTo(Q2Item it)
	   {
		 
		  //if(2-1==1) return 1;
		  
		  float preScore=this.getPredictValue();
		  if(preScore<0)
		  {
			  //xgboost predict first
			  if(this.getPredictValue()>it.getPredictValue()+0.1) return -1;
			  else  if(this.getPredictValue()+0.1<it.getPredictValue()) return 1;
			    
			  if(this.getTRValue()>it.getTRValue()) return -1;
			  else if(this.getTRValue()<it.getTRValue()) return 1;
			  
			  
			  if(this.getW2VValue()>it.getW2VValue()) return -1;
			  else if(this.getPredictValue()<it.getW2VValue()) return 1;
			 
			  if(this.getTransLMValue()>it.getTransLMValue()) return 1;
			  else if(this.getTransLMValue()<it.getTransLMValue()) return -1;

			  if(this.getEDValue()>it.getEDValue()) return -1;
			  else if(this.getEDValue()<it.getEDValue()) return 1;
			  
			  if(this.getLCSValue()>it.getLCSValue()) return -1;
			  else if(this.getLCSValue()<it.getLCSValue()) return 1;
			  
			  return -1;
			  
		  }
		  if(this.getPredictValue()>it.getPredictValue()) return -1;
		  else  if(this.getPredictValue()<it.getPredictValue()) return 1;
		  
		//translation-based language model
		  if(this.getTransLMValue()>it.getTransLMValue()) return 1;
		  else if(this.getTransLMValue()<it.getTransLMValue()) return -1;
		  
		  if(this.getEDValue()>it.getEDValue()) return -1;
		  else if(this.getEDValue()<it.getEDValue()) return 1;
		  
		  if(this.getLCSValue()>it.getLCSValue()) return -1;
		  else if(this.getLCSValue()<it.getLCSValue()) return 1;
		  //translation model
		  if(this.getTRValue()>it.getTRValue()) return -1;
		  else if(this.getTRValue()<it.getTRValue()) return 1;
		  
		  if(this.getW2VValue()>it.getW2VValue()) return -1;
		  else if(this.getPredictValue()<it.getW2VValue()) return 1;
		  
		 
		  
		  return -1;
	   }
}
