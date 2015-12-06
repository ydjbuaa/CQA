package com.ydjbuaa.npl.models;

import org.junit.Test;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.LCS;
import com.ydjbuaa.npl.cqa.models.LD;
import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.ModelsInitializer;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class ComputeSocoresTest {
	@Test
	public void testBM25()
	{
	   //	ModelsInitializer.initializeModels();
		String []q1=AnsjWordSeg.getWordsSegStrs("狂战觉醒技能怎么用");
		String []q2=AnsjWordSeg.getWordsSegStrs("dnf狂战觉醒技能有没有用 攻击多少 大概");
		String[] q3=AnsjWordSeg.getWordsSegStrs("福建华帜劳务派遣有限公司靠什么赚钱");
	
		//	System.out.println("LM:");
		//System.out.println("q1:q2\t"+LanguageModel.getLMScore(q1, q2));
		//TranslationModel.setRTPara(0.1f);
		System.out.println("q1:q2\t"+LCS.getLCSStrScore(q1, q2));
		System.out.println("q1:q3\t"+LCS.getLCSStrScore(q1, q3));
		//TranslationModel.setRTPara(0.3f);
	//	System.out.println("q1:q2\t"+TranslationModel.getTRScore(q1, q2));
		//TranslationModel.setRTPara(0.5f);
		//System.out.println("q1:q2\t"+TranslationModel.getTRScore(q1, q2));
		//System.out.println(BM25.getBMScore(q1, q2));
		//System.out.println(LCS.getLCSScore(q1, q2));
		//System.out.println(LD.getLDScore(q1, q2));
	}
}
