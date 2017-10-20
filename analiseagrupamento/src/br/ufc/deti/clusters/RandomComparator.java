package br.ufc.deti.clusters;

import java.util.Comparator;

public class RandomComparator implements Comparator {
	private static final RandomComparator randomComparator =
		new RandomComparator();
	private RandomComparator() {
	}
	public static Comparator getInstance() {
		return randomComparator;
	}
	public int compare(Object o1, Object o2) {
		if (Math.random() > Math.random()) {
			return -1;
		} else {
			return 1;	
		}
	}

}
