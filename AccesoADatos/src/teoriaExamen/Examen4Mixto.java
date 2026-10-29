package teoriaExamen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * SOLUCIÓN del Examen de prueba 4 - Mixto (ficheros, JDBC y JSON).
 * !! No la mires hasta haber intentado el examen !!
 *
 * Ejercicio 1: ventas.txt -> resumen_ventas.csv
 * Ejercicio 2: JDBC -> JSON (empleados de una oficina)
 * Ejercicio 3: JSON -> JDBC (importar empleados con validaciones)
 */
public class Examen4Mixto {

	public static void main(String[] args) {

		resumenVentas("ventas.txt", "resumen_ventas.csv");
		resumenVentas("no_existe.txt", "x.csv");

		exportarEmpleados("Paris", "empleados_paris.json");
		exportarEmpleados("Madrid", "empleados_madrid.json");

		importarEmpleados("nuevos_empleados.json");

	}

	static String formato(double v) {
		return String.format(Locale.ROOT, "%.2f", v);
	}

	// =====================================================================
	// EJERCICIO 1: ventas
	// Línea válida: AAAA-MM-DD;vendedor;importe
	// =====================================================================

	static final Pattern VENTA = Pattern.compile("\\d{4}-\\d{2}-\\d{2};([^;]+);(\\d+(?:\\.\\d+)?)");

	static class Acumulado {
		int ventas = 0;
		double total = 0;
	}

