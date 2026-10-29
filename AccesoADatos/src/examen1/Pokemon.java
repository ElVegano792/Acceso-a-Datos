package examen1;

public class Pokemon {
	
	private int id;
	private int nivel;
	private String nombre;
	private String tipo1;
	private String tipo2 = null;
	
	public Pokemon(int id,int nivel, String n, String t1) {
		
		this.id = id;
		this.nivel = nivel;
		this.nombre = n;
		this.tipo1 = t1;
		
	}
	
	public Pokemon(int id,int nivel, String n, String t1,String t2) {
		
		this.id = id;
		this.nivel = nivel;
		this.nombre = n;
		this.tipo1 = t1;
		this.tipo2 = t2;
		
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getNivel() {
		return this.nivel;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public String getTipo1() {
		return this.tipo1;
	}
	
	public String getTipo2() {
		return this.tipo2;
	}
	
	public void mostrar() {
		if(tipo2==null) {
		System.out.println("#0" + this.id + " " + this.nombre + " | " + this.tipo1 + " | Nivel " + this.nivel);
		} else
			System.out.println("#0" + this.id + " " + this.nombre + " | " + this.tipo1 + "/" + this.tipo2 +" | Nivel " + this.nivel);
	}
	
}
