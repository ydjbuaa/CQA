package com.ydjbuaa.npl.cqa.models;

public class LCS {
	public static Float getLCSStrScore(String [] querys,String[] questions)
	{
		  int i, j;
	        int len1, len2;
	        len1 = querys.length;
	        len2 = questions.length;
	        int maxLen = len1 > len2 ? len1 : len2;
	        int[] max = new int[maxLen];
	        int[] maxIndex = new int[maxLen];
	        int[] c = new int[maxLen]; // 记录对角线上的相等值的个数
	 
	        for (i = 0; i < len2; i++) {
	            for (j = len1 - 1; j >= 0; j--) {
	                if (questions[i] == querys[j]) {
	                    if ((i == 0) || (j == 0))
	                        c[j] = 1;
	                    else
	                        c[j] = c[j - 1] + 1;
	                } else {
	                    c[j] = 0;
	                }
	 
	                if (c[j] > max[0]) { // 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
	                    max[0] = c[j]; // 记录对角线元素的最大值，之后在遍历时用作提取子串的长度
	                    maxIndex[0] = j; // 记录对角线元素最大值的位置
	 
	                    for (int k = 1; k < maxLen; k++) {
	                        max[k] = 0;
	                        maxIndex[k] = 0;
	                    }
	                } else if (c[j] == max[0]) { // 有多个是相同长度的子串
	                    for (int k = 1; k < maxLen; k++) {
	                        if (max[k] == 0) {
	                            max[k] = c[j];
	                            maxIndex[k] = j;
	                            break; // 在后面加一个就要退出循环了
	                        }
	 
	                    }
	                }
	            }
	        }
	    //System.out.println("LCS String Length:\t"+max[0]);
	    return (float) (1.0* max[0]/maxLen);
		//return 0f;
	}
	public static Float getLCSScore(String[] querys,String[] qtexts)
	{
		Float score=0f;
		int c[][]=new int[querys.length+1][qtexts.length+1];
		if(qtexts.length==0||querys.length==0)
		{
			return 0f;
		}
		for(int i=0;i<=querys.length;i++)
		{
			for(int j=0;j<=qtexts.length;j++)
			{
				if(i==0||j==0)
				{
					c[i][j]=0;
					continue;
				}
				if(querys[i-1].equals(qtexts[j-1]))
				{
					c[i][j]=c[i-1][j-1]+1;
				}
				else if(c[i-1][j]>=c[i][j-1])
				{
					c[i][j]=c[i-1][j];
				}
				else{
					c[i][j]=c[i][j-1];
				}
			}
		}
		//System.out.println(c[querys.length][qtexts.length]);
		return (float) (c[querys.length][qtexts.length]*1.0/Math.max(querys.length,qtexts.length));
	}
}
