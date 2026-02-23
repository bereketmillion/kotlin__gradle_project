package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

private fun addBook(service: LibraryService) {
    print("ISBN: ")
    val isbn = readln().trim()
    print("Title: ")
    val title = readln().trim()
    print("Author: ")
    val author = readln().trim()
    print("Year: ")
    val year = readln().trim().toIntOrNull()

    if (year == null) {
        println("Invalid year")
        return
    }

    val added = service.addBook(Book(isbn, title, author, year))
    println("Book added: $added")
}

private fun registerPatron(repository: LibraryRepository) {
    print("Patron ID: ")
    val patronId = readln().trim()
    print("Name: ")
    val name = readln().trim()

    val added = repository.addPatron(Patron(patronId, name))
    println("Patron added: $added")
}

private fun borrowBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln().trim()
    print("ISBN: ")
    val isbn = readln().trim()

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Borrowed")
        BorrowResult.BOOK_NOT_FOUND -> println("Book not found")
        BorrowResult.PATRON_NOT_FOUND -> println("Patron not found")
        BorrowResult.NOT_AVAILABLE -> println("Book not available")
        BorrowResult.LIMIT_REACHED -> println("Limit reached")
    }
}

private fun returnBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln().trim()
    print("ISBN: ")
    val isbn = readln().trim()

    val returned = service.returnBook(patronId, isbn)
    println(if (returned) "Returned" else "Return failed")
}

private fun search(service: LibraryService) {
    print("Query: ")
    val query = readln().trim()

    val result = service.search(query)
    if (result.isEmpty()) {
        println("No books found")
        return
    }

    result.forEachIndexed { index, book ->
        println("${index + 1}. ${book.title} - ${book.author}")
    }
}

private fun listAllBooks(repository: LibraryRepository) {
    repository.getAllBooks().forEach { book ->
        val status = if (book.isAvailable) "Available" else "Borrowed"
        println("${book.isbn} | ${book.title} | $status")
    }
}
