package teoriaExamen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * SOLUCIÓN del Examen de prueba 3 - XML (DOM) y JSON (Gson).
 * !! No la mires hasta haber intentado el examen !!
 *
 * Ejercicio 1: catalogo.xml -> catalogo.json
 * Ejercicio 2: estadísticas leyendo el XML
 * Ejercicio 3: catalogo.json -> XML filtrado por año y ordenado por título
 * Ejercicio 4: préstamos en prestamos.json con reglas de validación
 */
public class Examen3XmlJson {

	public static void main(String[] args) throws Exception {

		System.out.println("===== EJERCICIO 1 =====");
		catalogoAJson("catalogo.xml", "catalogo.json");
		catalogoAJson("no_existe.xml", "x.json");

		System.out.println("\n===== EJERCICIO 2 =====");
		estadisticas("catalogo.xml");

		System.out.println("\n===== EJERCICIO 3 =====");
		jsonAXml("catalogo.json", "libros_filtrados.xml", 1985);
		jsonAXml("catalogo.json", "nada.xml", 2030);

		System.out.println("\n===== EJERCICIO 4 =====");
		listarPrestamos();
		prestar("LIB-002", "S01", "2025-03-20");
		prestar("LIB-002", "S03", "2025-03-21"); // ya prestado
		prestar("LIB-004", "S02", "2025-03-21"); // S02 ya tiene 2 libros
		prestar("LIB-999", "S03", "2025-03-21"); // no existe en el catálogo
		prestar("LIB-004", "S03", "21/03/2025"); // fecha mal escrita
		prestar("LIB-004", "S03", "2025-02-30"); // fecha inexistente
		devolver("LIB-001");
		devolver("LIB-001");
		listarPrestamos();

	}

	// =====================================================================
	// CLASES PARA GSON
	// =====================================================================

	static class Libro {

		String isbn;
		String titulo;
		List<String> autores = new ArrayList<>();
		int anio;
		double precio;

	}

	// Clase contenedora: el JSON es un objeto {"libros":[...]} y no una lista suelta
	static class Catalogo {
		List<Libro> libros = new ArrayList<>();
	}

	static class Prestamo {

		String isbn;
		String socio;
		String fecha;

		Prestamo(String isbn, String socio, String fecha) {
			this.isbn = isbn;
			this.socio = socio;
			this.fecha = fecha;
		}

	}

	static class Prestamos {
		List<Prestamo> prestamos = new ArrayList<>();
	}

	// =====================================================================
	// FUNCIONES REUTILIZABLES: XML (DOM)
	// =====================================================================

	// Lee un XML y devuelve el Document. Si no existe devuelve null.
	static Document leerXML(String fichero) throws Exception {

		File f = new File(fichero);
		if (!f.exists()) {
			return null;
		}
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
		doc.getDocumentElement().normalize();
		return doc;

	}

	// Guarda un Document con sangría de 4 espacios
	static void grabarXML(Document doc, String fichero) throws Exception {

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		t.transform(new DOMSource(doc), new StreamResult(new File(fichero)));

	}

	// Texto de la PRIMERA etiqueta hija con ese nombre (así se evitan los nodos de texto en blanco)
	static String textoHijo(Element padre, String etiqueta) {
		return padre.getElementsByTagName(etiqueta).item(0).getTextContent().trim();
	}

	// Convierte el XML completo en una lista de Libro
	static List<Libro> libroDesdeXML(Document doc) {

		List<Libro> libros = new ArrayList<>();
		NodeList nodos = doc.getElementsByTagName("libro");

		for (int i = 0; i < nodos.getLength(); i++) {
			Element e = (Element) nodos.item(i);
			Libro l = new Libro();
			l.isbn = e.getAttribute("isbn"); // atributo: getAttribute, no es una etiqueta hija
			l.titulo = textoHijo(e, "titulo");
			NodeList autores = e.getElementsByTagName("autor"); // puede haber varios
			for (int j = 0; j < autores.getLength(); j++) {
				l.autores.add(autores.item(j).getTextContent().trim());
			}
			l.anio = Integer.parseInt(textoHijo(e, "anio"));
			l.precio = Double.parseDouble(textoHijo(e, "precio"));
			libros.add(l);
		}
		return libros;

	}

