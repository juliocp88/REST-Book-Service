package br.com.jcpinto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jcpinto.Data.DBAccess;
import br.com.jcpinto.Model.Book;
import br.com.jcpinto.Model.Login;
import br.com.jcpinto.Model.ResponseDefault;
import br.com.jcpinto.Model.ResponseMessage;
import br.com.jcpinto.Model.ResponseResult;
import br.com.jcpinto.Model.Token;

@RestController
@RequestMapping(value="/api")
public class BookController {

	private final Integer offsetLimit = 5;
	private DBAccess booksDB;
    Hashtable<String, String> logins = new Hashtable<String, String>();


	public BookController() {
		booksDB = new DBAccess();
	} 
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<ResponseDefault> login(@RequestBody Login login) {

		HttpStatus httpStatus;
		String status;
		String message;		
		
		if ((login == null) || (login.getUser() == null || login.getPassword() == null)) {
			status = "error";
			httpStatus = HttpStatus.BAD_REQUEST;
			message = "Algumas informações do login não foram enviadas.";
		}else {
			System.out.println("login = user: " + login.getUser());
			Token token = new Token();
			if (booksDB.credentialsAreValid(login)) {
				if (logins.containsKey(login.getUser())) {
					token.setToken(logins.get(login.getUser()));
				}else {
					token.setToken(generateRandomString());
					logins.put(login.getUser(), token.getToken());
				}
				return new ResponseEntity<ResponseDefault>(token, HttpStatus.OK);
			}else {
				status = "error";
				httpStatus = HttpStatus.BAD_REQUEST;
				message = "Credencias enviadas não são validas.";
			}
		}
		System.out.println("saveNewBook = status: " + status + " message: " + message);
		return new ResponseEntity<ResponseDefault>(new ResponseMessage(status,message), httpStatus);
	}
	
	@RequestMapping(value = "/books", method = RequestMethod.GET)
	public ResponseEntity<ResponseDefault> getAllBooks(@RequestParam(value = "offset", required = false) Integer offset, 
														@RequestParam(value = "startYear", required = false) Integer startYear,
														@RequestParam(value = "endYear", required = false) Integer endYear,
														@RequestParam(value = "author", required = false) String author,
														@RequestParam(value = "title", required = false) String title,
														@RequestHeader("authentication") String token) {
		System.out.println("getAllBooks - " + token);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseDefault>(new ResponseMessage("Error", "Autenticação fornecida é invalida."), HttpStatus.UNAUTHORIZED);
		}
		
		ArrayList<Book> results;
		String nextPage = null;
		
		// Verifica o offset
		if (offset == null) 
			offset = 0;
		
		// Pega os dados e controi a resposta
		results = booksDB.getAllBooks(offsetLimit + 1, offset, startYear, endYear,  author, title);
		
		if (results.size() > offsetLimit) {
			results.remove(results.size()-1);
			offset = offset + offsetLimit;
			nextPage = "/books?offset=" + offset;
			
		}
		
