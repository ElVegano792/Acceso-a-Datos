package ejerciciosFicheros;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Ejercicio1 {

	public static void main(String[] args) {
		
		String ficheroPersonajes = "/home/alumno/personajes.txt";
		String ficheroAnimes = "/home/alumno/animes.txt";
		
		ArrayList<String> animeNaruto = new ArrayList<>();
		ArrayList<String> animeDan = new ArrayList<>();
		ArrayList<String> animeDemon = new ArrayList<>();
		ArrayList<String> animeOne = new ArrayList<>();
		ArrayList<String> animeHunter = new ArrayList<>();
		ArrayList<String> animeNulo = new ArrayList<>();
		
		
		try {
			FileReader archivo1 = new FileReader(ficheroPersonajes); // Personajes
			BufferedReader lector = new BufferedReader(archivo1);
			FileReader archivo2 = new FileReader(ficheroAnimes); // Animes
			BufferedReader lector2 = new BufferedReader(archivo2);
			
			String linea = lector.readLine();
			
			do {

				String personaje = "";
				String[] linea2 = linea.split(" ");
				
				for(int i=1;i<linea2.length;i++) {
					personaje+=linea2[i] + " ";
				}
				
				if(linea2[0].equals("17")) {
					animeNaruto.add(personaje);
				} else if(linea2[0].equals("22")) {
					animeDan.add(personaje);
				} else if(linea2[0].equals("4")) {
					animeDemon.add(personaje);
				} else if(linea2[0].equals("3")) {
					animeOne.add(personaje);
				} else if(linea2[0].equals("6")) {
					animeHunter.add(personaje);
				} else
					animeNulo.add(personaje);
				
				linea = lector.readLine();
			} while(linea != null);
			
			if(animeNaruto.size() >= 1) {
				System.out.println("Naruto");
				for(int i=0;i<animeNaruto.size();i++) {
					System.out.println("- "+animeNaruto.get(i));
				}
			}
			if(animeDan.size() >= 1) {
				System.out.println("Dan Da Dan");
				for(int i=0;i<animeDan.size();i++) {
					System.out.println("- "+animeDan.get(i));
				}
			}
			if(animeDemon.size() >= 1) {
				System.out.println("Demon Hunter");
				for(int i=0;i<animeDemon.size();i++) {
					System.out.println("- "+animeDemon.get(i));
				}
			}
			if(animeOne.size() >= 1) {
				System.out.println("One Piece");
				for(int i=0;i<animeOne.size();i++) {
					System.out.println("- "+animeOne.get(i));
				}
			}
			if(animeHunter.size() >= 1) {
				System.out.println("Hunter x Hunter");
				for(int i=0;i<animeHunter.size();i++) {
					System.out.println("- "+animeHunter.get(i));
				}
			}
			if(animeNulo.size() >= 1) {
				System.out.println("Personajes sin anime");
				for(int i=0;i<animeNulo.size();i++) {
					System.out.println("- "+animeNulo.get(i));
				}
			}
			
			archivo1.close();
			lector.close();
			archivo2.close();
			lector2.close();
		} catch(Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

}
