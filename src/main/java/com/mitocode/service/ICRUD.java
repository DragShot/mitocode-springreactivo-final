package com.mitocode.service;

import org.springframework.data.domain.Pageable;

import com.mitocode.pagination.PageSupport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICRUD<T, ID> {
	public Mono<T> registrar(T obj);
	public Mono<T> modificar(T obj);
	public Flux<T> listar();
	public Mono<PageSupport<T>> listarPage(Pageable page);
	public Mono<T> listarPorId(ID id);
	public Mono<Void> eliminar(ID id);
}
