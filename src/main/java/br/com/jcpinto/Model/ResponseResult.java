package br.com.jcpinto.Model;

import java.util.ArrayList;

public class ResponseResult extends ResponseDefault{

	private ArrayList<Book> results;
	private String nextPage;
	
	public ResponseResult(ArrayList<Book> results, String nextPage) {
		this.results = results;
		this.nextPage = nextPage;		
	}
	
	public ArrayList<Book> getResults() {
		return results;
	}
	public void setResults(ArrayList<Book> results) {
		this.results = results;
	}
	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
	
}
