package com.learn_kafa.unit.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn_kafa.controller.LibraryEventsController;
import com.learn_kafa.domain.Book;
import com.learn_kafa.domain.LibraryEvent;
import com.learn_kafa.producer.LibraryEventProducer;



@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerUnitTest {

	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	LibraryEventProducer libraryEventProducer;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Test
	void postLibraryEvent() throws Exception{
		
		  Book book = Book.builder()
		          .bookId(111)
		          .bookAuthor("APJ ABDUL KALAM")
		          .bookName("Wings of fire")
		          .build();
  
          LibraryEvent libraryEvent = LibraryEvent.builder()
		                                   .libraryEventId(null)
		                                   .book(book)
		                                   .build();
   
          String json = objectMapper.writeValueAsString(libraryEvent);
          when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

          
          mockMvc.perform(post("/v1/lib/")
        		  .content(json)
        		  .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated());
    
	}
	
	
	
	
	@Test
	void postLibraryEvent_4xx() throws Exception
	{
		
		 LibraryEvent libraryEvent = LibraryEvent.builder()
		                                   .libraryEventId(null)
		                                   .book(new Book())
		                                   .build();
		 
		  String json = objectMapper.writeValueAsString(libraryEvent);
		  when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

		  
		  mockMvc.perform(post("/v1/lib/")
				        .content(json)
				        .contentType(MediaType.APPLICATION_JSON))
		  .andExpect(status().is4xxClientError());	 
	}
}
