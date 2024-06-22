package com.kreative.keycaps;

import java.io.Serializable;
import java.util.Arrays;

public class Padding implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public float top;
	public float left;
	public float bottom;
	public float right;
	
	public Padding(float top, float left, float bottom, float right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	public Padding clone() {
		return new Padding(top, left, bottom, right);
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Padding) {
			Padding that = (Padding)obj;
			return (
				this.top == that.top && this.left == that.left &&
				this.bottom == that.bottom && this.right == that.right
			);
		}
		return false;
	}
	
	public int hashCode() {
		float[] a = {top, left, bottom, right};
		return Arrays.hashCode(a);
	}
	
	public void set(float top, float left, float bottom, float right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	public String toString() {
		return (
			Padding.class.getCanonicalName() +
			"[top=" + top +
			",left=" + left +
			",bottom=" + bottom +
			",right=" + right + "]"
		);
	}
}
