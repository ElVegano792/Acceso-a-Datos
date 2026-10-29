package examen1;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

public class Ejercicio2 {

	public static void main(String[] args) {
		
		String fichero = "/home/alumno/Descargas/marvel.json";
		String url = "jdbc:mysql://localhost:3306/";
		String usuario = "alumno";
		String password = "123";
		
		try {
//			crearBaseDatos(url,usuario,password);
			leerJSON(fichero);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
	}
	
	public static void leerJSON(String fichero) {
		try (Reader lector = new FileReader(fichero)) {
			Gson gson = new Gson();
			Pelicula pelicula = gson.fromJson(lector, Pelicula.class);
			List<Pelicula> peliculas = pelicula.getPeliculas();
			
			for(Pelicula p:peliculas) {
				System.out.println(p);
			}

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
	}
	
	public static void crearBaseDatos(String url, String usuario, String pass) throws SQLException {
		
		try(Connection conexion = DriverManager.getConnection(url,usuario,pass)) {
			PreparedStatement query = conexion.prepareStatement("CREATE DATABASE IF NOT EXISTS UCM; USE UCM; CREATE TABLE IF NOT EXISTS peliculas (titulo VARCHAR(150) PRIMARY KEY, anio INT NOT NULL, fase INT NOT NULL, director VARCHAR(100) NOT NULL);");
			ResultSet resultado = query.executeQuery();
			resultado.last();
			System.out.println("Base creada!");
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
	}
	
}
