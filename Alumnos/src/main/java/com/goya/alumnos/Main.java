package com.goya.alumnos;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
	private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
	public static void main(String[] args) {
		// Le decimos a hibernate que solo muestre los errores y nomensajes informativos
		LOG_HIBERNATE.setLevel(Level.SEVERE);
		
		añadirAlumno("16598452X","Martin Angel","Zvarych",20,1700.0);
		
	}

	public static void añadirAlumno(String d,String n,String a,int e,double m) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			
			em.persist(new Alumno(d,n,a,e,m));
			System.out.println("¡Alumno " + n + " añadido con éxito!");
			
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
		
	}
	
	public static void buscarPorNombre(String n) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			n+="%";
			String jpql = "FROM Alumno a WHERE a.nombre LIKE ?1";
			List<Alumno> lista = em.createQuery(jpql,Alumno.class).getResultList();
			for(Alumno a:lista)
				System.out.println(a);
			
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
		
	}
	
}
