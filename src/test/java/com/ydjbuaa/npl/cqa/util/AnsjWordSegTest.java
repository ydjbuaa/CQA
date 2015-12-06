package com.ydjbuaa.npl.cqa.util;

import java.util.ArrayList;

import org.junit.Test;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;
public class AnsjWordSegTest {
	 @Test
	 public void testAnsj()
	 {
		 String src1="好好学习，天天向上.给我推荐一款学生用笔记本电脑";
		 String[]seglist=AnsjWordSeg.getWordsSegStrs(src1);
		 for(String s:seglist)
		 {
			 System.out.print(s+"\t");
		}
		assert(seglist.length==6);
	 }
}
