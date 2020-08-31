package com.mitocode.controller;

import java.net.URI;
import java.util.List;

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

import com.mitocode.document.Curso;
import com.mitocode.document.Estudiante;
import com.mitocode.document.Matricula;
import com.mitocode.service.ICursoService;
import com.mitocode.service.IEstudianteService;
import com.mitocode.service.IMatriculaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/anot/matriculas")
public class MatriculaController {
	
	@Autowired
	private IMatriculaService service;
	
	@Autowired
	private ICursoService cursoServ;
	
	@Autowired
	private IEstudianteService estudianteServ;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Matricula>>> listar() {
		Flux<Matricula> fx = service.listar().flatMap(this::autoCompletar);
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON).body(fx));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Matricula>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id).flatMap(this::autoCompletar)
				.map(obj -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON).body(obj))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Matricula>> registrar(@Valid @RequestBody Matricula matricula, ServerHttpRequest req) {
		return service.registrar(matricula).flatMap(this::autoCompletar)
				.map(obj -> ResponseEntity.created(URI.create(req.getURI().toString() + "/" + obj.getId()))
						.contentType(MediaType.APPLICATION_JSON).body(obj));
	}
	
	@PutMapping
	public Mono<ResponseEntity<Matricula>> modificar(@Valid @RequestBody Matricula matricula) {
		return service.modificar(matricula).flatMap(this::autoCompletar)
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
	
	/**
	 * Recupera asincrónicamente la información correspondiente a los objetos
	 * internos de la matrícula, ante la falta de DBRef.
	 * @param matricula La matricula a procesar.
	 * @return Un {@code Mono<Matricula>} con la matrícula actualizada.
	 */
	protected Mono<Matricula> autoCompletar(Matricula matricula) {
		Mono<List<Curso>> transfCursos = Flux.fromIterable(matricula.getCursos())
				.flatMap(curso -> curso.getId() == null ? Mono.just(curso) : cursoServ.listarPorId(curso.getId())).collectList();
		Mono<Estudiante> transfEstudiante = matricula.getEstudiante().getId() == null ? Mono.just(matricula.getEstudiante()) : estudianteServ.listarPorId(matricula.getEstudiante().getId());
		return Mono.just(matricula)
				.zipWith(transfCursos, (mat, cursos) -> {
					mat.setCursos(cursos);
					return mat;
				}).zipWith(transfEstudiante, (mat, est) -> {
					mat.setEstudiante(est);
					return mat;
				});
	}
	
}
