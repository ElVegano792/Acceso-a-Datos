package com.dam2.goya;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPQL {
	
	private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
	
	public static void main(String[] args) {

		LOG_HIBERNATE.setLevel(Level.SEVERE);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			
//			String jpql = "FROM Producto";
			// Vv Para hacer un where vV \\
//			String jpql = "FROM Producto p WHERE p.precio > 100";
//			String jpql = "FROM Producto p WHERE p.nombre LIKE 'Teclado%'";
			String jpql = "FROM Producto p WHERE p.nombre LIKE 'Teclado%' ORDER BY p.precio DESC";
			List<Producto> lista = em.createQuery(jpql,Producto.class).getResultList();
			for(Producto p:lista) {
				System.out.println(p);
			}
			System.out.println("Total de productos en la tabla: " + lista.size());
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		em.close();
		emf.close();
	}

}
