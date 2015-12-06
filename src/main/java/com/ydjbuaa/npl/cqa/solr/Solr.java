package com.ydjbuaa.npl.cqa.solr;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.solr.common.*;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.xml.sax.SAXException;
import org.apache.solr.core.CoreContainer;

import org.apache.solr.core.SolrResourceLoader;

public class Solr {
	// solr url
	private final static String url = "http://219.224.169.11:8080/solr/cqa-core/";
	// private final static String solrHomePath=""
	static final int ROWS = 200;
	// solr http server
	private static  SolrServer solrserver = null;

	private static void initSolrServer(){
		if (solrserver != null) {
			return;
		}
		solrserver=new HttpSolrServer(url);

	}

	public static ArrayList<SolrDocument> query(String queryStr){
			//throws IOException, ParserConfigurationException, SAXException {
		// init server
		initSolrServer();
		//

		ArrayList<SolrDocument> docslist = new ArrayList<SolrDocument>();

		SolrQuery solrquery = new SolrQuery();
		String query = queryStr.replaceAll(" ", "");
		query = query.replaceAll("\\+", "");
		solrquery.setQuery("qtext:" + query);
		solrquery.setRows(ROWS);

		QueryResponse qrsp;
		try {
			qrsp = solrserver.query(solrquery);

			docslist = qrsp.getResults();

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return docslist;
	}

}
