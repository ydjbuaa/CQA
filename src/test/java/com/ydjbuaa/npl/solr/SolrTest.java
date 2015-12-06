package com.ydjbuaa.npl.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ydjbuaa.npl.cqa.solr.Solr;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;

public class SolrTest {
	@Test
	public void test()
	{

			ArrayList<SolrDocument>resDocs=Solr.query("潭柘寺");
			 Iterator<SolrDocument> it=resDocs.iterator();
			 while(it.hasNext())
			 {
				 SolrDocument solrdoc=it.next();
				 String urlid=solrdoc.getFieldValue("urlid").toString();
				 String qtext=solrdoc.getFieldValue("qtext").toString();
				 String atext=solrdoc.getFieldValue("atext").toString();
				System.out.println(urlid+"\t"+qtext+"\n"+atext); 
			 }
				 
			
		
	}
}
