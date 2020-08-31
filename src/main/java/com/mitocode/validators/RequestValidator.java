package com.mitocode.validators;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Validador genérico usado por endpoints funcionales.
 * @author MitoCode
 */
@Component
public class RequestValidator {

	@Autowired
	private Validator validador;

	public <T> Mono<T> validate(T obj) {
		if (obj == null)
			return Mono.error(new IllegalArgumentException());
		
		Set<ConstraintViolation<T>> violations = this.validador.validate(obj);
		
		if (violations == null || violations.isEmpty())
			return Mono.just(obj);
		else
			return Mono.error(new ConstraintViolationException(violations));
	}

}
