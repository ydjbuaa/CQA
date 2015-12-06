package com.ydjbuaa.npl.cqa_maven.util;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class CQAFileReader {
	/**
	 * get the train corpus for wrod2vec 
	 * @param srcPath
	 * @param dstPath
	 * @throws IOException 
	 */
	public static void getW2CCorpus(String srcPath,String dstPath) throws IOException
	{
		try {
			
			BufferedWriter bw=new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(dstPath),"UTF-8"));
			
		    readW2CCorpusfromDir(srcPath, bw);
			bw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	/**
	 * get align corpus for mgiza++ trianing
	 * @param srcPath:data path
	 * @param dstPath:file generated path
	 */
	public static void getAlignCorpus(String srcPath,String dstPath)
	{
		//init File Output Stream
		try {
				FileOutputStream aqfo=new FileOutputStream(dstPath+"aq");
				FileOutputStream qafo=new FileOutputStream(dstPath+"qa");
					
				BufferedWriter aqbw=new BufferedWriter(new OutputStreamWriter(aqfo,"UTF-8"));
				BufferedWriter qabw=new BufferedWriter(new OutputStreamWriter(qafo,"UTF-8"));
					
				readAlignCorpusfromDir(srcPath,aqbw,qabw);
				
				aqbw.close();
				qabw.close();
				aqfo.close();
				qafo.close();
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	private static void readW2CCorpusfromDir(String path,BufferedWriter bw)
	{
		File curfile=new File(path);
		File [] subfiles=curfile.listFiles();
		System.out.println(curfile.getAbsolutePath());
		for(File file :subfiles)
		{
			//System.out.println(file.getAbsolutePath());
			if(file.isDirectory())
			{
				//travel  the sub directory recursively
				readW2CCorpusfromDir(file.getAbsolutePath(), bw);
			}
			else if(file.getName().endsWith("Question.dat"))  //find the quesiton data file
			{
				//find the corresponding answer data file
				//String headFilePath=file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("Question.dat"));
				String answerFilePath=file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("Question.dat"))+"Answer.dat";
				
				//judge the answer file whether exsits
				//System.out.println(answerFilePath);
				if(new File(answerFilePath).exists())
				{
					getW2CCorpusfromFile(file.getAbsolutePath(), answerFilePath, bw);
					//getAlignCorpusfromFile(file.getAbsolutePath(),answerFilePath,aqbw,qabw);
				}
			}
		}
	}
	private static void getW2CCorpusfromFile(String qPath,String aPath,BufferedWriter bw)
	{
		try {
			FileInputStream qfi=new FileInputStream(qPath);
			FileInputStream afi=new FileInputStream(aPath);

			BufferedReader qbr=new BufferedReader(new InputStreamReader(qfi,"UTF-8"));
			BufferedReader abr=new BufferedReader(new InputStreamReader(afi,"UTF-8"));
			
		   String qline=null;
		   String aline=null;
		   
		   System.out.println("Read Data from:"+qPath+"\t"+aPath);
		   //read question answer file by line
		   int lineCount=0;
		   while((qline=qbr.readLine())!=null&&(aline=abr.readLine())!=null)
		   {
			   
			   lineCount++;
			   
			   String [] ss=qline.split("\t");
			   String qtext=ss[2];
			   String atext=aline;
			   if(!ss[3].equals("N/A"))
			   {
				   qtext+=ss[3];
			   }
			   String qSegStr=AnsjWordSeg.getWordsSegString(qtext);
			   String aSegStr=AnsjWordSeg.getWordsSegString(atext);
			   if(qSegStr!=""&&aSegStr!="")
			   {
				   //write the string words
					bw.append(qSegStr+"\n"+aSegStr+"\n");
			   }
			  if(lineCount%10000==0)
			  {
				bw.flush();
				lineCount=0;
			  }
		   }
		   //flush 
		    bw.flush();
			   
			 qbr.close();
			 abr.close();
			 qfi.close();
			 afi.close();
				
		   System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void readAlignCorpusfromDir(String path,BufferedWriter aqbw,BufferedWriter qabw)
	{

		File curfile=new File(path);
		File [] subfiles=curfile.listFiles();
		
		for(File file :subfiles)
		{
			//System.out.println(file.getAbsolutePath());
			if(file.isDirectory())
			{
				//travel  the sub directory recursively
				readAlignCorpusfromDir(file.getAbsolutePath(),aqbw,qabw);
			}
			else if(file.getName().endsWith("Question.dat"))  //find the quesiton data file
			{
				//find the corresponding answer data file
				//String headFilePath=file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("Question.dat"));
				String answerFilePath=file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("Question.dat"))+"Answer.dat";
				
				//judge the answer file whether exsits
				//System.out.println(answerFilePath);
				if(new File(answerFilePath).exists())
				{
					getAlignCorpusfromFile(file.getAbsolutePath(),answerFilePath,aqbw,qabw);
				}
			}
		}
	}
	private static void getAlignCorpusfromFile(String qPath,String aPath,BufferedWriter aqbw,BufferedWriter qabw)
	{
		try {
			FileInputStream qfi=new FileInputStream(qPath);
			FileInputStream afi=new FileInputStream(aPath);

			BufferedReader qbr=new BufferedReader(new InputStreamReader(qfi,"UTF-8"));
			BufferedReader abr=new BufferedReader(new InputStreamReader(afi,"UTF-8"));
			
		   String qline=null;
		   String aline=null;
		   
		   System.out.println("Read Data from:"+qPath+"\t"+aPath);
		   //read question answer file by line
		   int lineCount=0;
		   while((qline=qbr.readLine())!=null&&(aline=abr.readLine())!=null)
		   {
			   
			   lineCount++;
			   
			   String [] ss=qline.split("\t");
			   String qtext=ss[2];
			   String atext=aline;
			   if(!ss[3].equals("N/A"))
			   {
				   qtext+=ss[3];
			   }
			   String qSegStr=AnsjWordSeg.getWordsSegString(qtext);
			   String aSegStr=AnsjWordSeg.getWordsSegString(atext);
			   if(qSegStr!=""&&aSegStr!="")
			   {
					  aqbw.append(aSegStr+"\n"+qSegStr+"\n");
					  qabw.append(qSegStr+"\n"+aSegStr+"\n");
			   }
			  if(lineCount%10000==0)
			  {
				qabw.flush();
				aqbw.flush();
				lineCount=0;
			  }
		   }
		    qabw.flush();
			aqbw.flush();
			   
			qbr.close();
			 abr.close();
			 qfi.close();
			 afi.close();
				
		   System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static HashSet<Integer> genRandomArray(int len,int csize)
	{
	   int i=0;
	   HashSet<Integer> intset=new HashSet<Integer>();
	   while(i<len)
	   {
		   int x=(int) (Math.random()*csize);
		   if(!intset.contains(x))
		   {
			   intset.add(x);
			   i++;
		   }
	   }
	   return intset;
	}
	/**
	 * translate the labeled query-question data into list
	 * @param path:label data path
	 * @return list format by labelitem
	 */
	public static ArrayList<LabelItem>getLabelList(String path)
	{
		ArrayList<LabelItem> list=new ArrayList<LabelItem>();
		
		try {
			
			FileInputStream fi=new FileInputStream(path);
			BufferedReader br=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
			
			String line=null;
			while((line=br.readLine())!=null)
			{
				//<query>\t<candidate question>\t<label>\t<unique key>
				String[] items=line.split("\t");
				LabelItem it=new LabelItem(items[3],items[0],items[1],Integer.parseInt(items[2]));
				list.add(it);
			}
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
		return list;
	}
	public static void genCQATestFiles(String srcPath,String dstPath)
	{
		try {
			
			ArrayList<String> strlist=new ArrayList<String>();
			
			
			FileInputStream fi=new FileInputStream(srcPath);
			BufferedReader br=new BufferedReader(new InputStreamReader(fi,"UTF-8"));
           
			FileOutputStream fo=new FileOutputStream(dstPath);
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fo,"UTF-8"));
			
			String line=null;
			String str="";
			while((line=br.readLine())!=null)
			{
				String qstr=line.split("\t")[0];
				if(!str.equals(qstr))
				{
					strlist.add(qstr);
					str=qstr;
				}
			}
			HashSet<Integer> intset=genRandomArray(100,strlist.size());
			Iterator<Integer> it=intset.iterator();
		
			while(it.hasNext())
			{
				int x=it.next();
				String qstr=strlist.get(x);
				bw.write(qstr+"\n");
			}
			
			br.close();
			fi.close();
			bw.close();
			fo.close();
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
	private static void randomGroupData(List<String>group,BufferedWriter bw) throws IOException
	{
		boolean []flag=new boolean[group.size()];
		for(int i=0;i<flag.length;i++) flag[i]=false;
		Random random=new Random();
		for(int i=0;i<group.size();i++)
		{
			//get a random index in group
			int index=random.nextInt(group.size());
			while(flag[index])
			{
				index=random.nextInt(group.size());
			}
			flag[index]=true;
			bw.write(group.get(index)+"\n");
		}
	}
	public static void randomTestData(String srcPath,String dstPath) throws IOException, FileNotFoundException
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(srcPath),"UTF-8"));
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstPath),"UTF-8"));
		String curQueryStr="";
		String line=null;
		List<String> groupLines=new ArrayList<String>();
		while((line=br.readLine())!=null)
		{
			String query=line.split("\t")[0];
			if(!query.equals(curQueryStr))  //new query
			{
				// random the current group 
				if(groupLines.size()>0)
				randomGroupData(groupLines, bw);
				groupLines.clear();
				curQueryStr=query;
			}
			groupLines.add(line);
			//curQueryStr=query;
			
		}
		//flush the last group 
		randomGroupData(groupLines, bw);
		br.close();
		bw.close();
	}
}
