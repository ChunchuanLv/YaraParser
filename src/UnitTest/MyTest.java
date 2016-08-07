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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import Jama.Matrix;
import org.jblas.*;

public class MyTest {

	public static void main(String[] args) throws Exception {
		String s = " 0 1";
		String[] sv = s.split("[\t ]");
		for (int i = 0; i < sv.length; i++)
			System.out.println("" + i + ":" + sv[i]);
		// options.useExtendedFeatures = false;

		// ArrayList<Options> optionList =
		// Options.getAllPossibleOptions(options);
		// options.numOfThreads = 2;
		// for (Options o : optionList)
		// testOption(options);
		int length = 10;
		int size = 300;
		double[][] v1 = new double[1][size];
		double[][] m = new double[size][size];
		double[][] v2 = new double[size][1];
		for (int i = 0; i < size; i++) {
			v1[0][i] = i / 100;
			v2[i][0] = i / 100;
			for (int j = 0; j < size; j++)
				m[i][j] = i + j / 100;
		}
		FloatMatrix bm = new DoubleMatrix(m).toFloat();
		FloatMatrix b1 = new DoubleMatrix(v1).toFloat();
		FloatMatrix b2 = new DoubleMatrix(v2).toFloat();
		Matrix jm = new Matrix(m);
		Matrix j1 = new Matrix(v1);
		Matrix j2 = new Matrix(v2);

		float k=0f;
		final long startTime = System.currentTimeMillis();
		k=0f;
		for (int i = 0; i < length; i++) {
			// float k =bm.mulRowVector(b1).mulColumnVector(b2).get(0);
	k	 = b1.mmul(bm).mmul(b2).get(0);
		}
		final long endTime = System.currentTimeMillis();

		System.out.println("Total execution time jblas: " + (endTime - startTime)+" "+k);

		final long startTime1 = System.currentTimeMillis();
		for (int i = 0; i < length; i++) {
			 k = 0f;
			 for (int j =0;j<size;j++)
				 for (int l =0;l<size;l++)
					 k += m[j][l]*v1[0][j]*v2[l][0];
		}
		final long endTime1 = System.currentTimeMillis();

		System.out.println("Total execution time java: " + (endTime1 - startTime1)+" "+k);

		
		final long startTime2 = System.currentTimeMillis();
		for (int i = 0; i < length; i++) {
			 k = (float) j1.times(jm).times(j2).get(0, 0);
		}
		final long endTime2 = System.currentTimeMillis();

		System.out.println("Total execution time jmax: " + (endTime2 - startTime2)+" "+k);

		
		System.exit(0);
	}

	private static ByteBuffer hash(int x, int y, int z) {

		ByteBuffer key = ByteBuffer.allocate(10);
		key.putInt(x);
		key.putInt(y);
		key.putShort((short) z);

		System.out.println("this key:" + key);
		System.out.println("this x:" + key.getInt(0));
		System.out.println("this y:" + key.getInt(4));
		System.out.println("this z:" + key.getShort(8));
		return key;
	}

	public static void testOption(Options options) throws Exception {
		System.out.println("**********************************************");
		System.out.print(options);
		System.out.println("**********************************************");
		IndexMaps maps = CoNLLReader.createIndices(options.inputFile, options.labeled, options.lowercase,
				options.clusterFile, options.repPath, options.depMat, options.we, options.ce, options.depe);
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
		int stackWordId = sentence.getWords()[currentState.peek() - 1];
		int bufferWordId = sentence.getWords()[currentState.bufferHead() - 1];
		Object[] features = FeatureExtractor.extractAllParseFeatures(configuration, featureLength);
		int bufferF = (int) (long) features[4] - 2;
		int stackF = (int) (long) features[1] - 2;
		System.out.println("real stack buffer " + stackWordId + " " + bufferWordId);
		System.out.println("pred stack buffer " + stackF + " " + bufferF);
		String out = "";
		for (int i = 0; i < features.length / 2 - 1; i++)
			out += ", " + (int) (long) features[i];
		System.out.println(out + "\n");

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
