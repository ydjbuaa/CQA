package com.ydjbuaa.npl.cqa_maven.util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
public class AnsjWordSeg {
	/**
	 * get word-seg string by using ansj word segmentation tool
	 * @param srcStr: src string
	 * @return segStr 
	 */
	private static List<String> stopWordsList=null;
	public static String mergeIntoString(String [] strs)
	{
		String line="";
		for(String str:strs)
		{
			line+=str+",";
		}
		return line;
	}
	private static void initStopWordsList() throws IOException
	{
		
		FileInputStream fi=new FileInputStream("./library/stop-words-list/my-stop-words-zh-new.dic");
		BufferedReader br=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
	    String line=null;
	    stopWordsList=new ArrayList<String>();
	    while((line=br.readLine())!=null)
	    {
	    	line=line.trim();
	    	stopWordsList.add(line);
	    }
	    System.err.println("load stop words list over !");
	}
	public static boolean isStopWord(String word)
	{
		if(stopWordsList==null)
		{
			try {
				initStopWordsList();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(stopWordsList.contains(word))
		{
			return true;
		}
		return false;
	}
	/**
	 * use ansj word seg tool to get seged strs
	 * @param srcStr
	 * @return
	 */
	public static String[] getWordsSegStrs(String srcStr)
	{
				if(stopWordsList==null)
				{
					try {
						initStopWordsList();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	          	//remove \r\n
				srcStr=srcStr.replaceAll("\\\\r\\\\n", "");
				srcStr=srcStr.replaceAll("\\.","");
				//System.out.println(srcStr);
				List<Term> wordsegterms=ToAnalysis.parse(srcStr);
				ArrayList<String> wordsegStrs=new ArrayList<String>();
				for(Term term:wordsegterms)
				{
					String str=term.getName().trim();
					//remove stop words
					if(!stopWordsList.contains(str))
					{
						if(str!="")
						{
							wordsegStrs.add(str);
						}
					}
				}
			return wordsegStrs.toArray(new String[0]);
	}
	public static String getWordsSegString(String str)
	{
		String [] strs=getWordsSegStrs(str);
		String segStr="";
		for(String s:strs)
		{
			if(segStr=="")
			{
				segStr=s;
			}
			else{
				segStr+=" "+s;
			}
		}
		return segStr;
	}
}
