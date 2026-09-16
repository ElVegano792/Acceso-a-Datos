package ficheros;

import java.io.File;

public class Teoria1 {
	
	//private static final String DIR_CONFIG = "DAM2/martin";
//	private static final String DIR_CONFIG = "DAM2"; // Solo puede crear un directorio a la vez, se puede spamear para crearlo todo
	private static final String DIR_CONFIG = "DAM2"+File.separator+"martin"; // Con esto nos aseguramos de que se pueda hacer tanto en linux como en windows
	
	public static void main(String[] args) {
		
		File directorioActual = new File(".");
//		System.out.println(directorioActual.getAbsolutePath()); // Con esto obtenemos la ubicación actual del directorio
		
		// Vv Con esto y con el private static de arriba le decimos la ubcación del archivo que nosotros querramos vV \\
		File dirConfig = new File(DIR_CONFIG);
		System.out.println(dirConfig);
		
		// Esto es un metodo para comprobar si existe o no ese directorio
		if(dirConfig.exists() == true) {
			System.out.println("El directorio " + DIR_CONFIG + " existe.");
		}
		else {
			System.out.println("El directorio " + DIR_CONFIG + " no existe.");
			dirConfig.mkdirs(); // Y con esto lo creamos en linux
			System.out.println("Creando directorio " + DIR_CONFIG + ".");
		}
		
		File directorioConfig = new File(DIR_CONFIG+File.separator+"config.txt");
		if(directorioConfig.exists() == true) {
			System.out.println("El archivo ya existe");
		} else {
			System.out.println("El archivo no existe");
			
		}
		
	}
	
}
