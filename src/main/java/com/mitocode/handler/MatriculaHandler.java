package com.mitocode.handler;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.document.Curso;
import com.mitocode.document.Estudiante;
import com.mitocode.document.Matricula;
import com.mitocode.service.ICursoService;
import com.mitocode.service.IEstudianteService;
import com.mitocode.service.IMatriculaService;
import com.mitocode.validators.RequestValidator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MatriculaHandler implements ICrudHandler {
	
	@Autowired
	private IMatriculaService service;
	
	@Autowired
	private ICursoService cursoServ;
	
	@Autowired
	private IEstudianteService estudianteServ;
	
	@Autowired
	private RequestValidator validador;
	
	@Override
	public Mono<ServerResponse> listar(ServerRequest req) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar().flatMap(this::autoCompletar), Matricula.class);
	}
	
	@Override
	public Mono<ServerResponse> listarPorId(ServerRequest req) {
		return service.listarPorId(req.pathVariable("id"))
				.flatMap(this::autoCompletar)
				.flatMap(obj -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj))
				//Necesario en lugar de defaultIfEmpty() porque build()
				//devuelve un Mono<ServerResponse>
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	@Override
	public Mono<ServerResponse> registrar(ServerRequest req) {
		return req.bodyToMono(Matricula.class)
				.flatMap(validador::validate)
				.flatMap(service::registrar)
				.flatMap(this::autoCompletar)
				.flatMap(obj -> ServerResponse
						.created(URI.create(req.uri().toString() + "/" + obj.getId()))
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj));
	}
	
	@Override
	public Mono<ServerResponse> modificar(ServerRequest req) {
		return req.bodyToMono(Matricula.class)
				.flatMap(validador::validate)
				.flatMap(service::modificar)
				.flatMap(this::autoCompletar)
				.flatMap(obj -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(obj));
	}
	
	@Override
	public Mono<ServerResponse> eliminar(ServerRequest req) {
		return service.listarPorId(req.pathVariable("id"))
				.flatMap(obj -> service.eliminar(obj.getId()))
				.flatMap(obj -> ServerResponse.noContent().build())
				.switchIfEmpty(ServerResponse.notFound().build());
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
