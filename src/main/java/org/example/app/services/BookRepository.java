package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookShelfRegexException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", sqlParameterSource);
        logger.info("removed book id: " + bookIdToRemove);
        return true;
    }

    @Override
    public boolean removeItemByRegex(String queryRegex){

        String myRegex = "^[a-zA-Z\\d%]+$";
        Pattern pattern = Pattern.compile(myRegex);
        Matcher matcher = pattern.matcher(queryRegex);
        Boolean successfullyRemoved = false;
        if (matcher.matches() && !queryRegex.equals(""))
            try {
                {
                    MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
                    sqlParameterSource.addValue("regex", queryRegex);
                    int result = jdbcTemplate.queryForObject("SELECT count (1) FROM books where AUTHOR like :regex OR TITLE like :regex OR SIZE like :regex", sqlParameterSource, Integer.class);
                    logger.info("found " + result + " record(s)");
                    if (result == 0) {
                        return successfullyRemoved;
                    } else {
                        jdbcTemplate.update("DELETE FROM books WHERE AUTHOR like :regex OR TITLE like :regex OR SIZE like :regex", sqlParameterSource);
                        successfullyRemoved = Boolean.TRUE;
                        logger.info("removed " + result + " book(s) through regex: " + queryRegex);
                        return successfullyRemoved;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                logger.info("Something went wrong with your regex");
                return successfullyRemoved;
            }
        logger.info("Your regex didn't match a record");
        return false;

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
