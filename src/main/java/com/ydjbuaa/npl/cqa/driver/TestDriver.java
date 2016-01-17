package com.ydjbuaa.npl.cqa.driver;
import java.util.HashMap;

import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.TF_IDFTrain;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

import java.io.*;
public class TestDriver {
	//test some programs in the server
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//generate new tf-idf map
		System.out.println("test tf-idf");
		//TF_IDFTrain.train(args[0],args[1]);
		String  str1="如何用笔记本建立wifi  XP系统";
		String  str2="如何通过笔记本（xp系统）建立WLAN（WIFI）接口，供手机上网？";
		String  str3="北京哪里好玩？";
		String  [] querys=AnsjWordSeg.getWordsSegStrs(str1);
		String  [] qtexts=AnsjWordSeg.getWordsSegStrs(str2);
		String  [] ss=AnsjWordSeg.getWordsSegStrs(str3);
		for (String s:ss)
		{
			System.out.print(s+"\t");
		}
		System.out.println("");
		System.out.println(LanguageModel.getLMScorewithNormalization(querys, qtexts));
	}
	public static boolean reWriteFileByBinaryWay(String srcPath,String dstPath)
	{
		long startTime=System.currentTimeMillis();
		boolean writeFlag=true;
		try {
			
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(srcPath),"UTF-8"));
			DataOutputStream dos=new DataOutputStream(new FileOutputStream(dstPath));
			String line=null;
			String [] sizes=br.readLine().split(" ");
			//get data size and vector size
			int data_size=Integer.parseInt(sizes[0]);
			int vec_size=Integer.parseInt(sizes[1]);
			System.out.println("data size:\t"+data_size+"\tvector size:\t"+vec_size);
			//write into dos
			dos.writeInt(data_size);
			dos.writeInt(vec_size);
			int i;
			for(i=0;i<data_size;i++)
			{
				line=br.readLine();
				if(line==null) {writeFlag=false;break;}
				String [] ss=line.split(" ");
				dos.writeUTF(ss[0]);
				for(int j=0;j<vec_size;j++)
				{
					if(j+1>ss.length){
						writeFlag=false;
						break;
					}
					dos.writeFloat(Float.parseFloat(ss[j+1]));
				}
				if(writeFlag==false) break;
			}
			br.close();
			dos.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			writeFlag=false;
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			writeFlag=false;
			e.printStackTrace();
		} catch (IOException e) {
			writeFlag=false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 long endTime=System.currentTimeMillis();
		 float seconds = (endTime - startTime) / 1000F;
		 System.out.println("Rewrite time:"+seconds+"s");
		return writeFlag;
	}
	public static void readBinaryFile(String path)
	{
		try {
			long startTime=System.currentTimeMillis();
			DataInputStream dis=new DataInputStream(new FileInputStream(path));
		    int data_size=dis.readInt();
		    int vec_size=dis.readInt();
		    float [][] vec_matrix=new float[data_size][vec_size];
		    HashMap<String,Integer> w2vMap=new HashMap<String,Integer>();
		    for(int i=0;i<data_size;i++)
		    {
		    	String word=dis.readUTF();
		    	for(int k=0;k<vec_size;k++) vec_matrix[i][k]=dis.readFloat();
		    	w2vMap.put(word, i);
		    }
		    System.out.println(w2vMap.size());
		    long endTime=System.currentTimeMillis();
			float seconds = (endTime - startTime) / 1000F;
			System.out.println("Load time:"+seconds+"s");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
