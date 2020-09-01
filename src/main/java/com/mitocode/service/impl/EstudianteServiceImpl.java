package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitocode.document.Estudiante;
import com.mitocode.repo.IEstudianteRepo;
import com.mitocode.repo.IRepo;
import com.mitocode.service.IEstudianteService;

import reactor.core.publisher.Flux;

@Service
public class EstudianteServiceImpl extends CRUDImpl<Estudiante, String> implements IEstudianteService {
	@Autowired
	private IEstudianteRepo repo;
	
	@Override
	protected IRepo<Estudiante, String> getRepo() {
		return repo;
	}
	
	@Override
	public Flux<Estudiante> listarOrdenado() {
		return getRepo().findAll().collectList()
				.doOnNext(list -> list.sort(this::ordenEdadDescendente))
				.flux().flatMap(list -> Flux.fromIterable(list));
	}
	
	protected int ordenEdadDescendente(Estudiante e1, Estudiante e2) {
		return nvl(e2.getEdad(), 0) - nvl(e1.getEdad(), 0);
	}
	
	protected int nvl(Integer valor, int nulo) {
		return valor == null ? nulo : valor;
	}
}
