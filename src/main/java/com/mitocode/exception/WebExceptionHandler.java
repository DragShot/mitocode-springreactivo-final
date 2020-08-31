package com.mitocode.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@Order(-1) //Ordered.HIGHEST_PRECEDENCE
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

	public WebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
			ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
		super(errorAttributes, resourceProperties, applicationContext);
		this.setMessageWriters(configurer.getWriters());
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::makeErrorResponse);
	}
	
	ErrorAttributeOptions options = ErrorAttributeOptions.of(
			Include.EXCEPTION, Include.STACK_TRACE, Include.MESSAGE, Include.BINDING_ERRORS);
	
	protected Mono<ServerResponse> makeErrorResponse(ServerRequest request) {
		Map<String, Object> errorMap = getErrorAttributes(request, options);
		Map<String, Object> excepMap = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.resolve((int)errorMap.get("status"));
		if (httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		//Detectar errores de validación lanzados por RequestValidator
		if ("javax.validation.ConstraintViolationException".equals(errorMap.get("exception"))) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		//Detectar errores de autorización lanzados por RequestValidator
		if ("com.mitocode.security.AuthException".equals(errorMap.get("exception"))) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		}
		excepMap.put("error", httpStatus.value());
		excepMap.put("descripcion", httpStatus.getReasonPhrase());
		excepMap.put("razon", errorMap.get("message"));
		excepMap.put("ruta", request.uri());
		excepMap.put("fecha", LocalDateTime.now().toString());
		excepMap.put("excepcion", errorMap.get("exception"));
		return ServerResponse.status(httpStatus)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(excepMap));
	}
	
}
