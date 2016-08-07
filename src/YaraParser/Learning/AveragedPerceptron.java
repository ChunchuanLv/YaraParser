/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package YaraParser.Learning;

import YaraParser.Structures.CompactArray;
import YaraParser.Structures.IndexMaps;
import YaraParser.Structures.InfStruct;
import YaraParser.TransitionBasedSystem.Parser.Actions;

import java.nio.ByteBuffer;
import java.util.HashMap;

import Jama.Matrix;

public class AveragedPerceptron {
	/**
	 * This class tries to implement averaged Perceptron algorithm Collins,
	 * Michael. "Discriminative training methods for hidden Markov models:
	 * Theory and experiments with Perceptron algorithms." In Proceedings of the
	 * ACL-02 conference on Empirical methods in natural language
	 * processing-Volume 10, pp. 1-8. Association for Computational Linguistics,
	 * 2002.
	 * <p/>
	 * The averaging update is also optimized by using the trick introduced in
	 * Hal Daume's dissertation. For more information see the second chapter of
	 * his thesis: Harold Charles Daume' III. "Practical Structured
	 * YaraParser.Learning Techniques for Natural Language Processing", PhD
	 * thesis, ISI USC, 2006.
	 * http://www.umiacs.umd.edu/~hal/docs/daume06thesis.pdf
	 */
	/**
	 * For the weights for all features
	 */
	public HashMap<Object, Float>[] shiftFeatureWeights;
	public HashMap<Object, Float>[] reduceFeatureWeights;
	public HashMap<Object, CompactArray>[] leftArcFeatureWeights;
	public HashMap<Object, CompactArray>[] rightArcFeatureWeights;

	public int iteration;
	public int dependencySize;
	/**
	 * This is the main part of the extension to the original perceptron
	 * algorithm which the averaging over all the history
	 */
	public HashMap<Object, Float>[] shiftFeatureAveragedWeights;
	public HashMap<Object, Float>[] reduceFeatureAveragedWeights;
	public HashMap<Object, CompactArray>[] leftArcFeatureAveragedWeights;
	public HashMap<Object, CompactArray>[] rightArcFeatureAveragedWeights;
	public boolean dep;
	public boolean depMat;
	public int depIndex;

	private void setDep(Object[] features) {
		int featSize = features.length;
		dep = false;
		depMat = false;
		dep = (featSize == 27) || (featSize == 73) || (featSize == 154);
		switch (featSize) {
		case 27:
			dep = true;
			depMat = (((long) features[26]) == 1L);
			depIndex = 26;
			break;
		case 73:
			dep = true;
			depMat = (((long) features[72] )== 1L);
			depIndex = 72;
			break;
		case 154:
			dep = true;
			depMat = (((long) features[153]) == 1L);
			depIndex = 153;
			break;
		}

	}

	private AveragedPerceptron(int featSize, int dependencySize) {
		shiftFeatureWeights = new HashMap[featSize];
		reduceFeatureWeights = new HashMap[featSize];
		leftArcFeatureWeights = new HashMap[featSize];
		rightArcFeatureWeights = new HashMap[featSize];

		shiftFeatureAveragedWeights = new HashMap[featSize];
		reduceFeatureAveragedWeights = new HashMap[featSize];
		leftArcFeatureAveragedWeights = new HashMap[featSize];
		rightArcFeatureAveragedWeights = new HashMap[featSize];
		for (int i = 0; i < featSize; i++) {
			shiftFeatureWeights[i] = new HashMap<Object, Float>();
			reduceFeatureWeights[i] = new HashMap<Object, Float>();
			leftArcFeatureWeights[i] = new HashMap<Object, CompactArray>();
			rightArcFeatureWeights[i] = new HashMap<Object, CompactArray>();

			shiftFeatureAveragedWeights[i] = new HashMap<Object, Float>();
			reduceFeatureAveragedWeights[i] = new HashMap<Object, Float>();
			leftArcFeatureAveragedWeights[i] = new HashMap<Object, CompactArray>();
			rightArcFeatureAveragedWeights[i] = new HashMap<Object, CompactArray>();
		}
		iteration = 1;
		this.dependencySize = dependencySize;
	}

