package com.dam2.goya;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id // Id es para marcar la clave primaria de la tabla
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "producto", length = 100, nullable = false, unique = true)
	private String nombre;

	@Column(nullable = false)
	private double precio;

	// JPA exige siempre un constructor sin argumentos
	public Producto() {
	}

	public Producto(String nombre, double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	@Override
	public String toString() {
	 return "(" + id +") - Nombre: " + nombre + "\n Precio: " + precio + "€\n";
	}
}
