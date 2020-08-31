package com.mitocode.document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection="estudiantes")
public class Estudiante implements IDocumento {
	@Id
	private String id;
	
	@NotEmpty(message = "El nombre no debe estar vacío")
	private String nombres;
	
	@NotEmpty(message = "El apellido no debe estar vacío")
	private String apellidos;
	
	@NotNull(message = "Se debe incluir el DNI")
	@Size(min = 8, max = 8, message = "El DNI debe contener 8 dígitos")
	private String dni;
	
	@NotNull(message = "Se debe incluir la edad del estudiante")
	@Min(value = 0, message = "La edad no puede tener un valor negativo")
	private Integer edad;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}
}