	private AveragedPerceptron(HashMap<Object, Float>[] shiftFeatureAveragedWeights,
			HashMap<Object, Float>[] reduceFeatureAveragedWeights,
			HashMap<Object, CompactArray>[] leftArcFeatureAveragedWeights,
			HashMap<Object, CompactArray>[] rightArcFeatureAveragedWeights, int dependencySize) {
		this.shiftFeatureAveragedWeights = shiftFeatureAveragedWeights;
		this.reduceFeatureAveragedWeights = reduceFeatureAveragedWeights;
		this.leftArcFeatureAveragedWeights = leftArcFeatureAveragedWeights;
		this.rightArcFeatureAveragedWeights = rightArcFeatureAveragedWeights;
		int featSize = shiftFeatureAveragedWeights.length;
		dep = (featSize == 27) || (featSize == 73) || (featSize == 154);
		this.dependencySize = dependencySize;
	}

	public AveragedPerceptron(int featSize, int dependencySize, IndexMaps maps) {
		// TODO Auto-generated constructor stub
		this(featSize, dependencySize);
		wordRep = maps.getWordRep();
		contRep = maps.getContRep();
		labelRep =maps.getLabelRep();
		depMat = labelRep == null ||labelRep.size() >0;
		dep = (featSize == 27) || (featSize == 73) || (featSize == 154);
		wdp = maps.fastCompute();
	}

	private HashMap<Integer, float[]> wordRep;
    private HashMap<Integer,  float[]> contRep;
    private HashMap<Integer,  float[][]> labelRep;
    
	private AveragedPerceptron(InfStruct infStruct) {
		this(infStruct.shiftFeatureAveragedWeights, infStruct.reduceFeatureAveragedWeights,
				infStruct.leftArcFeatureAveragedWeights, infStruct.rightArcFeatureAveragedWeights,
				infStruct.dependencySize);
	}


	public AveragedPerceptron(InfStruct infStruct, IndexMaps maps) {
		// TODO Auto-generated constructor stub
		this(infStruct);
		wordRep = maps.getWordRep();
		contRep = maps.getContRep();
		labelRep =maps.getLabelRep();
		depMat = labelRep == null ||labelRep.size() >0;
		wdp = maps.fastCompute();
	}

	public float changeWeight(Actions actionType, int slotNum, Object featureName, int labelIndex, float change) {
		if (featureName == null)
			return 0;
		if (actionType == Actions.Shift) {
			if (!shiftFeatureWeights[slotNum].containsKey(featureName))
				shiftFeatureWeights[slotNum].put(featureName, change);
			else
				shiftFeatureWeights[slotNum].put(featureName, shiftFeatureWeights[slotNum].get(featureName) + change);

			if (!shiftFeatureAveragedWeights[slotNum].containsKey(featureName))
				shiftFeatureAveragedWeights[slotNum].put(featureName, iteration * change);
			else
				shiftFeatureAveragedWeights[slotNum].put(featureName,
						shiftFeatureAveragedWeights[slotNum].get(featureName) + iteration * change);
		} else if (actionType == Actions.Reduce) {
			if (!reduceFeatureWeights[slotNum].containsKey(featureName))
				reduceFeatureWeights[slotNum].put(featureName, change);
			else
				reduceFeatureWeights[slotNum].put(featureName, reduceFeatureWeights[slotNum].get(featureName) + change);

			if (!reduceFeatureAveragedWeights[slotNum].containsKey(featureName))
				reduceFeatureAveragedWeights[slotNum].put(featureName, iteration * change);
			else
				reduceFeatureAveragedWeights[slotNum].put(featureName,
						reduceFeatureAveragedWeights[slotNum].get(featureName) + iteration * change);
		} else if (actionType == Actions.RightArc) {
			changeFeatureWeight(rightArcFeatureWeights[slotNum], rightArcFeatureAveragedWeights[slotNum], featureName,
					labelIndex, change, dependencySize);
		} else if (actionType == Actions.LeftArc) {
			changeFeatureWeight(leftArcFeatureWeights[slotNum], leftArcFeatureAveragedWeights[slotNum], featureName,
					labelIndex, change, dependencySize);
		}

		return change;
	}

	public float changeWeight(Actions actionType, int slotNum, Object featureName, int labelIndex, float change,Object[] features) {
		if (!dep || slotNum!=depIndex) return changeWeight(actionType,slotNum,featureName,labelIndex,change); 
		if (actionType == Actions.RightArc) {
			if (getVecCost(features,rightArcFeatureWeights,false,labelIndex)<0) change = -change;
			changeFeatureWeight(rightArcFeatureWeights[slotNum], rightArcFeatureAveragedWeights[slotNum], featureName,
					labelIndex, change, dependencySize);
		} else if (actionType == Actions.LeftArc) {
			if (getVecCost(features,leftArcFeatureWeights,true,labelIndex)<0) change = -change;
			changeFeatureWeight(leftArcFeatureWeights[slotNum], leftArcFeatureAveragedWeights[slotNum], featureName,
					labelIndex, change, dependencySize);
		}
		return change;
	}

