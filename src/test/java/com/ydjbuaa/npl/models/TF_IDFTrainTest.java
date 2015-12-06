package com.ydjbuaa.npl.models;

import org.junit.Test;
import com.ydjbuaa.npl.cqa.models.TF_IDFTrain;
public class TF_IDFTrainTest {
@Test
	public void genTrainData()
	{
		TF_IDFTrain.train("E:\\Programs\\SOLR\\data\\ChCQA\\BaiduZhidaoData\\", "./library/tf-idf.map");
	}
}
