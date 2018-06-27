package per.itachi.test.algorithm;

import java.util.Arrays;

public class SortUtils {
	
	/**
	 * I am not very clear about what type this algorithm belongs to. 
	 * This is supposed to be a kind of low-performance selection sort, which is less efficient than simple selection sort. 
	 * 
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
			for (int j = 0; j < des.length - i - 1; j++) {
				// des.length - i - 1 is a kind of improvement. 
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
	 * Quick sort is a kind of improvement for bubble sort. 
	 * */
	public static <T extends Comparable<T>> T[] sortByQuick(T[] src) {
		return sortByQuick(src, 0, src.length);
	}
	
	/**
	 * Quick sort is a kind of improvement for bubble sort. 
	 * */
	public static <T extends Comparable<T>> T[] sortByQuick(T[] src, int start, int end) { 
		return null;
	}
	
	private static <T extends Comparable<T>> int getQuickSortPivot(T[] src, int low, int high) {
		int pivot = low;
		T tmp;
		while (low < high) {
		}
		return pivot;
	}
}
