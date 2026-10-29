package examen1;

import java.util.List;

public class Pelicula {
	
	private String titulo;
	private int año;
	private int fase;
	private String director;
	
	public Pelicula(String t, int a, int f, String d) {
		
		this.titulo = t;
		this.año = a;
		this.fase = f;
		this.director = d;
		
	}
	
	@Override
	public String toString() {
		String texto = Integer.toString(this.año) + " (" + Integer.toString(this.fase)  + ") - " + this.titulo + " [" + this.director + "]";
		return texto;
	}
	
	public String getTitulo() {
		return this.titulo;
	}
	
	public int getAño() {
		return año;
	}
	
	public int getFase() {
		return fase;
	}
	
	public String getDirector() {
		return director;
	}
	
	private List<Pelicula> peliculas;
	
	public List<Pelicula> getPeliculas(){
		return peliculas;
	}
	
}
