package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainJ {

	public static void main(String[] args) {
		
		String url = "jdbc:mysql://localhost/dam2";
		String usuario = "alumno";
		String password = "123";
		
		try {
			
			Connection conn = DriverManager.getConnection(url,usuario,password);
			System.out.println("Conexión realizada con éxito.");
			Statement sql = conn.createStatement();
			ResultSet resultado = sql.executeQuery("SELECT * FROM alumnos");
			while(resultado.next()) {
				System.out.println(resultado.getString("email"));
			}
			
			conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

}
