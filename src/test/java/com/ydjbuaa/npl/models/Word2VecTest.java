package com.ydjbuaa.npl.models;

import java.io.IOException;

import org.junit.Test;

import com.ydjbuaa.npl.cqa.models.Word2Vec;

public class Word2VecTest {
	@Test
	public void test() throws IOException {
		String word="圆通";
       Float [] fVecs=Word2Vec.getWordVector(word);
       System.out.println(word+"\tVectors:");
       for(Float f:fVecs)
       {
    	   System.out.print(f+" ");
       }
       System.out.println("");
	}
}
