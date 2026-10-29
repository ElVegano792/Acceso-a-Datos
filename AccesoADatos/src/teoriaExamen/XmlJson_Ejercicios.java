package teoriaExamen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PDF 3 - "Acceso a Datos: XML y JSON".
 * Resueltos: ejercicio 1 (agenda XML -> JSON), 2 (stock -> JSON), 3 (pokemons -> XML),
 * 4 (pokemons -> JSON) y 5 (alta de pokemon con sobrecarga de métodos).
 *
 * JSON con GSON (com.google.gson:gson). XML con DOM, que viene incluido en el JDK.
 *
 * Con Gson NO hay que construir el JSON "a mano" campo a campo: basta con tener una clase con los datos
 * y llamar a gson.toJson(objeto) / gson.fromJson(texto, tipo). Los nombres de los campos de la clase
 * pasan a ser las claves del JSON.
 * Uso clases normales y no records para que funcione con cualquier versión de Gson
 * (los records solo se deserializan bien desde la 2.10).
 *
 * ESTRATEGIA MODULAR: texto -> lista de objetos (UNA sola función de lectura)
 *                           -> escribirXml(lista) o escribirJson(lista)
 * Así los ejercicios 3, 4 y 5 comparten el 80% del código.
 */
public class XmlJson_Ejercicios {

    // ---------------------------------------------------------------------
    // MODELO (los campos privados se serializan igualmente: Gson usa reflexión sobre los campos)
    // ---------------------------------------------------------------------
    static class Pokemon {
        private final int numero;
        private final String nombre;
        private final double peso;
        private final double altura;
        private final List<String> tipos;

        Pokemon(int numero, String nombre, double peso, double altura, List<String> tipos) {
            this.numero = numero;
            this.nombre = nombre;
            this.peso = peso;
            this.altura = altura;
            this.tipos = tipos;
        }

        int numero() { return numero; }
        String nombre() { return nombre; }
        double peso() { return peso; }
        double altura() { return altura; }
        List<String> tipos() { return tipos; }
    }

    static class ArticuloStock {
        private final String referencia;
        private final String descripcion;
        private final int stock;

        ArticuloStock(String referencia, String descripcion, int stock) {
            this.referencia = referencia;
            this.descripcion = descripcion;
            this.stock = stock;
        }
    }

    /**
     * Contacto de la agenda. El orden de los campos es el orden en que saldrán en el JSON.
     * Los nombres de los campos son los nombres de las claves JSON: si en clase usasteis otros
     * (por ejemplo "telefonos" -> "numeros"), cámbialos aquí y ya está.
     */
    static class Contacto {
        private final String nombre;
        private final String dni;                // el XML no lo tiene: se deja en blanco
        private final List<String> telefonos;    // un contacto puede tener varios <telefono>
        private final String tipo;

        Contacto(String nombre, String dni, List<String> telefonos, String tipo) {
            this.nombre = nombre;
            this.dni = dni;
            this.telefonos = telefonos;
            this.tipo = tipo;
        }
    }

    // ---------------------------------------------------------------------
    // GSON: UNA sola instancia reutilizable. setPrettyPrinting = JSON con sangría legible.
    // disableHtmlEscaping evita que Gson convierta caracteres como < o = en <, =.
    // ---------------------------------------------------------------------
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** Escribe CUALQUIER objeto (o lista de objetos) como JSON. La usan los ejercicios 2, 4 y 5. */
    static void guardarJson(Object datos, String fichero) throws IOException {
        Files.writeString(Path.of(fichero), GSON.toJson(datos));
    }

    /** Lee un fichero JSON que contiene una lista de pokemons. Devuelve lista vacía si el fichero está en blanco. */
    public static List<Pokemon> leerJsonPokemons(String fichero) throws IOException {
        // TypeToken es necesario porque List<Pokemon> pierde su tipo genérico en tiempo de ejecución.
        Type tipoLista = new TypeToken<List<Pokemon>>() { }.getType();
        List<Pokemon> lista = GSON.fromJson(Files.readString(Path.of(fichero)), tipoLista);
        return lista == null ? new ArrayList<>() : lista;           // fromJson devuelve null con texto vacío
    }

