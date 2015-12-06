package com.ydjbuaa.npl.cqa.util;

import java.io.IOException;

import org.junit.Test;

import com.ydjbuaa.npl.cqa_maven.util.CQAFileReader;

public class CQAFileReaderTest {
   @Test
   public void test() throws IOException
   {
	   testGenW2CCorpus();
	  // testGenAlignCorpus();
   }
   private void testGenW2CCorpus() throws IOException
   {
	   String srcPath="E:\\Programs\\SOLR\\data\\ChCQA\\BaiduZhidaoData\\";
	   String dstPath="./data/w2c.dat/";
	   CQAFileReader.getW2CCorpus(srcPath, dstPath);
   }
   private void testGenAlignCorpus()
   {
	   String srcPath="E:\\Programs\\SOLR\\data\\ChCQA\\BaiduZhidaoData\\cate1\\";
	   String dstPath="./data/w2c.dat/";
	   CQAFileReader.getAlignCorpus(srcPath, dstPath);
   }
	public  void testCQAGenerator()
	{
	    String srcPath="E:/Programs/SOLR/data/ChCQA/LabelData/Baidu Data/baidu.data";
	    String dstPath="./data/test/baidulabel_randmon100.data";
		CQAFileReader.genCQATestFiles(srcPath, dstPath);
	}
}
