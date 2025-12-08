package com.mjdi.word;

public class WordDTO {
	private int word_id;
	private String word;
	private String doc;
	private String korean;
	private String jlpt;
	public int getWord_id() {
		return word_id;
	}
	public void setWord_id(int word_id) {
		this.word_id = word_id;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getKorean() {
		return korean;
	}
	public void setKorean(String korean) {
		this.korean = korean;
	}
	public String getJlpt() {
		return jlpt;
	}
	public void setJlpt(String jlpt) {
		this.jlpt = jlpt;
	}
}
