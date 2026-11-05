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
			String jpql1 = "SELECT p.nombre FROM Producto p";
			List<String> lista1 = em.createQuery(jpql1,String.class).getResultList();
			for(String s:lista1) {
				System.out.println(s);
			}
			System.out.println("-------------------------------------");
			
			// Vv Por si quieres recuperar varias columnas a la vez vV \\
			String jpql2 = "SELECT p.nombre, p.precio FROM Producto p";
			List<Object[]> lista2 = em.createQuery(jpql2,Object[].class).getResultList();
			// La primera será un String y la segunda un double \\
			// Los tipos del syso tienen que coincidir con lo que estás sacando \\
			for(Object[] o :lista2) {
				System.out.println((String)o[0] + " - " + (Double)o[1]);
			}
			System.out.println("-------------------------------------");
			
			// Vv Esto es para imprimir todas las columnas vV
			String jpql3 = "FROM Producto p";
			List<Object[]> lista3 = em.createQuery(jpql3,Object[].class).getResultList();
			for(Object[] o:lista3) {
				int i=0;
				System.out.println(o[i]);
				i++;
			}
			System.out.println("-------------------------------------");
			
			// Para introducir un valor de un valor externo \\
			// Poner ':' antes de lo que queramos, parecido a MySql con el ? \\
			String jpql4 = "FROM Producto p WHERE p.precio > :precioMax AND p.nombre LIKE :nombre";
			List<Producto> lista4 = em.createQuery(jpql4,Producto.class)
					.setParameter("precioMax", 100)
					.setParameter("nombre", "Cas%")
					.getResultList();
			
			// Esto es con otros nombres \\
//			String jpql4 = "FROM Producto p WHERE p.precio > ?1 AND p.nombre LIKE ?2";
//			List<Producto> lista4 = em.createQuery(jpql4,Producto.class)
//					.setParameter(1, 100)
//					.setParameter(2, "Cas%")
//					.getResultList();
			for(Producto p:lista4) {
				System.out.println(p);
			}
			System.out.println("-------------------------------------");
			
			System.out.println("Total de productos en la tabla: " + lista.size());
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		em.close();
		emf.close();
	}

}
