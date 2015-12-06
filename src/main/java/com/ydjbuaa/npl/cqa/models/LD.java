package com.ydjbuaa.npl.cqa.models;

public class LD {
	public static Float getLDScore(String[] querys,String[] qtexts)
	{
		   
	        int n = querys.length;
	        int m = qtexts.length;
	        int d[][]=new int[n+1][m+1]; // 矩阵
	        int i; // 遍历str1的
	        int j; // 遍历str2的
	        String ch1; // str1的
	        String ch2; // str2的
	        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
	        if (n == 0) {
	            return (float) (1-m*1.0/Math.max(n, m));
	        }
	        if (m == 0) {
	            return (float) (1-n*1.0/Math.max(n, m));
	        }
	        d = new int[n + 1][m + 1];
	        for (i = 0; i <= n; i++) { // 初始化第一列
	            d[i][0] = i;
	        }
	        for (j = 0; j <= m; j++) { // 初始化第一行
	            d[0][j] = j;
	        }
	        for (i = 1; i <= n; i++) { // 遍历str1
	   
	            for (j = 1; j <= m; j++) {
	              
	                if (querys[i-1].equals(qtexts[j-1])) {
	                    temp = 0;
	                } else {
	                    temp = 1;
	                }
	                // 左边+1,上边+1, 左上角+temp取最小
	                int d1=Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1);
	                d[i][j] = Math.min(d1, d[i - 1][j - 1]+ temp);
	            }
	        }
		return (float) (1-1.0*d[n][m]/Math.max(n, m));
	}
}
