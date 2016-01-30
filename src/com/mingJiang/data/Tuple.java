package com.mingJiang.data;

public class Tuple<A,B,C> {

	private A first;
	private B mid;
	private C last;
	public Tuple(A a, B b, C c) {
		first = a ;
		mid = b; 
		last = c;
	}
	/**
	 * @return the first
	 */
	public A getFirst() {
		return first;
	}
	/**
	 * @return the mid
	 */
	public B getMid() {
		return mid;
	}
	/**
	 * @return the last
	 */
	public C getLast() {
		return last;
	}
	/**
	 * @param first the first to set
	 */
	public void setFirst(A first) {
		this.first = first;
	}
	/**
	 * @param mid the mid to set
	 */
	public void setMid(B mid) {
		this.mid = mid;
	}
	/**
	 * @param last the last to set
	 */
	public void setLast(C last) {
		this.last = last;
	}

}
