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
        String empty = "";
    book.setId(book.hashCode());
    logger.info("stored a new book: " + book);
    if ((!book.getAuthor().equals(empty)) || (!book.getTitle().equals(empty)) || (book.getSize() !=null)) {
        repo.add(book);
    }else {
        logger.info("cannot store a book with ALL empty fields (author='', title='', size=null): " + book);
    }
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        for (Book book : retrieveAll()){
            if (book.getId().equals(bookIdToRemove)){
                logger.info("removed book: " + book);
                return repo.remove(book);
            }
        }
        logger.info("no book with id: " + bookIdToRemove);
        return false;
    }

    @Override
    public boolean removeItemByRegex(String queryRegex) {
        Boolean successfullyRemoved = false;
        for (Book book : retrieveAll()){
            if (book.getAuthor().matches(queryRegex)
                    || (book.getTitle().matches(queryRegex))
                    || ((String.valueOf(book.getSize())).matches(queryRegex))){
                logger.info("regex given: " + queryRegex);
                logger.info("removed book: " + book);
                successfullyRemoved = repo.remove(book);
                // = true;
                //return repo.remove(book);
            } else{
                logger.info("no books found with regex: " + queryRegex);
                return successfullyRemoved;
            }
          //  return successfullyRemoved;
        }
       // logger.info("no books found with regex: " + queryRegex);
        return successfullyRemoved;
    }
}
