package com.mitocode.service.impl;

import org.springframework.data.domain.Pageable;

import com.mitocode.pagination.PageSupport;
import com.mitocode.repo.IRepo;
import com.mitocode.service.ICRUD;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {
	
	protected abstract IRepo<T, ID> getRepo();

	@Override
	public Mono<T> registrar(T obj) {
		//obj.setId(null);
		return getRepo().save(obj);
	}

	@Override
	public Mono<T> modificar(T obj) {
		//if (obj.getId() == null)
		//	throw new IllegalArgumentException("$id == null");
		return getRepo().save(obj);
	}

	@Override
	public Flux<T> listar() {
		return getRepo().findAll();
	}
	
	@Override
	public Mono<PageSupport<T>> listarPage(Pageable page) {
		//db.platos.find().skip(5).limit(5);
		return getRepo().findAll().collectList().map(list -> 
			new PageSupport<T>(//list.stream().skip(page.getPageNumber() * page.getPageSize()).limit(page.getPageSize()).collect(Collectors.toList())
					list.subList(page.getPageNumber() * page.getPageSize(), (page.getPageNumber() + 1) * page.getPageSize()),
					page.getPageNumber(), page.getPageSize(), list.size()));
	}

	@Override
	public Mono<T> listarPorId(ID id) {
		return getRepo().findById(id);
	}

	@Override
	public Mono<Void> eliminar(ID id) {
		return getRepo().deleteById(id);
	}

}
