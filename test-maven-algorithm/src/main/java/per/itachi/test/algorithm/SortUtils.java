package per.itachi.test.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		T[] des = Arrays.copyOf(src, src.length);
		sortByQuick(des, 0, src.length - 1);
		return des;
	}
	
	/**
	 * Quick sort is a kind of improvement for bubble sort. 
	 * */
	public static <T extends Comparable<T>> void sortByQuick(T[] src, int start, int end) {
		int pivot = 0;
		if (start < end) {
			pivot = getQuickSortPivot(src, start, end);
			sortByQuick(src, start, pivot - 1);
			sortByQuick(src, pivot + 1, end);
		}
	}
	
	/**
	 * Quick sort of Non-recursive version 
	 * */
	public static <T extends Comparable<T>> T[] sortByQuickNonRecursive(T[] src) {
		return sortByQuickNonRecursive(src, 0, src.length - 1);
	}
	
	public static <T extends Comparable<T>> T[] sortByQuickNonRecursive(T[] src, int start, int end) {
		T[] des = Arrays.copyOf(src, src.length);
		List<Integer> stackPartition = new ArrayList<>(32);
		int low, high;
		int pivot;
		stackPartition.add(end);
		stackPartition.add(start);
		while (!stackPartition.isEmpty()) {
			low = stackPartition.remove(stackPartition.size() - 1);
			high = stackPartition.remove(stackPartition.size() - 1);
			pivot = getQuickSortPivot(des, low, high);
			// low < pivot - 1 should be better and more efficient, instead of low < pivot 
			if (low < pivot - 1) {
				stackPartition.add(pivot - 1);
				stackPartition.add(low);
			}
			// pivot + 1 < high should be better and more efficient, pivot < high 
			if (pivot + 1 < high) {
				stackPartition.add(high);
				stackPartition.add(pivot + 1);
			}
		}
		return des;
	}
	
	private static <T extends Comparable<T>> int getQuickSortPivot(T[] src, int low, int high) {
		//或者定义一个枢轴索引的变量，或者定义一个存枢轴的变量，总要有一个变量保存枢轴的位置；
		T pivot = src[low];
		while (low < high) {
			//如果使用交换的方式，临时变量需要重新定义，不能和枢轴的那个弄混了；
//			T tmp;
			// Firstly, puts less elements into the left of the pivot; puts one element each time; 
			while (low < high && pivot.compareTo(src[high]) <= 0) {
				--high;
			}
			// Actually, it is not necessary to swap both low and high, because one of them is pivot element;
//			tmp = src[low];
//			src[low] = src[high];
//			src[high] = tmp;
			src[low] = src[high];
			// And then, puts larger elements into the right of the pivot; puts one element each time; 
			while (low < high && pivot.compareTo(src[low]) >= 0) {
				++low;
			}
			// Actually, it is not necessary to swap both low and high, because one of them is pivot element;
//			tmp = src[low];
//			src[low] = src[high];
//			src[high] = tmp;
			src[high] = src[low];
		}
		src[low] = pivot;
		return low;
	}
	
	public static <T extends Comparable<T>> T[] sortByMerging(T[] src) {
		T[] des = Arrays.copyOf(src, src.length);
		T[] aux = Arrays.copyOf(src, src.length);
		sortByMerging(des, aux, 0, src.length - 1);
		return des;
	}
	
	public static <T extends Comparable<T>> void sortByMerging(T[] src, T[] des, int start, int end) {
		// >= isn't necessary? 
		if (start == end) {
			des[start] = src[start];
		} 
		else if (start < end) { // It is also OK to place else. 
			int mid = (start + end) >> 1; // (start + end) / 2
			sortByMerging(src, des, start, mid);
			sortByMerging(src, des, mid + 1, end);
			mergeOrderedSequence(src, des, start, mid, end);
		}
		else {
		}
	}
	
	private static <T extends Comparable<T>> void mergeOrderedSequence(T[] src, T[] des, int start, int mid, int end) {
		int i = start;		// the 1st sub-sequence (left)
		int j = mid + 1;	// the 2nd sub-sequence (right)
		int k = start;			// the destination sequence *** 
		// mid是左右子序列的分割点，比较符号就得是闭集的了，不能是开集的
		while (i <= mid && j <= end) {
			if (src[i].compareTo(src[j]) <= 0) {
				des[k++] = src[i++];
			}
			else {
				des[k++] = src[j++];
			}
		}
		// clear the rest of elements 
		if (i <= mid) { // i <= mid 
			// clear the left sub-sequence 
			while (i <= mid) {
				des[k++] = src[i++];
			}
		}
		if (j <= end) { // j <= end 
			// clear the right sub-sequence 
			while (j <= end) {
				des[k++] = src[j++];
			}
		}
		// It is very essential to copy auxiliary back to source. 很关键 
		System.arraycopy(des, start, src, start, end - start + 1);
	}
}
