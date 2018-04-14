package com.avenuecode.imagestore;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(String userId) {
		super("User with username '" + userId + "' was not found." );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