	public static void resumenVentas(String fichero, String destino) {

		if (!Files.exists(Paths.get(fichero))) {
			System.out.println("El fichero " + fichero + " no existe");
			return;
		}

		try {
			Map<String, Acumulado> porVendedor = new TreeMap<>();
			int ignoradas = 0;

			for (String linea : Files.readAllLines(Paths.get(fichero), StandardCharsets.UTF_8)) {
				if (linea.trim().isEmpty()) {
					continue;
				}
				Matcher m = VENTA.matcher(linea.trim());
				if (!m.matches()) {
					ignoradas++;
					continue;
				}
				Acumulado a = porVendedor.get(m.group(1));
				if (a == null) {
					a = new Acumulado();
					porVendedor.put(m.group(1), a);
				}
				a.ventas++;
				a.total += Double.parseDouble(m.group(2));
			}

			if (porVendedor.isEmpty()) {
				System.out.println("No hay ninguna venta válida");
				return;
			}

			// Ordenamos por total descendente: pasamos las entradas a una lista y la ordenamos
			List<Map.Entry<String, Acumulado>> orden = new ArrayList<>(porVendedor.entrySet());
			orden.sort((x, y) -> Double.compare(y.getValue().total, x.getValue().total));

			List<String> csv = new ArrayList<>();
			csv.add("Vendedor;Ventas;Total;Media");
			for (Map.Entry<String, Acumulado> e : orden) {
				Acumulado a = e.getValue();
				csv.add(e.getKey() + ";" + a.ventas + ";" + formato(a.total) + ";" + formato(a.total / a.ventas));
			}
			Files.write(Paths.get(destino), csv, StandardCharsets.UTF_8);

			System.out.println("Vendedores: " + orden.size() + " (líneas ignoradas: " + ignoradas + ")");
			System.out.println("Mejor vendedor: " + orden.get(0).getKey() + " (" + formato(orden.get(0).getValue().total) + ")");
			System.out.println("Resumen guardado en " + destino);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// =====================================================================
	// CLASES PARA GSON
	// =====================================================================

	static class Empleado {

		int numero;
		String nombre;
		String apellido;
		String email;
		String extension;
		String codigoOficina;
		String puesto;
		Integer jefe; // Integer (no int): si falta en el JSON vale null

	}

	static class ListaEmpleados {
		List<Empleado> empleados = new ArrayList<>();
	}

	// =====================================================================
	// EJERCICIO 2: BD -> JSON
	// =====================================================================

	// Devuelve el officeCode de la ciudad o null si no hay oficina
	static String buscarOficina(Connection conn, String ciudad) throws SQLException {

		try (PreparedStatement ps = conn.prepareStatement("SELECT officeCode FROM offices WHERE city = ?")) {
			ps.setString(1, ciudad);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getString(1) : null;
		}

	}

	static List<Empleado> empleadosDeOficina(Connection conn, String oficina) throws SQLException {

		List<Empleado> lista = new ArrayList<>();
		String sql = "SELECT employeeNumber, firstName, lastName, email, extension, officeCode, jobTitle, reportsTo "
				+ "FROM employees WHERE officeCode = ? ORDER BY employeeNumber";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, oficina);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Empleado e = new Empleado();
				e.numero = rs.getInt("employeeNumber");
				e.nombre = rs.getString("firstName");
				e.apellido = rs.getString("lastName");
				e.email = rs.getString("email");
				e.extension = rs.getString("extension");
				e.codigoOficina = rs.getString("officeCode");
				e.puesto = rs.getString("jobTitle");
				int jefe = rs.getInt("reportsTo");
				e.jefe = rs.wasNull() ? null : jefe; // wasNull distingue un NULL de un 0 real
				lista.add(e);
			}
		}
		return lista;

	}

	public static void exportarEmpleados(String ciudad, String fichero) {

		try (Connection conn = ConexionBD.conectar("classicmodels")) {

			String oficina = buscarOficina(conn, ciudad);
			if (oficina == null) {
				System.out.println("No existe oficina en " + ciudad);
				return;
			}
			ListaEmpleados datos = new ListaEmpleados();
			datos.empleados = empleadosDeOficina(conn, oficina);
			if (datos.empleados.isEmpty()) {
				System.out.println("La oficina de " + ciudad + " no tiene empleados: no se crea " + fichero);
				return;
			}
			guardarJSON(datos, fichero);
			System.out.println(datos.empleados.size() + " empleados de " + ciudad + " guardados en " + fichero);

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

	}

	static void guardarJSON(Object objeto, String fichero) throws IOException {

		try (FileWriter w = new FileWriter(fichero)) {
			new GsonBuilder().setPrettyPrinting().create().toJson(objeto, w);
		}

	}

	// =====================================================================
	// EJERCICIO 3: JSON -> BD
	// =====================================================================

	static boolean existe(Connection conn, String sql, Object valor) throws SQLException {

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setObject(1, valor);
			return ps.executeQuery().next();
		}

	}

	// Devuelve el motivo de rechazo o null si el empleado es válido (así importarEmpleados solo decide qué contar)
	static String motivoRechazo(Connection conn, Empleado e) throws SQLException {

		if (e.email == null || !e.email.contains("@")) {
			return "email no válido";
		}
		if (existe(conn, "SELECT 1 FROM employees WHERE employeeNumber = ?", e.numero)) {
			return "el número ya existe";
		}
		if (!existe(conn, "SELECT 1 FROM offices WHERE officeCode = ?", e.codigoOficina)) {
			return "la oficina " + e.codigoOficina + " no existe";
		}
		if (e.jefe != null && !existe(conn, "SELECT 1 FROM employees WHERE employeeNumber = ?", e.jefe)) {
			return "el jefe " + e.jefe + " no existe";
		}
		return null;

	}

	static void insertar(Connection conn, Empleado e) throws SQLException {

		String sql = "INSERT INTO employees (employeeNumber, lastName, firstName, extension, email, officeCode, reportsTo, jobTitle) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, e.numero);
			ps.setString(2, e.apellido);
			ps.setString(3, e.nombre);
			ps.setString(4, e.extension);
			ps.setString(5, e.email);
			ps.setString(6, e.codigoOficina);
			if (e.jefe == null) {
				ps.setNull(7, Types.INTEGER); // reportsTo admite NULL
			} else {
				ps.setInt(7, e.jefe);
			}
			ps.setString(8, e.puesto);
			ps.executeUpdate();
		}

	}

	public static void importarEmpleados(String fichero) {

		if (!new File(fichero).exists()) {
			System.out.println("El fichero " + fichero + " no existe");
			return;
		}

		try (FileReader r = new FileReader(fichero); Connection conn = ConexionBD.conectar("classicmodels")) {

			ListaEmpleados datos = new Gson().fromJson(r, ListaEmpleados.class);
			if (datos == null || datos.empleados.isEmpty()) {
				System.out.println("El fichero no contiene empleados");
				return;
			}

			int insertados = 0;
			int rechazados = 0;
			conn.setAutoCommit(false);
			try {
				for (Empleado e : datos.empleados) {
					String motivo = motivoRechazo(conn, e);
					if (motivo == null) {
						insertar(conn, e);
						insertados++;
						System.out.println("Insertado " + e.numero + " " + e.nombre + " " + e.apellido);
					} else {
						rechazados++;
						System.out.println("Rechazado " + e.numero + " " + e.nombre + " " + e.apellido + ": " + motivo);
					}
				}
				conn.commit();
			} catch (SQLException ex) {
				conn.rollback();
				throw ex;
			}
			System.out.println("Resumen: " + insertados + " insertados, " + rechazados + " rechazados");

		} catch (SQLException | IOException ex) {
			ex.printStackTrace();
		}

	}
}
