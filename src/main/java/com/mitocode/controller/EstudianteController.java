package com.mitocode.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitocode.document.Estudiante;
import com.mitocode.service.IEstudianteService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/anot/estudiantes")
public class EstudianteController {
	
	@Autowired
	private IEstudianteService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Estudiante>>> listar() {
		Flux<Estudiante> fx = service.listar();
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON).body(fx));
	}
	
	@GetMapping("/ordenado")
	public Mono<ResponseEntity<Flux<Estudiante>>> listarOrdenado() {
		Flux<Estudiante> fx = service.listarOrdenado();
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON).body(fx));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Estudiante>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id)
				.map(obj -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON).body(obj))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Estudiante>> registrar(@Valid @RequestBody Estudiante estudiante, ServerHttpRequest req) {
		return service.registrar(estudiante)
				.map(obj -> ResponseEntity.created(URI.create(req.getURI().toString() + "/" + obj.getId()))
						.contentType(MediaType.APPLICATION_JSON).body(obj));
	}
	
	@PutMapping
	public Mono<ResponseEntity<Estudiante>> modificar(@Valid @RequestBody Estudiante estudiante) {
		return service.modificar(estudiante)
				.map(obj -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON).body(obj));
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
		return service.listarPorId(id)
				.flatMap(obj -> service.eliminar(id)
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
}
