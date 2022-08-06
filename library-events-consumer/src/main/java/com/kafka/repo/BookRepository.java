package com.kafka.repo;

import org.springframework.data.repository.CrudRepository;

import com.kafka.entity.Book;

public interface BookRepository extends CrudRepository<Book, Integer> {

}