	public void changeFeatureWeight(HashMap<Object, CompactArray> map, HashMap<Object, CompactArray> aMap,
			Object featureName, int labelIndex, float change, int size) {
		CompactArray values = map.get(featureName);
		CompactArray aValues;
		if (values != null) {
			values.expandArray(labelIndex, change);
			aValues = aMap.get(featureName);
			aValues.expandArray(labelIndex, iteration * change);
		} else {
			float[] val = new float[] { change };
			values = new CompactArray(labelIndex, val);
			map.put(featureName, values);

			float[] aVal = new float[] { iteration * change };
			aValues = new CompactArray(labelIndex, aVal);
			aMap.put(featureName, aValues);
		}
	}

	/**
	 * Adds to the iterations
	 */
	public void incrementIteration() {
		iteration++;
	}

	private float featureToDouble(Object value) {
		float r = (float) 1.0;
		if ((value instanceof Byte) || (value instanceof Short) || (value instanceof Integer) || (value instanceof Long)
				|| (value instanceof Float) || (value instanceof Double)) {
			r = (float) value;
		}
		return r;
	}

	public float shiftScore(final Object[] features, boolean decode) {
		float score = 0.0f;

		HashMap<Object, Float>[] map = decode ? shiftFeatureAveragedWeights : shiftFeatureWeights;
		for (int i = 0; i < features.length; i++) {
			if (features[i] == null || (i >= 26 && i < 32))
				continue;
			Float values = map[i].get(features[i]);
			if (values != null) {
				score += values;
			}
		}

		return score;
	}

	public float reduceScore(final Object[] features, boolean decode) {
		float score = 0.0f;

		HashMap<Object, Float>[] map = decode ? reduceFeatureAveragedWeights : reduceFeatureWeights;

		for (int i = 0; i < features.length; i++) {
			if (features[i] == null || (i >= 26 && i < 32))
				continue;
			Float values = map[i].get(features[i]) ;
			if (values != null) {
				score += values;
			}
		}
		return score;
	}

	public float[] leftArcScores(final Object[] features, boolean decode) {
		float scores[] = new float[dependencySize];

		HashMap<Object, CompactArray>[] map = decode ? leftArcFeatureAveragedWeights : leftArcFeatureWeights;

		for (int i = 0; i < features.length; i++) {
			if (features[i] == null || (dep && i == depIndex))
				continue;
			CompactArray values = map[i].get(features[i]);
			if (values != null) {
				int offset = values.getOffset();
				float[] weightVector = values.getArray();

				for (int d = offset; d < offset + weightVector.length; d++) {
					scores[d] += weightVector[d - offset];
				}
			}
		}
		if (dep) {
			float[] addScore = getVecCost(features,map,true);
			for (int i=0; i<addScore.length;i++) scores[i] += addScore[i];
		}

		return scores;
	}
public float[] getVecCost(final Object[] features,HashMap<Object, CompactArray>[]  map,boolean left) {
	float scores[] = new float[dependencySize];
	if (dep) {
		if (features[1]==null||features[4]==null) return scores;
		int head;
		int word;
		if (left) {
		head = (int) (long)features[4] - 2;
	 word = (int)(long) features[1] - 2;
		}else
		{
			head =  (int) (long)features[1] - 2;
			word =(int) (long)features[4] - 2; 
		}
		CompactArray values = map[depIndex].get(features[depIndex]);
		if (values != null) {
			int offset = values.getOffset();
			float[] weightVector = values.getArray();
			if (depMat)
				for (int d = offset; d < offset + weightVector.length; d++)
					scores[d] = weightVector[d - offset] * getCostDep(word, head, d);
			else {
				float cost = getCost(word, head);
				for (int d = offset; d < offset + weightVector.length; d++)
					scores[d] = weightVector[d - offset] * cost;
			}
		}
	}
	
	return scores;
}


public float getVecCost(final Object[] features,HashMap<Object, CompactArray>[]  map,boolean left,int d) {
	float scores = 0;
	if (dep) {
		if (features[1]==null||features[4]==null) return scores;
		int head;
		int word;
		if (left) {
		head = (int) (long)features[4] - 2;
	 word = (int)(long) features[1] - 2;
		}else
		{
			head =  (int) (long)features[1] - 2;
			word =(int) (long)features[4] - 2; 
		}
		CompactArray values = map[depIndex].get(features[depIndex]);
		if (values != null) {
			int offset = values.getOffset();
			float[] weightVector = values.getArray();
			if ( offset<= d &&d< offset + weightVector.length)
			if (depMat)
					scores= weightVector[d - offset] * getCostDep(word, head, d);
			else {
				float cost = getCost(word, head);
					scores = weightVector[d - offset] * cost;
			}
		}
	}
	
	return scores;
}
	private float getCost(int word, int head) {

		if (!wordRep.containsKey(word)||!contRep.containsKey(head)) return 0;
		float result = 0;
		float[] v1 =  wordRep.get(word);
		float[] v2 =  contRep.get(head);
		int size = v1.length;
		for (int i=0;i<size;i++)
			result += v1[i]*v2[i];
		return result ;
	}

