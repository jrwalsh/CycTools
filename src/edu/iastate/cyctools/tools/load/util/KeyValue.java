package edu.iastate.cyctools.tools.load.util;

public class KeyValue {
	private int key;
	private String value;

	public KeyValue(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}