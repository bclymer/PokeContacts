package com.bclymer.pokecontacts.util;

public class StringUtil {
	public static String pad(String str, int size, char padChar) {
		StringBuffer padded = new StringBuffer(str);
		while (padded.length() < size) {
			padded.insert(0, padChar);
		}
		return padded.toString();
	}

}
