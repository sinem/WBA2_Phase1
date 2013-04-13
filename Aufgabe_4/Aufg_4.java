package Aufgabe4;

import Aufgabe4.Recipe;
import Aufgabe4.ObjectFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;



public class Aufg_4 {

	private static final String RECIPE = "src/Aufgabe_3/Aufg_3c.xsd";

	public static void main(String[] args) throws JAXBException, IOException {
		

		JAXBContext context = JAXBContext.newInstance(Recipe.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	    
	    Unmarshaller um = context.createUnmarshaller();
	    Recipe recipe = (Recipe) um.unmarshal(new FileReader(RECIPE));
	    
	    
	      System.out.println("Rezept: " + recipe.rezept.getTitel());
	      System.out.println("Zutaten:");
	      System.out.println( recipe.rezept.zutaten.getZutat());
	      System.out.println("Zubereitung: " + recipe.rezept.zubereitung.getValue());
	      System.out.println("Schwierigkeitsgrad: " + recipe.rezept.schwierigkeitsgrad.getValue() + " Abeitszeit: " + recipe.rezept.arbeitszeit.getValue() + " Brennwert: " + recipe.rezept.brennwert.getValue());
	      System.out.println("Kommentare:");

	      System.out.println(recipe.rezept.kommentar.getKommentar());

	      int a;
		  Scanner scanner = new Scanner(System.in);
		  System.out.println("Möchten Sie einen Kommentar hinzufügen?(1/0)");
		  a = scanner.nextInt();
		  if(a==1){
			  String b;
			  System.out.println("Geben sie ihren Kommentar ein.");
			  b = scanner.nextLine();
		  }
	}
}