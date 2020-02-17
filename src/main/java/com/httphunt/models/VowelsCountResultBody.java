package com.httphunt.models;

import java.util.HashMap;
import java.util.Map;

public class VowelsCountResultBody {
	Map<String, Long> output = new HashMap<String, Long>();
	
	public void setVowels(Map<String, Long> countMap) {
		this.output.putAll(countMap);
	}
	public void setA(Long a) {
		this.output.put("a", a);
	}
	
	public void setE(Long e) {
		this.output.put("e", e);
	}
	
	public void setI(Long i) {
		this.output.put("i", i);
	}
	
	public void setO(Long o) {
		this.output.put("o", o);
	}
	
	public void setU(Long u) {
		this.output.put("u", u);
	}
	
	public Long getA() {
		
		return this.output.get("a");
	}
	
	public Long getE() {
		
		return this.output.get("e");
	}

	public Long getI() {
	
		return this.output.get("i");
	}
	
	public Long getO() {
	
		return this.output.get("o");
	}
	
	public Long getU() {
	
		return this.output.get("u");
	}
	@Override
	public String toString() {
		return "{a: " + this.getA() + "e: " + this.getE() + "i: " + this.getI() + "o: " + this.getO() + "u: "+ this.getU() +" }";
	}
	

}