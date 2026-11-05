package com.goya.alumnos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alumnos")
public class Alumno {
	
	@Id // Id es para marcar la clave primaria de la tabla
	@Column(name = "dni", length = 9, nullable = false, unique = true)
	private String dni;

	@Column(name = "nombre", length = 50)
	private String nombre;
	
	@Column(name = "apellidos", length = 100)
	private String apellidos;
	
	@Column(name = "edad")
	private int edad;
	
	@Column(name = "matricula")
	private double matricula;

	// JPA exige siempre un constructor sin argumentos
	public Alumno() {
	}

	public Alumno(String d,String n, String a, int e, double m) {
		this.dni = d;
		this.nombre = n;
		this.apellidos = a;
		this.edad = e;
		this.matricula = m;
	}
	
	public String getDni() {
		return dni;
	}
	public String getNombre() {
		return nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public int getEdad() {
		return edad;
	}
	public double getMatricula() {
		return matricula;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	@Override
	public String toString() {
	 return "DNI: " + dni + " | " + apellidos + ", " + nombre + " | Edad: " + edad + " | Matrícula: " + matricula;
	}
	
}
