package com.haupsystem.service.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DataBindingViolationException extends DataIntegrityViolationException {

	private static final long serialVersionUID = -4576895094027709553L;

	public DataBindingViolationException(String message) {
        super(message);
    }

}
