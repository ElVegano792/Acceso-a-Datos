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

//		añadirAlumno("16598452X", "Martin Angel", "Zvarych", 20, 1700.0);
//		añadirAlumno("65962348Y","Jeremias Perez","Vasquez de la Cruz",19,0);
//		mostrarAlumnos();
//		buscarPorDNI("X12");
//		buscarPorNombre("Martin");
		buscarPorRangoEdad(19, 22);

	}

	public static void añadirAlumno(String d, String n, String a, int e, double m) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();

			em.persist(new Alumno(d, n, a, e, m));

			em.getTransaction().commit();
			System.out.println("¡Alumno " + n + " añadido con éxito!");
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}

	public static void mostrarAlumnos() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			String jpql = "FROM Alumno";
			List<Alumno> lista = em.createQuery(jpql, Alumno.class).getResultList();
			em.getTransaction().commit();
			System.out.println("=== ALUMNOS REGISTRADOS ===");
			for (Alumno a : lista)
				System.out.println(a);
			System.out.println("-----------------");
			System.out.println("Total alumnos: " + lista.size());
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}

	}

	public static void buscarPorNombre(String n) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			String nombre = n + "%";
			String jpql = "FROM Alumno a WHERE a.nombre LIKE ?1";
			List<Alumno> lista = em.createQuery(jpql, Alumno.class).setParameter(1, nombre).getResultList();
			em.getTransaction().commit();
			for (Alumno a : lista)
				System.out.println(a);
			System.out.println("-----------------");
			System.out.println("Total alumnos: " + lista.size());
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}

	public static void buscarPorDNI(String d) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			String jpql = "FROM Alumno a WHERE a.dni LIKE ?1";
			List<Alumno> lista = em.createQuery(jpql, Alumno.class)
					.setParameter(1, d)
					.getResultList();
			if (lista.isEmpty() == false)
				for (Alumno a : lista)
					System.out.println(a);
			else {
				System.out.println("No hay coincidencias con el DNI " + d + ".");
			}
			em.getTransaction().commit();
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();

	}
	
	public static void buscarPorEdad(int e) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			String jpql = "FROM Alumno a WHERE a.edad LIKE ?1";
			List<Alumno> lista = em.createQuery(jpql, Alumno.class)
					.setParameter(1, e)
					.getResultList();
			em.getTransaction().commit();
			if (lista.isEmpty() == false)
				for (Alumno a : lista)
					System.out.println(a);
			else {
				System.out.println("No hay ningún alumnos que tenga " + e + " años.");
			}
			System.out.println("-----------------");
			System.out.println("Total alumnos: " + lista.size());
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
		
	}
	
	public static void buscarPorRangoEdad(int eMin,int eMax) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			String jpql = "FROM Alumno a WHERE a.edad BETWEEN ?1 AND ?2";
			List<Alumno> lista = em.createQuery(jpql, Alumno.class)
					.setParameter(1, eMin)
					.setParameter(2, eMax)
					.getResultList();
			
			em.getTransaction().commit();
			if (lista.isEmpty() == false)
				for (Alumno a : lista)
					System.out.println(a);
			else {
				System.out.println("No hay ningún alumnos que tenga entre " + eMin + " y " + eMax + " años.");
			}
			System.out.println("-----------------");
			System.out.println("Total alumnos: " + lista.size());
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
		
	}
	
}
