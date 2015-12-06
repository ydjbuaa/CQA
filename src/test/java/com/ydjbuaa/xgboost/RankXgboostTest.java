package com.ydjbuaa.xgboost;

import org.dmlc.xgboost4j.DMatrix;
import org.dmlc.xgboost4j.util.XGBoostError;
import org.junit.Test;
import com.ydjbuaa.npl.xgboost.*;
public class RankXgboostTest {

	@Test
	public void test() throws XGBoostError
	{
		RankXgboost.train("./library/baidu/baidu.train","./library/baidu/baidu.model");
		//RankXgboost.train("./data/mq2008/mq2008.train", "./mq2008/mq2008.model");
	}
}