	// =====================================================================
	// FUNCIONES REUTILIZABLES: JSON (Gson)
	// =====================================================================

	static void guardarJSON(Object objeto, String fichero) throws IOException {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter w = new FileWriter(fichero)) {
			gson.toJson(objeto, w);
		}

	}

	// Lee un JSON y lo convierte a la clase indicada. Si el fichero no existe devuelve null.
	static <T> T cargarJSON(String fichero, Class<T> clase) throws IOException {

		if (!new File(fichero).exists()) {
			return null;
		}
		try (FileReader r = new FileReader(fichero)) {
			return new Gson().fromJson(r, clase);
		}

	}

	static String formato(double valor) {
		return String.format(Locale.ROOT, "%.2f", valor);
	}

	// =====================================================================
	// EJERCICIO 1: XML -> JSON
	// =====================================================================

	public static void catalogoAJson(String xml, String json) throws Exception {

		Document doc = leerXML(xml);
		if (doc == null) {
			System.out.println("El fichero " + xml + " no existe");
			return;
		}
		Catalogo catalogo = new Catalogo();
		catalogo.libros = libroDesdeXML(doc);
		guardarJSON(catalogo, json);
		System.out.println(catalogo.libros.size() + " libros convertidos a " + json);

	}

	// =====================================================================
	// EJERCICIO 2: estadísticas
	// =====================================================================

	public static void estadisticas(String xml) throws Exception {

		Document doc = leerXML(xml);
		if (doc == null) {
			System.out.println("El fichero " + xml + " no existe");
			return;
		}
		List<Libro> libros = libroDesdeXML(doc);
		if (libros.isEmpty()) {
			System.out.println("El catálogo está vacío");
			return;
		}

		double suma = 0;
		Libro masCaro = libros.get(0);
		Libro masAntiguo = libros.get(0);
		Map<String, Integer> librosPorAutor = new TreeMap<>(); // TreeMap: el empate lo gana el autor que va antes
		List<String> variosAutores = new ArrayList<>();

		for (Libro l : libros) {
			suma += l.precio;
			if (l.precio > masCaro.precio) {
				masCaro = l;
			}
			if (l.anio < masAntiguo.anio) {
				masAntiguo = l;
			}
			for (String autor : l.autores) {
				Integer n = librosPorAutor.get(autor);
				librosPorAutor.put(autor, n == null ? 1 : n + 1);
			}
			if (l.autores.size() > 1) {
				variosAutores.add(l.titulo);
			}
		}

		String autorTop = null;
		int max = 0;
		for (Map.Entry<String, Integer> e : librosPorAutor.entrySet()) {
			if (e.getValue() > max) {
				max = e.getValue();
				autorTop = e.getKey();
			}
		}

		System.out.println("Libros: " + libros.size());
		System.out.println("Precio medio: " + formato(suma / libros.size()));
		System.out.println("Libro más caro: " + masCaro.titulo + " (" + formato(masCaro.precio) + ")");
		System.out.println("Libro más antiguo: " + masAntiguo.titulo + " (" + masAntiguo.anio + ")");
		System.out.println("Autor con más libros: " + autorTop + " (" + max + ")");
		System.out.println("Libros con varios autores: " + String.join(", ", variosAutores));

	}

	// =====================================================================
	// EJERCICIO 3: JSON -> XML filtrado y ordenado
	// =====================================================================

	public static void jsonAXml(String json, String xml, int anioDesde) throws Exception {

		Catalogo catalogo = cargarJSON(json, Catalogo.class);
		if (catalogo == null) {
			System.out.println("El fichero " + json + " no existe");
			return;
		}

		List<Libro> elegidos = new ArrayList<>();
		for (Libro l : catalogo.libros) {
			if (l.anio >= anioDesde) {
				elegidos.add(l);
			}
		}
		if (elegidos.isEmpty()) {
			System.out.println("Ningún libro publicado desde " + anioDesde + ": no se crea " + xml);
			return;
		}
		Collections.sort(elegidos, new Comparator<Libro>() {
			@Override
			public int compare(Libro a, Libro b) {
				return a.titulo.compareToIgnoreCase(b.titulo);
			}
		});

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element raiz = doc.createElement("catalogo");
		doc.appendChild(raiz);
		for (Libro l : elegidos) {
			raiz.appendChild(libroAElemento(doc, l));
		}
		grabarXML(doc, xml);
		System.out.println(elegidos.size() + " libros publicados desde " + anioDesde + " guardados en " + xml);

	}

	// Crea <libro isbn="..."> con sus hijos. crearHijo evita repetir createElement/setTextContent/appendChild.
	static Element libroAElemento(Document doc, Libro l) {

		Element libro = doc.createElement("libro");
		libro.setAttribute("isbn", l.isbn);
		crearHijo(doc, libro, "titulo", l.titulo);
		for (String autor : l.autores) {
			crearHijo(doc, libro, "autor", autor);
		}
		crearHijo(doc, libro, "anio", String.valueOf(l.anio));
		crearHijo(doc, libro, "precio", formato(l.precio));
		return libro;

	}

	static void crearHijo(Document doc, Element padre, String etiqueta, String texto) {

		Element hijo = doc.createElement(etiqueta);
		hijo.setTextContent(texto);
		padre.appendChild(hijo);

	}

	// =====================================================================
	// EJERCICIO 4: préstamos
	// =====================================================================

	static final String FICHERO_PRESTAMOS = "prestamos.json";
	static final String FICHERO_CATALOGO = "catalogo.xml";
	static final int MAX_POR_SOCIO = 2;

	// Carga los préstamos; si el fichero no existe empieza con una lista vacía
	static Prestamos cargarPrestamos() throws IOException {

		Prestamos p = cargarJSON(FICHERO_PRESTAMOS, Prestamos.class);
		return p == null ? new Prestamos() : p;

	}

	// Devuelve el título del libro o null si el isbn no está en el catálogo
	static String tituloDeLibro(String isbn) throws Exception {

		Document doc = leerXML(FICHERO_CATALOGO);
		if (doc == null) {
			return null;
		}
		for (Libro l : libroDesdeXML(doc)) {
			if (l.isbn.equals(isbn)) {
				return l.titulo;
			}
		}
		return null;

	}

	static boolean fechaValida(String fecha) {

		try {
			LocalDate.parse(fecha); // exige yyyy-MM-dd y que el día exista (rechaza 2025-02-30)
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}

	}

	public static void prestar(String isbn, String socio, String fecha) throws Exception {

		String titulo = tituloDeLibro(isbn);
		if (titulo == null) {
			System.out.println("Error: el libro " + isbn + " no existe en el catálogo");
			return;
		}
		if (!fechaValida(fecha)) {
			System.out.println("Error: la fecha " + fecha + " no es válida (formato AAAA-MM-DD)");
			return;
		}

		Prestamos datos = cargarPrestamos();
		int delSocio = 0;
		for (Prestamo p : datos.prestamos) {
			if (p.isbn.equals(isbn)) {
				System.out.println("Error: el libro " + isbn + " ya está prestado al socio " + p.socio);
				return;
			}
			if (p.socio.equals(socio)) {
				delSocio++;
			}
		}
		if (delSocio >= MAX_POR_SOCIO) {
			System.out.println("Error: el socio " + socio + " ya tiene " + MAX_POR_SOCIO + " libros prestados");
			return;
		}

		datos.prestamos.add(new Prestamo(isbn, socio, fecha));
		guardarJSON(datos, FICHERO_PRESTAMOS);
		System.out.println("Prestado \"" + titulo + "\" al socio " + socio);

	}

	public static void devolver(String isbn) throws Exception {

		Prestamos datos = cargarPrestamos();
		for (int i = 0; i < datos.prestamos.size(); i++) {
			if (datos.prestamos.get(i).isbn.equals(isbn)) {
				datos.prestamos.remove(i);
				guardarJSON(datos, FICHERO_PRESTAMOS);
				System.out.println("Devuelto el libro " + isbn);
				return;
			}
		}
		System.out.println("Error: el libro " + isbn + " no estaba prestado");

	}

	public static void listarPrestamos() throws Exception {

		Prestamos datos = cargarPrestamos();
		if (datos.prestamos.isEmpty()) {
			System.out.println("No hay préstamos");
			return;
		}
		System.out.println("Préstamos (" + datos.prestamos.size() + "):");
		for (Prestamo p : datos.prestamos) {
			System.out.println("- " + tituloDeLibro(p.isbn) + " | socio " + p.socio + " | " + p.fecha);
		}

	}

}