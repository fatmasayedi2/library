package com.libraryapp;
import java.time.LocalDate; 
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.libraryapp.entities.Book;
import com.libraryapp.entities.User;
import com.libraryapp.services.BookService;
import com.libraryapp.services.NotificationService;
import com.libraryapp.services.UserService;
import com.libraryapp.utils.MidnightApplicationRefresh;

@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@Autowired
	BookService bookService;
	
	@Autowired
	UserService usService;
	
	@Autowired
	NotificationService notifService;
	
	@Autowired
	BCryptPasswordEncoder pwEncoder;
	
	@Autowired
	MidnightApplicationRefresh midAppRef;
	
	@Bean
	CommandLineRunner runner() {
		return args -> {
		
			
			User user2 = new User("employee", pwEncoder.encode("test"), "employe@gmail.com", "employe", "1", "sahloul", "7054875", "tunisia");
			user2.setRole("ROLE_EMPLOYEE");
			
		
			usService.save(user2);

			
			 Book book1 = new Book("The Pragmatic Programmer", "David Thomas, Andrew Hunt", 2006, 1);
		        Book book2 = new Book("Clean Code", "Robert C. Martin", 2020, 2);
		        Book book3 = new Book("Code Complete", "Steve McConnell", 2012, 1);
		        Book book4 = new Book("Refactoring", "Martin Fowler", 2017, 4);
		        Book book5 = new Book("Head First Design Patterns", "Eric Freeman, Bert Bates, Kathy Sierra, Elisabeth Robson", 2019, 5);
		        Book book6 = new Book("The Mythical Man-Month", "Frederick P. Brooks Jr", 1999, 1);
		        Book book7 = new Book("The Clean Coder", "Robert Martin", 2021, 3);
		        Book book8 = new Book("Working Effectively with Legacy Code", "Micheal Feathers", 2015, 1);
		        Book book9 = new Book("Design Patterns", "Erich Gamma, Richard Helm. Ralph Johnson, John Vlissides", 2019, 2);
		        Book book10 = new Book("Cracking the Coding Interview", "Gayle Laakmann McDowell", 2018, 3);
		        
			bookService.save(book1);
			bookService.save(book2);
			bookService.save(book3);
			bookService.save(book4);
			bookService.save(book5);
			bookService.save(book6);
			bookService.save(book7);
			bookService.save(book8);
			bookService.save(book9);
			bookService.save(book10);
			
			
		
			
			bookService.save(book1);
			bookService.save(book10);
		
						
			midAppRef.midnightApplicationRefresher();	
		};
	}
}
