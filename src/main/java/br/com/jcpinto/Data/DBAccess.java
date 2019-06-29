package br.com.jcpinto.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.com.jcpinto.Model.Book;
import br.com.jcpinto.Model.Login;
import br.com.jcpinto.Utils.AES;

public class DBAccess {

    private static final String secretKey = "$#segredoSoftDesign#$";
        
	public DBAccess() {
		createNewDatabase();
	}
	
    private static Connection connectToBookDB() throws SQLException, ClassNotFoundException {
    	Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:db/books.db";
        Connection conn = null;
        conn = DriverManager.getConnection(url);
        return conn;
    }

    private static void closeConnection(Connection connection) {
		if(connection != null) {
	    	try{
	    		connection.close();
	    	} catch(SQLException e) {
	    		System.err.println("closeConnection - " + e);
	    	}
		}
    }
    
    private static void closeStatement(Statement statement) {
		if(statement != null) {
	    	try{
	    		statement.close();
	    	} catch(SQLException e) {
	    		System.err.println("closeStatement - " + e);
	    	}
		}
    }

    private static void closePreparedStatement(PreparedStatement statement) {
		if(statement != null) {
	    	try{
	    		statement.close();
	    	} catch(SQLException e) {
	    		System.err.println("closeStatement - " + e);
	    	}
		}
    }

    private static void closeResultSet(ResultSet rs) {
		if(rs != null) {
	    	try{
	    		rs.close();
	    	} catch(SQLException e) {
	    		System.err.println("closeResultSet - " + e);
	    	}
		}
    }
    