	private  HashMap<Integer,Float> wdp ;
	private float getCostDep(int word, int head, int dep) {

		if (!wordRep.containsKey(word)||!contRep.containsKey(head)||!labelRep.containsKey(dep)) {
			return 0;
		}
		ByteBuffer key =  ByteBuffer.allocate(10);
		key.putInt(word);
		key.putInt(head);
		key.putShort((short) dep);
		if (wdp.containsKey(key)) {
		//	System.out.println("contrain:"+key);
			return wdp.get(key);
		}

		float[] v1 =  wordRep.get(word);
		float[] v2 =  contRep.get(head);
		float[][] m = labelRep.get(dep);
		float result = 0f;
		int size1 = v1.length;
		int size2 = v2.length;
		 for (int i =0;i<size1;i++)
			 for (int j =0;j<size2;j++)
				 result +=  v1[i]*v2[j]*m[i][j];
		wdp.put(key.hashCode(), result);
	//	System.out.println("word,head,label ,result "+word+" "+head+" "+dep+" "+result);
		return  result;
	}
	

	private float[] getCostDep(int word, int head, int depS, int depE) {
		float[] result = new float[depE-depS];
	//	System.out.println("word,head,label ,result "+word+" "+head+" "+dep+" "+result);
		return  result;
	}
	

	public float[] rightArcScores(final Object[] features, boolean decode) {
		float scores[] = new float[dependencySize];

		HashMap<Object, CompactArray>[] map = decode ? rightArcFeatureAveragedWeights : rightArcFeatureWeights;

		for (int i = 0; i < features.length; i++) {
			if (features[i] == null || (dep && i == depIndex))
				continue;
			CompactArray values = map[i].get(features[i]);
			if (values != null) {
				int offset = values.getOffset();
				float[] weightVector = values.getArray();

				for (int d = offset; d < offset + weightVector.length; d++) {
					scores[d] += weightVector[d - offset];
				}
			}
		}

		if (dep) {
			float[] addScore = getVecCost(features,map,false);
			for (int i=0; i<addScore.length;i++) scores[i] += addScore[i];
		}
		return scores;
	}

	public int featureSize() {
		return shiftFeatureAveragedWeights.length;
	}

	public int raSize() {
		int size = 0;
		for (int i = 0; i < leftArcFeatureAveragedWeights.length; i++) {
			for (Object feat : rightArcFeatureAveragedWeights[i].keySet()) {
				size += rightArcFeatureAveragedWeights[i].get(feat).length();
			}
		}
		return size;
	}

	public int effectiveRaSize() {
		int size = 0;
		for (int i = 0; i < leftArcFeatureAveragedWeights.length; i++) {
			for (Object feat : rightArcFeatureAveragedWeights[i].keySet()) {
				for (float f : rightArcFeatureAveragedWeights[i].get(feat).getArray())
					if (f != 0f)
						size++;
			}
		}
		return size;
	}

	public int laSize() {
		int size = 0;
		for (int i = 0; i < leftArcFeatureAveragedWeights.length; i++) {
			for (Object feat : leftArcFeatureAveragedWeights[i].keySet()) {
				size += leftArcFeatureAveragedWeights[i].get(feat).length();
			}
		}
		return size;
	}

	public int effectiveLaSize() {
		int size = 0;
		for (int i = 0; i < leftArcFeatureAveragedWeights.length; i++) {
			for (Object feat : leftArcFeatureAveragedWeights[i].keySet()) {
				for (float f : leftArcFeatureAveragedWeights[i].get(feat).getArray())
					if (f != 0f)
						size++;
			}
		}
		return size;
	}
}