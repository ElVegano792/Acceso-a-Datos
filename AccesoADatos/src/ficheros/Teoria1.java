package ficheros;

import java.io.File;
import java.io.FileWriter;

public class Teoria1 {
	
	//private static final String DIR_CONFIG = "DAM2/martin";
//	private static final String DIR_CONFIG = "DAM2"; // Solo puede crear un directorio a la vez, se puede spamear para crearlo todo
	private static final String DIR_CONFIG = "DAM2"+File.separator+"martin"; // TODO Con esto nos aseguramos de que se pueda hacer tanto en linux como en windows
	private static final String ARCHIVO_CONFIG = DIR_CONFIG + File.separator + "config.txt"; // cuidado con poner PathSeparator que se vuelve loco
	
	public static void main(String[] args) {
		
		try {
		File directorioActual = new File(".");
//		System.out.println(directorioActual.getAbsolutePath()); // Con esto obtenemos la ubicación actual del directorio
		
		// Vv Con esto y con el private static de arriba le decimos la ubcación del archivo que nosotros querramos vV \\
		File dirConfig = new File(DIR_CONFIG);
//		System.out.println(dirConfig);
		boolean crearFichero = true;
		
		// Esto es un metodo para comprobar si existe o no ese directorio
		if(dirConfig.exists() == true) {
			System.out.println("El directorio " + DIR_CONFIG + " existe.");
		}
		else {
			System.out.println("El directorio " + DIR_CONFIG + " no existe.");
//			System.out.println("Creando directorio " + DIR_CONFIG + ".");
//			dirConfig.mkdirs(); // Y con esto lo creamos en linux
			if(dirConfig.mkdirs() == false) {
				crearFichero = false;
				System.out.println("No he podido crear el directorio.");
			}
		}
		
		// ahora creamos el fichero 
		if(crearFichero == true) {
			FileWriter escritor = new FileWriter(ARCHIVO_CONFIG, true);
			if(escritor == null) {
				System.out.println("No he podido crear el archivo");
			} else {
				System.out.println("Archivo creado o ya existente");
				escritor.close();
				}
			
			File archivo = new File(ARCHIVO_CONFIG);
			if(archivo.createNewFile())
				System.out.println("Archivo creado.");
			else
				System.out.println("No puedo crearlo o ya existe.");
			
			}
		
		// Para obtener el espacio libre en el disco en gigas \\
		File dirConfig2 = new File(DIR_CONFIG);
		long espacio = dirConfig2.getFreeSpace() /1024 /1024 /1024; // de base el getFreeSpace nos lo da en Bytes
		System.out.println("Espacio libre en el disco: " + espacio + "GB.");
		
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
}