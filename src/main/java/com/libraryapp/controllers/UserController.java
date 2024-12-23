package com.libraryapp.controllers;

import java.math.BigDecimal; 
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.libraryapp.entities.Book;
import com.libraryapp.entities.User;
import com.libraryapp.security.CurrentUserFinder;
import com.libraryapp.services.BookService;
import com.libraryapp.services.UserService;
import com.libraryapp.utils.DateTracker;
import com.libraryapp.utils.FineCalculator;
import com.libraryapp.utils.ListInStringConverter;

@Controller
@RequestMapping("/user") 
public class UserController {
	
	
	@Autowired
	UserService usService; 
	
	@Autowired
	BookService bookService; 
	
	@Autowired
	CurrentUserFinder currentUserFinder; 
	
	@Autowired
	FineCalculator fineCalculator; 
	
	@Autowired
	DateTracker dateTracker; 
	
	@Autowired
	ListInStringConverter listConverter; 
	
	private int maximumWeeksToExtend = 3; 
	
	
	@GetMapping
	public String userHome(Model model) {
		User currentUser = currentUserFinder.getCurrentUser(); 
		
		model.addAttribute("booksWithFines", fineCalculator.selectBooksWithFines(currentUser.getBooks()));
		model.addAttribute("currentUser", currentUser); 
		return "user/user-home.html"; 
	}
	

	

	
	// Affiche la page de paiement des amendes pour un livre spécifique
	@GetMapping(value="/yourbooks/payfine/{bookId}")
	public String payFine(Model model, @PathVariable (value="bookId") Long bookId) {
		Book book = bookService.findById(bookId);
		BigDecimal fine = fineCalculator.getFineOfBook(book); // Calcule l'amende pour ce livre
		int weeksToExtend = dateTracker.getWeeksToExtendReturnDate(book); // Détermine les semaines possibles d'extension
			
		model.addAttribute("weeksToExtend", weeksToExtend);
		model.addAttribute("fine", fine);
		model.addAttribute("book", book);
		
		return "user/user-pay-fine.html"; // Retourne la vue HTML de paiement
	}
	
	// Effectue une action pour payer l'amende
	@PostMapping(value="/yourbooks/dopayment")
	public String doPayment(@RequestParam int weeksToExtend,
							@RequestParam BigDecimal fineAmount,
							@RequestParam long bookId,
							Model model) {
		Book currentBook = bookService.findById(bookId);
		model.addAttribute("fineAmount", fineAmount);
		model.addAttribute("weeksToExtend", weeksToExtend);
		model.addAttribute("book", currentBook);
		return "user/user-do-payment.html";
	}
		

	
	// Permet à l'utilisateur de parcourir les livres disponibles
	@GetMapping(value="/browsebooks")
	public String browseBooks(@RequestParam (required=false) String title,
							  @RequestParam (required=false) String author,
							  @RequestParam (required=false) String showAllBooks,
							  @RequestParam (required=false) Long  reservedBookId,
							  @RequestParam (required=false) Long removeBookId,
							  @RequestParam (required=false) String reservedBookIdsInString,
							  Model model) {
		// Gère les réservations de livres via des IDs
		Set<Long> reservedBookIds = new LinkedHashSet<Long>();
		if (reservedBookIdsInString != null) reservedBookIds = listConverter.convertListInStringToSetInLong(reservedBookIdsInString);		
		if (removeBookId != null) reservedBookIds.remove(removeBookId);
		if(reservedBookId != null) reservedBookIds.add(reservedBookId);
		
		Map<Book, String> listedBookReservations = dateTracker.listedBookReservations(reservedBookIds); // Suit les réservations
				
		// Recherche des livres selon les filtres
		List<Book> books;
		if (showAllBooks == null) books = bookService.searchBooks(title, author);
		else books = bookService.findAll();
		
		// Ajoute les données au modèle
		model.addAttribute("userHasFine", fineCalculator.hasFineOrNot(currentUserFinder.getCurrentUser()));
		model.addAttribute("listedBookReservations", listedBookReservations);
		model.addAttribute("reservedBookIds", reservedBookIds);
		model.addAttribute("title", title);
		model.addAttribute("author", author);
		model.addAttribute("showAllBooks", showAllBooks);
		model.addAttribute("books", books);
		return "user/user-browse-books.html"; 
	}
	
	// Affiche une FAQ pour l'utilisateur
	@GetMapping(value="/FAQ")
	public String FAQ() {
		return "user/user-FAQ.html";
	}
	
	// Affiche une page pour payer les réservations
	@PostMapping(value="/browsebooks/payreservation")
	public String payReservation(@RequestParam String reservedBookIdsInString,
                                 @RequestParam Double amountToPay, 
                                 Model model) {
		model.addAttribute("amountToPay", amountToPay);
		model.addAttribute("reservedBookIdsInString", reservedBookIdsInString);    
		return "user/user-pay-reservation.html";
	}

	// Sauvegarde les réservations des livres
	@PostMapping(value="browsebooks/savereservation")
	public String saveBookReservations(@RequestParam String reservedBookIdsInString) {
		Set<Long> reservedBookIds = listConverter.convertListInStringToSetInLong(reservedBookIdsInString);
		dateTracker.setReserervationDatesAndReservedByCurrentUserForMultipleBooks(reservedBookIds);        
		return "redirect:/user/yourreservations";
	}

	// Affiche les réservations actuelles de l'utilisateur
	@GetMapping(value="/yourreservations")
	public String yourReservations(Model model) {
		User currentUser = currentUserFinder.getCurrentUser(); 
		model.addAttribute("reservedBooks", currentUser.getReservedBooks()); 
		return "user/user-your-reservations.html"; 
	}
}
