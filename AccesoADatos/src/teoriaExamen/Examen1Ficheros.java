package teoriaExamen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SOLUCIÓN del Examen de prueba 1 - Ficheros (texto, CSV y binarios).
 * !! No la mires hasta haber intentado el examen !!
 *
 * Ejercicio 1: CSV de videojuegos (resumenInventario)
 * Ejercicio 2: log de servidor con expresiones regulares (analizarLog, exportarErrores)
 * Ejercicio 3: fichero binario de temperaturas (registrar, listar, corregir con RandomAccessFile)
 */
public class Examen1Ficheros {

	public static void main(String[] args) throws Exception {

		System.out.println("===== EJERCICIO 1 =====");
		resumenInventario("videojuegos.csv");
		System.out.println();
		resumenInventario("no_existe.csv");

		System.out.println("\n===== EJERCICIO 2 =====");
		analizarLog("servidor.log");
		exportarErrores("servidor.log", "errores.txt");

		System.out.println("\n===== EJERCICIO 3 =====");
		Files.deleteIfExists(Paths.get(FICHERO_TEMPERATURAS)); // para que la prueba sea repetible
		listarTemperaturas(); // el fichero todavía no existe
		registrarTemperatura(1, 12.5, 3.0);
		registrarTemperatura(5, 21.5, 9.0);
		registrarTemperatura(2, 14.0, 5.5);
		registrarTemperatura(3, 9.5, 1.0);
		registrarTemperatura(2, 15.0, 6.0); // día repetido
		registrarTemperatura(40, 15.0, 6.0); // día fuera de rango
		registrarTemperatura(6, 5.0, 8.0); // máxima menor que la mínima
		listarTemperaturas();
		corregirTemperatura(3, 10.5, 2.0);
		corregirTemperatura(20, 10.0, 2.0); // día sin registro
		listarTemperaturas();

	}

	// =====================================================================
	// UTILIDADES COMUNES
	// =====================================================================

	// Lee todas las líneas del fichero (en UTF-8, para que no fallen las tildes)
	static List<String> leerLineas(String fichero) throws IOException {
		return Files.readAllLines(Paths.get(fichero), StandardCharsets.UTF_8);
	}

	static boolean existe(String fichero) {
		return Files.exists(Paths.get(fichero));
	}

	// Formatea con punto decimal y los decimales indicados, sin depender del idioma del sistema
	static String formato(double valor, int decimales) {
		return String.format(Locale.ROOT, "%." + decimales + "f", valor);
	}

	// =====================================================================
	// EJERCICIO 1: CSV de videojuegos
	// Cabecera: Titulo;Plataforma;Precio;Stock
	// =====================================================================

	static class Videojuego {

		private String titulo;
		private String plataforma;
		private double precio;
		private int stock;

		Videojuego(String titulo, String plataforma, double precio, int stock) {
			this.titulo = titulo;
			this.plataforma = plataforma;
			this.precio = precio;
			this.stock = stock;
		}

		String getTitulo() { return titulo; }
		String getPlataforma() { return plataforma; }
		double getPrecio() { return precio; }
		int getStock() { return stock; }

	}

	// Resultado de leer el CSV: los videojuegos correctos y cuántas líneas se han ignorado
	static class LecturaCSV {
		List<Videojuego> validos = new ArrayList<>();
		int ignoradas = 0;
	}

	// Convierte UNA línea en un Videojuego. Devuelve null si la línea es errónea
	// (no tiene 4 campos, o el precio o el stock no son números válidos).
	// Se usa una expresión regular y no solo parseDouble porque parseDouble aceptaría cosas como "NaN".
	static Videojuego parsearVideojuego(String linea) {

		String[] campos = linea.split(";", -1); // -1 para no perder campos vacíos al final
		if (campos.length != 4) {
			return null;
		}
		String precio = campos[2].trim();
		String stock = campos[3].trim();
		if (!precio.matches("\\d+(\\.\\d+)?") || !stock.matches("\\d+")) {
			return null;
		}
		return new Videojuego(campos[0].trim(), campos[1].trim(), Double.parseDouble(precio),
				Integer.parseInt(stock));

	}

