package per.itachi.test.algorithm;

import java.util.Random;

public class Entry {

	public static void main(String[] args) {
		Random random = new Random(System.currentTimeMillis());
		Integer[] arrSrc = null;
		Integer[] arrDes = null;
		arrSrc = new Integer[32];
		System.out.println("Source: ");
		for (int i = 0; i < arrSrc.length; i++) {
			arrSrc[i] = random.nextInt(100);
			System.out.printf("%d ", arrSrc[i]);
		}
		System.out.println();
		System.out.println("Destination: ");
//		arrDes = SortUtils.sortByCustom(arrSrc);
//		arrDes = SortUtils.sortByBubble(arrSrc);
//		arrDes = SortUtils.sortBySelectSimple(arrSrc);
//		arrDes = SortUtils.sortByQuick(arrSrc);
		arrDes = SortUtils.sortByQuickNonRecursive(arrSrc);
//		arrDes = SortUtils.sortByMerging(arrSrc);
		for (int i = 0; i < arrDes.length; i++) {
			System.out.printf("%d ", arrDes[i]);
		}
		System.out.println();
	}
	
}
