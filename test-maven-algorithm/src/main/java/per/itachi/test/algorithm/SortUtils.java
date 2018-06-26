package per.itachi.test.algorithm;

import java.util.Arrays;

public class SortUtils {
	
	/**
	 * I am not very clear about what type this algorithm belongs to. 
	 * */
	public static <T extends Comparable<T>> T[] sortByCustom(T[] src) {
		T[] des = Arrays.copyOf(src, src.length);
		T tmp = null;
		for (int i = 0; i < des.length - 1; i++) {
			for (int j = i + 1; j < des.length; j++) {
				if (des[i].compareTo(des[j]) > 0) {
					tmp = des[i];
					des[i] = des[j];
					des[j] = tmp;
				}
			}
		}
		return des;
	}
	
	/**
	 * Bubble Sort 
	 * */
	public static <T extends Comparable<T>> T[] sortByBubble(T[] src) {
		T[] des = Arrays.copyOf(src, src.length);
		T tmp = null;
		for (int i = 0; i < des.length - 1; i++) {
			for (int j = 0; j < des.length - 1; j++) {
				if (des[j].compareTo(des[j + 1]) > 0) {
					tmp = des[j + 1];
					des[j + 1] = des[j];
					des[j] = tmp;
				}
			}
		}
		return des;
	}
	
	/**
	 * Simple Selection Sort 
	 * */
	public static <T extends Comparable<T>> T[] sortBySelectSimple(T[] src) {
		T[] des = Arrays.copyOf(src, src.length);
		T tmp = null;
		int pos;
		for (int i = 0; i < des.length - 1; i++) {
			pos = i;
			for (int j = i + 1; j < des.length; j++) {
				if (des[pos].compareTo(des[j]) > 0) {
					pos = j;
				}
			}
			if (pos != i) {
				tmp = des[i];
				des[i] = des[pos];
				des[pos] = tmp;
			}
		}
		return des;
	}
	
	/**
	 * 
	 * */
	public static <T extends Comparable<T>> T[] sortByQuick(T[] src) {
		return sortByQuick(src, 0, src.length);
	}
	
	/**
	 * 
	 * */
	public static <T extends Comparable<T>> T[] sortByQuick(T[] src, int start, int end) {
		return null;
	}
}
