/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project 0 for terms.
 */

package YaraParser.TransitionBasedSystem.Features;

import YaraParser.Structures.Sentence;
import YaraParser.TransitionBasedSystem.Configuration.Configuration;
import YaraParser.TransitionBasedSystem.Configuration.State;

public class FeatureExtractor {
	/**
	 * Given a list of templates, extracts all features for the given state
	 *
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public static Object[] extractAllParseFeatures(Configuration configuration, int length) {
		Object[] result;
		if (length == 27)
			result = extractBasicFeatures(configuration, length);
		else if (length == 73)
			result = extractExtendedFeatures(configuration, length);
		else if (length == 154)
			result = extractExtendedFeaturesWithBrownClusters(configuration, length);
		else if (length == 26)
			result = extractBasicFeatures(configuration, length);
		else if (length == 72)
			result = extractExtendedFeatures(configuration, length);
		else
			result = extractExtendedFeaturesWithBrownClusters(configuration, length);

		return result;
	}

	/**
	 * Given a list of templates, extracts all features for the given state
	 *
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	private static Object[] extractExtendedFeatures(Configuration configuration, int length) {
		Object[] featureMap = new Object[length];

		State state = configuration.state;
		Sentence sentence = configuration.sentence;

		int b0Position = 0;
		int b1Position = 0;
		int b2Position = 0;
		int s0Position = 0;

		long svr = 0; // stack right valency
		long svl = 0; // stack left valency
		long bvl = 0; // buffer left valency

		long b0w = 0;
		long b0p = 0;

		long b1w = 0;
		long b1p = 0;

		long b2w = 0;
		long b2p = 0;

		long s0w = 0;
		long s0p = 0;
		long s0l = 0;

		long bl0p = 0;
		long bl0w = 0;
		long bl0l = 0;

		long bl1w = 0;
		long bl1p = 0;
		long bl1l = 0;

		long sr0p = 0;
		long sr0w = 0;
		long sr0l = 0;

		long sh0w = 0;
		long sh0p = 0;
		long sh0l = 0;

		long sl0p = 0;
		long sl0w = 0;
		long sl0l = 0;

		long sr1w = 0;
		long sr1p = 0;
		long sr1l = 0;

		long sh1w = 0;
		long sh1p = 0;

		long sl1w = 0;
		long sl1p = 0;
		long sl1l = 0;

		long sdl = 0;
		long sdr = 0;
		long bdl = 0;

		int[] words = sentence.getWords();
		int[] tags = sentence.getTags();

		if (0 < state.bufferSize()) {
			b0Position = state.bufferHead();
			b0w = b0Position == 0 ? 0 : words[b0Position - 1];
			b0w += 2;
			b0p = b0Position == 0 ? 0 : tags[b0Position - 1];
			b0p += 2;
			bvl = state.leftValency(b0Position);

			int leftMost = state.leftMostModifier(state.getBufferItem(0));
			if (leftMost >= 0) {
				bl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				bl0p += 2;
				bl0w = leftMost == 0 ? 0 : words[leftMost - 1];
				bl0w += 2;
				bl0l = state.getDependency(leftMost);
				bl0l += 2;

				int l2 = state.leftMostModifier(leftMost);
				if (l2 >= 0) {
					bl1w = l2 == 0 ? 0 : words[l2 - 1];
					bl1w += 2;
					bl1p = l2 == 0 ? 0 : tags[l2 - 1];
					bl1p += 2;
					bl1l = state.getDependency(l2);
					bl1l += 2;
				}
			}

			if (1 < state.bufferSize()) {
				b1Position = state.getBufferItem(1);
				b1w = b1Position == 0 ? 0 : words[b1Position - 1];
				b1w += 2;
				b1p = b1Position == 0 ? 0 : tags[b1Position - 1];
				b1p += 2;

				if (2 < state.bufferSize()) {
					b2Position = state.getBufferItem(2);

					b2w = b2Position == 0 ? 0 : words[b2Position - 1];
					b2w += 2;
					b2p = b2Position == 0 ? 0 : tags[b2Position - 1];
					b2p += 2;
				}
			}
		}

		if (0 < state.stackSize()) {
			s0Position = state.peek();
			s0w = s0Position == 0 ? 0 : words[s0Position - 1];
			s0w += 2;
			s0p = s0Position == 0 ? 0 : tags[s0Position - 1];
			s0p += 2;
			s0l = state.getDependency(s0Position);
			s0l += 2;

			svl = state.leftValency(s0Position);
			svr = state.rightValency(s0Position);

			int leftMost = state.leftMostModifier(s0Position);
			if (leftMost >= 0) {
				sl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				sl0p += 2;
				sl0w = leftMost == 0 ? 0 : words[leftMost - 1];
				sl0w += 2;
				sl0l = state.getDependency(leftMost);
				sl0l += 2;
			}

			int rightMost = state.rightMostModifier(s0Position);
			if (rightMost >= 0) {
				sr0p = rightMost == 0 ? 0 : tags[rightMost - 1];
				sr0p += 2;
				sr0w = rightMost == 0 ? 0 : words[rightMost - 1];
				sr0w += 2;
				sr0l = state.getDependency(rightMost);
				sr0l += 2;
			}

			int headIndex = state.getHead(s0Position);
			if (headIndex >= 0) {
				sh0w = headIndex == 0 ? 0 : words[headIndex - 1];
				sh0w += 2;
				sh0p = headIndex == 0 ? 0 : tags[headIndex - 1];
				sh0p += 2;
				sh0l = state.getDependency(headIndex);
				sh0l += 2;
			}

			if (leftMost >= 0) {
				int l2 = state.leftMostModifier(leftMost);
				if (l2 >= 0) {
					sl1w = l2 == 0 ? 0 : words[l2 - 1];
					sl1w += 2;
					sl1p = l2 == 0 ? 0 : tags[l2 - 1];
					sl1p += 2;
					sl1l = state.getDependency(l2);
					sl1l += 2;
				}
			}
			if (headIndex >= 0) {
				if (state.hasHead(headIndex)) {
					int h2 = state.getHead(headIndex);
					sh1w = h2 == 0 ? 0 : words[h2 - 1];
					sh1w += 2;
					sh1p = h2 == 0 ? 0 : tags[h2 - 1];
					sh1p += 2;
				}
			}
			if (rightMost >= 0) {
				int r2 = state.rightMostModifier(rightMost);
				if (r2 >= 0) {
					sr1w = r2 == 0 ? 0 : words[r2 - 1];
					sr1w += 2;
					sr1p = r2 == 0 ? 0 : tags[r2 - 1];
					sr1p += 2;
					sr1l = state.getDependency(r2);
					sr1l += 2;
				}
			}
		}
		int index = 0;

		long b0wp = b0p;
		b0wp |= (b0w << 8);
		long b1wp = b1p;
		b1wp |= (b1w << 8);
		long s0wp = s0p;
		s0wp |= (s0w << 8);
		long b2wp = b2p;
		b2wp |= (b2w << 8);

		/**
		 * From single words
		 */
		if (s0w != 1) {
			featureMap[index++] = s0wp;
			featureMap[index++] = s0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p;

		if (b0w != 1) {
			featureMap[index++] = b0wp;
			featureMap[index++] = b0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p;

		if (b1w != 1) {
			featureMap[index++] = b1wp;
			featureMap[index++] = b1w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b1p;

		if (b2w != 1) {
			featureMap[index++] = b2wp;
			featureMap[index++] = b2w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b2p;

		/**
		 * from word pairs
		 */
		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0wp << 28) | b0wp;
			featureMap[index++] = (s0wp << 20) | b0w;
			featureMap[index++] = (s0w << 28) | b0wp;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0w != 1) {
			featureMap[index++] = (s0wp << 8) | b0p;
		} else {
			featureMap[index++] = null;
		}

		if (b0w != 1) {
			featureMap[index++] = (s0p << 28) | b0wp;
		} else {
			featureMap[index++] = null;
		}

		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0w << 20) | b0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p << 8) | b0p;
		featureMap[index++] = (b0p << 8) | b1p;

