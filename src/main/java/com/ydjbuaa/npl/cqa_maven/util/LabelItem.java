package com.ydjbuaa.npl.cqa_maven.util;

public class LabelItem implements Comparable<LabelItem>{

   private String uniquekey;
   private String query;
   private String question;
   private int label;
   private float score;
   public LabelItem(String ukey,String query,String question,int label)
   {
	   this.uniquekey=ukey;
	   this.query=query;
	   this.question=question;
	   this.label=label;
   }
   public String getUniqueKey()
   {
	   return this.uniquekey;
   }
   public String getQueryStr()
   {
	   return this.query;
   }
   public int getLabel()
   {
	   return this.label;
   }
   public String getQuestionStr()
   {
	   return this.question;
   }
   public void setScore(float s)
   {
	   this.score=s;
   }
   public float getScore()
   {
	   return this.score;
   }
   public int compareTo(LabelItem it)
   {
	  if(this.score>it.score)
	  {
		  return -1;
	  }
	  else if(this.score<it.score)
	  {
		  return 1;
	  }
	  return 1;
   }
}
