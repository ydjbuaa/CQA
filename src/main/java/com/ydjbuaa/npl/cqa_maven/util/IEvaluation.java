package com.ydjbuaa.npl.cqa_maven.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.ydjbuaa.npl.cqa.models.BM25;
import com.ydjbuaa.npl.cqa.models.LCS;
import com.ydjbuaa.npl.cqa.models.LD;
import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.NCross;
import com.ydjbuaa.npl.cqa.models.NerLM;
import com.ydjbuaa.npl.cqa.models.TransLM;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa.models.VSM;
import com.ydjbuaa.npl.cqa.models.Word2Vec;

public class IEvaluation {
	// evaluate the performace of the retreieval

	public enum ModelType {
		LM, BM25, LCS, LD, TR, TransLM, NCross, Word2Vec,VSM,NerLM,NerCross
	}
	public enum EVAL_METRIC{
		MAP,PRESSION
	}

	//
	private static  ArrayList<LabelItem> randomArray(ArrayList<LabelItem> group)
	{
		ArrayList<LabelItem> rGroup=new ArrayList<LabelItem>();
		if(group.size()==0) return rGroup;
		LabelItem [] list=new LabelItem[group.size()];
		boolean []flag=new boolean[group.size()];
		for(int i=0;i<flag.length;i++) flag[i]=false;
		Random random=new Random();
		for(int i=0;i<group.size();i++)
		{
			int index=random.nextInt(group.size());
			while(flag[index])
			{
				index=random.nextInt(group.size());
			}
			flag[index]=true;
			//insert the current item randomly
			list[index]=group.get(i);
		}
	
		for(int i=0;i<list.length;i++) rGroup.add(list[i]);
		return rGroup;
	}
	private static ArrayList<LabelItem> randomList(ArrayList<LabelItem> list) 
	{
		ArrayList<LabelItem>rList=new ArrayList<LabelItem>();
		ArrayList<LabelItem> group=new ArrayList<LabelItem>();
		String curQuery = "";
		for(int i=0;i<list.size();i++)
		{
			LabelItem it = list.get(i);
			if (!curQuery.equals(it.getQueryStr())) {
				//random current group 
				ArrayList<LabelItem> rGroup=randomArray(group);
				rList.addAll(rGroup);
				group.clear();
				curQuery = it.getQueryStr();
			}
			group.add(it);
		}
		if(group.size()>0) 
		{
			//random current group 
			ArrayList<LabelItem> rGroup=randomArray(group);
			rList.addAll(rGroup);
		}
		return rList;
	}
	
