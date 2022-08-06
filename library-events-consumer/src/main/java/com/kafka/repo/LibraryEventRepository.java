package com.kafka.repo;

import org.springframework.data.repository.CrudRepository;

import com.kafka.entity.LibraryEvent;

public interface LibraryEventRepository extends CrudRepository<LibraryEvent	, Integer> {

}
