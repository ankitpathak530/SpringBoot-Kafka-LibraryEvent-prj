package com.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.entity.LibraryEvent;
import com.kafka.repo.LibraryEventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryEventService {

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private LibraryEventRepository libraryEventRepository;
	
	
	public void processLibrary(ConsumerRecord<Integer, String> consumerRecord) throws JsonMappingException, JsonProcessingException
	{
		LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
		log.info("::::::::::::LibraryEvent inside Service:::::::::\n{}",libraryEvent);
		
		switch(libraryEvent.getLibraryEventType()) {
		case NEW:
			   // save operation
			   save(libraryEvent);
			    break;
		case UPDATE:
			   // Update Opearation
			  update(libraryEvent);
			    break;
	     default:
	    	 log.info("Something went wrong, Can't insert data");
		}
		
	}
	private LibraryEvent save(LibraryEvent libraryEvent)
	{
		libraryEvent.getBook().setLibraryEvent(libraryEvent);
		LibraryEvent save = libraryEventRepository.save(libraryEvent);
		log.info("Successfully persisted the library EVent {} ",libraryEvent);
		return save;
	}
	private void update(LibraryEvent libraryEvent)
	{
		validate(libraryEvent);
		
		libraryEvent.getBook().setLibraryEvent(libraryEvent);
		libraryEventRepository.save(libraryEvent);
		log.info("Successfully persisted the library EVent {} ",libraryEvent);
	}
	private void validate(LibraryEvent libraryEvent) {
		if(libraryEvent.getLibraryEventId() == null) {
			throw new IllegalArgumentException("Invalid LibraryEvent Id. It can not be null or empty.");
		}
	}
}
