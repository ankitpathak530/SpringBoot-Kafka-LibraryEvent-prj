package com.kafka.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class Book {

	@Id
	private Integer bookId;

	private String bookName;

	private String bookAuthor;
	
	@OneToOne
	@ToString.Exclude
	@JoinColumn( name = "libraryEventId")
	private LibraryEvent libraryEvent;
}
