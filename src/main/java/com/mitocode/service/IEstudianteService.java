package com.mitocode.service;

import com.mitocode.document.Estudiante;

import reactor.core.publisher.Flux;

public interface IEstudianteService extends ICRUD<Estudiante, String> {
	public Flux<Estudiante> listarOrdenado();
}
