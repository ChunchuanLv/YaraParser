/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package YaraParser.Accessories;

import Jama.Matrix;
import YaraParser.Structures.IndexMaps;
import YaraParser.Structures.Sentence;
import YaraParser.TransitionBasedSystem.Configuration.CompactTree;
import YaraParser.TransitionBasedSystem.Configuration.GoldConfiguration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;

public class CoNLLReader {
	/**
	 * An object for reading the CoNLL file
	 */
	BufferedReader fileReader;

	/**
	 * Initializes the file reader
	 *
	 * @param filePath
	 *            Path to the file
	 * @throws Exception
	 *             If the file path is not correct or there are not enough
	 *             permission to read the file
	 */
	public CoNLLReader(String filePath) throws Exception {
		fileReader = new BufferedReader(new FileReader(filePath));
	}

	public static double hitted = 0;
	public static double hittedl = 0;
	public static double hittedC = 0;

	private static int readWordEmbed(HashMap<Integer, Matrix> wordRep, HashMap<String, Integer> wordMap, String file,
			BufferedReader reader) throws NumberFormatException, IOException {
		String line;
		double[][] vector;
		int size = 0;
		reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			String[] spl = line.split("[\t ]");
			if (spl.length > 0) {
				String word = spl[0];
				size = spl.length - 1;
				vector = new double[1][size];
				if (!wordMap.containsKey(word)) {
					if (CoNLLReader.lowercased)
						word.toLowerCase();
					wordMap.put(word, wordMap.size());
				} else {
					hitted++;
				}
				int id = wordMap.get(word);
				for (int i = 0; i < size; i++)
					vector[0][i] = Double.parseDouble(spl[i + 1]);
				wordRep.put(id, new Matrix(vector));
			}
		}
		return size;
	}

	private static int readContextEmbed(HashMap<Integer, Matrix> wordRep, HashMap<String, Integer> wordMap, String file,
			BufferedReader reader) throws NumberFormatException, IOException {
		String line;
		double[][] vector;
		int size = 0;
		reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			String[] spl = line.split("[\t ]");
			if (spl.length > 0) {
				String word = spl[0];
				size = spl.length - 1;
				vector = new double[1][size];
				if (!wordMap.containsKey(word)) {
					if (CoNLLReader.lowercased)
						word.toLowerCase();
					wordMap.put(word, wordMap.size());
				} else {
					hittedC++;
				}
				int id = wordMap.get(word);
				for (int i = 0; i < size; i++)
					vector[0][i] = Double.parseDouble(spl[i + 1]);
				wordRep.put(id, new Matrix(vector).transpose());
			}
		}
		return size;
	}

	private static int[] getLabelId(String label, HashMap<String, String[]> labelToLabel,
			HashMap<String, Integer> wordMap, HashMap<Integer, Integer> labels) {
		String[] trueLabels = new String[] { label };
		if (labelToLabel.containsKey(label))
			trueLabels = labelToLabel.get(label);

		int[] ids = new int[trueLabels.length];
		for (int i = 0; i < trueLabels.length; i++) {
			String trueLabel = trueLabels[i];
			if (labels.containsKey(wordMap.get(trueLabel)))
				ids[i] = labels.get(wordMap.get(trueLabel));
			System.out.println("id " + trueLabel);
		}
		return ids;
	}

	private static void readLabEmbed(HashMap<Integer, Matrix> labelRep, HashMap<String, Integer> wordMap,
			HashMap<Integer, Integer> labels, String file, BufferedReader reader, int e1, int e2, String filepath)
			throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		HashMap<String, String[]> labelToLabel = createLabelToLabel(filepath + "/labels");
		String line;
		double[][] mat = new double[e1][e2];
		reader = new BufferedReader(new FileReader(file));
		int row = 0;
		int[] ids = new int[] {};
		String label;
		while ((line = reader.readLine()) != null) {
			String[] spl = line.split("[\t ]");
			if (spl.length == 1) {
				label = spl[0].toUpperCase();
				System.out.println("first line: " + label);
				ids = getLabelId(label, labelToLabel, wordMap, labels);
				row = 0;
				mat = new double[e1][e2];
			} else if (spl.length > 1) {
				for (int i = 0; i < e2; i++) {
					mat[row][i] = Double.parseDouble(spl[i]);
				}
				row++;
				if (row == e1 && ids.length != 0) {
					for (int id : ids) {
						System.out.println("to put " + id);
						if (!labelRep.containsKey(id)) {
							labelRep.put(id, new Matrix(mat));
							hittedl++;
						} else {
							labelRep.put(id, labelRep.get(id).plus(new Matrix(mat)));
						}
					}
				}
			}
		}
	}

	private static HashMap<String, String[]> createLabelToLabel(String filepath) throws IOException {
		HashMap<String, String[]> lTol = new HashMap<String, String[]>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] spl = line.trim().split("-");
			for (int i = 0; i < spl.length; i++) {
				System.out.println(spl[i] + "  " + line);
				if (!lTol.containsKey(spl[i])) {
					lTol.put(spl[i], new String[] { line });
				} else {
					String[] old = lTol.get(spl[i]);
					String[] ls = new String[1 + old.length];
					for (int j = 0; j < ls.length - 1; j++)
						ls[j] = old[j];
					ls[ls.length - 1] = line;
					lTol.put(spl[i], ls);
				}
			}
		}
		reader.close();
		return lTol;
	}

	private static boolean lowercased;

	public static IndexMaps createIndices(String filePath, boolean labeled, boolean lowercased, String clusterFile,
			String repPath, boolean depMat, String we, String ce, String depe) throws Exception {
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		HashMap<Integer, Integer> labels = new HashMap<Integer, Integer>();
		HashMap<String, Integer> clusterMap = new HashMap<String, Integer>();
		HashMap<Integer, Integer> cluster4Map = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> cluster6Map = new HashMap<Integer, Integer>();

		HashMap<Integer, Matrix> labelRep = new HashMap<Integer, Matrix>();
		HashMap<Integer, Matrix> wordRep = new HashMap<Integer, Matrix>();
		HashMap<Integer, Matrix> contRep = new HashMap<Integer, Matrix>();
		CoNLLReader.lowercased = lowercased;
		int labelCount = 1;
		String rootString = "ROOT";

		int wi = 1;
		wordMap.put("ROOT", 0);
		labels.put(0, 0);

		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] spl = line.trim().split("\t");
			if (spl.length > 7) {
				String label = spl[7];
				int head = Integer.parseInt(spl[6]);
				if (head == 0)
					rootString = label;

				if (label.equals("_"))
					label = "-";
				if (!labeled)
					label = "~";
				if (!wordMap.containsKey(label)) {
					labels.put(wi, labelCount++);
					wordMap.put(label, wi++);
				}
			}
		}

		reader = new BufferedReader(new FileReader(filePath));
		while ((line = reader.readLine()) != null) {
			String[] spl = line.trim().split("\t");
			if (spl.length > 7) {
				String pos = spl[3];
				if (!wordMap.containsKey(pos)) {
					wordMap.put(pos, wi++);
				}
			}
		}

		if (clusterFile.length() > 0) {
			reader = new BufferedReader(new FileReader(clusterFile));
			while ((line = reader.readLine()) != null) {
				String[] spl = line.trim().split("\t");
				if (spl.length > 2) {
					String cluster = spl[0];
					String word = spl[1];
					String prefix4 = cluster.substring(0, Math.min(4, cluster.length()));
					String prefix6 = cluster.substring(0, Math.min(6, cluster.length()));
					int clusterNum = wi;

					if (!wordMap.containsKey(cluster)) {
						clusterMap.put(word, wi);
						wordMap.put(cluster, wi++);
					} else {
						clusterNum = wordMap.get(cluster);
						clusterMap.put(word, clusterNum);
					}

					int pref4Id = wi;
					if (!wordMap.containsKey(prefix4)) {
						wordMap.put(prefix4, wi++);
					} else {
						pref4Id = wordMap.get(prefix4);
					}

					int pref6Id = wi;
					if (!wordMap.containsKey(prefix6)) {
						wordMap.put(prefix6, wi++);
					} else {
						pref6Id = wordMap.get(prefix6);
					}

					cluster4Map.put(clusterNum, pref4Id);
					cluster6Map.put(clusterNum, pref6Id);
				}
			}
		}

		reader = new BufferedReader(new FileReader(filePath));
		while ((line = reader.readLine()) != null) {
			String[] spl = line.trim().split("\t");
			if (spl.length > 7) {
				String word = spl[1];
				if (lowercased)
					word = word.toLowerCase();
				if (!wordMap.containsKey(word)) {
					wordMap.put(word, wi++);
				}
			}
		}

		if (repPath.length() > 0) {
			int e1 = readWordEmbed(wordRep, wordMap, repPath + "/" + we, reader);
			int e2 = readContextEmbed(contRep, wordMap, repPath + "/" + ce, reader);
			if (depMat) {
				readLabEmbed(labelRep, wordMap, labels, repPath + "/" + depe, reader, e1, e2, repPath);
				System.out.println(hittedl/ labels.size());
				System.out.println(labelRep.get(26));
			}
			System.out.println(hitted / wordRep.size());
			System.out.println(hittedC / contRep.size());
		}
		return new IndexMaps(wordMap, labels, rootString, cluster4Map, cluster6Map, clusterMap, wordRep, contRep,
				labelRep);
	}

	/**
	 * @param limit
	 *            it is used if we want to read part of the data
	 * @return
	 */
	public ArrayList<GoldConfiguration> readData(int limit, boolean keepNonProjective, boolean labeled,
			boolean rootFirst, boolean lowerCased, IndexMaps maps) throws Exception {
		HashMap<String, Integer> wordMap = maps.getWordMap();
		ArrayList<GoldConfiguration> configurationSet = new ArrayList<GoldConfiguration>();

		String line;
		ArrayList<Integer> tokens = new ArrayList<Integer>();
		ArrayList<Integer> tags = new ArrayList<Integer>();
		ArrayList<Integer> cluster4Ids = new ArrayList<Integer>();
		ArrayList<Integer> cluster6Ids = new ArrayList<Integer>();
		ArrayList<Integer> clusterIds = new ArrayList<Integer>();

		HashMap<Integer, Pair<Integer, Integer>> goldDependencies = new HashMap<Integer, Pair<Integer, Integer>>();
		int sentenceCounter = 0;
		while ((line = fileReader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				if (tokens.size() >= 1) {
					sentenceCounter++;
					if (!rootFirst) {
						for (int gold : goldDependencies.keySet()) {
							if (goldDependencies.get(gold).first.equals(0))
								goldDependencies.get(gold).setFirst(tokens.size() + 1);
						}
						tokens.add(0);
						tags.add(0);
						cluster4Ids.add(0);
						cluster6Ids.add(0);
						clusterIds.add(0);
					}
					Sentence currentSentence = new Sentence(tokens, tags, cluster4Ids, cluster6Ids, clusterIds);
					GoldConfiguration goldConfiguration = new GoldConfiguration(currentSentence, goldDependencies);
					if (keepNonProjective || !goldConfiguration.isNonprojective())
						configurationSet.add(goldConfiguration);
					goldDependencies = new HashMap<Integer, Pair<Integer, Integer>>();
					tokens = new ArrayList<Integer>();
					tags = new ArrayList<Integer>();
					cluster4Ids = new ArrayList<Integer>();
					cluster6Ids = new ArrayList<Integer>();
					clusterIds = new ArrayList<Integer>();
				} else {
					goldDependencies = new HashMap<Integer, Pair<Integer, Integer>>();
					tokens = new ArrayList<Integer>();
					tags = new ArrayList<Integer>();
					cluster4Ids = new ArrayList<Integer>();
					cluster6Ids = new ArrayList<Integer>();
					clusterIds = new ArrayList<Integer>();
				}
				if (sentenceCounter >= limit) {
					System.out.println("buffer full..." + configurationSet.size());
					break;
				}
			} else {
				String[] splitLine = line.split("\t");
				if (splitLine.length < 8)
					throw new Exception("wrong file format");
				int wordIndex = Integer.parseInt(splitLine[0]);
				String word = splitLine[1].trim();
				if (lowerCased)
					word = word.toLowerCase();
				String pos = splitLine[3].trim();

				int wi = -1;
				if (wordMap.containsKey(word))
					wi = wordMap.get(word);

				int pi = -1;
				if (wordMap.containsKey(pos))
					pi = wordMap.get(pos);

				tags.add(pi);
				tokens.add(wi);

				int headIndex = Integer.parseInt(splitLine[6]);
				String relation = splitLine[7];
				if (relation.equals("_"))
					relation = "-";
				if (!labeled)
					relation = "~";

				if (headIndex == 0)
					relation = "ROOT";

				int ri = -1;
				if (wordMap.containsKey(relation))
					ri = wordMap.get(relation);
				if (headIndex == -1)
					ri = -1;

				int[] ids = maps.clusterId(word);
				clusterIds.add(ids[0]);
				cluster4Ids.add(ids[1]);
				cluster6Ids.add(ids[2]);

				if (headIndex >= 0)
					goldDependencies.put(wordIndex, new Pair<Integer, Integer>(headIndex, ri));
			}
		}
		if (tokens.size() > 0) {
			if (!rootFirst) {
				for (int gold : goldDependencies.keySet()) {
					if (goldDependencies.get(gold).first.equals(0))
						goldDependencies.get(gold).setFirst(goldDependencies.size() + 1);
				}
				tokens.add(0);
				tags.add(0);
				cluster4Ids.add(0);
				cluster6Ids.add(0);
				clusterIds.add(0);
			}
			sentenceCounter++;
			Sentence currentSentence = new Sentence(tokens, tags, cluster4Ids, cluster6Ids, clusterIds);
			configurationSet.add(new GoldConfiguration(currentSentence, goldDependencies));
		}

		return configurationSet;
	}

	public ArrayList<CompactTree> readStringData() throws Exception {
		ArrayList<CompactTree> treeSet = new ArrayList<CompactTree>();

		String line;
		ArrayList<String> tags = new ArrayList<String>();

		HashMap<Integer, Pair<Integer, String>> goldDependencies = new HashMap<Integer, Pair<Integer, String>>();
		while ((line = fileReader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				if (tags.size() >= 1) {
					CompactTree goldConfiguration = new CompactTree(goldDependencies, tags);
					treeSet.add(goldConfiguration);
				}
				tags = new ArrayList<String>();
				goldDependencies = new HashMap<Integer, Pair<Integer, String>>();
			} else {
				String[] splitLine = line.split("\t");
				if (splitLine.length < 8)
					throw new Exception("wrong file format");
				int wordIndex = Integer.parseInt(splitLine[0]);
				String pos = splitLine[3].trim();

				tags.add(pos);

				int headIndex = Integer.parseInt(splitLine[6]);
				String relation = splitLine[7];

				if (headIndex == 0) {
					relation = "ROOT";
				}

				if (pos.length() > 0)
					goldDependencies.put(wordIndex, new Pair<Integer, String>(headIndex, relation));
			}
		}

		if (tags.size() > 0) {
			treeSet.add(new CompactTree(goldDependencies, tags));
		}

		return treeSet;
	}

}
