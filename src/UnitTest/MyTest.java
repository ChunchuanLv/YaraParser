/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package UnitTest;

import YaraParser.Accessories.CoNLLReader;
import YaraParser.Accessories.Options;
import YaraParser.Accessories.Pair;
import YaraParser.Learning.AveragedPerceptron;
import YaraParser.Structures.IndexMaps;
import YaraParser.Structures.Sentence;
import YaraParser.TransitionBasedSystem.Configuration.Configuration;
import YaraParser.TransitionBasedSystem.Configuration.GoldConfiguration;
import YaraParser.TransitionBasedSystem.Configuration.State;
import YaraParser.TransitionBasedSystem.Features.FeatureExtractor;
import YaraParser.TransitionBasedSystem.Parser.ArcEager;
import YaraParser.TransitionBasedSystem.Trainer.ArcEagerBeamTrainer;

import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MyTest {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.inputFile = "../data/train.dep";
		options.devPath = "../data/dev.dep  ";
		options.modelFile = "../data/model/test";
		options.changePunc("punc_files/wsj.puncs");
		options.repPath = "../data";
		options.trainingIter = 3;
		options.train = true;
		options.beamWidth = 4;
		options.useDynamicOracle = false;
		options.labeled = true;
		options.useMaxViol = false;
		options.numOfThreads = 2;
		//options.useExtendedFeatures = false;

		// ArrayList<Options> optionList =
		// Options.getAllPossibleOptions(options);
		// options.numOfThreads = 2;
		// for (Options o : optionList)
		testOption(options);

		System.exit(0);
	}

	public static void testOption(Options options) throws Exception {
		System.out.println("**********************************************");
		System.out.print(options);
		System.out.println("**********************************************");
		IndexMaps maps = CoNLLReader.createIndices(options.inputFile, options.labeled, options.lowercase,
				options.clusterFile, options.repPath, options.depMat);
		CoNLLReader reader = new CoNLLReader(options.inputFile);
		ArrayList<GoldConfiguration> dataSet = reader.readData(Integer.MAX_VALUE, false, options.labeled,
				options.rootFirst, options.lowercase, maps);
		System.out.println("CoNLL data reading done!");

		ArrayList<Integer> dependencyLabels = new ArrayList<Integer>();
		for (int lab : maps.getLabels().keySet())
			dependencyLabels.add(lab);

		int featureLength = options.useExtendedFeatures ? 72 : 26;
		if (options.useExtendedWithBrownClusterFeatures || maps.hasClusters())
			featureLength = 153;
		if (options.repPath.length() > 0)
			featureLength++;

		System.out.println("size of training data (#sens): " + dataSet.size());

		HashMap<String, Integer> labels = new HashMap<String, Integer>();
		int labIndex = 0;
		labels.put("sh", labIndex++);
		labels.put("rd", labIndex++);
		labels.put("us", labIndex++);
		for (int label : dependencyLabels) {
			if (options.labeled) {
				labels.put("ra_" + label, 3 + label);
				labels.put("la_" + label, 3 + dependencyLabels.size() + label);
			} else {
				labels.put("ra_" + label, 3);
				labels.put("la_" + label, 4);
			}
		}

		System.out.print("writing objects....");

		AveragedPerceptron per = new AveragedPerceptron(featureLength, dependencyLabels.size(), maps);
		for (int i = 0; i < 100; i++)
			test(i, options, featureLength, dataSet, per);

		// String stackWord = getWord(stackWordId,maps.getWordMap());

	}

	private static void test(int ith, Options options, int featureLength, ArrayList<GoldConfiguration> dataSet,
			AveragedPerceptron per) throws Exception {
		Sentence sentence = dataSet.get(ith).getSentence();
		Configuration configuration = new Configuration(sentence, options.rootFirst, true, false);
		State currentState = configuration.state;
		ArcEager.shift(currentState);
		int stackWordId = sentence.getWords()[currentState.peek()-1];
		int bufferWordId = sentence.getWords()[ currentState.bufferHead()-1];
		Object[] features = FeatureExtractor.extractAllParseFeatures(configuration, featureLength);
		int bufferF = (int) (long) features[4] - 2;
		int stackF = (int) (long) features[1] - 2;
		System.out.println("real stack buffer "+stackWordId+" "+bufferWordId);
		System.out.println("pred stack buffer "+stackF+" "+bufferF);
		String out = "";
		for (int i=0;i<features.length/2-1;i++)
			out += ", "+(int) (long) features[i];
		System.out.println(out+"\n");

	}

	private static String getWord(int stackWordId, HashMap<String, Integer> wordMap) {
		Iterator it = wordMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			if ((int) pair.getValue() == stackWordId)
				return (String) pair.getKey();
			it.remove(); // avoids a ConcurrentModificationException
		}
		return null;
	}
}
