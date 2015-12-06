package com.ydjbuaa.npl.models;

import org.junit.Test;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class TranslationModelTest {
    @Test
	public void testTR()
	{
    	String []q1=AnsjWordSeg.getWordsSegStrs("社保卡初始密码掉了怎么办");
		String []q2=AnsjWordSeg.getWordsSegStrs("如果社保密码初始化阿？");
		System.out.println(TranslationModel.getTRScore(q1, q2));
	}
}
