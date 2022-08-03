package com.learn_kafa.domain;

import javax.validation.Valid;
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
public class LibraryEvent {
	
	private Integer libraryEventId;
	private LibraryEventType libraryEventType;
	
	@NotNull
	@Valid
	private Book book;
	
}
