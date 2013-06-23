package Aufgabe_4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import generated.Rezepte;
import generated.Rezepte.Rezept;
import generated.Rezepte.Rezept.Kommentare;
import generated.Rezepte.Rezept.Kommentare.Kommentar;
import generated.Rezepte.Rezept.Kommentare.Kommentar.Benutzer;
import generated.Rezepte.Rezept.Zutaten.Zutat;
import generated.Rezepte.Rezept.Bilder.Bild;
import generated.Rezepte.Rezept.Vorbereitung;

/**
 * 
 *         Dieses Programm basiert auf dem Schema der Aufgabe 3 und ermšglicht
 *         es dem Nutzer ein Rezept anzusehen und einen Kommentar dazu
 *         abzugeben.
 *         </p>
 * 
 * 
 */

public class Main {

	public static InputStream in;

	/**
	 * @param args
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void main(String[] args) throws SAXException, JAXBException,
			NumberFormatException, IOException {
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));

		// "Holen" der Daten des Kochbuchs
		Rezepte rez = recipeUnmarshall();

		// Auswahl des Rezeptes
		int menuChoice = menu(rez) - 1;

		Rezept rezept = rez.getRezept().get(menuChoice);

		// Auswahl, was mit dem rezept gemacht werden soll
		int choice = submenu(rezept);
		switch (choice) {
		case 1:
			// Verfassen des neuen Kommentars
			Kommentare kom = addComment(rezept);

			// Anfuegen an das Rezept
			rezept.setKommentare(kom);

			// Uebergabe an das xml-File
			recipeMarshal("src/Aufgabe_3/Aufgabe3d.xsd", "src/Aufgabe_3/Aufg_3a.xml", rez);
		case 0:
			int portion = 0;
			do {
				System.out.println("Wie viele Portionen?");
				portion = Integer.parseInt(console.readLine());
			} while (portion <= 0);
			// Ausgabe in Konsole, incl. berechneter Zutatenmenge abhŠngig der
			// eingegebenen Portionen
			printRecipe(rezept, portion);
			break;
		default:
			System.out.print("Das Programm wird beendet, da sie der super Kuchen nicht interessiert hat.");
			System.exit(0);
		}

	}

	/**
	 * 
	 * @return
	 * @throws JAXBException
	 */
	private static int menu(Rezepte cb) throws JAXBException {
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		do {
			try {
				// Menue aller Rezepte
				for (Rezept r : cb.getRezept()) {
					System.out.println("Wollen Sie das Rezept \""
							+ r.getTitel() + "\" ansehen? (" + r.getId() + ")");
				}

				int code = Integer.parseInt(console.readLine());
				if ((code > 0)
						&& (code <= recipeUnmarshall().getRezept().size())) {
					return code;
				}
			} catch (IOException e) {
				// Sollte eigentlich nie passieren
				e.printStackTrace();
			}
		} while (true);
	}

