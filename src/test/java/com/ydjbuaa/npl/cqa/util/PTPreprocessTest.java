package com.ydjbuaa.npl.cqa.util;

import org.junit.Test;

import com.ydjbuaa.npl.cqa_maven.util.ProbabilityTablePreprocess;

public class PTPreprocessTest {
@Test
	public void test()
	{
	   //String srcpath="E:\\Programs\\SOLR\\data\\ChCQA\\zhidao_all.trans\\";
		String srcpath="E:\\Programs\\SOLR\\data\\ChCQA\\tr\\mgiza.tr.dic";
		String dstpath="./data/migza_final.tr.dic";
		ProbabilityTablePreprocess.preprocess(srcpath, dstpath);
	}
}
