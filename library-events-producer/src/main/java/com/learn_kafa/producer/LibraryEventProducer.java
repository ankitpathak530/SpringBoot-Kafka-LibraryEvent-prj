package com.learn_kafa.producer;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn_kafa.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer {

	@Autowired
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Autowired
	ObjectMapper ojMapper;
	
	private static String topic = "library-events";
	
	/* It's Asynchronous behavior approach 1
	* 
	* kafkaTemplate sendDefault tight to the configuration value 
	* we have written in aplication.properties file.
	*
	*/
	public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
		
		Integer key = libraryEvent.getLibraryEventId();
		String value = ojMapper.writeValueAsString(libraryEvent);
					
		ListenableFuture<SendResult<Integer, String>> listenableFuture = this.kafkaTemplate.sendDefault(key,value);
        listenableFuture.addCallback( new ListenableFutureCallback<SendResult<Integer,String>>(){

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key,value,ex);
			}		
	    });
	
	}
	
	
	  /*It's Asynchronous behavior
	   *Advantage of this approach is that this kafkaTemplate send method publish n no. of topics with same 
	   *instance of kafka template.
	   *
	   */
		public ListenableFuture<SendResult<Integer, String>> sendLibraryEventApproach2(LibraryEvent libraryEvent) throws JsonProcessingException {
			
			Integer key = libraryEvent.getLibraryEventId();
			String value = ojMapper.writeValueAsString(libraryEvent);
						
			ListenableFuture<SendResult<Integer, String>> listenableFuture = this.kafkaTemplate.send(topic,key,value);
	        listenableFuture.addCallback( new ListenableFutureCallback<SendResult<Integer,String>>(){

				@Override
				public void onSuccess(SendResult<Integer, String> result) {
					handleSuccess(key, value, result);
				}
				@Override
				public void onFailure(Throwable ex) {
					handleFailure(key,value,ex);
				}		
		    });
		   return listenableFuture;
		}
		
		
		
	
		/*
		 * It's Approach 3
		 * 
		 * This kafkaTemplate send method take ProducerRecord as argument
		 */
		public void sendLibraryEventApproach3(LibraryEvent libraryEvent) throws JsonProcessingException {
		
			Integer key = libraryEvent.getLibraryEventId();
			String value = ojMapper.writeValueAsString(libraryEvent);

			ProducerRecord<Integer, String> producerRecord = buildProducerRecord(topic, key, value);
			ListenableFuture<SendResult<Integer, String>> listenableFuture = this.kafkaTemplate.send(producerRecord);
			 listenableFuture.addCallback( new ListenableFutureCallback<SendResult<Integer,String>>(){

					@Override
					public void onSuccess(SendResult<Integer, String> result) {
						handleSuccess(key, value, result);
					}
					@Override
					public void onFailure(Throwable ex) {
						handleFailure(key,value,ex);
					}		
			    });
			
		}

		private ProducerRecord<Integer, String> buildProducerRecord(String topic, Integer key, String value) {
			
			List<Header> headers = List.of(
				    	new RecordHeader("event-source", "scanner".getBytes())
					);
			
			// topic,partition,key,value,header
			return new ProducerRecord<>(topic, null, key, value, headers);

		}
		
		
		
		
		
		// It's Synchronous behavior
		public SendResult<Integer, String> sendLibraryEventsSynchronously(LibraryEvent libraryEvent)
				throws JsonProcessingException {
			Integer key = libraryEvent.getLibraryEventId();
			String value = ojMapper.writeValueAsString(libraryEvent);

			SendResult<Integer, String> sendResult = null;
			try {
				sendResult = this.kafkaTemplate.sendDefault(key, value).get();
			} catch (ExecutionException | InterruptedException e) {
				log.error("ExecutionException/InterruptedException Sending the Message and the exception is {}",
						e.getMessage());
			}

			return sendResult;
		}
	
	
	
	
	protected void handleFailure(Integer key, String value, Throwable ex) {
		log.error("Error Sending the message and the exception is {}",ex.getMessage());
		try {
			throw ex;
		}catch(Throwable throwable) {
			log.error("Error in on failure: {}",throwable.getMessage());
		}
	}

	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		log.info("Message Sent Successfully for the key : {} and the Value is {}, partiton is {} ",key,value,result.getRecordMetadata().partition());
	}
}
