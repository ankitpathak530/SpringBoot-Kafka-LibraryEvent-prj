package com.learn_kafa.domain;

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
public class Book {

	@NotNull
	private Integer bookId;
	@NotBlank
	private String bookName;
	@NotBlank
	private String bookAuthor;
}
