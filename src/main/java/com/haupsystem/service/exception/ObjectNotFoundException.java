package com.haupsystem.service.exception;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = -3145253480422349303L;

	public ObjectNotFoundException(String message) {
        super(message);
    }

}
