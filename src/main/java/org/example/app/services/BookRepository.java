package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository<T> implements ProjectRepository<Book>{

private final Logger logger = Logger.getLogger(BookRepository.class);
private final List<Book> repo = new ArrayList<>();

    @Override
    public List<Book> retrieveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public void store(Book book) {
    book.setId(book.hashCode());
    logger.info("stored new book: " + book);
    repo.add(book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        for (Book book : retrieveAll()){
            if (book.getId().equals(bookIdToRemove)){
                logger.info("removed book: " + book);
                return repo.remove(book);
            }
        }
        return false;
    }
}
