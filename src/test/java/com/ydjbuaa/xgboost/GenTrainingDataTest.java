package com.ydjbuaa.xgboost;

import org.dmlc.xgboost4j.DMatrix;
import org.dmlc.xgboost4j.util.XGBoostError;
import org.junit.Test;
import com.ydjbuaa.npl.xgboost.GenTrainingData;
public class GenTrainingDataTest {
    @Test
    public void genData() throws XGBoostError
    {
    	//GenTrainingData.genTrainingData("E:\\Programs\\SOLR\\data\\ChCQA\\LabelData\\Baidu Data\\baidu.data","./library/baidu/");
    	DMatrix dmat=new DMatrix("./test.txt");
    }
}
