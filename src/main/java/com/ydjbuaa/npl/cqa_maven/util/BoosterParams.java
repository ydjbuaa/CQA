package com.ydjbuaa.npl.cqa_maven.util;
/*
 Copyright (c) 2014 by Contributors 

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
    
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */


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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap;


/**
 * a util class for handle params
 * @author hzx
 */
public class BoosterParams implements Iterable<Entry<String, Object>>{
    List<Entry<String, Object>> params = new ArrayList<>();
    
    /**
     * put param key-value pair
     * @param key
     * @param value 
     */
    public void put(String key, Object value) {
        params.add(new AbstractMap.SimpleEntry<>(key, value));
    }
    public String toPrintString()
    {
    	String str="";
    	for(int i=0;i<params.size();i++)
    	{
    		str+=params.get(i).getKey()+":"+params.get(i).getValue()+",";
    	}
    	return str;
    }
    @Override
    public String toString(){ 
        String paramsInfo = "";
        for(Entry<String, Object> param : params) {
            paramsInfo += param.getKey() + ":" + param.getValue() + "\n";
        }
        return paramsInfo;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return params.iterator();
    }
    public void genConfFile(String confPath,String dataPath,String modelPath,boolean testFlag,String prePath) throws IOException
    {
    	BufferedWriter bw=new BufferedWriter(
    			new OutputStreamWriter(
    					new FileOutputStream(confPath),"UTF-8"));
    	
    	
    	bw.write("# General Parameters, see comment for each definition\n\n");
    	bw.write("# specify objective\nobjective=\"rank:pairwise\"\n");
    	float eta = 0;
    	float gamma = 0;
    	float min_child_weight = 0;
    	int max_depth = 0;
    	int num_round=1;
    	for(int i=0;i<params.size();i++)
    	{
    		Entry<String, Object> pEntry=params.get(i);
    		
    		switch(pEntry.getKey())
    		{
    		case "eta":
    			eta=(float) pEntry.getValue();
    			break;
    		case "gamma":
    			gamma=(float) pEntry.getValue();
    			break;
    		case "min_child_weight":
    			min_child_weight=(float) pEntry.getValue();
    			break;
    		case "max_depth":
    			max_depth=(int) pEntry.getValue();
    		case "num_round":
    			num_round=(int) pEntry.getValue();
    		}
    	}
    	String treeboostParamStr=String.format("# Tree Booster Parameters  \n" +
    			"# step size shrinkage\n"+
    			"eta = %f\n" +
    			"# minimum loss reduction required to make a further partition\n"+
    			"gamma = %f\n"+ 
    			"# minimum sum of instance weight(hessian) needed in a child\n"+
    			"min_child_weight = %f\n"+
    			"# maximum depth of a tree\n"+
    			"max_depth = %d\n", eta,gamma,min_child_weight,max_depth);
   
    	bw.write(treeboostParamStr);
    	
    	String taskParamStr=String.format("# Task parameters\n"+
    					"# the number of round to do boosting\n"+
    					"num_round = %d\n"+
    					"# 0 means do not save any model except the final round model\n"+
    					"save_period = 0\n"
    					, num_round);
    	
    	bw.write(taskParamStr);
       //train configuration
    	if(testFlag==false){
    	   // train data path
    		String trainString=String.format("# The path of training data\n"+
					"data = \"%s\"\n"+ 
					"# The path of validation data, used to monitor training process, here [test] sets name of the validation set\n"+
					"eval[train] = \"%s\"\n"+
					"#train model out path\n"+
					"model_out=\"%s\"\n"
					,dataPath,dataPath,modelPath);
    			
    		bw.write(trainString);
       }
    	//test configuration
    	else{
    		String testString=String.format("# The path of test data\n"+
    				"test:data = \"%s\"\n"+
    				"# The path of validation data, used to monitor training process, here [test] sets name of the validation set\n"+
					"eval[test] = \"%s\"\n"+
    				"#test model in\n"+
    				"model_in=\"%s\"\n"+		
    				"#pre task\n"+
    				"task=\"%s\"\n"+
    				"name_pred=\"%s\"\n", dataPath,dataPath,modelPath,"pred",prePath);
    		bw.write(testString);
    	}
    	bw.close();
    }
}
