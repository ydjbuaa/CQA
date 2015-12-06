package com.ydjbuaa.npl.cqa.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.ydjbuaa.npl.cqa.models.LanguageModel;
import com.ydjbuaa.npl.cqa.models.NCross;
import com.ydjbuaa.npl.cqa.models.NerLM;
import com.ydjbuaa.npl.cqa.models.TransLM;
import com.ydjbuaa.npl.cqa.models.TranslationModel;
import com.ydjbuaa.npl.cqa_maven.util.BoosterParams;
import com.ydjbuaa.npl.cqa_maven.util.CQAFileReader;
import com.ydjbuaa.npl.cqa_maven.util.IEvaluation;
import com.ydjbuaa.npl.cqa_maven.util.IEvaluation.EVAL_METRIC;
import com.ydjbuaa.npl.cqa_maven.util.LabelItem;
import com.ydjbuaa.npl.cqa_maven.util.ModelDataList;
import com.ydjbuaa.npl.cqa_maven.util.Q2Item;
import com.ydjbuaa.npl.xgboost.RankSVM;
import com.ydjbuaa.npl.xgboost.XgBoost;

public class ServerTestDirver {
	// system default path
	private static String srcTestDataPath = "./data/baidu.data";
	private static String randomTestDataPath = "./data/baidu_random.data";
	private static String modelDataPath = "./data/model_exp_random_stop_new.dat";
	// private static String trPath = "./library/tr/";
	private static String workPath = "./work/";
	private static boolean generateFlag = false;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// random the baidu test data
		// CQAFileReader.randomTestData(srcTestDataPath, randomTestDataPath);

		systemSet(args);
		// save data into file
		if (generateFlag)
			IEvaluation.genTestData(randomTestDataPath, modelDataPath);

		//testLM();
		//testBM25();
		//testLCS_ED();
		//testTR();
		//testTransLM();
		//testTopNCross();
		//testWord2Vec();

