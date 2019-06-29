package br.com.jcpinto.Model;

public class Book extends ResponseDefault{

	private Integer id;
	private String title;
	private String author;
	private Integer year;
	private boolean isRented;


    public Book() {
    }
    
	public String toString() { 
	    return    "         id : '"    	+ this.id 	  		+ "',\n"
	    		+ "      title : '" 	+ this.title 		+ "',\n"
		    	+ "     author : '" 	+ this.author 		+ "',\n"
		    	+ "       year : '" 	+ this.year 		+ "',\n"
	    	    + "   isRented : '" 	+ this.isRented 	+ "'";
	}
	
	/////////////////////////////////////////////////////////////
	///	Gets e Sets
	/////////////////////////////////////////////////////////////

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public boolean isRented() {
		return isRented;
	}
	public void setIsRented(boolean isRented) {
		this.isRented = isRented;
	} 
	
	
}