    private static void createNewDatabase() {

		Connection connection = null;
		Statement statement = null;
        try {
        	connection = connectToBookDB();
            //System.out.println("A new database has been created.");
            
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	
	        String sql = "CREATE TABLE IF NOT EXISTS book (\n"
	                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
	                + "	title text NOT NULL,\n"
	                + "	author text NOT NULL,\n"
	                + "	year integer,\n"
	                + "	isRented integer\n"
	                + ");";
	        
	        statement.executeUpdate(sql);

	        sql = "CREATE TABLE IF NOT EXISTS user (\n"
	            + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
	            + "	user text NOT NULL UNIQUE,\n"
	            + "	password text NOT NULL\n"
	            + ");";
	        
	        statement.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("createNewDatabase - " + e.getMessage());
        }
	    finally{
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
    }
  
    
  	public Integer getBookRentStatus(Integer id) {
  		
  		Integer bookStatus = 2;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
        try {
        	connection = connectToBookDB();
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	        
	        rs = statement.executeQuery("select isRented from book where book.id = " + id);
	        
	        while(rs.next()){
	        	bookStatus = rs.getInt("isRented");
	        }	
	        
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("getBookById - " + e.getMessage());
	    }
	    finally{
	    	closeResultSet(rs);
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
  		
  		return bookStatus;
  	}
  	
  	public void saveNewBookInDB(Book book) {
	    Connection connection = null;
		PreparedStatement pstmt = null;
	    try{
	        connection = connectToBookDB();
	        
	        String sql = "INSERT INTO book(title, author, year, isRented) VALUES(?, ?, ?, ?)";

            pstmt = connection.prepareStatement(sql);
	        pstmt.setString(1, book.getTitle());
	        pstmt.setString(2, book.getAuthor());
	        pstmt.setInt(3, book.getYear());
	        pstmt.setInt(4, 0);
	        Integer executeUpdate = pstmt.executeUpdate();
	        
	    	System.out.println("saveNewBookInDB - executeUpdate = " + executeUpdate);
        } catch (SQLException | ClassNotFoundException e) {
	    	System.err.println("saveNewBookInDB - " + e.getMessage());
	    }
	    finally{
	    	closePreparedStatement(pstmt);
	    	closeConnection(connection);
	    }
		
	}
  	
	public Book getBookById(Integer id) {

        Book book = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
        try {
        	connection = connectToBookDB();
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	        
	        rs = statement.executeQuery("select * from book where book.id = " + id);
	        
	        while(rs.next()){
	        	book = new Book();
		        // read the result set
	        	book.setId(rs.getInt("id"));
	        	book.setTitle(rs.getString("title"));
	        	book.setAuthor(rs.getString("author"));
	        	book.setIsRented(rs.getInt("isRented") != 0);
	        	book.setYear(rs.getInt("year"));
	        	
		        System.out.println("id       = " + rs.getInt("id")      	+ "',\n" +
		        				   "title    = " + rs.getString("title")	+ "',\n" +
		        				   "author   = " + rs.getString("author")	+ "',\n" +
		        				   "isRented = " + rs.getInt("isRented")	+ "',\n" +
		        				   "year     = " + rs.getInt("year"));
		        System.out.println("-------------");
	        }
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("getBookById - " + e.getMessage());
	    }
	    finally{
	    	closeResultSet(rs);
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
        
		return book;
	}
	
	public ArrayList<Book> getAllBooks(Integer limit,Integer offset, Integer startYear, Integer endYear, String author, String title){
		ArrayList<Book> books = new ArrayList<Book>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        try {
        	connection = connectToBookDB();

	        Integer pos = 1;
	        String sql = "SELECT * FROM book ";
	        if(startYear != null || endYear != null || author != null || title != null)
	        	sql = sql + " WHERE ";
	        if(startYear != null)
	        	sql = sql + " year >= ? AND";
	        if(endYear != null)	
	        	sql = sql + " year <= ? AND";
	        if(author != null)
	        	sql = sql + " author = ? AND";
	        if (title != null)	
	        	sql = sql + " title = ? ";

	        if (sql.substring(sql.length() - 3, sql.length()).equals("AND"))
	        	sql = sql.substring(0, sql.length() - 3);
	        
	        sql = sql + " LIMIT ? OFFSET ? ";
	        
            pstmt = connection.prepareStatement(sql); 
	        if (startYear != null) {
	        	pstmt.setInt(pos, startYear);
	        	pos++;
	        }
	        if (endYear != null) {
	        	pstmt.setInt(pos, endYear);
	        	pos++;
	        }
	        if (author != null) {
	        	pstmt.setString(pos, author);
	        	pos++;
	        }
	        if (title != null) {
	        	pstmt.setString(pos, title);
	        	pos++;
	        }
        	pstmt.setInt(pos, limit);
        	pos++;
        	pstmt.setInt(pos, offset);
	        
	        rs = pstmt.executeQuery();
	        Book book;
	        while(rs.next()){
	        	book = new Book();
		        // read the result set
	        	book.setId(rs.getInt("id"));
	        	book.setTitle(rs.getString("title"));
	        	book.setAuthor(rs.getString("author"));
	        	book.setIsRented(rs.getInt("isRented") != 0);
	        	book.setYear(rs.getInt("year"));
	        	
	        	books.add(book);
	        }
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("getBooks - " + e.getMessage());
	    }
	    finally{
	    	closeResultSet(rs);
	    	closePreparedStatement(pstmt);
	    	closeConnection(connection);
	    }
		
		return books;
	}
 
	public Integer updateBook(Book book, Integer id) {
		Integer executeUpdate = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
        try {
        	connection = connectToBookDB();  
	        
	        Integer pos = 1;
	        String sql = "UPDATE book SET ";
	        if (book.getTitle() != null)
	        	sql = sql + " title = ? ,"; 
	        if (book.getAuthor() != null)
	        	sql = sql + " author = ? ,"; 
	        if (book.getYear() != null)
	        	sql = sql + " year = ? "; 
	        if (sql.charAt(sql.length() - 1) == ',')
	        	sql = sql.substring(0, sql.length() - 1);
	        sql = sql + "WHERE id = ? ";

            pstmt = connection.prepareStatement(sql);

	        if (book.getTitle() != null) {
	        	pstmt.setString(pos, book.getTitle());
	        	pos++;
	        }
	        if (book.getAuthor() != null) {
	        	pstmt.setString(pos, book.getAuthor());
	        	pos++;
	        }
	        if (book.getYear() != null) {
	        	pstmt.setInt(pos, book.getYear());
	        	pos++;
	        }
	        pstmt.setInt(pos, id);
	        executeUpdate = pstmt.executeUpdate();
	        
	    	System.out.println("updateBook - executeUpdate = " + executeUpdate);
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("updateBook - " + e.getMessage());
	    }finally{
	    	closePreparedStatement(pstmt);
	    	closeConnection(connection);
	    }
        return executeUpdate;
	}

	public Integer rentBook(Integer id) {
		Integer executeUpdate = 0;
		Connection connection = null;
		Statement statement = null;
        try {
        	connection = connectToBookDB();
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	        
	        String sql = "UPDATE book "
	        			+ "set isRented = 1 "
	        			+ "WHERE id = " + id;
	        
	        executeUpdate = statement.executeUpdate(sql);

	    	System.out.println("rentBook - executeUpdate = " + executeUpdate);
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("rentBook - " + e.getMessage());
	    }
	    finally{
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
        return executeUpdate;
	}

	public Integer returnBook(Integer id) {
		Integer executeUpdate = 0;
		Connection connection = null;
		Statement statement = null;
        try {
        	connection = connectToBookDB();
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	        
	        String sql = "UPDATE book "
	        			+ "set isRented = 0 "
	        			+ "WHERE id = " + id;
	        
	        executeUpdate = statement.executeUpdate(sql);

	    	System.out.println("returnBook - executeUpdate = " + executeUpdate);
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("returnBook - " + e.getMessage());
	    }
	    finally{
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
        return executeUpdate;
	}
	
	public Integer deleteBook(Integer id) {
		Integer executeUpdate = 0;
		Connection connection = null;
		Statement statement = null;
        try {
        	connection = connectToBookDB();
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  
	        
	        String sql = "DELETE FROM book WHERE id = " + id;
	        
	        executeUpdate = statement.executeUpdate(sql);

	    	System.out.println("updateBook - executeUpdate = " + executeUpdate);
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("updateBook - " + e.getMessage());
	    }
	    finally{
	    	closeStatement(statement);
	    	closeConnection(connection);
	    }
        return executeUpdate;		
	}
	
	public boolean credentialsAreValid(Login login) {
		
		boolean userStatus = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        try {
        	connection = connectToBookDB();

            String sql = "SELECT password FROM user WHERE user.user = ?";
            
	        pstmt  = connection.prepareStatement(sql);
	        
            pstmt.setString(1,login.getUser());
            rs  = pstmt.executeQuery();
            
	        String dbPassword = "";
	        while(rs.next()){
	        	dbPassword = rs.getString("password");
	        	
	        }
    	    String encryptedString = AES.encrypt(login.getPassword(), secretKey) ;
        	if (encryptedString.equals(dbPassword)) {
        		userStatus = true;
			}
	    }catch(SQLException | ClassNotFoundException e){
	    	System.err.println("credentialsAreValid - " + e.getMessage());
	    }
	    finally{
	    	closeResultSet(rs);
	    	closePreparedStatement(pstmt);
	    	closeConnection(connection);
	    }	
		
		return userStatus;
	}
		
}
	