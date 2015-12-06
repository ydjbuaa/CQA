package com.ydjbuaa.npl.cqa.driver;

import java.util.*;

import com.ydjbuaa.npl.xgboost.XgBoost;

import org.apache.solr.common.SolrDocument;
import com.ydjbuaa.npl.cqa.solr.Solr;
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;
import com.ydjbuaa.npl.cqa_maven.util.IEvaluation;
import com.ydjbuaa.npl.cqa_maven.util.Q2Item;
import com.ydjbuaa.npl.cqa.models.*;

public class CQADriver {

	final static int MODEL_NUM = 10;
	final static int RETURN_NUM = 20;

	public static List<Q2Item> getQueryResults(String query) throws Exception {
		// get word-seg results
		String[] querys = AnsjWordSeg.getWordsSegStrs(query);

		// get top 200 relevant docs return by solr
		ArrayList<SolrDocument> resDocs = Solr.query(AnsjWordSeg.getWordsSegString(query));
		Iterator<SolrDocument> it = resDocs.iterator();

		int index = 0;

		// System.out.println("Solr Query Result Size:\t"+resDocs.size());
		ArrayList<Q2Item> retriList = new ArrayList<Q2Item>();
		// return solr-retrievaled results directly
		while (it.hasNext()) {
			SolrDocument solrdoc = it.next();

			String qtext = solrdoc.getFieldValue("qtext").toString();
			String atext = solrdoc.getFieldValue("atext").toString();
			Q2Item q2Item = new Q2Item(1, query, qtext, 1, new float[MODEL_NUM]);
			q2Item.setAnswerStr(atext);
			retriList.add(q2Item);
		}
		if (true)
			return retriList;
		float[][] features = new float[resDocs.size()][MODEL_NUM];
		while (it.hasNext()) {
			SolrDocument solrdoc = it.next();

			String qtext = solrdoc.getFieldValue("qtext").toString();
			String[] qtexts = AnsjWordSeg.getWordsSegStrs(qtext);
			// System.out.println(qtext);
			// compute model score

			float s1 = LanguageModel.getLMScorewithNormalization(querys, qtexts);
			float s2 = BM25.getBMScore(querys, qtexts);
			float s3 = LCS.getLCSScore(querys, qtexts);
			float s4 = LD.getLDScore(querys, qtexts);
			float s5 = TranslationModel.getTRScorewithNormalization(querys, qtexts);
			float s6 = TransLM.getTransLMScorewithNormalization(querys, qtexts);
			float s7 = NCross.getTopNCrossScore(querys, qtexts);
			float s8 = Word2Vec.getW2CScore(querys, qtexts);
			float s9 = NerLM.getNerLMScorewithNormalization(query, qtext);
			float s10 = NerLM.getNerCrossScore(query, qtext);

			// set feature
			features[index][0] = s1;
			features[index][1] = s2;
			features[index][2] = s3;
			features[index][3] = s4;
			features[index][4] = s5;
			features[index][5] = s6;
			features[index][6] = s7;
			features[index][7] = s8;
			features[index][8] = s9;
			features[index][9] = s10;

			Q2Item q2Item = new Q2Item(1, query, qtext, 1, features[index]);
			retriList.add(q2Item);

			index++;
		}
		// Xgboost predict and set predict results
		float[] fpres = XgBoost.predict(features);
		IEvaluation.setScores(retriList, fpres);
		// rank and select top 20 return
		List<Q2Item> rankList = retriList;
		Collections.sort(rankList);
		List<Q2Item> returnList = new ArrayList<Q2Item>();
		for (int i = 0; i < RETURN_NUM; i++) {
			returnList.add(rankList.get(i));
		}
		return returnList;
	}

	public static void main(String[] args) throws Exception {
		String query = "北京潭柘寺";
		// retList;
		List<Q2Item> retList = CQADriver.getQueryResults(query);
		for (int i = 0; i < retList.size(); i++) {
			String question = retList.get(i).getQuestionStr();
			String answer = retList.get(i).getQueryStr();
			System.out.println(question);
			System.out.println(answer);
		}

	}

}
