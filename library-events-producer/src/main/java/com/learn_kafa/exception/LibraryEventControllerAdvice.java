package com.learn_kafa.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice {

	
	@ExceptionHandler(MethodArgumentNotValidException.class) 
	public  ResponseEntity<?> handleException(MethodArgumentNotValidException ex, BindingResult bindingResult)
	{
	     List<FieldError> fieldErrors = bindingResult.getFieldErrors();	
	     String errorMessage = fieldErrors.stream()
	    		                     .map(e->e.getField() + " - "+ e.getDefaultMessage())
	    		                     .collect(Collectors.joining(", "));
	     
		
	     log.error("Error Message: {} ",errorMessage);
		
		return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
		
	}
}
