package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository<T> implements ProjectRepository<Book>, ApplicationContextAware {

private final Logger logger = Logger.getLogger(BookRepository.class);
private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retrieveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        String empty = "";
   // book.setId(context.getBean(IdProvider.class).provideId(book));
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("author", book.getAuthor());
        sqlParameterSource.addValue("title", book.getTitle());
        sqlParameterSource.addValue("size", book.getSize());

        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES (:author, :title, :size)", sqlParameterSource);
    logger.info("stored a new book: " + book);

/*    if ((!book.getAuthor().equals(empty)) || (!book.getTitle().equals(empty)) || (book.getSize() !=null)) {
       // repo.add(book);
    }else {
        logger.info("cannot store a book with ALL empty fields (author='', title='', size=null): " + book);
    }*/
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", sqlParameterSource);
        logger.info("removed book id: " + bookIdToRemove);
        return true;

/*        for (Book book : retrieveAll()){
            if (book.getId().equals(bookIdToRemove)){
                logger.info("removed book: " + book);
                return true; //repo.remove(book);
            }
        }
        logger.info("no book with id: " + bookIdToRemove);
        return false;*/
    }

    @Override
    public boolean removeItemByRegex(String queryRegex) {
        Boolean successfullyRemoved = false;
        if (!queryRegex.equals("")) {
            for (Book book : retrieveAll()) {
                if (book.getAuthor().matches(queryRegex)
                        | (book.getTitle().matches(queryRegex))
                        | ((String.valueOf(book.getSize())).matches(queryRegex))) {
                    logger.info("regex given: " + queryRegex);
                    logger.info("removed book: " + book);
                   // successfullyRemoved = repo.remove(book);
                } else {
                    logger.info("no books found with regex: " + queryRegex);
                    return successfullyRemoved;
                }
                //  return successfullyRemoved;
            }
            // logger.info("no books found with regex: " + queryRegex);
            return successfullyRemoved;
        } else{
            return successfullyRemoved;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit(){
        logger.info("default INIT in book repository bean");


    }

    private void defaultDestroy(){
        logger.info("default DESTROY in book repository bean");


    }
}