	/**
	 * 
	 * @return
	 * @throws JAXBException
	 */
	private static int submenu(Rezept rezept) throws JAXBException {
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			System.out.println("Wollen Sie das Rezept \"" + rezept.getTitel()
					+ "\" ausgeben? (0)");
			System.out.println("Wollen Sie einen Kommentar zu dem Rezept \""
					+ rezept.getTitel() + "\" abgeben? (1)");
			System.out.println("Wollen Sie das Programm abbrechen? (2)");
			int code = Integer.parseInt(console.readLine());
			if ((code >= 0) && (code < 3)) {
				return code;
			}
		} catch (IOException e) {
			// Sollte eigentlich nie passieren
			e.printStackTrace();
		}
		return 2;
	}

	/**
	 * Helper-Class zum Einlesen des XML-Files
	 * 
	 * @return neues Rezept vom Typ Recipe
	 * @throws JAXBException
	 */
	private static Rezepte recipeUnmarshall() throws JAXBException {
		// Package der JAVA-Objekte des XML-Schemas
		JAXBContext jc = JAXBContext.newInstance("generated");
		// Instanz des Unmarshaller
		Unmarshaller u = jc.createUnmarshaller();
		Rezepte rez = (Rezepte) u.unmarshal(new File("src/Aufg_3a.xml"));
		return rez;
	}

	/**
	 * Vorbereitungsklasse fuer das Schreiben in ein XML-File
	 * 
	 * @param xsdSchema
	 *            zugehoeriges XSD-Schema
	 * @param xmlDatei
	 *            zu erzeugendes XML-File
	 * @param jaxbElement
	 *            Element, welches in das XML-File gesetzt werden soll
	 * @throws JAXBException
	 * @throws SAXException
	 */
	private static void recipeMarshal(String xsdSchema, String xmlDatei,
			Rezepte jaxbElement) throws SAXException, JAXBException {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = (xsdSchema == null || xsdSchema.trim().length() == 0) ? null
				: schemaFactory.newSchema(new File(xsdSchema));
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement
				.getClass().getPackage().getName());
		marshal(jaxbContext, schema, xmlDatei, jaxbElement);
	}

	/**
	 * Helper-Class zum Schreiben in XML-File
	 * 
	 * @param jaxbContext
	 *            Package des Objektes
	 * @param schema
	 *            XSD-File
	 * @param xmlDatei
	 *            XML-Filename
	 * @param jaxbElement
	 *            Objekt
	 * @throws JAXBException
	 */
	private static void marshal(JAXBContext jaxbContext, Schema schema,
			String xmlDatei, Rezepte jaxbElement) throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(jaxbElement, new File(xmlDatei));
	}

	/**
	 * Methode fuer Ausgabe des gesamten Rezeptes
	 */
	private static void printRecipe(Rezept rezept, int portion)
			throws JAXBException {
		for (int i = 0; i < 30; i++) {
			System.out.println();
		}

		System.out.println("***********************************************************");
		System.out.println("***********************************************************");
		System.out.println(rezept.getTitel() + '\n');
		if (rezept.getUntertitel()!= null)
			System.out.println(rezept.getUntertitel() + '\n');
		System.out.println("***********************************************************");

		//Ausgabe der Anzeigebilder, falls vorhanden
		if (!rezept.getBilder().getBild().isEmpty()) {
			System.out.println("Userbilder:");

			for (Bild a : rezept.getBilder().getBild()) {
				System.out.print('\t' + "User: " + a.getBenutzer());
				System.out.print('\t' + "Bild: " + a.getUrl() + '\n');
			}
			System.out.println("***********************************************************");
		}else{
			System.out.println("***********************************************************");
			System.out.println("\nKeine Anzeigebilder vorhanden");
		}

		System.out.println('\n' + "Zutaten (" + portion + " Portionen):");
		for (Zutat a : rezept.getZutaten().getZutat()) {
			double p = portion;
			double i = a.getBetrag();

			double gesamt = Math.rint(p * i * 10) / 10.0;
			System.out.print('\t');
			System.out.print(gesamt);
			System.out.print(' ');
			System.out.print('\t');
			System.out.print(a.getEinheit());
			System.out.print('\t');
			System.out.print(a.getName() + '\n');
		}

		System.out.println('\n' + "Zubereitung:" + '\n');

		Vorbereitung vorb = rezept.getVorbereitung();
		if(vorb.getArbeitszeit() >= 0)
		System.out.print("Arbeitszeit: " + vorb.getArbeitszeit());

		if(vorb.getBrennwert() >= 0)
		System.out.print("Brennwert: " + vorb.getBrennwert());

		System.out.println(vorb.getZubereitung());

		//Ausgabe der Kommentarliste, falls vorhanden
		if (!rezept.getKommentare().getKommentar().isEmpty()) {
			System.out.println("***********************************************************");
			System.out.println('\n' + "Benutzerkommentare:" + '\n');
			for (Kommentar a : rezept.getKommentare().getKommentar()) {
				System.out.print("Benutzer: " + a.getBenutzer().getBenutzername() + '\t');
				System.out.print("Avatar: " + a.getBenutzer().getAvatar() + '\n');
				System.out.println("Datum: " + a.getDatum());
				System.out.print("Nachricht: " + a.getNachricht() + '\n' + '\n');
			}
		} else{
			System.out.println("***********************************************************");
			System.out.println("\nKeine Kommentare vorhanden");
		}
	}

	/**
	 * Erzeugen einer erweiterten Kommentarliste
	 * 
	 * @return Kommentarliste vom Typ Comments
	 * @throws JAXBException
	 */
	private static Kommentare addComment(Rezept rezept) {
		Kommentare kom = new Kommentare();
		Kommentar komm = newKommentar();
		for (Kommentar a : rezept.getKommentare().getKommentar()) {
			kom.getKommentar().add(a);
		}
		kom.getKommentar().add(komm);

		return kom;
	}

	/**
	 * Erzeugen eines neuen Kommentars
	 * @return Comment neuer Kommentar 
	 */
	private static Kommentar newKommentar() {
		Kommentar kommentar = new Kommentar();

		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			System.out.print("Wie hei§en Sie?");
			Benutzer benutzer = new Benutzer();
			benutzer.setBenutzername(console.readLine());
			System.out.print("Wo ist ihr Bild zu finden?");
			benutzer.setAvatar(console.readLine());
			kommentar.setBenutzer(benutzer);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm 'Uhr'");
			kommentar.setDatum(dateFormat.format(new Date()));
			System.out.print("Was haben Sie zu sagen?");
			kommentar.setNachricht(console.readLine());
		} catch (IOException e) {
			// Sollte eigentlich nie passieren
			e.printStackTrace();
		}
		return kommentar;
	}

}