package com.learn_kafa.unit.producer;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn_kafa.domain.Book;
import com.learn_kafa.domain.LibraryEvent;
import com.learn_kafa.producer.LibraryEventProducer;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerTest {

	@InjectMocks
	LibraryEventProducer eventProducer;
	
	@Mock
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Spy
	ObjectMapper ojMapper;
	
	private static String topic = "library-events";
	
	private static  Book book = Book.builder()
            .bookId(123)
            .bookAuthor("Dilip")
            .bookName("Kafka using Spring Boot")
            .build();
	
	
	
	void sendLibraryEventTest() throws JsonProcessingException {

        LibraryEvent libraryEvent = LibraryEvent.builder()
		                                   .libraryEventId(null)
		                                   .book(book)
		                                   .build();
        
        when(kafkaTemplate.sendDefault(isA(String.class))).then(null); 
        eventProducer.sendLibraryEvent(libraryEvent);
	}
	
	@Test
    void sendLibraryEvent_Approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
     
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        String json = ojMapper.writeValueAsString(libraryEvent);
        
        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception Calling Kafka"));
    
        when(kafkaTemplate.send(topic,libraryEvent.getLibraryEventId(),json)).thenReturn(future);
        assertThrows(Exception.class, ()->eventProducer.sendLibraryEventApproach2(libraryEvent).get());
    
    }
	
	
	@Test
    void sendLibraryEvent_Approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {
      
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        String json = ojMapper.writeValueAsString(libraryEvent);
        
        SettableListenableFuture future = new SettableListenableFuture();
	        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(topic, libraryEvent.getLibraryEventId(),json);
	        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1), 0, 0, 0, 0, 0);
	       
	        SendResult<Integer, String> sendResult = new SendResult<Integer,String>(producerRecord, recordMetadata);
        future.set(sendResult);

        when(kafkaTemplate.send(topic,libraryEvent.getLibraryEventId(),json)).thenReturn(future);
      

        ListenableFuture<SendResult<Integer, String>> listenableFuture = eventProducer.sendLibraryEventApproach2(libraryEvent);
        sendResult =  listenableFuture.get();
        assert sendResult.getRecordMetadata().partition() == 1;
    }
}
