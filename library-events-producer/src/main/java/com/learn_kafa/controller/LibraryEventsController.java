package com.learn_kafa.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn_kafa.domain.LibraryEvent;
import com.learn_kafa.domain.LibraryEventType;
import com.learn_kafa.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class LibraryEventsController {

	
	@Autowired
	LibraryEventProducer libraryEventProducer;
	
	
	@PostMapping("/lib/")
	public ResponseEntity<LibraryEvent> postLibraryEven(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException{

		/* 
		 * libraryEventProducer.sendLibraryEvent(libraryEvent);
		 * libraryEventProducer.sendLibraryEventsSynchronously(libraryEvent);
		 */
		
		//If it's new Book then set to NEW as LibararyEventType
		log.info("Library Event Controller called...");
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);
		libraryEventProducer.sendLibraryEventApproach3(libraryEvent);
	
		
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
}
