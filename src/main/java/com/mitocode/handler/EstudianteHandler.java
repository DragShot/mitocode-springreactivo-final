package com.mitocode.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.document.Estudiante;
import com.mitocode.service.IEstudianteService;
import com.mitocode.validators.RequestValidator;

import reactor.core.publisher.Mono;

@Component
public class EstudianteHandler implements ICrudHandler {
	
	@Autowired
	private IEstudianteService service;
	
	@Autowired
	private RequestValidator validador;
	
	public Mono<ServerResponse> listar(ServerRequest req) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Estudiante.class);
	}
	
	public Mono<ServerResponse> listarPorId(ServerRequest req) {
		return service.listarPorId(req.pathVariable("id"))
				.flatMap(obj -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj))
				//Necesario en lugar de defaultIfEmpty() porque build()
				//devuelve un Mono<ServerResponse>
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> registrar(ServerRequest req) {
		return req.bodyToMono(Estudiante.class)
				.flatMap(validador::validate)
				.flatMap(service::registrar)
				.flatMap(obj -> ServerResponse
						.created(URI.create(req.uri().toString() + "/" + obj.getId()))
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj));
	}
	
	public Mono<ServerResponse> modificar(ServerRequest req) {
		return req.bodyToMono(Estudiante.class)
				.flatMap(validador::validate)
				.flatMap(service::modificar)
				.flatMap(obj -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj));
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest req) {
		return service.listarPorId(req.pathVariable("id"))
				.flatMap(obj -> service.eliminar(obj.getId()))
				.flatMap(obj -> ServerResponse.noContent().build())
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}