		return new ResponseEntity<ResponseDefault>(new ResponseResult(results, nextPage), HttpStatus.OK);
	}

	@RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseDefault> getBook(@PathVariable("id") Integer id, @RequestHeader("authentication") String token) {
		System.out.println("getBook - id = " + id);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseDefault>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
		
		Book book = booksDB.getBookById(id);
		
		if (book != null) {
			return new ResponseEntity<ResponseDefault>(book, HttpStatus.OK);
		}else {
			return new ResponseEntity<ResponseDefault>(new ResponseMessage("Error", "O id da conta passada não foi encontrado."), HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = "/books", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> saveNewBook(@RequestBody Book book, @RequestHeader("authentication") String token) {
		System.out.println("saveNewBook = \n" + book);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
		
		HttpStatus httpStatus;
		String status;
		String message;

		if ((book == null) || (book.getAuthor() == null || book.getTitle() == null || book.getYear() == null)) {
			httpStatus = HttpStatus.CREATED;
			status = "error";
			message = "Um ou mais parâmetros, do livro a ser criado, não foram enviados.";
		}else {
			booksDB.saveNewBookInDB(book);
			
			httpStatus = HttpStatus.CREATED;
			status = "success";
			message = "Livro criado com sucesso";
		}
				
		System.out.println("saveNewBook = status: " + status + " message: " + message);
		return new ResponseEntity<ResponseMessage>(new ResponseMessage(status,message), httpStatus);
	}

	@RequestMapping(value = "/books/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> updateBook(@PathVariable("id") Integer id, @RequestBody Book book, @RequestHeader("authentication") String token) {
		System.out.println("updateBook - \n" + book);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
				
		HttpStatus httpStatus;
		String status;
		String message;
		Integer bookStatus = booksDB.getBookRentStatus(id);
		
		System.out.println("updateBook - bookStatus = " + bookStatus);
		if (bookStatus > 0) {
			status = "error";
			if (bookStatus == 1) {
				httpStatus = HttpStatus.FORBIDDEN;
				message = "O livro está alugado. Não é possivel atualizar um livro alugado.";
			}else {
				httpStatus = HttpStatus.NOT_FOUND;
				message = "Não foi possivel encontrar o id do livro passado.";
			}
		}else {
			Integer update = booksDB.updateBook(book, id);
			if (update == 0) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				status = "error";
				message = "Ocorreu um erro inesperado ao tentar atualizar o livro.";
			}else {
				httpStatus = HttpStatus.OK;
				status = "success";
				message = "O livro foi atualizado com sucesso";
			}
		}			
		System.out.println("updateBook - status: " + status + " message: " + message);
		return new ResponseEntity<ResponseMessage>(new ResponseMessage(status,message), httpStatus);
	}

	@RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseMessage> deleteBook(@PathVariable("id") Integer id, @RequestHeader("authentication") String token) {
		System.out.println("deleteBook - id = " + id);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
		
		HttpStatus httpStatus;
		String status;
		String message;
		Integer bookStatus = booksDB.getBookRentStatus(id);
		
		if (bookStatus > 0) {
			status = "error";
			if (bookStatus == 1) {
				httpStatus = HttpStatus.FORBIDDEN;
				message = "O livro está alugado. Não é possivel remover um livro alugado.";
			}else {
				httpStatus = HttpStatus.NOT_FOUND;
				message = "Não foi possivel encontrar o id do livro passado.";
			}
		}else {
			Integer deleteStatus = booksDB.deleteBook(id);
			if (deleteStatus == 0) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				status = "error";
				message = "Ocorreu um erro inesperado ao tentar atualizar o livro.";
			}else {
				httpStatus = HttpStatus.OK;
				status = "success";
				message = "O livro foi removido com sucesso";
			}
		}
				
		return new ResponseEntity<ResponseMessage>(new ResponseMessage(status,message), httpStatus);
	}

	@RequestMapping(value = "/books/rent/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> rentBook(@PathVariable("id") Integer id, @RequestHeader("authentication") String token) {
		System.out.println("rentBook - id = " + id);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
		
		HttpStatus httpStatus;
		String status;
		String message;
		Integer bookStatus = booksDB.getBookRentStatus(id);
		
		if (bookStatus > 0) {
			status = "error";
			if (bookStatus == 1) {
				httpStatus = HttpStatus.FORBIDDEN;
				message = "O livro está alugado. Não é possivel alugar um livro alugado.";
			}else {
				httpStatus = HttpStatus.NOT_FOUND;
				message = "Não foi possivel encontrar o id do livro passado.";
			}
		}else {
			Integer update = booksDB.rentBook(id);
			if (update == 0) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				status = "error";
				message = "Ocorreu um erro inesperado ao tentar alugar o livro.";
			}else {
				httpStatus = HttpStatus.OK;
				status = "success";
				message = "O livro foi alugado com sucesso";
			}
		}			
		System.out.println("rentBook - status: " + status + " message: " + message);
		return new ResponseEntity<ResponseMessage>(new ResponseMessage(status,message), httpStatus);
	}

	@RequestMapping(value = "/books/return/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> returnBook(@PathVariable("id") Integer id, @RequestHeader("authentication") String token) {
		System.out.println("returnBook - id = " + id);

		if (!logins.containsValue(token)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Error", "Autenticação fornecida não é valida."), HttpStatus.UNAUTHORIZED);
		}
		
		HttpStatus httpStatus;
		String status;
		String message;
		Integer bookStatus = booksDB.getBookRentStatus(id);
		
		if (bookStatus != 1) {
			status = "error";
			if (bookStatus == 0) {
				httpStatus = HttpStatus.FORBIDDEN;
				message = "O livro não está alugado. Não é possivel retornar um livro que não está alugado.";
			}else {
				httpStatus = HttpStatus.NOT_FOUND;
				message = "Não foi possivel encontrar o id do livro passado.";
			}
		}else {
			Integer update = booksDB.returnBook(id);
			if (update == 0) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				status = "error";
				message = "Ocorreu um erro inesperado ao tentar retornar o livro.";
			}else {
				httpStatus = HttpStatus.OK;
				status = "success";
				message = "O livro foi retornado com sucesso";
			}
		}			
		System.out.println("returnBook - status: " + status + " message: " + message);
		return new ResponseEntity<ResponseMessage>(new ResponseMessage(status,message), httpStatus);
	}
	 	
	/////////////////////////////////////////////////////////////
	///	Funções Auxiliares
	/////////////////////////////////////////////////////////////

	public static String generateRandomString() {
	    int length = 35;
	    boolean useLetters = true;
	    boolean useNumbers = true;
	    return RandomStringUtils.random(length, useLetters, useNumbers);
	}
	
}
