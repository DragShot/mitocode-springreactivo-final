package com.mitocode.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.security.AuthRequest;
import com.mitocode.security.AuthResponse;
import com.mitocode.security.ErrorLogin;
import com.mitocode.security.JWTUtil;
import com.mitocode.security.User;
import com.mitocode.service.IUsuarioService;

import io.jsonwebtoken.lang.Strings;
import reactor.core.publisher.Mono;

@Component
public class LoginHandler {
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private IUsuarioService service;
	
	public Mono<ServerResponse> login(ServerRequest req) {
		return req.bodyToMono(AuthRequest.class)
				.map(ar -> new Tupla().ar(ar)) //Almacenar en un envoltorio compartido
				.flatMap(tp -> service.buscarPorUsuario(tp.ar.getUsername()).map(user -> tp.user(user)))
				.flatMap(tp -> this.autenticar(tp.user, tp.ar.getPassword()))
				.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.bodyValue(new ErrorLogin("credenciales incorrectas")));
	}
	
	public Mono<ServerResponse> loginv2(ServerRequest req) {
		String usuario = req.headers().firstHeader("usuario");
		String clave = req.headers().firstHeader("clave");
		//Rechazar la solicitud si faltan elementos
		if (!Strings.hasText(usuario) || !Strings.hasText(clave))
			return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
		//Procesar el inicio de sesión
		return service.buscarPorUsuario(usuario)
				.flatMap(user -> this.autenticar(user, clave))
				.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.bodyValue(new ErrorLogin("credenciales incorrectas")));
	}
	
	protected Mono<ServerResponse> autenticar(User user, String clave) {
		if (BCrypt.checkpw(clave, user.getPassword())) {
			String token = jwtUtil.generateToken(user);
			Date expiracion = jwtUtil.getExpirationDateFromToken(token);
			return ServerResponse.ok().bodyValue(new AuthResponse(token, expiracion));
		} else {
			return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.bodyValue(new ErrorLogin("credenciales incorrectas"));
		}
	}
	
	//Envoltorio para el AuthRequest y el User en #login()
	static class Tupla {
		public AuthRequest ar;
		public User user;
		
		public Tupla ar(AuthRequest ar) {
			this.ar = ar;
			return this;
		}
		
		public Tupla user(User user) {
			this.user = user;
			return this;
		}
	}
}
