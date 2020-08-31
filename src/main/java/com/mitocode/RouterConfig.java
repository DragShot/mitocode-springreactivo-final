package com.mitocode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.handler.CursoHandler;
import com.mitocode.handler.EstudianteHandler;
import com.mitocode.handler.ICrudHandler;
import com.mitocode.handler.LoginHandler;
import com.mitocode.handler.MatriculaHandler;

@Configuration
public class RouterConfig {
	
	@Bean
	public RouterFunction<ServerResponse> rutasCurso(CursoHandler handler) {
		return rutasCRUD("/func/cursos", handler);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasEstudiante(EstudianteHandler handler) {
		return rutasCRUD("/func/estudiantes", handler);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasMatricula(MatriculaHandler handler) {
		return rutasCRUD("/func/matriculas", handler);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasLogin(LoginHandler handler) {
		return RouterFunctions.route(RequestPredicates.POST("/login"), handler::login)
				.andRoute(RequestPredicates.POST("/v2/login"), handler::loginv2);
	}
	
	/**
	 * Método de asistencia para vincular las operaciones CRUD definidas por la
	 * interfaz IHandler con sus rutas correspondientes. 
	 * @param handler  El endpoint a vincular.
	 * @param ruta     La ruta que usará el endpoint.
	 * @return Un RouterFunction<ServerResponse> que incluye la rutas
	 *         predefinidas. Se le pueden añadir fácilmente más rutas encima de
	 *         ser necesario.
	 */
	public RouterFunction<ServerResponse> rutasCRUD(String ruta, ICrudHandler handler) {
		//Encadenable: GET("/func/cursos").or(GET("/func/cursos2"))
		return RouterFunctions.route(RequestPredicates.GET(ruta), handler::listar)
				.andRoute(RequestPredicates.GET(ruta + "/{id}"), handler::listarPorId)
				.andRoute(RequestPredicates.POST(ruta), handler::registrar)
				.andRoute(RequestPredicates.PUT(ruta), handler::modificar)
				.andRoute(RequestPredicates.DELETE(ruta + "/{id}"), handler::eliminar);
	}
}
