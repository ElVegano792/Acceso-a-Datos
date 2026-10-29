package teoriaExamen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * SOLUCIÓN del Examen de prueba 2 - JDBC (classicmodels y naciones).
 * !! No la mires hasta haber intentado el examen !!
 * Usa ConexionBD (la misma de los ejercicios de clase) y PreparedStatement.
 *
 * Ejercicio 1: pedidos de un cliente con su importe total
 * Ejercicio 2: subir los precios de una línea de producto (con validaciones y transacción)
 * Ejercicio 3: resumen de un país de la base naciones
 */
public class Examen2Jdbc {

	public static void main(String[] args) {

		pedidosDeCliente("Atelier graphique");
		pedidosDeCliente("Cliente Fantasma");

		subirPrecios("Motorcycles", 10);
		subirPrecios("Motorcycles", 75);
		subirPrecios("Cohetes", 5);

		resumenPais("Spain");
		resumenPais("Narnia");

	}

	static String dinero(double valor) {
		return String.format(Locale.ROOT, "%.2f", valor);
	}

	// =====================================================================
	// EJERCICIO 1: pedidos de un cliente
	// =====================================================================

	// Devuelve el customerNumber o null si no existe el cliente
	static Integer buscarCliente(Connection conn, String nombre) throws SQLException {

		try (PreparedStatement ps = conn.prepareStatement("SELECT customerNumber FROM customers WHERE customerName = ?")) {
			ps.setString(1, nombre);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getInt(1) : null;
		}

	}

	public static void pedidosDeCliente(String cliente) {

		// El importe de un pedido es la suma de cantidad * precio de sus líneas.
		// LEFT JOIN + GROUP BY: un pedido sin líneas saldría con total 0 en vez de desaparecer.
		String sql = "SELECT o.orderNumber, o.orderDate, o.status, COALESCE(SUM(od.quantityOrdered * od.priceEach), 0) AS total "
				+ "FROM orders o LEFT JOIN orderdetails od ON od.orderNumber = o.orderNumber "
				+ "WHERE o.customerNumber = ? GROUP BY o.orderNumber, o.orderDate, o.status ORDER BY o.orderDate";
		
		try (Connection conn = ConexionBD.conectar("classicmodels")) {

			Integer numero = buscarCliente(conn, cliente);
			if (numero == null) {
				System.out.println("Error: el cliente " + cliente + " no existe");
				return;
			}

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, numero);
				ResultSet rs = ps.executeQuery();

				if (!rs.next()) {
					System.out.println("El cliente " + cliente + " no tiene pedidos");
					return;
				}
				System.out.println("Pedidos de " + cliente + ":");
				double acumulado = 0;
				int pedidos = 0;
				do {
					double total = rs.getDouble("total");
					System.out.println("- Pedido " + rs.getInt("orderNumber") + " | " + rs.getDate("orderDate") + " | "
							+ rs.getString("status") + " | " + dinero(total));
					acumulado += total;
					pedidos++;
				} while (rs.next());
				System.out.println("Total: " + pedidos + " pedidos, " + dinero(acumulado));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// =====================================================================
	// EJERCICIO 2: subir precios de una línea de producto
	// =====================================================================

	static boolean existeLinea(Connection conn, String linea) throws SQLException {

		try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM productlines WHERE productLine = ?")) {
			ps.setString(1, linea);
			return ps.executeQuery().next();
		}

	}

	// Número de productos y precio medio (MSRP) de la línea; devuelve {n, media}
	static double[] estadisticaLinea(Connection conn, String linea) throws SQLException {

		try (PreparedStatement ps = conn
				.prepareStatement("SELECT COUNT(*), COALESCE(AVG(MSRP), 0) FROM products WHERE productLine = ?")) {
			ps.setString(1, linea);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return new double[] { rs.getInt(1), rs.getDouble(2) };
		}

	}

	static int aplicarSubida(Connection conn, String linea, double porcentaje) throws SQLException {

		try (PreparedStatement ps = conn
				.prepareStatement("UPDATE products SET MSRP = ROUND(MSRP * (1 + ? / 100), 2) WHERE productLine = ?")) {
			ps.setDouble(1, porcentaje);
			ps.setString(2, linea);
			return ps.executeUpdate();
		}

	}

	public static void subirPrecios(String linea, double porcentaje) {

		if (porcentaje <= 0 || porcentaje > 50) { // validación previa: ni siquiera abrimos la conexión
			System.out.println("Error: el porcentaje debe ser mayor que 0 y como máximo 50");
			return;
		}

		try (Connection conn = ConexionBD.conectar("classicmodels")) {

			if (!existeLinea(conn, linea)) {
				System.out.println("Error: la línea " + linea + " no existe");
				return;
			}
			double[] antes = estadisticaLinea(conn, linea);
			if (antes[0] == 0) {
				System.out.println("La línea " + linea + " no tiene productos");
				return;
			}

			conn.setAutoCommit(false); // transacción: o se suben todos o ninguno
			try {
				int modificados = aplicarSubida(conn, linea, porcentaje);
				conn.commit();
				double[] despues = estadisticaLinea(conn, linea);
				System.out.println("Línea " + linea + ": " + modificados + " productos subidos un " + dinero(porcentaje) + "%");
				System.out.println("Precio medio antes: " + dinero(antes[1]) + " | después: " + dinero(despues[1]));
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// =====================================================================
	// EJERCICIO 3: resumen de un país (naciones)
	// =====================================================================

	public static void resumenPais(String pais) {

		// Cabecera: país, región y continente (dos JOIN)
		String sqlPais = "SELECT c.country_id, c.name, c.area, r.name AS region, co.name AS continente "
				+ "FROM countries c JOIN regions r ON r.region_id = c.region_id "
				+ "JOIN continents co ON co.continent_id = r.continent_id WHERE c.name = ?";
		// Últimos datos: la fila de country_stats con el año más reciente
		String sqlStats = "SELECT year, population, gdp FROM country_stats WHERE country_id = ? "
				+ "ORDER BY year DESC LIMIT 1";
		String sqlIdiomas = "SELECT COUNT(*) AS total, COALESCE(SUM(official), 0) AS oficiales "
				+ "FROM country_languages WHERE country_id = ?";

		try (Connection conn = ConexionBD.conectar("naciones");
				PreparedStatement psPais = conn.prepareStatement(sqlPais)) {

			psPais.setString(1, pais);
			ResultSet rs = psPais.executeQuery();
			if (!rs.next()) {
				System.out.println(pais + " no es un país real o no está dado de alta en la base de datos");
				return;
			}
			int id = rs.getInt("country_id");
			System.out.println(rs.getString("name") + " (" + rs.getString("region") + ", " + rs.getString("continente") + ")");
			System.out.println("Superficie: " + dinero(rs.getDouble("area")) + " km2");

			try (PreparedStatement ps = conn.prepareStatement(sqlStats)) {
				ps.setInt(1, id);
				ResultSet st = ps.executeQuery();
				if (st.next()) {
					System.out.println("Último dato (" + st.getInt("year") + "): población " + st.getLong("population")
							+ ", PIB " + st.getLong("gdp"));
				} else {
					System.out.println("Sin estadísticas registradas");
				}
			}

			try (PreparedStatement ps = conn.prepareStatement(sqlIdiomas)) {
				ps.setInt(1, id);
				ResultSet idi = ps.executeQuery();
				idi.next();
				System.out.println("Idiomas: " + idi.getInt("total") + " (oficiales: " + idi.getInt("oficiales") + ")");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