    // =====================================================================
    // EJERCICIO 1: agenda XML -> JSON (el DNI, que el XML no tiene, queda en blanco)
    // =====================================================================

    /** Abre y parsea un fichero XML. La usan este ejercicio y el alta en XML del ejercicio 5. */
    public static Document abrirXml(String fichero) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(fichero));
    }

    /**
     * Textos de TODOS los hijos DIRECTOS con ese nombre (un contacto puede tener varios <telefono>).
     * Se recorre con getFirstChild/getNextSibling y se filtra por tipo ELEMENT_NODE porque entre las
     * etiquetas hay nodos de texto con solo espacios y saltos de línea (como en agenda.xml) que NO queremos.
     */
    public static List<String> textosHijos(Element padre, String etiqueta) {
        List<String> textos = new ArrayList<>();
        for (Node hijo = padre.getFirstChild(); hijo != null; hijo = hijo.getNextSibling()) {
            if (hijo.getNodeType() == Node.ELEMENT_NODE && hijo.getNodeName().equals(etiqueta)) {
                textos.add(hijo.getTextContent().trim());
            }
        }
        return textos;
    }

    /** Texto del primer hijo con ese nombre, o "" si no existe. Reutiliza textosHijos. */
    public static String textoHijo(Element padre, String etiqueta) {
        List<String> textos = textosHijos(padre, etiqueta);
        return textos.isEmpty() ? "" : textos.get(0);
    }

    public static Contacto contactoDesdeElemento(Element e) {
        return new Contacto(textoHijo(e, "nombre"), "", textosHijos(e, "telefono"), textoHijo(e, "tipo"));
    }

    public static List<Contacto> leerAgendaXml(String fichero) throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        NodeList elementos = abrirXml(fichero).getElementsByTagName("contacto");
        for (int i = 0; i < elementos.getLength(); i++) {
            contactos.add(contactoDesdeElemento((Element) elementos.item(i)));
        }
        return contactos;
    }

    public static void agendaAJson(String origen, String destino) throws Exception {
        guardarJson(leerAgendaXml(origen), destino);                // misma guardarJson que los demás ejercicios
        System.out.println("Agenda convertida a JSON en " + destino);
    }

    // =====================================================================
    // EJERCICIO 2: fichero de stock (3 líneas por artículo) -> JSON
    // =====================================================================

    /** Lee grupos de 3 líneas: referencia, descripción, stock. */
    public static List<ArticuloStock> leerStock(String fichero) throws IOException {
        List<String> lineas = Files.readAllLines(Path.of(fichero));
        List<ArticuloStock> articulos = new ArrayList<>();
        // i += 3 porque cada artículo ocupa exactamente tres líneas
        for (int i = 0; i + 2 < lineas.size(); i += 3) {
            articulos.add(new ArticuloStock(
                    lineas.get(i).trim(),
                    lineas.get(i + 1).trim(),
                    Integer.parseInt(lineas.get(i + 2).trim())));
        }
        return articulos;
    }

    public static void stockAJson(String origen, String destino) throws IOException {
        guardarJson(leerStock(origen), destino);                    // una línea: Gson convierte la lista entera
        System.out.println("Stock convertido a JSON en " + destino);
    }

    // =====================================================================
    // LECTURA COMÚN DE POKEMONS (ejercicios 3, 4 y 5)
    // Formato de línea: 163, Hoothoot, 21.2, 0.7, Normal, Volador
    // =====================================================================

    public static List<Pokemon> leerPokemons(String fichero) throws IOException {
        List<Pokemon> lista = new ArrayList<>();
        for (String linea : Files.readAllLines(Path.of(fichero))) {
            if (linea.isBlank()) continue;
            String[] partes = linea.split(",");
            if (partes.length < 5) continue;                       // línea incompleta: se ignora
            List<String> tipos = new ArrayList<>();
            for (int i = 4; i < partes.length; i++) {              // del 5º campo en adelante: 1 o 2 tipos
                tipos.add(partes[i].trim());
            }
            lista.add(new Pokemon(
                    Integer.parseInt(partes[0].trim()),
                    partes[1].trim(),
                    Double.parseDouble(partes[2].trim()),
                    Double.parseDouble(partes[3].trim()),
                    tipos));
        }
        return lista;
    }

    // =====================================================================
    // EJERCICIO 3: pokemons -> XML (DOM)
    // =====================================================================

    /** Crea un documento XML vacío con el elemento raíz indicado. */
    public static Document nuevoDocumento(String raiz) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        doc.appendChild(doc.createElement(raiz));
        return doc;
    }

    /** Crea <nombre>valor</nombre>. Evita repetir 3 líneas por cada campo. */
    public static Element elementoTexto(Document doc, String nombre, String valor) {
        Element e = doc.createElement(nombre);
        e.setTextContent(valor);
        return e;
    }

    /**
     * Convierte un Pokemon en su elemento XML.
     * Los tipos van en UNA sola línea: <tipo>Bicho, Veneno</tipo> (así lo pide el enunciado).
     */
    public static Element pokemonAElemento(Document doc, Pokemon p) {
        Element e = doc.createElement("pokemon");
        e.appendChild(elementoTexto(doc, "numero", String.valueOf(p.numero())));
        e.appendChild(elementoTexto(doc, "nombre", p.nombre()));
        e.appendChild(elementoTexto(doc, "peso", String.valueOf(p.peso())));
        e.appendChild(elementoTexto(doc, "altura", String.valueOf(p.altura())));
        e.appendChild(elementoTexto(doc, "tipo", String.join(", ", p.tipos())));
        return e;
    }

    /** Vuelca un documento DOM en un fichero con sangría. */
    public static void guardarXml(Document doc, String fichero) throws Exception {
        quitarEspaciosVacios(doc.getDocumentElement());            // evita líneas en blanco acumuladas
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(new File(fichero)));
    }

    /** Borra los nodos de texto que solo tienen espacios/saltos (los que mete un XML ya indentado). */
    public static void quitarEspaciosVacios(Node nodo) {
        for (int i = nodo.getChildNodes().getLength() - 1; i >= 0; i--) {
            Node hijo = nodo.getChildNodes().item(i);
            if (hijo.getNodeType() == Node.TEXT_NODE && hijo.getTextContent().isBlank()) {
                nodo.removeChild(hijo);
            } else {
                quitarEspaciosVacios(hijo);
            }
        }
    }

    public static void escribirXml(List<Pokemon> pokemons, String fichero) throws Exception {
        Document doc = nuevoDocumento("pokedex");
        for (Pokemon p : pokemons) {
            doc.getDocumentElement().appendChild(pokemonAElemento(doc, p));
        }
        guardarXml(doc, fichero);
    }

    public static void pokemonsAXml(String origen, String destino) throws Exception {
        escribirXml(leerPokemons(origen), destino);
        System.out.println("Pokemons convertidos a XML en " + destino);
    }

    // =====================================================================
    // EJERCICIO 4: pokemons -> JSON (reutiliza leerPokemons y guardarJson)
    // =====================================================================

    public static void escribirJson(List<Pokemon> pokemons, String fichero) throws IOException {
        guardarJson(pokemons, fichero);                             // en JSON "tipos" sale como array: ["Bicho","Veneno"]
    }

    public static void pokemonsAJson(String origen, String destino) throws IOException {
        escribirJson(leerPokemons(origen), destino);
        System.out.println("Pokemons convertidos a JSON en " + destino);
    }

    // =====================================================================
    // EJERCICIO 5: alta de un pokemon (1 o 2 tipos) en XML o JSON
    // Todo por parámetros y SOBRECARGA de métodos (nada de Scanner).
    // =====================================================================

    /** Versión con UN tipo. Delegará en la versión principal. */
    public static void altaPokemon(int numero, String nombre, double peso, double altura,
                                   String tipo, String formato, String fichero) throws Exception {
        altaPokemon(new Pokemon(numero, nombre, peso, altura, List.of(tipo)), formato, fichero);
    }

    /** Versión con DOS tipos (mismo nombre, distinto número de parámetros = sobrecarga). */
    public static void altaPokemon(int numero, String nombre, double peso, double altura,
                                   String tipo1, String tipo2, String formato, String fichero) throws Exception {
        altaPokemon(new Pokemon(numero, nombre, peso, altura, Arrays.asList(tipo1, tipo2)), formato, fichero);
    }

    /** Versión principal: decide en qué estructura se guarda. Las otras dos solo construyen el objeto. */
    public static void altaPokemon(Pokemon p, String formato, String fichero) throws Exception {
        switch (formato.toUpperCase()) {
            case "XML" -> anyadirAXml(p, fichero);
            case "JSON" -> anyadirAJson(p, fichero);
            default -> System.out.println("Formato no válido: " + formato + " (usa XML o JSON)");
        }
    }

    /** Si el fichero existe lo abre y añade al final; si no, lo crea desde cero. */
    static void anyadirAXml(Pokemon p, String fichero) throws Exception {
        Document doc;
        if (Files.exists(Path.of(fichero))) {
            doc = abrirXml(fichero);
        } else {
            doc = nuevoDocumento("pokedex");
        }
        doc.getDocumentElement().appendChild(pokemonAElemento(doc, p));
        guardarXml(doc, fichero);
        System.out.println(p.nombre() + " añadido a " + fichero);
    }

    /** Con Gson: leer la lista completa, añadir el objeto y volver a escribir la lista entera. */
    static void anyadirAJson(Pokemon p, String fichero) throws IOException {
        List<Pokemon> pokemons = Files.exists(Path.of(fichero))
                ? leerJsonPokemons(fichero)
                : new ArrayList<>();
        pokemons.add(p);
        guardarJson(pokemons, fichero);
        System.out.println(p.nombre() + " añadido a " + fichero);
    }

    // =====================================================================
    // PRUEBAS RÁPIDAS: crea los ficheros de ejemplo del enunciado y los convierte
    // =====================================================================
    public static void main(String[] args) throws Exception {
        Files.writeString(Path.of("stock.txt"),
                "T-3456-23\nTornillos cabeza plana 50mm\n3450\nE-4544-12\nEscuadra aluminio 10cm\n350\n");
        Files.writeString(Path.of("pokemons.txt"),
                "152, Chikorita, 6.4, 0.9, Planta\n158, Totodile, 9.5, 0.6, Agua\n"
                        + "163, Hoothoot, 21.2, 0.7, Normal, Volador\n155, Cyndaquil, 7.9, 0.5, Fuego\n"
                        + "167, Spinarak, 8.5, 0.5, Bicho, Veneno\n");

        Files.writeString(Path.of("agenda.xml"),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<agenda>\n"
                        + "  <contacto><nombre>José María</nombre><telefono>6598473215</telefono><tipo>fijo</tipo></contacto>\n"
                        + "  <contacto><nombre>Leonor</nombre><telefono>568466568</telefono>"
                        + "<telefono>416398487</telefono><tipo>fijo</tipo></contacto>\n</agenda>\n");

        agendaAJson("agenda.xml", "agenda.json");       // ejercicio 1
        stockAJson("stock.txt", "stock.json");          // ejercicio 2
        pokemonsAXml("pokemons.txt", "pokemons.xml");   // ejercicio 3
        pokemonsAJson("pokemons.txt", "pokemons.json"); // ejercicio 4

        // ejercicio 5: un tipo / dos tipos, XML / JSON
        altaPokemon(25, "Pikachu", 6.0, 0.4, "Eléctrico", "JSON", "pokemons.json");
        altaPokemon(6, "Charizard", 90.5, 1.7, "Fuego", "Volador", "XML", "pokemons.xml");
    }
}
