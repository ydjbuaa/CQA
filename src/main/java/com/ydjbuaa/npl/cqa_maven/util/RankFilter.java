package com.ydjbuaa.npl.cqa_maven.util;

import com.ydjbuaa.npl.cqa_maven.util.LMDocItem;
public class RankFilter {
  
	//define the topN output
	 final static int topN=20; 
	 //the top items
	 private LMDocItem[] docs=null;
	 private int tp;
	 public RankFilter()
	 {
		 docs=new LMDocItem[topN];
		 tp=0;
	 }
	 public void filter(LMDocItem nd)
	 {
		 int i;
		 for(i=0;i<tp;i++)
		 {
			 if(nd.getScore()>docs[i].getScore())
			 {
				 break;
			 }
		 }
		 if(tp<topN)
		 {
			 tp=tp+1;
		 }
		 if(i==topN)
		 {
			 return ;
		 }
		 for(int j=tp-1;j>i;j--)
		 {
			 docs[j]=docs[j-1];
		 }
		 docs[i]=nd;
	 }
	 public void printRankResult()
	 {
		for(int i=0;i<tp;i++)
		{
			docs[i].Print();
		}
	 }
	 public LMDocItem[] returnRankResults()
	 {
		 return docs;
	 }
}
