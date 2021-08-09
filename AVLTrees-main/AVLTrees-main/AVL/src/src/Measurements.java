package src;

import java.util.*;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import src.MeasureAVL.IAVLNode;

public class Measurements {

	public static long insertionSort(int[] array) {
		long count = 0;
		for (int i = 0; i < array.length; i++) {
			int j = i;
			while (j > 0 && array[j - 1] > array[j]) {
				swap(array, j - 1, j);
				j = j - 1;
				count++;
			}
		}

		return count;
	}

	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static int[] getShuffledArray(int size) {
		ArrayList<Integer> lst = new ArrayList<Integer>();

		for (int i = 1; i <= size; i++) {
			lst.add(i);
		}

		java.util.Collections.shuffle(lst);

		int[] array = new int[size];
		int i = 0;

		for (int item : lst) {
			array[i] = item;
			i++;
		}

		return array;
	}
	
	public static int[] getBackwards(int size) {
		int[] array = new int[size];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = size - i;
		}
		
		return array;
	}

	public static void main(String[] args) {

		for (int i = 1; i <= 10; i++) {
			MeasureAVL tree = new MeasureAVL();
			int[] shuffledArray = getBackwards(i * 10000);
			long count = 0;
			
			for (int item: shuffledArray) {
				count += tree.fingerSearchCounter(item);
				tree.insert(item, String.valueOf(item));
			}
			
			System.out.println("tree: counts for " + i +": " + count);
			
			long swaps = insertionSort(shuffledArray);
			System.out.println("array: swaps for " + i + ": " + swaps);
		}
	}

	static final int COUNT = 5;

	public static void print2DUtil(IAVLNode root, int space) {
		// Base case
		if (root == null)
			return;

		// Increase distance between levels
		space += COUNT;

		// Process right child first
		print2DUtil(root.getRight(), space);

		// Print current node after space
		// count
		System.out.print("\n");
		for (int i = COUNT; i < space; i++)
			System.out.print(" ");
		System.out.print(root.getKey() + "\n");

		// Process left child
		print2DUtil(root.getLeft(), space);
	}

	// Wrapper over print2DUtil()
	public static void print2D(IAVLNode root) {
		// Pass initial space count as 0
		print2DUtil(root, 0);
	}
}
