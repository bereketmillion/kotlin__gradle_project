package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.repository.LibraryRepository

class DefaultLibraryService(
    private val repository: LibraryRepository
) : LibraryService {

    private val maxBorrowLimit = 5

    override fun addBook(book: Book): Boolean = repository.addBook(book)

    override fun borrowBook(patronId: String, isbn: String): BorrowResult {
        val patron = repository.findPatron(patronId) ?: return BorrowResult.PATRON_NOT_FOUND
        val book = repository.findBook(isbn) ?: return BorrowResult.BOOK_NOT_FOUND

        if (!book.isAvailable) return BorrowResult.NOT_AVAILABLE
        if (patron.borrowedBooks.size >= maxBorrowLimit) return BorrowResult.LIMIT_REACHED

        repository.updateBook(book.copy(isAvailable = false))
        repository.updatePatron(patron.copy(borrowedBooks = patron.borrowedBooks + isbn))
        return BorrowResult.SUCCESS
    }

    override fun returnBook(patronId: String, isbn: String): Boolean {
        val patron = repository.findPatron(patronId) ?: return false
        val book = repository.findBook(isbn) ?: return false
        if (isbn !in patron.borrowedBooks) return false

        repository.updatePatron(patron.copy(borrowedBooks = patron.borrowedBooks - isbn))
        repository.updateBook(book.copy(isAvailable = true))
        return true
    }

    override fun search(query: String): List<Book> {
        val normalized = query.trim().lowercase()
        return repository.getAllBooks().filter { book ->
            book.title.lowercase().contains(normalized) || book.author.lowercase().contains(normalized)
        }
    }
}
