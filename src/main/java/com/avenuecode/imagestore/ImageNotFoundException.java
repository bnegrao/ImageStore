package com.avenuecode.imagestore;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ImageNotFoundException(Long imageId) {
		super ("Image with id '" + imageId + "' was not found." );
	}

}