	static LecturaCSV leerVideojuegos(String fichero) throws IOException {

		LecturaCSV lectura = new LecturaCSV();
		List<String> lineas = leerLineas(fichero);

		for (int i = 1; i < lineas.size(); i++) { // i = 1: la primera línea es la cabecera y se salta
			String linea = lineas.get(i);
			if (linea.trim().isEmpty()) {
				continue; // una línea en blanco no cuenta como errónea
			}
			Videojuego juego = parsearVideojuego(linea);
			if (juego == null) {
				lectura.ignoradas++;
			} else {
				lectura.validos.add(juego);
			}
		}

		return lectura;

	}

	public static void resumenInventario(String fichero) throws IOException {

		if (!existe(fichero)) {
			System.out.println("El fichero " + fichero + " no existe");
			return;
		}

		LecturaCSV lectura = leerVideojuegos(fichero);
		if (lectura.validos.isEmpty()) {
			System.out.println("No hay ningún videojuego válido en el fichero");
			return;
		}

		int unidades = 0;
		double valor = 0;
		Videojuego masCaro = lectura.validos.get(0);
		Map<String, Integer> titulosPorPlataforma = new TreeMap<>(); // TreeMap: salen ordenadas alfabéticamente
		List<String> agotados = new ArrayList<>();

		for (Videojuego j : lectura.validos) {
			unidades += j.getStock();
			valor += j.getPrecio() * j.getStock();
			if (j.getPrecio() > masCaro.getPrecio()) {
				masCaro = j;
			}
			Integer actual = titulosPorPlataforma.get(j.getPlataforma());
			titulosPorPlataforma.put(j.getPlataforma(), actual == null ? 1 : actual + 1);
			if (j.getStock() == 0) {
				agotados.add(j.getTitulo() + " (" + j.getPlataforma() + ")");
			}
		}

		System.out.println("Videojuegos válidos: " + lectura.validos.size() + " (líneas ignoradas: "
				+ lectura.ignoradas + ")");
		System.out.println("Unidades en stock: " + unidades);
		System.out.println("Valor del inventario: " + formato(valor, 2));
		System.out.println("Títulos por plataforma:");
		for (Map.Entry<String, Integer> e : titulosPorPlataforma.entrySet()) {
			System.out.println("- " + e.getKey() + ": " + e.getValue());
		}
		System.out.println("Juego más caro: " + masCaro.getTitulo() + " (" + masCaro.getPlataforma() + ") - "
				+ formato(masCaro.getPrecio(), 2));
		System.out.println("Agotados: " + (agotados.isEmpty() ? "ninguno" : String.join(", ", agotados)));

	}

	// =====================================================================
	// EJERCICIO 2: log de servidor
	// Línea válida: 2025-03-14 10:32:11 [ERROR] Mensaje
	// =====================================================================

	// Se compila UNA vez y se reutiliza para todas las líneas. Los paréntesis son los grupos que luego se recuperan:
	// 1 = hora completa (HH:mm:ss), 2 = solo la hora, 3 = nivel, 4 = mensaje
	static final Pattern LINEA_LOG = Pattern
			.compile("\\d{4}-\\d{2}-\\d{2} ((\\d{2}):\\d{2}:\\d{2}) \\[(INFO|WARN|ERROR)\\] (.+)");

	static class EntradaLog {

		private String hora; // 10:32:11
		private String soloHora; // 10
		private String nivel;
		private String mensaje;

		EntradaLog(String hora, String soloHora, String nivel, String mensaje) {
			this.hora = hora;
			this.soloHora = soloHora;
			this.nivel = nivel;
			this.mensaje = mensaje;
		}

		String getHora() { return hora; }
		String getSoloHora() { return soloHora; }
		String getNivel() { return nivel; }
		String getMensaje() { return mensaje; }

	}

	static class LecturaLog {
		List<EntradaLog> validas = new ArrayList<>();
		int ignoradas = 0;
	}

	// Una línea -> EntradaLog, o null si no cumple el formato (matches exige que cumpla TODA la línea)
	static EntradaLog parsearLog(String linea) {

		Matcher m = LINEA_LOG.matcher(linea.trim());
		if (!m.matches()) {
			return null;
		}
		return new EntradaLog(m.group(1), m.group(2), m.group(3), m.group(4));

	}

