package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookShelfLoginException;
import org.example.app.exceptions.BookShelfRegexException;
import org.example.app.exceptions.NoFilenameGivenException;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Controller
@RequestMapping(value = "books")
@Scope("singleton")
public class BookShelfController {
    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        } else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }

    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            //return "redirect:/books/shelf";
            return "book_shelf";
        } else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }

    }

    @PostMapping("/removeByRegex")
    public String removeBookByRegex(@RequestParam(value = "queryRegex") String queryRegex) throws BookShelfRegexException {
        if (bookService.removeBookByRegex(queryRegex)) {
            return "redirect:/books/shelf";
        } else {
            throw new BookShelfRegexException("No books match your regex. No empty regex allowed. Please use regex for LIKE filter in SQL");
            }
        }


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws NoFilenameGivenException {
        try {
            String name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();

            //create dir
            String rootPath = System.getProperty("catalina.home");
            File dir = new File(rootPath + File.separator + "external_uploads");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //create file
            File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();

            logger.info("saved new file: " + serverFile.getAbsolutePath());

            return "redirect:/books/shelf";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("no file name given");
            throw  new NoFilenameGivenException("Please provide file name");
            //return "redirect:/books/shelf";
        }

    }


    @ExceptionHandler(BookShelfRegexException.class)
        public String handleError(Model model, BookShelfRegexException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/regex_info";
    }

    @ExceptionHandler(NoFilenameGivenException.class)
    public String handleError(Model model, NoFilenameGivenException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/no_file";
    }
}