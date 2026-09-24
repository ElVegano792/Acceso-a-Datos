package ejerciciosFicheros;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Ejercicio7 {
	
	final static String ruta = "coordenadas.dat";
	
	
	public static void main(String[] args) {
		final int NUM_REGISTROS = 3;
//		final int TAMAÑO_REGISTRO = 20;
		try(DataInputStream fichero = new DataInputStream(new FileInputStream(ruta))){
			
//			File ficheroFisico = new File(ruta);
//			final int NUM_REGISTROS1 = (int)ficheroFisico.length()/TAMAÑO_REGISTRO;
			System.out.println("SATELITES Y COORDENADAS");
			for(int i = 0;i< NUM_REGISTROS;i++) {
				int id = fichero.readInt();
				float latitud = fichero.readFloat();
				float longitud = fichero.readFloat();
				String estado = "";
				for (int j=0;j<4;j++) {
					estado+=fichero.readChar();
				}
				System.out.printf("Satélite ID: %d | Posición: (%.4f, %.4f) | Estado: %s \n",id,latitud,longitud,estado);
			}
			
			// Vv Con esto tendríamos un bucle infinito que seguiría hasta encontrarse una excepción vV
//			while(true) {
//				int id = fichero.readInt();
//				float latitud = fichero.readFloat();
//				float longitud = fichero.readFloat();
//				String estado = "";
//				for (int j=0;j<4;j++) {
//					estado+=fichero.readChar();
//				}
//				System.out.printf("Satélite ID: %d | Posición: (%.4f, %.4f) | Estado: %s \n",id,latitud,longitud,estado);
//			}
			
		} catch (Exception e) {
			System.out.println("Error al leer el fichero.");
		}
		
	}	
}