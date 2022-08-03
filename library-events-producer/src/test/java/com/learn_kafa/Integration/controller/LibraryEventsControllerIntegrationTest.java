package com.learn_kafa.Integration.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn_kafa.domain.Book;
import com.learn_kafa.domain.LibraryEvent;
import com.learn_kafa.producer.LibraryEventProducer;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

	@Autowired
	TestRestTemplate restTemplate;
	
	private Consumer<Integer, String> consumer;
	
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	

	
	@BeforeEach
	void setUp()
	{
		Map<String,Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory(configs, new IntegerDeserializer(),new StringDeserializer()).createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	void testDwon()
	{
		consumer.close();
	}
	
	
	
	@Test
	@Timeout(3)
	void postLibraryEvent() throws InterruptedException
	{
		  Book book = Book.builder()
				          .bookId(111)
				          .bookAuthor("APJ ABDUL KALAM")
				          .bookName("Wings of fire")
				          .build();
		  
		   LibraryEvent libraryEvent = LibraryEvent.builder()
				                                   .libraryEventId(null)
				                                   .book(book)
				                                   .build();
		   
		   HttpHeaders headers = new HttpHeaders();
		   headers.set("content-type", MediaType.APPLICATION_JSON.toString());
		   HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,headers);
		   
		   //when
		   ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/lib/", HttpMethod.POST,request,LibraryEvent.class);
		 


		   //then
		   assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		   
		   
		   ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(this.consumer, "library-events");
		   String expectedValue = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":111,\"bookName\":\"Wings of fire\",\"bookAuthor\":\"APJ ABDUL KALAM\"}}";
		   String value = consumerRecord.value();
		   assertEquals(expectedValue, value);
	}
	
}