		//testNerLM();
		// testRankSVM();
		// testRankSVMwithParams();
		//testXgBoostRank();
		 testXgboostWithParams();
		// genModel();
		System.err.println("Test Over !");
	}

	private static void genW2CCorppus(String path) throws IOException {
		String srcPath = path;
		String dstPath = "./w2c.dat/";
		CQAFileReader.getW2CCorpus(srcPath, dstPath);
	}

	private static void systemSet(String[] args) throws FileNotFoundException {
		// out and err stream reset,
		if (args.length < 1)
			return;
		setOutStream(args[0]);
		if (args.length < 2) {
			generateFlag = false;
			return;
		}
		generateFlag = true;
	}

	private static void genModel() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		BoosterParams param = new BoosterParams();
		param.put("eta", 0.14f);
		param.put("max_depth", 3);
		param.put("gamma", 100f);
		param.put("min_child_weight", 1f);
		param.put("objective", "rank:pairwise");
		param.put("num_round", 4);

		String baiduworkPath = "./xgboost/baidu.model";
		String dataPath = "./xgboost/baidu.dat";
		XgBoost.genTrainAndGroupFile(list, dataPath);

		// train
		XgBoost.train(workPath, dataPath, baiduworkPath, param);
		float[] tpres = XgBoost.predict(workPath, dataPath, baiduworkPath, param);

		// float tmap = IEvaluation.culMAP(list, tpres);
		// System.out.println("Final MAP:\t"+tmap);

	}

	private static void setOutStream(String path) throws FileNotFoundException {
		// create the out folder
		File outfolder = new File(path);
		if (!(outfolder.exists() && outfolder.isDirectory())) {
			outfolder.mkdir();
		}
		// create the temp model folder
		File modelfolder = new File(path + "/model/");
		if (!(modelfolder.exists() && modelfolder.isDirectory())) {
			modelfolder.mkdirs();
		}
		// set model path
		workPath = path + "/model/";
		// out and err stream reset
		PrintStream outPrint = new PrintStream(new FileOutputStream(path + "/res.out"));
		System.setOut(outPrint);
	}

	// test word2vector
	public static void testWord2Vec() throws Exception {
		// get the test list
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP@Word2Vec:\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.Word2Vec));
	}

	public static void testXgBoostRank() throws Exception {

		System.err.println("test xgboost rank......");
		ModelDataList dataList = new ModelDataList(modelDataPath);

		// ArrayList<LabelItem> list =
		// CQAFileReader.getLabelList(randomTestDataPath);
		BoosterParams params = new BoosterParams();
		params.put("eta", 0.3f);
		params.put("max_depth", 8);
		params.put("gamma", 26f);
		params.put("min_child_weight", 1f);
		params.put("objective", "rank:pairwise");
		params.put("num_round", 24);

		// System.err.println("Data Size:" + list.size());
		int block_num = 4;
		int block_size = dataList.getDataSize() / block_num;
		float train_score = 0f;
		float test_score = 0f;
		float train_pression = 0f;
		float test_pression = 0f;
		// train data test data and model trained path
		String baiduModelPath = workPath + "baidu.model";
		String trainPath = workPath + "baidu.train";
		String testPath = workPath + "baidu.test";
		for (int i = 0; i < block_num; i++) {
			// generate train and test data
			dataList.genTrainAndTestData(workPath, block_size, i);
			// xgboost run train
			XgBoost.train(workPath, trainPath, baiduModelPath, params);
			// xgboost run predict
			float[] t_pres = XgBoost.predict(workPath, trainPath, baiduModelPath, params);
			ArrayList<Q2Item> trainList = dataList.getTrainList();
			ArrayList<Q2Item> testList = dataList.getTestList();
			// set scores
			IEvaluation.setScores(trainList, t_pres);
			// cul MAP
			float train_map = IEvaluation.culMAP(trainList, EVAL_METRIC.MAP);
			System.err.println("Round " + (i + 1) + "\t Train Map:\t" + train_map);
			float train_p = IEvaluation.culMAP(trainList, EVAL_METRIC.PRESSION);
			System.err.println("Round " + (i + 1) + "\t Train Pression:\t" + train_p);
			float[] s_pres = XgBoost.predict(workPath, testPath, baiduModelPath, params);
			IEvaluation.setScores(testList, s_pres);
			float test_map = IEvaluation.culMAP(testList, EVAL_METRIC.MAP);
			float test_p = IEvaluation.culMAP(testList, EVAL_METRIC.PRESSION);
			System.err.println("Round " + (i + 1) + "\t Test Map:\t" + test_map);
			System.err.println("Round " + (i + 1) + "\t Test Pression:\t" + test_p);
			train_score += train_map;
			test_score += test_map;
			train_pression += train_p;
			test_pression += test_p;
		}

		float train_map = train_score / block_num;
		float test_map = test_score / block_num;
		float tr_pr = train_pression / block_num;
		float te_pr = test_pression / block_num;
		System.out.println("Train MAP:\t" + train_map);
		System.out.println("Train Pression:\t" + tr_pr);
		System.out.println("Test MAP:\t" + test_map);
		System.out.println("Test Pression:\t" + te_pr);
		System.err.println("Train MAP:\t" + train_map);
		System.err.println("Train Pression:\t" + tr_pr);
		System.err.println("Test MAP:\t" + test_map);
		System.err.println("Test Pression:\t" + te_pr);
		System.err.println("test xgboost rank over !");
	}

	public static void testTransLM() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(TransLM):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.TransLM));
		float pa = 0.8f;
		
		System.out.println("Pa\tPb\tMAP");
		while (pa <= 1) {
			float pb = 0.1f;
			while (pb <=1) {
				TransLM.setParams(pa, pb);
				System.out.println(pa+"\t" + pb + "\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.TransLM));
				pb += 0.02d;
			}
			pa += 0.02f;
		}
	}

	public static void testXgboostWithParams() throws Exception {

		System.err.println("test xgboost rank......");
		ModelDataList dataList = new ModelDataList(modelDataPath);
		ArrayList<BoosterParams> paramList = new ArrayList<BoosterParams>();

		float eta = 0.1f;
		while (eta <= 0.3f) {
			float gamma = 20f;
			while (gamma <= 30f) {
				int max_depth = 8;
				while (max_depth <= 8) {
					int num_round = 5;
					while (num_round <= 40) {
						// set the min_child_weight to 0.1
						float min_child_weight = 1.0f;
						while (min_child_weight <= 1.0f) {
							BoosterParams param = new BoosterParams();
							param.put("eta", eta);
							param.put("max_depth", max_depth);
							param.put("gamma", gamma);
							param.put("min_child_weight", min_child_weight);
							param.put("num_round", num_round);
							param.put("objective", "rank:pairwise");
							paramList.add(param);
							min_child_weight += 0.5f;
						}
						num_round += 2;
					}
					max_depth += 1;
				}
				gamma += 2d;
			}
			eta += 0.05d;
		}
		// set block size and block num
		int block_num = 4;
		int block_size = dataList.getDataSize() / block_num;

		Iterator<BoosterParams> bit = paramList.iterator();
		while (bit.hasNext()) {

			BoosterParams param = bit.next();
			// cross validation
			float train_score = 0f;
			float test_score = 0f;
			// train data test data and model trained path
			String baiduModelPath = workPath + "baidu.model";
			String trainPath = workPath + "baidu.train";
			String testPath = workPath + "baidu.test";
			for (int i = 0; i < block_num; i++) {
				// generate train and test data
				dataList.genTrainAndTestData(workPath, block_size, i);
				// xgboost run train
				XgBoost.train(workPath, trainPath, baiduModelPath, param);
				// xgboost run predict
				float[] t_pres = XgBoost.predict(workPath, trainPath, baiduModelPath, param);
				ArrayList<Q2Item> trainList = dataList.getTrainList();
				ArrayList<Q2Item> testList = dataList.getTestList();
				// set scores
				IEvaluation.setScores(trainList, t_pres);
				// cul MAP
				float train_map = IEvaluation.culMAP(trainList, EVAL_METRIC.MAP);
				System.err.println("Round " + (i + 1) + "\t Train Map:\t" + train_map);
				float[] s_pres = XgBoost.predict(workPath, testPath, baiduModelPath, param);
				IEvaluation.setScores(testList, s_pres);
				float test_map = IEvaluation.culMAP(testList, EVAL_METRIC.MAP);

				System.err.println("Round " + (i + 1) + "\t Test Map:\t" + test_map);
				train_score += train_map;
				test_score += test_map;
			}

			float train_map = train_score / block_num;
			float test_map = test_score / block_num;
			System.err.println(param.toPrintString() + "\t" + test_map);
			System.out.println(param.toPrintString() + "\t" + train_map + "\t" + test_map);
		}
	}

	/**
	 * public static void testRankSVM() throws Exception { //get source test
	 * data ArrayList<LabelItem> list =
	 * CQAFileReader.getLabelList(randomTestDataPath); int block_num = 4; int
	 * block_size=list.size()/block_num; float t_score=0f; float cPa=0.01f;
	 * for(int i=0;i<block_num;i++) { ArrayList<LabelItem> train_list=new
	 * ArrayList<LabelItem>(); ArrayList<LabelItem> test_list=new ArrayList
	 * <LabelItem>(); for(int j=0;j<list.size();j++) { if (j
	 * <block_size*(i+1)&&j>=block_size*i) { test_list.add(list.get(j)); } else
	 * { train_list.add(list.get(j)); } } // String
	 * trainPath=workPath+"baidu.train"; String testPath=workPath+"baidu.test";
	 * String modelPath=workPath+"baidu.model";
	 * RankSVM.genTrainAndGroupFile(train_list,trainPath);
	 * RankSVM.genTrainAndGroupFile(test_list, testPath); RankSVM.train(
	 * trainPath, modelPath, cPa); float [] t_pres=RankSVM.predict(workPath,
	 * testPath, modelPath); IEvaluation.setScores(test_list, t_pres); float
	 * map=IEvaluation.culMAP(test_list, EVAL_METRIC.MAP); System.err.println(
	 * "Round "+(i+1)+" MAP:\t"+map); t_score+=map; } float
	 * test_map=t_score/block_num; System.err.println("RankSVM MAP:\t"
	 * +test_map); System.out.println("RankSVM MAP:\t"+test_map); }
	 */
	public static void testRankSVMwithParams() throws Exception {
		// get source test data
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		ArrayList<Float> paList = new ArrayList<Float>();
		float pa = 0.001f;
		while (pa < 1f) {
			paList.add(pa);
			pa += 0.002f;
		}
		while (pa < 10f) {
			paList.add(pa);
			pa += 0.05f;
		}
		int block_num = 4;
		int block_size = list.size() / block_num;
		for (int k = 0; k < paList.size(); k++) {
			float t_score = 0f;
			float cPa = paList.get(k);
			for (int i = 0; i < block_num; i++) {
				ArrayList<LabelItem> train_list = new ArrayList<LabelItem>();
				ArrayList<LabelItem> test_list = new ArrayList<LabelItem>();

				for (int j = 0; j < list.size(); j++) {
					// Q2Item iItem=new Q2Item(list.get(i).getLabel(),
					// list.get(i).getQueryStr(), list.get(i).getQuestionStr(),
					// list.get(i)., modelValueMatrix[i]);
					if (j < block_size * (i + 1) && j >= block_size * i) {
						test_list.add(list.get(j));
					} else {
						train_list.add(list.get(j));
					}
				}
				//
				String trainPath = workPath + "baidu.train";
				String testPath = workPath + "baidu.test";
				String modelPath = workPath + "baidu.model";
				RankSVM.genTrainAndGroupFile(train_list, trainPath);
				RankSVM.genTrainAndGroupFile(test_list, testPath);
				RankSVM.train(trainPath, modelPath, cPa);
				float[] t_pres = RankSVM.predict(workPath, testPath, modelPath);
				IEvaluation.setSVMScores(test_list, t_pres);
				float map = IEvaluation.culSVMMAP(list, IEvaluation.EVAL_METRIC.MAP);
				System.err.println("Round " + (i + 1) + " MAP:\t" + map);
				t_score += map;
			}
			float test_map = t_score / block_num;
			System.err.println("PA:\t" + cPa + "\tmap:\t" + test_map);
			System.out.println("PA:\t" + cPa + "\tmap:\t" + test_map);
		}
	}

	public static void testVSM() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(VSM):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.VSM));
	}

	public static void testLM() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(LM):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.LM));
	
		float pa = 0.1f;
		System.out.println("Pa\tMAP");
		while (pa <=1d) {
			LanguageModel.setPara(pa);
			System.out.println(pa + "\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.LM));
			pa += 0.05d;
		}
	
	}

	public static void testBM25() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(BM25):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.BM25));
	}

	public static void testLCS_ED() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(LCS):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.LCS));
		System.out.println("MAP(ED):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.LD));
	}

	public static void testTR() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(TR):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.TR));
		float pa = 0.1f;
		System.out.println("Pa\tMAP");
		while (pa <=1) {
			TranslationModel.setRTPara(pa);
			System.out.println(pa + "\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.TR));
			pa += 0.05f;
		}

	}

	public static void testNerLM() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(NerLM):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.NerLM));
		float pa=0f;
		while(pa<=1)
		{
			NerLM.setParam(pa);
			System.out.println(pa+"\t"+IEvaluation.culMAP(list,IEvaluation.ModelType.NerLM));		
			pa+=0.02f;
		}
		System.out.println("MAP(NerCross):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.NerCross));
	}

	private static void testTopNCross() throws Exception {
		ArrayList<LabelItem> list = CQAFileReader.getLabelList(randomTestDataPath);
		System.out.println("MAP(TopNCorss):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.NCross));
		/**
		for (int n = 2; n <= 10; n++) {
			NCross.setTopN(n);
			System.out.println(
					"TOP_N:\t" + n + "\tMAP(TopNCorss):\t" + IEvaluation.culMAP(list, IEvaluation.ModelType.NCross));
		}*/
	}
}
