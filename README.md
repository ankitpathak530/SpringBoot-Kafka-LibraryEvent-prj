# This is simple Library-Event Application used to manage book in library. 

# What I learnt and Implemented are as below..

  1. Created SpringBoot kafka producer
     * Produce the record to the kafka topics using two Rest point.
         i.  Add new book to library
         ii. Update book to library
     
  2. Created SpringBoot Kafka consumer
     * Consume the record produced by kafka producer and Persist to H2-Database
    
  3. Integration Testing using Embedded Kafka
  
  4. Unit testing using Mockito
