package com.mitocode.handler;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

/**
 * Interfaz que declara las operaciones comunes de los endpoints funcionales
 * que ofrecen CRUD.
 */
@NoRepositoryBean
public interface ICrudHandler {
	public Mono<ServerResponse> listar(ServerRequest req);
	public Mono<ServerResponse> listarPorId(ServerRequest req);
	public Mono<ServerResponse> registrar(ServerRequest req);
	public Mono<ServerResponse> modificar(ServerRequest req);
	public Mono<ServerResponse> eliminar(ServerRequest req);
}
