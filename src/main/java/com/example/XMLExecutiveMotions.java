package com.example;

import com.example.util.ApplicationProperties;
import com.example.util.Constants;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XMLExecutiveMotions {

    private final Document XMLDoc;


    public XMLExecutiveMotions(String file) {
        Reader reader = null;
        try {
            File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            String CSVPath = jarPath.getParentFile().getAbsolutePath();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(CSVPath + file), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();

        List<String[]> CSVDoc = null;
        try {
            CSVDoc = csvReader.readAll();
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Integer> firstIndexList = new ArrayList<>(300);
        List<Integer> rowsInMotionList = new ArrayList<>(300);
        int motions = 0;
        int rows = 0;
        int firstIndex;
        for (int i = 1; i < (CSVDoc.size() - 1); i++) {
            rows++;
            if (!CSVDoc.get(i)[0].equals(CSVDoc.get(i + 1)[0])) {
                firstIndex = i + 1 - rows;
                firstIndexList.add(firstIndex);
                rowsInMotionList.add(rows);
                rows = 0;
                motions++;
            } else if (i == CSVDoc.size() - 2) {
                firstIndex = i + 1 - rows;
                firstIndexList.add(firstIndex);
                rowsInMotionList.add(rows + 1);
                motions++;
            }
        }

        this.XMLDoc = DocumentHelper.createDocument();
        createXML(CSVDoc, motions, firstIndexList, rowsInMotionList);
    }

    public String XMLWithoutDeclaration() {
        return XMLDoc.asXML().substring(39);
    }

    public void prettyPrint() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(XMLDoc);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not print the motion!");
        }
    }

    private void createXML(List<String[]> CSVDoc, int motions, List<Integer> firstIndexList, List<Integer> rowsInMotionList) {

        Element root = XMLDoc.addElement("epu:WnioskiEgzekucyjneEPU")
                .addAttribute("OznaczeniePaczki", "Firma_" + LocalDateTime.now().toLocalDate().toString());

        this.XMLDoc.getRootElement().addNamespace("epu", "http://www.currenda.pl/epu");

        for (int i = 0; i < motions; i++) {

            int firstIndex = firstIndexList.get(i);
            int rows = rowsInMotionList.get(i);

            Element wniosekEgzekucyjny = root.addElement("epu:WniosekEgzekucyjny")
                    .addAttribute("ID", Integer.toString(i))
                    .addAttribute("version", "1.0")
                    .addAttribute("dataWniosku", LocalDateTime.now().toLocalDate().toString());

            wniosekEgzekucyjny.addElement("epu:Komornik")
                    .addAttribute("ID", CSVDoc.get(firstIndex)[33])
                    .addElement("epu:Nazwa").addText(CSVDoc.get(firstIndex)[34]);


            Element sad = wniosekEgzekucyjny.addElement("epu:Sad");
            sad.addElement("epu:Nazwa").addText("Sąd Rejonowy Lublin-Zachód w Lublinie");
            sad.addElement("epu:Wydzial").addText("VI Wydział Cywilny");


            Element nakaz = wniosekEgzekucyjny.addElement("epu:Nakaz");
            nakaz.addElement("epu:IDNakazu").addText(CSVDoc.get(firstIndex)[15]);
            nakaz.addElement("epu:Sygnatura").addText(CSVDoc.get(firstIndex)[1]);
            nakaz.addElement("epu:DataNakazu").addText(CSVDoc.get(firstIndex)[17]);
            nakaz.addElement("epu:KodDecyzji").addText(CSVDoc.get(firstIndex)[16]);


            Element klauzula = wniosekEgzekucyjny.addElement("epu:Klauzula");
            klauzula.addElement("epu:IDNakazu").addText(CSVDoc.get(firstIndex)[15]);
            klauzula.addElement("epu:IDKlauzuli").addText(CSVDoc.get(firstIndex)[19]);
            klauzula.addElement("epu:Sygnatura").addText(CSVDoc.get(firstIndex)[1]);
            klauzula.addElement("epu:DataKlauzuli").addText(CSVDoc.get(firstIndex)[18]);
            klauzula.addElement("epu:KodKlauzuli").addText(CSVDoc.get(firstIndex)[20]);

            Element osobaSkaladajaca = wniosekEgzekucyjny.addElement("epu:OsobaSkladajaca")
                    .addAttribute("pelnomocnik", "1")
                    .addAttribute("podstawa", Constants.DEPUTYSHIP);

            Element osoba = osobaSkaladajaca.addElement("epu:Osoba");
            osoba.addElement("epu:Imie").addText(ApplicationProperties.getName());
            osoba.addElement("epu:Nazwisko").addText(ApplicationProperties.getSurname());
            osoba.addElement("epu:PESEL").addText(ApplicationProperties.getPesel());
            osoba.addElement("epu:stanowisko").addText("radca prawny");

            osobaSkaladajaca.addElement("epu:Nazwa").addText("***");
            osobaSkaladajaca.addElement("epu:Adres")
                    .addAttribute("ulica", "***")
                    .addAttribute("nr_domu", "***")
                    .addAttribute("miejscowosc", "***")
                    .addAttribute("poczta", "***")
                    .addAttribute("wojewodztwo", "Dolnośląskie")
                    .addAttribute("kod", "**-***");

            Element listaWierzycieli = wniosekEgzekucyjny.addElement("epu:ListaWierzycieli");
            Element wierzyciel = listaWierzycieli.addElement("epu:Wierzyciel")
                    .addAttribute("ID", "0");

            wierzyciel.addElement("epu:rodzaj").addText("2");

            Element instytucja = wierzyciel.addElement("epu:Instytucja");
            instytucja.addElement("epu:Nazwa").addText("**** S.A.");
            instytucja.addElement("epu:Siedziba").addText("Warszawie");
            instytucja.addElement("epu:REGON").addText("*****");
            instytucja.addElement("epu:czyRejestr").addText("1");
            instytucja.addElement("epu:KRS").addText("*****");

            wierzyciel.addElement("epu:NIP").addText("*****");
            wierzyciel.addElement("epu:Adres")
                    .addAttribute("kod", "**-***")
                    .addAttribute("miejscowosc", "Warszawa")
                    .addAttribute("nr_domu", "***")
                    .addAttribute("ulica", "****")
                    .addAttribute("wojewodztwo", "Mazowieckie");

            wierzyciel.addElement("epu:KontoBankowe").addText(CSVDoc.get(firstIndex)[14]);

            Element listaDluznikow = wniosekEgzekucyjny.addElement("epu:ListaDluznikow");
            Element dluznik = listaDluznikow.addElement("epu:Dluznik")
                    .addAttribute("ID", "0");

            String typDluznika = CSVDoc.get(firstIndex)[8];
            String czyUstawowe = null;

            switch (typDluznika) {
                case "3":
                    czyUstawowe = "8";
                    dluznik.addElement("epu:rodzaj").addText("0");
                    Element osobaFizyczna = dluznik.addElement("epu:OsobaFizyczna");
                    osobaFizyczna.addElement("epu:Imie").addText(CSVDoc.get(firstIndex)[3]);
                    osobaFizyczna.addElement("epu:Nazwisko").addText(CSVDoc.get(firstIndex)[4]);
                    osobaFizyczna.addElement("epu:PESEL").addText(CSVDoc.get(firstIndex)[5]);
                    break;

                case "2":
                    czyUstawowe = "15";
                    dluznik.addElement("epu:rodzaj").addText("2");
                    instytucja = dluznik.addElement("epu:Instytucja");
                    instytucja.addElement("epu:Nazwa").addText(CSVDoc.get(firstIndex)[26]);
                    instytucja.addElement("epu:Siedziba").addText(CSVDoc.get(firstIndex)[13]);
                    instytucja.addElement("epu:czyRejestr").addText("1");
                    instytucja.addElement("epu:KRS").addText(CSVDoc.get(firstIndex)[7]);
                    dluznik.addElement("epu:NIP").addText(CSVDoc.get(firstIndex)[6]);
                    break;

                case "6":
                    czyUstawowe = "15";
                    dluznik.addElement("epu:rodzaj").addText("1");
                    osobaFizyczna = dluznik.addElement("epu:OsobaFizyczna");
                    osobaFizyczna.addElement("epu:Imie").addText(CSVDoc.get(firstIndex)[3]);
                    osobaFizyczna.addElement("epu:Nazwisko").addText(CSVDoc.get(firstIndex)[4]);
                    dluznik.addElement("epu:NIP").addText(CSVDoc.get(firstIndex)[6]);
                    break;
            }


            dluznik.addElement("epu:Adres")
                    .addAttribute("ulica", CSVDoc.get(firstIndex)[9])
                    .addAttribute("nr_domu", CSVDoc.get(firstIndex)[10])
                    .addAttribute("nr_mieszkania", CSVDoc.get(firstIndex)[11])
                    .addAttribute("kod", CSVDoc.get(firstIndex)[12])
                    .addAttribute("miejscowosc", CSVDoc.get(firstIndex)[13]);

            Element listaSpososobow = dluznik.addElement("epu:ListaSposobow");

            Element sposobEgzekucji = listaSpososobow.addElement("epu:SposobEgzekucji");
            sposobEgzekucji.addElement("epu:Rodzaj").addText("z ruchomości");
            sposobEgzekucji.addElement("epu:Opis").addText("");

            sposobEgzekucji = listaSpososobow.addElement("epu:SposobEgzekucji");
            sposobEgzekucji.addElement("epu:Rodzaj").addText("z rachunków bankowych");
            sposobEgzekucji.addElement("epu:Opis").addText("");

            sposobEgzekucji = listaSpososobow.addElement("epu:SposobEgzekucji");
            sposobEgzekucji.addElement("epu:Rodzaj").addText("z wynagrodzenia za pracę");
            sposobEgzekucji.addElement("epu:Opis").addText("");

            sposobEgzekucji = listaSpososobow.addElement("epu:SposobEgzekucji");
            sposobEgzekucji.addElement("epu:Rodzaj").addText("z wierzytelności");
            sposobEgzekucji.addElement("epu:Opis").addText("");

            Element listaRoszczen = wniosekEgzekucyjny.addElement("epu:ListaRoszczen");
            int claimNo = 1;
            for (int j = 0; j < rows; j++) {
                int row = firstIndex + j;

                String claimValue = CSVDoc.get(row)[22].trim().replace(',', '.');
                if (!claimValue.equals("0.00")) {
                    if (CSVDoc.get(row)[21].equals("dokument")) {
                        Element roszczenie = listaRoszczen.addElement("epu:Roszczenie")
                                .addAttribute("numer", Integer.toString(claimNo))
                                .addAttribute("wartosc", claimValue)
                                .addAttribute("waluta", "PLN")
                                .addAttribute("odsetki", "1")
                                .addAttribute("solidarnie", "1")
                                .addAttribute("typ", "0");

                        roszczenie.addElement("epu:Odsetki").addElement("epu:OkresOdsetkowy")
                                .addAttribute("dataOd", CSVDoc.get(row)[23])
                                .addAttribute("czyUstawowe", czyUstawowe)
                                .addAttribute("odWniesienia", "0")
                                .addAttribute("doZaplaty", "1");

                    } else if (CSVDoc.get(row)[21].equals("koszty")) {
                        Element roszczenie = listaRoszczen.addElement("epu:Roszczenie")
                                .addAttribute("numer", Integer.toString(claimNo))
                                .addAttribute("wartosc", claimValue)
                                .addAttribute("waluta", "PLN")
                                .addAttribute("odsetki", "1")
                                .addAttribute("solidarnie", "1")
                                .addAttribute("typ", "0")
                                .addAttribute("opis", "Tytułem zwrotu kosztów procesu");

                        roszczenie.addElement("epu:Odsetki").addElement("epu:OkresOdsetkowy")
                                .addAttribute("czyUstawowe", "100");
                    }
                    claimNo++;
                }
            }

            String caseNo = "\n\nNasz znak: " + CSVDoc.get(firstIndex)[2];
            wniosekEgzekucyjny.addElement("epu:InformacjeDodatkowe").addText(Constants.STATEMENT + caseNo);

            wniosekEgzekucyjny.addElement("epu:KosztyZastepstwa")
                    .addAttribute("wartosc", "0")
                    .addAttribute("zasadzenie", "1")
                    .addAttribute("wgNorm", "1");

            wniosekEgzekucyjny.addElement("epu:ZleceniePoszukiwaniaMajatku").addText("1");
            wniosekEgzekucyjny.addElement("epu:ZlecenieProwadzeniaArt85").addText("0");
        }
    }
}
