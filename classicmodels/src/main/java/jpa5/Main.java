package jpa5;

	import java.util.List;
	import java.util.logging.Level;
	import java.util.logging.Logger;
	import jakarta.persistence.EntityManager;
	import jakarta.persistence.EntityManagerFactory;
	import jakarta.persistence.Persistence;
	import model.*;
	public class Main {
		
	private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
		
	public static void main(String[] args) {
	LOG_HIBERNATE.setLevel(Level.SEVERE);
	EntityManagerFactory emf =
	Persistence.createEntityManagerFactory("clasePU");
	EntityManager em = emf.createEntityManager();
	try {
	listadoNombresPaises(em);
	} catch (Exception e) {
	System.out.println("Error: " + e.getMessage());
	}
	em.close();
	emf.close();
	}
	public static void listadoNombresPaises(EntityManager em){
		String jpql = "SELECT e.firstName,e.lastName,e.email FROM Employees e";
		
		List<String> listaNombres = em.createQuery(jpql, String.class).getResultList();
		System.out.println("Datos de empleados");
		for (String nombre : listaNombres) {
		System.out.println(nombre);
		}
//		System.out.println("Total de paises: " + listaNombres.size());
		}

}