	static LecturaLog leerLog(String fichero) throws IOException {

		LecturaLog lectura = new LecturaLog();
		for (String linea : leerLineas(fichero)) {
			if (linea.trim().isEmpty()) {
				continue;
			}
			EntradaLog entrada = parsearLog(linea);
			if (entrada == null) {
				lectura.ignoradas++;
			} else {
				lectura.validas.add(entrada);
			}
		}
		return lectura;

	}

	public static void analizarLog(String fichero) throws IOException {

		if (!existe(fichero)) {
			System.out.println("El fichero " + fichero + " no existe");
			return;
		}

		LecturaLog lectura = leerLog(fichero);
		if (lectura.validas.isEmpty()) {
			System.out.println("No hay ninguna línea válida en el fichero");
			return;
		}

		// Los niveles se muestran siempre en este orden y aunque tengan 0 eventos
		String[] niveles = { "INFO", "WARN", "ERROR" };
		Map<String, Integer> porNivel = new TreeMap<>();
		for (String nivel : niveles) {
			porNivel.put(nivel, 0);
		}
		Map<String, Integer> porHora = new TreeMap<>(); // TreeMap: las horas salen en orden, así el empate gana la más temprana

		for (EntradaLog e : lectura.validas) {
			porNivel.put(e.getNivel(), porNivel.get(e.getNivel()) + 1);
			Integer actual = porHora.get(e.getSoloHora());
			porHora.put(e.getSoloHora(), actual == null ? 1 : actual + 1);
		}

		System.out.println("Líneas válidas: " + lectura.validas.size() + " (líneas ignoradas: " + lectura.ignoradas
				+ ")");
		for (String nivel : niveles) {
			long porcentaje = Math.round(porNivel.get(nivel) * 100.0 / lectura.validas.size());
			System.out.println(nivel + ": " + porNivel.get(nivel) + " (" + porcentaje + "%)");
		}

		String horaPico = null;
		int maximo = 0;
		for (Map.Entry<String, Integer> e : porHora.entrySet()) {
			if (e.getValue() > maximo) { // estrictamente mayor: en caso de empate se queda la primera
				maximo = e.getValue();
				horaPico = e.getKey();
			}
		}
		System.out.println("Hora con más eventos: " + horaPico + "h (" + maximo + " eventos)");

	}

	public static void exportarErrores(String fichero, String destino) throws IOException {

		if (!existe(fichero)) {
			System.out.println("El fichero " + fichero + " no existe");
			return;
		}

		List<String> errores = new ArrayList<>();
		for (EntradaLog e : leerLog(fichero).validas) { // reutiliza leerLog
			if (e.getNivel().equals("ERROR")) {
				errores.add(e.getHora() + " - " + e.getMensaje());
			}
		}

		if (errores.isEmpty()) {
			System.out.println("No hay ningún ERROR: no se crea el fichero " + destino);
			return;
		}
		Files.write(Paths.get(destino), errores, StandardCharsets.UTF_8);
		System.out.println(errores.size() + " errores guardados en " + destino);

	}

	// =====================================================================
	// EJERCICIO 3: temperaturas en fichero BINARIO
	// Cada registro ocupa siempre lo mismo: int (4 bytes) + double (8) + double (8) = 20 bytes
	// =====================================================================

	static final String FICHERO_TEMPERATURAS = "temperaturas.dat";
	static final int TAM_REGISTRO = 4 + 8 + 8;

	static class Registro {

		private int dia;
		private double maxima;
		private double minima;

		Registro(int dia, double maxima, double minima) {
			this.dia = dia;
			this.maxima = maxima;
			this.minima = minima;
		}

		int getDia() { return dia; }
		double getMaxima() { return maxima; }
		double getMinima() { return minima; }

	}

