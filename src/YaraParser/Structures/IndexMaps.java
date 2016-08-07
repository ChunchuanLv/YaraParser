/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package YaraParser.Structures;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;

public class IndexMaps implements Serializable {
    public final String rootString;
    public String[] revWords;
    private HashMap<String, Integer> wordMap;
    private HashMap<Integer, Integer> labels;
    private HashMap<Integer, Integer> brown4Clusters;
    private HashMap<Integer, Integer> brown6Clusters;
    private HashMap<String, Integer> brownFullClusters;
	private static  HashMap<BigInteger,Float> wdp ;
    public HashMap<Integer, float[][]> getLabelRep() {
		return labelRep;
	}

	public HashMap<BigInteger, Float>  fastCompute () {
		return wdp;
	}
	public HashMap<Integer, float[]> getWordRep() {
		return wordRep;
	}

	public HashMap<Integer, float[]> getContRep() {
		return contRep;
	}

	private static HashMap<Integer, float[]> wordRep;
    private static HashMap<Integer, float[]> contRep;
    private static HashMap<Integer, float[][]> labelRep;

    public IndexMaps(HashMap<String, Integer> wordMap, HashMap<Integer, Integer> labels, String rootString,
                     HashMap<Integer, Integer> brown4Clusters, HashMap<Integer, Integer> brown6Clusters, HashMap<String, Integer> brownFullClusters,
                     HashMap<Integer, float[]> wordRep,    HashMap<Integer, float[]>  contRep,HashMap<Integer, float[][]> labelRep,
                     HashMap<BigInteger,Float> wdp) {
        this.wordMap = wordMap;
        this.labels = labels;
        this.wordRep = wordRep;
        this.contRep = contRep;
        this.labelRep = labelRep;
        revWords = new String[wordMap.size() + 1];
        revWords[0] = "ROOT";

        for (String word : wordMap.keySet()) {
            revWords[wordMap.get(word)] = word;
        }

        this.brown4Clusters = brown4Clusters;
        this.brown6Clusters = brown6Clusters;
        this.brownFullClusters = brownFullClusters;
        this.rootString = rootString;
        this.wdp = wdp;
    }

    public Sentence makeSentence(String[] words, String[] posTags, boolean rootFirst, boolean lowerCased) {
        ArrayList<Integer> tokens = new ArrayList<Integer>();
        ArrayList<Integer> tags = new ArrayList<Integer>();
        ArrayList<Integer> bc4 = new ArrayList<Integer>();
        ArrayList<Integer> bc6 = new ArrayList<Integer>();
        ArrayList<Integer> bcf = new ArrayList<Integer>();

        int i = 0;
        for (String word : words) {
            if (word.length() == 0)
                continue;
            String lowerCaseWord = word.toLowerCase();
            if (lowerCased)
                word = lowerCaseWord;

            int[] clusterIDs = clusterId(word);
            bcf.add(clusterIDs[0]);
            bc4.add(clusterIDs[1]);
            bc6.add(clusterIDs[2]);

            String pos = posTags[i];

            int wi = -1;
            if (wordMap.containsKey(word))
                wi = wordMap.get(word);

            int pi = -1;
            if (wordMap.containsKey(pos))
                pi = wordMap.get(pos);

            tokens.add(wi);
            tags.add(pi);

            i++;
        }

        if (!rootFirst) {
            tokens.add(0);
            tags.add(0);
            bcf.add(0);
            bc6.add(0);
            bc4.add(0);
        }

        return new Sentence(tokens, tags, bc4, bc6, bcf);
    }

    public HashMap<String, Integer> getWordMap() {
        return wordMap;
    }


    public HashMap<Integer, Integer> getLabels() {
        return labels;
    }

    public int[] clusterId(String word) {
        int[] ids = new int[3];
        ids[0] = -100;
        ids[1] = -100;
        ids[2] = -100;
        if (brownFullClusters.containsKey(word))
            ids[0] = brownFullClusters.get(word);

        if (ids[0] > 0) {
            ids[1] = brown4Clusters.get(ids[0]);
            ids[2] = brown6Clusters.get(ids[0]);
        }
        return ids;
    }

    public boolean hasClusters() {
        if (brownFullClusters != null && brownFullClusters.size() > 0)
            return true;
        return false;
    }
}
