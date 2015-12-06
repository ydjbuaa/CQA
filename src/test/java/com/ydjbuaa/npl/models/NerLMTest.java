package com.ydjbuaa.npl.models;

import org.junit.Test;

import com.ydjbuaa.npl.cqa.models.NerLM;

public class NerLMTest {
	@Test
	public void testNerLM() throws Exception {
		String string="北京 有 什么 著名 的 小吃 ？";
		String string1="天津 有 什么 著名 的 小吃 ？";
		NerLM.getNerWeight(string);
	}
}
