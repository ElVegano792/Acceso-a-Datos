package com.dam2.goya;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	// Hibernate usa Sys.err en vez de Sys.out, por lo cual no confundir con error
// Atributo estático para deshabilitar el logger de hibernate
	private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");

	public static void main(String[] args) {
		// Le decimos a hibernate que solo muestre los errores y nomensajes informativos
		LOG_HIBERNATE.setLevel(Level.SEVERE);

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			Producto p = new Producto("Teclado", 25.5);
			em.persist(p);
//			Producto p;

			// Vv Para meter más productos vV \\
			p = new Producto("Cascos", 250.5);
			em.persist(p);
			p = new Producto("Fuente de alimentacion 500W", 80.0);
			em.persist(p);
			p = new Producto("Monitor 21", 179.99);
			em.persist(p);
			// Vv También se puede hacer esto para ahorrarte una línea vV \\
//			em.persist(new Producto("Monitor 21", 179.99));
			em.getTransaction().commit();
//			System.out.println("Producto guardado con id " + p.getId());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			// Por si salta una excepcion hacemos un rollback
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}
}