		/**
		 * from three words
		 */
		featureMap[index++] = (b0p << 16) | (b1p << 8) | b2p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | b1p;
		featureMap[index++] = (sh0p << 16) | (s0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sl0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sr0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | bl0p;

		/**
		 * distance
		 */
		long distance = 0;
		if (s0Position > 0 && b0Position > 0)
			distance = Math.abs(b0Position - s0Position);
		if (s0w != 1) {
			featureMap[index++] = s0w | (distance << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (distance << 8);
		if (b0w != 1) {
			featureMap[index++] = b0w | (distance << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p | (distance << 8);
		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = s0w | (b0w << 20) | (distance << 40);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (b0p << 8) | (distance << 28);

		/**
		 * Valency information
		 */
		if (s0w != 1) {
			featureMap[index++] = s0w | (svr << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (svr << 8);
		if (s0w != 1) {
			featureMap[index++] = s0w | (svl << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (svl << 8);
		if (b0w != 1) {
			featureMap[index++] = b0w | (bvl << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p | (bvl << 8);

		/**
		 * Unigrams
		 */
		if (sh0w != 1) {
			featureMap[index++] = sh0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sh0p;
		featureMap[index++] = s0l;
		if (sl0w != 1) {
			featureMap[index++] = sl0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sl0p;
		featureMap[index++] = sl0l;
		if (sr0w != 1) {
			featureMap[index++] = sr0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sr0p;
		featureMap[index++] = sr0l;
		if (bl0w != 1) {
			featureMap[index++] = bl0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = bl0p;
		featureMap[index++] = bl0l;

		/**
		 * From third order features
		 */
		if (sh1w != 1) {
			featureMap[index++] = sh1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sh1p;
		featureMap[index++] = sh0l;
		if (sl1w != 1) {
			featureMap[index++] = sl1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sl1p;
		featureMap[index++] = sl1l;
		if (sr1w != 1) {
			featureMap[index++] = sr1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sr1p;
		featureMap[index++] = sr1l;
		if (bl1w != 1) {
			featureMap[index++] = bl1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = bl1p;
		featureMap[index++] = bl1l;
		featureMap[index++] = s0p | (sl0p << 8) | (sl1p << 16);
		featureMap[index++] = s0p | (sr0p << 8) | (sr1p << 16);
		featureMap[index++] = s0p | (sh0p << 8) | (sh1p << 16);
		featureMap[index++] = b0p | (bl0p << 8) | (bl1p << 16);

		/**
		 * label set
		 */
		if (s0Position >= 0) {
			sdl = state.leftDependentLabels(s0Position);
			sdr = state.rightDependentLabels(s0Position);
		}

		if (b0Position >= 0) {
			bdl = state.leftDependentLabels(b0Position);
		}

		if (s0w != 1) {
			featureMap[index++] = (s0w + "|" + sdr);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p + "|" + sdr);
		if (s0w != 1) {
			featureMap[index++] = s0w + "|" + sdl;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p + "|" + sdl);
		if (b0w != 1) {
			featureMap[index++] = (b0w + "|" + bdl);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (b0p + "|" + bdl);

		if (featureMap.length==73) {
			if (configuration.depMat)
				featureMap[index++] = 1L;
			else
				featureMap[index++] = 0L;
		}
		return featureMap;
	}

	/**
	 * Given a list of templates, extracts all features for the given state
	 *
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	private static Long[] extractBasicFeatures(Configuration configuration, int length) {
		Long[] featureMap = new Long[length];

		State state = configuration.state;
		Sentence sentence = configuration.sentence;

		int b0Position = 0;
		int b1Position = 0;
		int b2Position = 0;
		int s0Position = 0;

		long b0w = 0;
		long b0p = 0;

		long b1w = 0;
		long b1p = 0;

		long b2w = 0;
		long b2p = 0;

		long s0w = 0;
		long s0p = 0;
		long bl0p = 0;
		long sr0p = 0;
		long sh0p = 0;

		long sl0p = 0;

		int[] words = sentence.getWords();
		int[] tags = sentence.getTags();

		if (0 < state.bufferSize()) {
			b0Position = state.bufferHead();
			b0w = b0Position == 0 ? 0 : words[b0Position - 1];
			b0w += 2;
			b0p = b0Position == 0 ? 0 : tags[b0Position - 1];
			b0p += 2;

			int leftMost = state.leftMostModifier(state.getBufferItem(0));
			if (leftMost >= 0) {
				bl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				bl0p += 2;
			}

			if (1 < state.bufferSize()) {
				b1Position = state.getBufferItem(1);
				b1w = b1Position == 0 ? 0 : words[b1Position - 1];
				b1w += 2;
				b1p = b1Position == 0 ? 0 : tags[b1Position - 1];
				b1p += 2;

				if (2 < state.bufferSize()) {
					b2Position = state.getBufferItem(2);

					b2w = b2Position == 0 ? 0 : words[b2Position - 1];
					b2w += 2;
					b2p = b2Position == 0 ? 0 : tags[b2Position - 1];
					b2p += 2;
				}
			}
		}

		if (0 < state.stackSize()) {
			s0Position = state.peek();
			s0w = s0Position == 0 ? 0 : words[s0Position - 1];
			s0w += 2;
			s0p = s0Position == 0 ? 0 : tags[s0Position - 1];
			s0p += 2;

			int leftMost = state.leftMostModifier(s0Position);
			if (leftMost >= 0) {
				sl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				sl0p += 2;
			}

			int rightMost = state.rightMostModifier(s0Position);
			if (rightMost >= 0) {
				sr0p = rightMost == 0 ? 0 : tags[rightMost - 1];
				sr0p += 2;
			}

			int headIndex = state.getHead(s0Position);
			if (headIndex >= 0) {
				sh0p = headIndex == 0 ? 0 : tags[headIndex - 1];
				sh0p += 2;
			}

		}
		int index = 0;

		long b0wp = b0p;
		b0wp |= (b0w << 8);
		long b1wp = b1p;
		b1wp |= (b1w << 8);
		long s0wp = s0p;
		s0wp |= (s0w << 8);
		long b2wp = b2p;
		b2wp |= (b2w << 8);

		/**
		 * From single words
		 */
		if (s0w != 1) {
			featureMap[index++] = s0wp;
			featureMap[index++] = s0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p;

		if (b0w != 1) {
			featureMap[index++] = b0wp;
			featureMap[index++] = b0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p;

		if (b1w != 1) {
			featureMap[index++] = b1wp;
			featureMap[index++] = b1w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b1p;

		if (b2w != 1) {
			featureMap[index++] = b2wp;
			featureMap[index++] = b2w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b2p;

		/**
		 * from word pairs
		 */
		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0wp << 28) | b0wp;
			featureMap[index++] = (s0wp << 20) | b0w;
			featureMap[index++] = (s0w << 28) | b0wp;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0w != 1) {
			featureMap[index++] = (s0wp << 8) | b0p;
		} else {
			featureMap[index++] = null;
		}

		if (b0w != 1) {
			featureMap[index++] = (s0p << 28) | b0wp;
		} else {
			featureMap[index++] = null;
		}

		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0w << 20) | b0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p << 8) | b0p;
		featureMap[index++] = (b0p << 8) | b1p;

		/**
		 * from three words
		 */
		featureMap[index++] = (b0p << 16) | (b1p << 8) | b2p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | b1p;
		featureMap[index++] = (sh0p << 16) | (s0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sl0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sr0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | bl0p;

		if (featureMap.length==27) {
			if (configuration.depMat)
				featureMap[index++] = 1L;
			else
				featureMap[index++] = 0L;
		}
		return featureMap;
	}

	private static Object[] extractExtendedFeaturesWithBrownClusters(Configuration configuration, int length) {
		Object[] featureMap = new Object[length];

		State state = configuration.state;
		Sentence sentence = configuration.sentence;

		int b0Position = 0;
		int b1Position = 0;
		int b2Position = 0;
		int s0Position = 0;

		int svr = 0; // stack right valency
		int svl = 0; // stack left valency
		int bvl = 0; // buffer left valency

		long b0w = 0; // buffer first word
		long b0p = 0; // pos tag
		long b0bc4 = 0;
		long b0bc6 = 0;
		long b0bcf = 0;

		long b1w = 0;
		long b1p = 0;

		long b2w = 0;
		long b2p = 0;

		long s0w = 0;
		long s0p = 0;
		long s0bc4 = 0;
		long s0bc6 = 0;
		long s0bcf = 0;

		long s0l = 0;

		long bl0p = 0;
		long bl0w = 0;
		long bl0l = 0;

		long bl1w = 0;
		long bl1p = 0;
		long bl1l = 0;

		long sr0p = 0;
		long sr0w = 0;
		long sr0l = 0;

		long sh0w = 0;
		long sh0p = 0;
		long sh0l = 0;

		long sl0p = 0;
		long sl0w = 0;
		long sl0l = 0;

		long sr1w = 0;
		long sr1p = 0;
		long sr1l = 0;

		long sh1w = 0;
		long sh1p = 0;

		long sl1w = 0;
		long sl1p = 0;
		long sl1l = 0;

		long sdl = 0;
		long sdr = 0;
		long bdl = 0;

		int[] words = sentence.getWords();
		int[] tags = sentence.getTags();
		int[] bc4 = sentence.getBrownCluster4thPrefix();
		int[] bc6 = sentence.getBrownCluster6thPrefix();
		int[] bcf = sentence.getBrownClusterFullString();

		if (0 < state.bufferSize()) {
			b0Position = state.bufferHead();
			b0w = b0Position == 0 ? 0 : words[b0Position - 1];
			b0w += 2;
			b0p = b0Position == 0 ? 0 : tags[b0Position - 1];
			b0p += 2;
			b0bc4 = b0Position == 0 ? 0 : bc4[b0Position - 1];
			b0bc4 += 2;
			b0bc6 = b0Position == 0 ? 0 : bc6[b0Position - 1];
			b0bc6 += 2;
			b0bcf = b0Position == 0 ? 0 : bcf[b0Position - 1];
			b0bcf += 2;

			bvl = state.leftValency(b0Position);

			int leftMost = state.leftMostModifier(state.getBufferItem(0));
			if (leftMost >= 0) {
				bl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				bl0p += 2;
				bl0w = leftMost == 0 ? 0 : words[leftMost - 1];
				bl0w += 2;
				bl0l = state.getDependency(leftMost);
				bl0l += 2;

				int l2 = state.leftMostModifier(leftMost);
				if (l2 >= 0) {
					bl1w = l2 == 0 ? 0 : words[l2 - 1];
					bl1w += 2;
					bl1p = l2 == 0 ? 0 : tags[l2 - 1];
					bl1p += 2;
					bl1l = state.getDependency(l2);
					bl1l += 2;
				}
			}

			if (1 < state.bufferSize()) {
				b1Position = state.getBufferItem(1);
				b1w = b1Position == 0 ? 0 : words[b1Position - 1];
				b1w += 2;
				b1p = b1Position == 0 ? 0 : tags[b1Position - 1];
				b1p += 2;

				if (2 < state.bufferSize()) {
					b2Position = state.getBufferItem(2);

					b2w = b2Position == 0 ? 0 : words[b2Position - 1];
					b2w += 2;
					b2p = b2Position == 0 ? 0 : tags[b2Position - 1];
					b2p += 2;
				}
			}
		}

		if (0 < state.stackSize()) {
			s0Position = state.peek();
			s0w = s0Position == 0 ? 0 : words[s0Position - 1]; // stack top
			s0w += 2;
			s0p = s0Position == 0 ? 0 : tags[s0Position - 1];
			s0p += 2;
			s0bc4 = s0Position == 0 ? 0 : bc4[s0Position - 1];
			s0bc4 += 2;
			s0bc6 = s0Position == 0 ? 0 : bc6[s0Position - 1];
			s0bc6 += 2;
			s0bcf = s0Position == 0 ? 0 : bcf[s0Position - 1];
			s0bcf += 2;

			s0l = state.getDependency(s0Position);
			s0l += 2;

			svl = state.leftValency(s0Position);
			svr = state.rightValency(s0Position);

			int leftMost = state.leftMostModifier(s0Position);
			if (leftMost >= 0) {
				sl0p = leftMost == 0 ? 0 : tags[leftMost - 1];
				sl0p += 2;
				sl0w = leftMost == 0 ? 0 : words[leftMost - 1];
				sl0w += 2;
				sl0l = state.getDependency(leftMost);
				sl0l += 2;
			}

			int rightMost = state.rightMostModifier(s0Position);
			if (rightMost >= 0) {
				sr0p = rightMost == 0 ? 0 : tags[rightMost - 1];
				sr0p += 2;
				sr0w = rightMost == 0 ? 0 : words[rightMost - 1];
				sr0w += 2;
				sr0l = state.getDependency(rightMost);
				sr0l += 2;
			}

			int headIndex = state.getHead(s0Position);
			if (headIndex >= 0) {
				sh0w = headIndex == 0 ? 0 : words[headIndex - 1];
				sh0w += 2;
				sh0p = headIndex == 0 ? 0 : tags[headIndex - 1];
				sh0p += 2;
				sh0l = state.getDependency(headIndex);
				sh0l += 2;
			}

			if (leftMost >= 0) {
				int l2 = state.leftMostModifier(leftMost);
				if (l2 >= 0) {
					sl1w = l2 == 0 ? 0 : words[l2 - 1];
					sl1w += 2;
					sl1p = l2 == 0 ? 0 : tags[l2 - 1];
					sl1p += 2;
					sl1l = state.getDependency(l2);
					sl1l += 2;
				}
			}
			if (headIndex >= 0) {
				if (state.hasHead(headIndex)) {
					int h2 = state.getHead(headIndex);
					sh1w = h2 == 0 ? 0 : words[h2 - 1];
					sh1w += 2;
					sh1p = h2 == 0 ? 0 : tags[h2 - 1];
					sh1p += 2;
				}
			}
			if (rightMost >= 0) {
				int r2 = state.rightMostModifier(rightMost);
				if (r2 >= 0) {
					sr1w = r2 == 0 ? 0 : words[r2 - 1];
					sr1w += 2;
					sr1p = r2 == 0 ? 0 : tags[r2 - 1];
					sr1p += 2;
					sr1l = state.getDependency(r2);
					sr1l += 2;
				}
			}
		}
		int index = 0;

		long b0wp = b0p;
		b0wp |= (b0w << 8);
		long b1wp = b1p;
		b1wp |= (b1w << 8);
		long s0wp = s0p;
		s0wp |= (s0w << 8);
		long b2wp = b2p;
		b2wp |= (b2w << 8);

		/**
		 * From single words
		 */
		if (s0w != 1) {
			featureMap[index++] = s0wp;
			featureMap[index++] = s0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p;

		if (b0w != 1) {
			featureMap[index++] = b0wp;
			featureMap[index++] = b0w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p;

		if (b1w != 1) {
			featureMap[index++] = b1wp;
			featureMap[index++] = b1w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b1p;

		if (b2w != 1) {
			featureMap[index++] = b2wp;
			featureMap[index++] = b2w;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		featureMap[index++] = b2p;

		/**
		 * from word pairs
		 */
		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0wp << 28) | b0wp;
			featureMap[index++] = (s0wp << 20) | b0w;
			featureMap[index++] = (s0w << 28) | b0wp;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0w != 1) {
			featureMap[index++] = (s0wp << 8) | b0p;
		} else {
			featureMap[index++] = null;
		}

		if (b0w != 1) {
			featureMap[index++] = (s0p << 28) | b0wp;
		} else {
			featureMap[index++] = null;
		}

		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = (s0w << 20) | b0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p << 8) | b0p;
		featureMap[index++] = (b0p << 8) | b1p;

		/**
		 * from three words
		 */
		featureMap[index++] = (b0p << 16) | (b1p << 8) | b2p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | b1p;
		featureMap[index++] = (sh0p << 16) | (s0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sl0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (sr0p << 8) | b0p;
		featureMap[index++] = (s0p << 16) | (b0p << 8) | bl0p;

		/**
		 * distance
		 */
		long distance = 0;
		if (s0Position > 0 && b0Position > 0)
			distance = Math.abs(b0Position - s0Position);
		if (s0w != 1) {
			featureMap[index++] = s0w | (distance << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (distance << 8);
		if (b0w != 1) {
			featureMap[index++] = b0w | (distance << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p | (distance << 8);
		if (s0w != 1 && b0w != 1) {
			featureMap[index++] = s0w | (b0w << 20) | (distance << 40);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (b0p << 8) | (distance << 28);

		/**
		 * Valency information
		 */
		if (s0w != 1) {
			featureMap[index++] = s0w | (svr << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (svr << 8);
		if (s0w != 1) {
			featureMap[index++] = s0w | (svl << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = s0p | (svl << 8);
		if (b0w != 1) {
			featureMap[index++] = b0w | (bvl << 20);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = b0p | (bvl << 8);

		/**
		 * Unigrams
		 */
		if (sh0w != 1) {
			featureMap[index++] = sh0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sh0p;
		featureMap[index++] = s0l;
		if (sl0w != 1) {
			featureMap[index++] = sl0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sl0p;
		featureMap[index++] = sl0l;
		if (sr0w != 1) {
			featureMap[index++] = sr0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sr0p;
		featureMap[index++] = sr0l;
		if (bl0w != 1) {
			featureMap[index++] = bl0w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = bl0p;
		featureMap[index++] = bl0l;

		/**
		 * From third order features
		 */
		if (sh1w != 1) {
			featureMap[index++] = sh1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sh1p;
		featureMap[index++] = sh0l;
		if (sl1w != 1) {
			featureMap[index++] = sl1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sl1p;
		featureMap[index++] = sl1l;
		if (sr1w != 1) {
			featureMap[index++] = sr1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = sr1p;
		featureMap[index++] = sr1l;
		if (bl1w != 1) {
			featureMap[index++] = bl1w;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = bl1p;
		featureMap[index++] = bl1l;
		featureMap[index++] = s0p | (sl0p << 8) | (sl1p << 16);
		featureMap[index++] = s0p | (sr0p << 8) | (sr1p << 16);
		featureMap[index++] = s0p | (sh0p << 8) | (sh1p << 16);
		featureMap[index++] = b0p | (bl0p << 8) | (bl1p << 16);

		/**
		 * label set
		 */
		if (s0Position >= 0) {
			sdl = state.leftDependentLabels(s0Position);
			sdr = state.rightDependentLabels(s0Position);
		}

		if (b0Position >= 0) {
			bdl = state.leftDependentLabels(b0Position);
		}

		if (s0w != 1) {
			featureMap[index++] = (s0w + "|" + sdr);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p + "|" + sdr);
		if (s0w != 1) {
			featureMap[index++] = s0w + "|" + sdl;
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (s0p + "|" + sdl);
		if (b0w != 1) {
			featureMap[index++] = (b0w + "|" + bdl);
		} else {
			featureMap[index++] = null;
		}
		featureMap[index++] = (b0p + "|" + bdl);

		/**
		 * Brown cluster features full string for b0w and s0w 4 and 6 prefix
		 * string for s0p and b0p
		 */
		long b0wbc4 = b0bc4;
		b0wbc4 |= (b0w << 12);
		if (b0w == 1)
			b0wbc4 = 0;
		long b0wbc6 = b0bc6;
		b0wbc6 |= (b0w << 12);
		if (b0w == 1)
			b0wbc6 = 0;
		long b0bcfP = b0p;
		b0bcfP |= (b0bcf << 8);
		long s0wbc4 = s0bc4;
		s0wbc4 |= (s0w << 12);
		if (s0w == 0)
			s0wbc4 = 0;
		long s0wbc6 = s0bc6;
		s0wbc6 |= (s0w << 12);
		if (s0w == 0)
			s0wbc6 = 0;
		long s0bcfP = s0p;
		s0bcfP |= (s0bcf << 8);

		/**
		 * From single words
		 */
		if (s0bcf > 0) {
			if (s0w != 1) {
				featureMap[index++] = s0wbc4;
				featureMap[index++] = s0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
			featureMap[index++] = s0bcfP;

			featureMap[index++] = s0bcf;

			featureMap[index++] = s0bc4;
			featureMap[index++] = s0bc6;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			if (b0w != 1) {
				featureMap[index++] = b0wbc4;
				featureMap[index++] = b0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
			featureMap[index++] = b0bcfP;

			featureMap[index++] = b0bcf;

			featureMap[index++] = b0bc4;
			featureMap[index++] = b0bc6;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		/**
		 * from word pairs
		 */
		if (s0bcf > 0 && s0w != 1) {
			if (b0bcf > 0 && b0w != 1) {
				featureMap[index++] = (s0wbc4 << 32) | b0wbc4;
				featureMap[index++] = (s0wbc6 << 32) | b0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
			if (b0w != 1) {
				featureMap[index++] = (s0wbc4 << 28) | b0wp;
				featureMap[index++] = (s0wbc6 << 28) | b0wp;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		if (b0bcf > 0 && s0w != 1 & b0w != 1) {
			featureMap[index++] = (s0wp << 32) | b0wbc4;
			featureMap[index++] = (s0wp << 32) | b0wbc6;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0 && s0w != 1) {
			if (b0w != 1) {
				featureMap[index++] = (s0wbc4 << 20) | b0w;
				featureMap[index++] = (s0wbc6 << 20) | b0w;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
			if (b0bcf > 0) {
				featureMap[index++] = (s0wbc4 << 12) | b0bcf;
				featureMap[index++] = (s0wbc6 << 12) | b0bcf;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0 && s0w != 1) {
			featureMap[index++] = (s0wp << 12) | b0bcf;
		} else {
			featureMap[index++] = null;
		}

		if (s0bcf > 0 && b0w != 1) {
			featureMap[index++] = (s0bcf << 28) | b0wp;
		} else {
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			if (s0w != 1 && b0w != 1) {
				featureMap[index++] = (s0w << 32) | b0wbc4;
				featureMap[index++] = (s0w << 32) | b0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
			if (s0bcf > 0 && b0w != 1) {
				featureMap[index++] = (s0bcf << 32) | b0wbc4;
				featureMap[index++] = (s0bcf << 32) | b0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0 && s0w != 1) {
			featureMap[index++] = (s0wbc4 << 8) | b0p;
			featureMap[index++] = (s0wbc6 << 8) | b0p;
			if (b0bcf > 0) {
				featureMap[index++] = (s0wbc4 << 8) | b0bc4;
				featureMap[index++] = (s0wbc6 << 8) | b0bc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0 && b0w != 1) {
			featureMap[index++] = (s0bc4 << 28) | b0wp;
			featureMap[index++] = (s0bc6 << 28) | b0wp;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0 && b0w != 1) {
			featureMap[index++] = (s0p << 32) | b0wbc4;
			featureMap[index++] = (s0p << 32) | b0wbc6;

			if (s0bcf > 0) {
				featureMap[index++] = (s0bc4 << 32) | b0wbc4;
				featureMap[index++] = (s0bc6 << 32) | b0wbc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0 && s0w != 1) {
			featureMap[index++] = (s0w << 12) | b0bcf;
		} else {
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			if (b0w != 1) {
				featureMap[index++] = (s0bcf << 20) | b0w;
			} else {
				featureMap[index++] = null;
			}
			if (b0bcf > 0) {
				featureMap[index++] = (s0bcf << 12) | b0bcf;
			} else {
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			featureMap[index++] = (s0bc4 << 8) | b0p;
			featureMap[index++] = (s0bc6 << 8) | b0p;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			featureMap[index++] = (s0p << 12) | b0bc4;
			featureMap[index++] = (s0p << 12) | b0bc6;

			if (s0bcf > 0) {
				featureMap[index++] = (s0bc4 << 12) | b0bc4;
				featureMap[index++] = (s0bc6 << 12) | b0bc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}

			featureMap[index++] = (b0bc4 << 8) | b1p;
			featureMap[index++] = (b0bc6 << 8) | b1p;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		/**
		 * from three words
		 */
		if (b0bcf > 0) {
			featureMap[index++] = (b0bc4 << 16) | (b1p << 8) | b2p;
			featureMap[index++] = (b0bc6 << 16) | (b1p << 8) | b2p;

			featureMap[index++] = (s0p << 20) | (b0bc4 << 8) | b1p;
			featureMap[index++] = (s0p << 20) | (b0bc6 << 8) | b1p;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			featureMap[index++] = (s0bc4 << 16) | (b2p << 8) | b1p;
			featureMap[index++] = (s0bc6 << 16) | (b2p << 8) | b1p;
			if (b0bcf > 0) {
				featureMap[index++] = (s0bc4 << 20) | (b0bc4 << 8) | b1p;
				featureMap[index++] = (s0bc6 << 20) | (b0bc6 << 8) | b1p;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}

			featureMap[index++] = (sh0p << 20) | (s0bc4 << 8) | b0p;
			featureMap[index++] = (sh0p << 20) | (s0bc6 << 8) | b0p;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			featureMap[index++] = (sh0p << 20) | (s0p << 12) | b0bc4;
			featureMap[index++] = (sh0p << 20) | (s0p << 12) | b0bc6;
			if (s0bcf > 0) {
				featureMap[index++] = (sh0p << 24) | (s0bc4 << 12) | b0bc4;
				featureMap[index++] = (sh0p << 24) | (s0bc6 << 12) | b0bc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			featureMap[index++] = (s0p << 20) | (sl0p << 12) | b0bc4;
			featureMap[index++] = (s0p << 20) | (sl0p << 12) | b0bc6;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			featureMap[index++] = (s0bc4 << 16) | (sl0p << 8) | b0p;
			featureMap[index++] = (s0bc6 << 16) | (sl0p << 8) | b0p;
			if (b0bcf > 0) {
				featureMap[index++] = (s0bc4 << 20) | (sl0p << 12) | b0bc4;
				featureMap[index++] = (s0bc6 << 20) | (sl0p << 12) | b0bc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			featureMap[index++] = (s0p << 20) | (sr0p << 12) | b0bc4;
			featureMap[index++] = (s0p << 20) | (sr0p << 12) | b0bc6;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			featureMap[index++] = (s0bc4 << 16) | (sr0p << 8) | b0p;
			featureMap[index++] = (s0bc6 << 16) | (sr0p << 8) | b0p;
			if (b0bcf > 0) {
				featureMap[index++] = (s0bc4 << 20) | (sr0p << 12) | b0bc4;
				featureMap[index++] = (s0bc6 << 20) | (sr0p << 12) | b0bc6;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (b0bcf > 0) {
			featureMap[index++] = (s0p << 20) | (b0bc4 << 8) | bl0p;
			featureMap[index++] = (s0p << 20) | (b0bc6 << 8) | bl0p;
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
		}

		if (s0bcf > 0) {
			featureMap[index++] = (s0bc4 << 16) | (b0p << 8) | bl0p;
			featureMap[index++] = (s0bc6 << 16) | (b0p << 8) | bl0p;
			if (b0bcf > 0) {
				featureMap[index++] = (s0bc4 << 20) | (b0bc4 << 8) | bl0p;
				featureMap[index++] = (s0bc6 << 20) | (b0bc6 << 8) | bl0p;
			} else {
				featureMap[index++] = null;
				featureMap[index++] = null;
			}
		} else {
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
			featureMap[index++] = null;
		}
		if (featureMap.length==154) {
			if (configuration.depMat)
				featureMap[index++] = 1L;
			else
				featureMap[index++] = 0L;
		}
		return featureMap;
	}

}