	// Lee todos los registros. Si el fichero no existe devuelve una lista vacía.
	// No sabemos cuántos hay: se lee hasta que salta EOFException (fin de fichero).
	static List<Registro> leerRegistros(String fichero) throws IOException {

		List<Registro> registros = new ArrayList<>();
		if (!existe(fichero)) {
			return registros;
		}

		try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fichero)))) {
			while (true) {
				registros.add(new Registro(in.readInt(), in.readDouble(), in.readDouble()));
			}
		} catch (EOFException fin) {
			// fin normal del fichero
		}
		return registros;

	}

	// Devuelve el mensaje de error o null si los datos son correctos (lo usan registrar y corregir)
	static String validar(int dia, double maxima, double minima) {

		if (dia < 1 || dia > 31) {
			return "El día debe estar entre 1 y 31";
		}
		if (maxima < minima) {
			return "La máxima (" + maxima + ") no puede ser menor que la mínima (" + minima + ")";
		}
		return null;

	}

	// Funciones del enunciado: trabajan siempre sobre temperaturas.dat
	public static void registrarTemperatura(int dia, double maxima, double minima) throws IOException {
		registrarTemperatura(FICHERO_TEMPERATURAS, dia, maxima, minima);
	}

	public static void listarTemperaturas() throws IOException {
		listarTemperaturas(FICHERO_TEMPERATURAS);
	}

	public static void corregirTemperatura(int dia, double maxima, double minima) throws IOException {
		corregirTemperatura(FICHERO_TEMPERATURAS, dia, maxima, minima);
	}

	static void registrarTemperatura(String fichero, int dia, double maxima, double minima) throws IOException {

		String error = validar(dia, maxima, minima);
		if (error != null) {
			System.out.println("Error: " + error);
			return;
		}
		for (Registro r : leerRegistros(fichero)) {
			if (r.getDia() == dia) {
				System.out.println("Error: el día " + dia + " ya tiene temperaturas registradas");
				return;
			}
		}

		// El segundo parámetro de FileOutputStream (true) es "append": añade al final sin borrar lo que hay
		try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fichero, true)))) {
			out.writeInt(dia);
			out.writeDouble(maxima);
			out.writeDouble(minima);
		}
		System.out.println("Registrado día " + dia + ": máx " + formato(maxima, 1) + ", mín " + formato(minima, 1));

	}

	static void listarTemperaturas(String fichero) throws IOException {

		List<Registro> registros = leerRegistros(fichero);
		if (registros.isEmpty()) {
			System.out.println("No hay temperaturas registradas");
			return;
		}

		// Los registros están en el orden en que se añadieron: los mostramos ordenados por día
		Collections.sort(registros, new Comparator<Registro>() {
			@Override
			public int compare(Registro a, Registro b) {
				return Integer.compare(a.getDia(), b.getDia());
			}
		});

		double sumaMaximas = 0;
		Registro masCaluroso = registros.get(0);
		for (Registro r : registros) {
			System.out.println(String.format(Locale.ROOT, "Día %02d | máx %s | mín %s | amplitud %s", r.getDia(),
					formato(r.getMaxima(), 1), formato(r.getMinima(), 1), formato(r.getMaxima() - r.getMinima(), 1)));
			sumaMaximas += r.getMaxima();
			if (r.getMaxima() > masCaluroso.getMaxima()) {
				masCaluroso = r;
			}
		}
		System.out.println("Media de máximas: " + formato(sumaMaximas / registros.size(), 2));
		System.out.println("Día más caluroso: " + masCaluroso.getDia() + " (" + formato(masCaluroso.getMaxima(), 1) + ")");

	}

	// Modifica un registro EN SU SITIO, sin reescribir el fichero entero, con RandomAccessFile:
	// como cada registro mide 20 bytes, el registro número n empieza en el byte n * 20.
	static void corregirTemperatura(String fichero, int dia, double maxima, double minima) throws IOException {

		String error = validar(dia, maxima, minima);
		if (error != null) {
			System.out.println("Error: " + error);
			return;
		}
		if (!existe(fichero)) {
			System.out.println("El día " + dia + " no tiene registro");
			return;
		}

		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {

			long totalRegistros = raf.length() / TAM_REGISTRO;
			for (long n = 0; n < totalRegistros; n++) {

				raf.seek(n * TAM_REGISTRO); // nos colocamos al principio del registro n
				if (raf.readInt() == dia) { // lo primero del registro es el día
					double antesMax = raf.readDouble();
					double antesMin = raf.readDouble();
					raf.seek(n * TAM_REGISTRO + 4); // saltamos el int del día (4 bytes) y sobrescribimos los dos doubles
					raf.writeDouble(maxima);
					raf.writeDouble(minima);
					System.out.println("Día " + dia + " corregido (antes: máx " + formato(antesMax, 1) + ", mín "
							+ formato(antesMin, 1) + "; ahora: máx " + formato(maxima, 1) + ", mín " + formato(minima, 1) + ")");
					return;
				}

			}

		}
		System.out.println("El día " + dia + " no tiene registro");

	}

}