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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			verEmpleados(em);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		em.close();
		emf.close();
	}

	public static void verEmpleados(EntityManager em) {
		
		String jpql = "SELECT e.firstName, e.lastName, e.email FROM Employees e";
		List<Object[]> listaEmpleados = em.createQuery(jpql, Object[].class).getResultList();

		if (listaEmpleados.isEmpty())
			System.out.println("No hay empleados");
		else {
			for (Object[] fila : listaEmpleados) {
				System.out.println("Nombre: " + fila[0] + ", " + fila[1] + " Email: " + fila[2]);
			}
		}
	}

}