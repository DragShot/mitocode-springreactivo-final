package com.mitocode.security;

public class AuthException extends RuntimeException {
	
	private static final long serialVersionUID = -894529334222947253L;

	public AuthException() {}
	
	public AuthException(String mensaje) {
		super(mensaje);
	}
	
	public AuthException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
