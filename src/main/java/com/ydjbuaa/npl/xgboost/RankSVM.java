package com.ydjbuaa.npl.xgboost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

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
import com.ydjbuaa.npl.cqa_maven.util.AnsjWordSeg;
import com.ydjbuaa.npl.cqa_maven.util.LabelItem;

public class RankSVM {
	
	private final static String RANKSVM_PATH="../ranksvm/";
	public static void train(String dataPath, String modelPath,
			float cPa) throws IOException {
		long startTime = System.currentTimeMillis();
		String[] execString = new String[] { RANKSVM_PATH + "svm_rank_learn", "-c",
				cPa + "", dataPath,modelPath };
		Process process = Runtime.getRuntime().exec(execString);
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader(ir);
		String line = null;
		while ((line = input.readLine()) != null) {
			System.err.println(line);
		}
		InputStreamReader eir = new InputStreamReader(process.getErrorStream());
		BufferedReader einput = new BufferedReader(eir);

		String eline = null;

		while ((eline = einput.readLine()) != null) {

			System.err.println(eline);
		}

		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("rank_svm train over, use time:" + seconds + "s");
	}

	public static float[] predict(String svmPath, String dataPath,
			String modelPath) throws IOException {
		long startTime = System.currentTimeMillis();
		String prePath = svmPath + "pred.txt";
		String[] execString = new String[] { RANKSVM_PATH + "svm_rank_classify",
				dataPath, modelPath, prePath };
		Process process = Runtime.getRuntime().exec(execString);
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader(ir);
		String line = null;
		while ((line = input.readLine()) != null) {
			System.err.println(line);
		}
		InputStreamReader eir = new InputStreamReader(process.getErrorStream());
		BufferedReader einput = new BufferedReader(eir);

		String eline = null;

		while ((eline = einput.readLine()) != null) {

			System.err.println(eline);
		}
		// read pred.txt

		BufferedReader preBr = new BufferedReader(new InputStreamReader(
				new FileInputStream(prePath), "UTF-8"));
		String pline = null;
		ArrayList<Float> pList = new ArrayList<Float>();
		while ((pline = preBr.readLine()) != null) {
			String[] pps = pline.split(" ");
			Float p = Float.parseFloat(pps[0]);
			pList.add(p);

		}
		float[] pres = new float[pList.size()];
		for (int i = 0; i < pList.size(); i++) {
			pres[i] = pList.get(i);
		}
		long endTime = System.currentTimeMillis();
		float seconds = (endTime - startTime) / 1000F;
		System.err.println("xgboost predict over, use time:" + seconds + "s");
		// Float[] pres=pList.toArray(new Float[0]);
		return pres;

	}

	// generate the train data file and group file from List
	public static void genTrainAndGroupFile(ArrayList<LabelItem> list,
			String path) throws Exception {
		Iterator<LabelItem> it = list.iterator();
		// travel the list

		try {

			FileOutputStream fot = new FileOutputStream(path);
			//FileOutputStream fog = new FileOutputStream(path + ".group");

			BufferedWriter bwt = new BufferedWriter(new OutputStreamWriter(fot,
					"UTF-8"));
		//	BufferedWriter bwg = new BufferedWriter(new OutputStreamWriter(fog,
			//		"UTF-8"));

			String groupStr = "";
			int groupLen = 0;
			int qid = 0;
			while (it.hasNext()) {
				LabelItem lit = it.next();
				String query = lit.getQueryStr();
				String question = lit.getQuestionStr();

				String[] querys = AnsjWordSeg.getWordsSegStrs(query);
				String[] candidates = AnsjWordSeg.getWordsSegStrs(question);

				if (!groupStr.equals(query)) {
					if (groupLen > 0) {
						//bwg.write(groupLen + "\n");
					}
					qid++;
					groupLen = 1;
					groupStr = query;
				} else {
					groupLen++;
				}
				// add label
				bwt.append(lit.getLabel() + " ");
				bwt.append(" qid:" + qid);
				// language model feature
				Float s1 = LanguageModel.getLMScorewithNormalization(querys,
						candidates);
				if (!s1.equals(0d)) {
					bwt.append(" 1:" + s1);
				}

				// BM 25 model feature
				Float s2 = BM25.getBMScore(querys, candidates);
				if (!s2.equals(0d)) {
					bwt.append(" 2:" + s2);
				}

				// LCS feature
				Float s3 = LCS.getLCSScore(querys, candidates);
				if (!s3.equals(0d)) {
					bwt.append(" 3:" + s3);
				}
				// LD feature
				Float s4 = LD.getLDScore(querys, candidates);
				if (!s4.equals(0d)) {
					bwt.append(" 4:" + s4);
				}
				// TR feature
				Float s5=TranslationModel.getTRScorewithNormalization(querys, candidates);
				//Float s5 = TranslationModel.getTRScore(querys, candidates);
				if (!s5.equals(0d)) {
					bwt.append(" 5:" + s5);
				}
				// transLM
				Float s6=TransLM.getTransLMScorewithNormalization(querys, candidates);
				//Float s6=TransLM.getTransLMScore(querys, candidates);
				//Float s6 = TransLM.getTransLMScore(querys, candidates);
				if (!s6.equals(0d)) {
					bwt.append(" 6:" + s6);
				}
			
				Float s7=NCross.getTopNCrossScore(querys, candidates);
				if(!s7.equals(0d))
				{
					bwt.append(" 7:"+s7);
				}
				Float s8=Word2Vec.getW2CScore(querys, candidates);
				if(s8!=0d)
				{
					bwt.append(" 8:"+s8);
				}
				float s9=NerLM.getNerLMScorewithNormalization(query, question);
				if(s9!=0f)
				{
					bwt.append(" 9:"+s9);
				}
				float s10=NerLM.getNerCrossScore(query, question);
				if(s10!=0f)
				{
					bwt.append(" 10:"+s10);
				}
				// write \n
				bwt.append("\n");
				bwt.flush();
			}
			//bwg.write(groupLen + "\n");
			bwt.close();
			//bwg.close();
			fot.close();
		//	fog.close();
			// System.out.println("Wirte "+path+" and group file over!");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
