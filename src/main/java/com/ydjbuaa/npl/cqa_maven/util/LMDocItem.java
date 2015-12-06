package com.ydjbuaa.npl.cqa_maven.util;

public class LMDocItem {
        private String id;
        private String qtext;
        private String  atext;
        private float  score;
        
       public LMDocItem(String id,String q,String a,float pred)
       {
    	   this.id=id;
    	   this.qtext=q;
    	   this.atext=a;
    	   this.score=pred;
       }
       public Float getScore()
       {
    	   return this.score;
       }
       public void Print()
       {
    	     System.out.println("Urlid:"+id+"\tScore:"+score);
			 System.out.println("qtext:"+qtext);
			 System.out.println("atext"+atext);
       }
       public String getQtext()
       {
    	   return this.qtext;
       }
       public String getQAnswer()
       {
    	   return this.atext;
       }
       
}
