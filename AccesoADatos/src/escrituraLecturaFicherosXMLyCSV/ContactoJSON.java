package escrituraLecturaFicherosXMLyCSV;

public class ContactoJSON {
	
	private String nombre;
	private String telefono;
	private String dni;
	
	public ContactoJSON(String n, String t, String d) {
		
		this.nombre = n;
		this.telefono = t;
		this.dni = d;
		
	}
	
	public void mostrar() {
		System.out.println("Nombre: " + this.nombre);
		System.out.println("Telefono: " + this.telefono);
		System.out.println("DNI: " + this.dni);
		System.out.println();
	}

	public String getNombre() {
		return nombre;
	}
	
	@Override
	public String toString() {
		
		return "Nombre: " + this.nombre + "\nTeléfono: " + this.telefono + "\nDNI: " + this.dni + "\n";
	}
	
}