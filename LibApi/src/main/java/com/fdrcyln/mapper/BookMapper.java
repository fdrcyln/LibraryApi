package com.fdrcyln.mapper;

import com.fdrcyln.dto.request.CreateBookRequest;
import com.fdrcyln.dto.response.BookResponse;
import com.fdrcyln.entities.Book;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(CreateBookRequest request) {
        Book book = new Book();

        BeanUtils.copyProperties(request, book);

        book.setAvailableStock(request.getTotalStock());
        book.setActive(true);

        return book;
    }

    public BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();

        BeanUtils.copyProperties(book, response);
        
        if (book.getCategory() != null) {
            response.setCategoryId(book.getCategory().getId());
            response.setCategoryName(book.getCategory().getName());
        }

        return response;
    }
}