	public static void genTestData(String srcDataPath, String dstDataPath) throws Exception {
		//get random labelitem list
		//ArrayList<LabelItem> list =randomList( CQAFileReader.getLabelList(srcDataPath));
		ArrayList<LabelItem> list= CQAFileReader.getLabelList(srcDataPath);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstDataPath), "UTF-8"));
		bw.write(list.size() + "\n");
		String curQuery = "";
		int qid = 0;
		for (int i = 0; i < list.size(); i++) {
			LabelItem it = list.get(i);
			if (!curQuery.equals(it.getQueryStr())) {
				qid++;
				curQuery = it.getQueryStr();
			}
			String[] querys = AnsjWordSeg.getWordsSegStrs(it.getQueryStr());
			String[] questions = AnsjWordSeg.getWordsSegStrs(it.getQuestionStr());
			
			float f_lm = LanguageModel.getLMScorewithNormalization(querys, questions);
			float f_bm = BM25.getBMScore(querys, questions);
			float f_lcs = LCS.getLCSScore(querys, questions);
			float f_ld = LD.getLDScore(querys, questions);
			float f_tr = TranslationModel.getTRScorewithNormalization(querys, questions);
			float f_translm = TransLM.getTransLMScorewithNormalization(querys, questions);
			float f_ncross =NCross.getTopNCrossScore(querys, questions);
			float f_w2c=Word2Vec.getW2CScore(querys, questions);
			float f_nerlm=0f;//NerLM.getNerLMScorewithNormalization(it.getQueryStr(), it.getQuestionStr());
			float f_nercross=0f;//NerLM.getNerCrossScore(it.getQueryStr(), it.getQuestionStr());
			bw.write(it.getLabel() + "\t" + it.getQueryStr() + "\t" + it.getQuestionStr() + "\t" + it.getUniqueKey()
					+ "\t" + qid + "\t" + f_lm + "\t" + f_bm + "\t" + f_lcs + "\t" + f_ld + "\t" + f_tr + "\t"
					+ f_translm +"\t"+f_ncross+"\t"+f_w2c+"\t"+f_nerlm+"\t"+f_nercross+"\n");
		}
		bw.close();
	}

	private static float getModelScore(String query, String question, ModelType type) throws Exception {
		String[] querys = AnsjWordSeg.getWordsSegStrs(query);
		String[] qtexts = AnsjWordSeg.getWordsSegStrs(question);

		if (type == ModelType.LM) {
			return LanguageModel.getLMScorewithNormalization(querys, qtexts);
		} else if (type == ModelType.BM25) {
			return BM25.getBMScore(querys, qtexts);
		} else if (type == ModelType.LCS) {
			return LCS.getLCSScore(querys, qtexts);
		} else if (type == ModelType.LD) {
			return LD.getLDScore(querys, qtexts);
		} else if (type == ModelType.TR) {
			return TranslationModel.getTRScorewithNormalization(querys, qtexts);
			//return TranslationModel.getTRScore(querys, qtexts);
		} else if (type == ModelType.TransLM) {
			return TransLM.getTransLMScorewithNormalization(querys, qtexts);
			//return TransLM.getTransLMScore(querys, qtexts);
		} else if (type == ModelType.NCross) {
			return NCross.getTopNCrossScore(querys, qtexts);
		} else if (type == ModelType.Word2Vec) {
			return Word2Vec.getW2CScore(querys, qtexts);
		}
		else if(type==ModelType.VSM)
		{
			return VSM.getVSMScore(querys, qtexts);
		}
		else if(type==ModelType.NerLM)
		{
			return NerLM.getNerLMScorewithNormalization(query, question);
			//return NerLM.getNerLMScore(query, question);
		}
		else if(type==ModelType.NerCross)
		{
		    return NerLM.getNerCrossScore(query, question);
			//return NerLM.getNerLMScorewithNormalization(query, question);
		}
		return 0f;
	}
   private static float getQueryPression(ArrayList<Q2Item> group)
   {
		List<Q2Item> grouplist = group;
		// sort
		Collections.sort(grouplist);
		int positiveNum = 0;
		int retrievalNum=0;
		float score = 0f;
		for (int i = 0; i < grouplist.size(); i++) {
			Q2Item it = grouplist.get(i);
			//System.out.println(it.getLabel()+"\t"+it.getQueryStr()+"\t"+it.getQuestionStr()+"\t"+it.getScore());
			if (it.getPredictValue()>0.6f) { //relevant documents
				retrievalNum++;
				if(it.getLabel()==1)
				{
					positiveNum++;
				}
				//if(it.getScore()>0.5) //retrieved documents
				score += ((float) positiveNum) / (i + 1);
			}
		}
		float f=0f;
		if (retrievalNum > 0) {
			f= (float) (positiveNum*1.0/retrievalNum);
		}
		return f;
   }
   private static float getQueryMap(ArrayList<Q2Item>group)
   {
	   List<Q2Item> grouplist=group;
	   //sort 
	   Collections.sort(grouplist);
		int positiveNum = 0;
		float score = 0f;
		for (int i = 0; i < grouplist.size(); i++) {
			Q2Item it = grouplist.get(i);
			//System.out.print(it.getLabel()+"\t"+it.getQueryStr()+"\t"+it.getQuestionStr()+"\t"+it.getPredictValue());
			//for(int kk=0;kk<it.getModelValues().length;kk++) System.out.print("\t"+it.getModelValues()[kk]);
			//System.out.println("");
			if (it.getLabel() == 1) { //relevant documents
				positiveNum++;
				score += ((float) positiveNum) / (i + 1);
			}
		}
		float f=0f;
		
		if (positiveNum > 0) {
			f= score / positiveNum;
		}
		//System.out.println("Group MAP:\t"+f+"\n");
		return f;
   }
	// cul the the same query MAP
	private static float getQueryMAP(ArrayList<LabelItem> group) {

		List<LabelItem> grouplist = group;
		// sort
		Collections.sort(grouplist);
		int positiveNum = 0;
		float score = 0f;
		for (int i = 0; i < grouplist.size(); i++) {
			LabelItem it = grouplist.get(i);
			//System.out.println(it.getLabel()+"\t"+it.getQueryStr()+"\t"+it.getQuestionStr()+"\t"+it.getScore());
			if (it.getLabel() == 1) { //relevant documents
				positiveNum++;
				score += ((float) positiveNum) / (i + 1);
			}
		}
		float f=0f;
		
		if (positiveNum > 0) {
			f= score / positiveNum;
		}
		//System.out.println("Group MAP:\t"+f+"\n");
		return f;
	}
	public static void setScores(ArrayList<Q2Item> list,float[] pres) throws Exception{
		if (list.size() != pres.length) {
			System.err.println("list size " + list.size() + "\tpres size:" + pres.length);
			throw new Exception("list size does not match the pred size");
		}
		//set scores predicted by the rank 
		for (int i = 0; i < list.size(); i++) {
			Q2Item item = list.get(i);
			// set the score computed by model,such as LM ,LD,LCS,TR
			item.setPredictValue(pres[i]);
		}
	}
	public static void setSVMScores(ArrayList<LabelItem> list,float [] pres)throws Exception
	{
		if (list.size() != pres.length) {
			System.err.println("list size " + list.size() + "\tpres size:" + pres.length);
			throw new Exception("list size does not match the pred size");
		}
		for(int i=0;i<list.size();i++)
		{
			list.get(i).setScore(pres[i]);
		}
	}
	public static float getQueryEvalValue(ArrayList<Q2Item> group,EVAL_METRIC eval_type)
	{
		if(eval_type==EVAL_METRIC.MAP)
		{
			return getQueryMap(group);
		//	return getQueryMAP(group);
		}
		else if(eval_type==EVAL_METRIC.PRESSION)
		{
			return getQueryPression(group);
		}
		return 0f;
	}
	public static float culSVMMAP(ArrayList<LabelItem> list,EVAL_METRIC type)
	{
		long startTime = System.currentTimeMillis();
		ArrayList<LabelItem> group = new ArrayList<LabelItem>();
		int groupLength = 0;
		String curQuery = "";
		float mapsScore = 0f;
		// cul the MAP for every query
		for (int i = 0; i < list.size(); i++) {
			LabelItem it = list.get(i);
			if ((!it.getQueryStr().equals(curQuery)) || i == list.size() - 1) {
				// cul the current gourp query MAP
				if (group.size() > 0) {
					if (i == list.size() - 1) {
						group.add(it);
					}
					float curmap = getQueryMAP(group);
					// System.out.println(curQuery+"\t"+curmap);
					mapsScore += curmap;
				}
				if (!it.getQueryStr().equals(curQuery)) {
					groupLength++;
					curQuery = it.getQueryStr();
					// group.add(it);
				}
				group.clear();
			}

			group.add(it);

		}

		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		// System.out.println("Cul MAP over, use time:"+seconds+"s");

		if (groupLength > 0)
			return mapsScore / groupLength;
		return 0f;
	}

	public static float culMAP(ArrayList<Q2Item> list, EVAL_METRIC eval_type) throws Exception {


		long startTime = System.currentTimeMillis();
		
		ArrayList<Q2Item> group = new ArrayList<Q2Item>();
		int groupLength = 0;
		String curQuery = "";
		float mapsScore = 0f;
		// cul the MAP for every query
		for (int i = 0; i < list.size(); i++) {
			Q2Item it = list.get(i);
			if ((!it.getQueryStr().equals(curQuery)) || i == list.size() - 1) {
				// cul the current gourp query MAP
				if (group.size() > 0) {
					//get eval value
					float map =getQueryEvalValue(group, eval_type); 
					// System.out.println(curQuery+"\t"+curmap);
					mapsScore += map;
				}
				if (!it.getQueryStr().equals(curQuery)) {
					groupLength++;
					curQuery = it.getQueryStr();
					// group.add(it);
				}
				group.clear();
			}

			group.add(it);

		}

		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		// System.out.println("Cul MAP over, use time:"+seconds+"s");

		if (groupLength > 0)
			return mapsScore / groupLength;
		return 0f;
	}

	// cul the MAP
	public static float culMAP(ArrayList<LabelItem> list, ModelType type) throws Exception {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < list.size(); i++) {
			LabelItem item = list.get(i);
			// set the score computed by model,such as LM ,LD,LCS,TR
			item.setScore(getModelScore(item.getQueryStr(), item.getQuestionStr(), type));
		}

		ArrayList<LabelItem> group = new ArrayList<LabelItem>();
		int groupLength = 0;
		String curQuery = "";
		float mapsScore = 0f;
		// cul the MAP for every query
		for (int i = 0; i < list.size(); i++) {
			LabelItem it = list.get(i);
			if ((!it.getQueryStr().equals(curQuery)) || i == list.size() - 1) {
				// cul the current gourp query MAP
				if (group.size() > 0) {
					if (i == list.size() - 1) {
						group.add(it);
					}
					float curmap = getQueryMAP(group);
					// System.out.println(curQuery+"\t"+curmap);
					mapsScore += curmap;
				}
				if (!it.getQueryStr().equals(curQuery)) {
					groupLength++;
					curQuery = it.getQueryStr();
					// group.add(it);
				}
				group.clear();
			}

			group.add(it);

		}

		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		// System.out.println("Cul MAP over, use time:"+seconds+"s");

		if (groupLength > 0)
			return mapsScore / groupLength;
		return 0f;
	}

}
