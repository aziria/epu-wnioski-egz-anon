package com.example;

import com.example.util.Constants;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EPUSender {

    private final XMLExecutiveMotions motions;

    public EPUSender(XMLExecutiveMotions motions) {
        this.motions = motions;
    }

    public void send() throws IOException {
        String httpsURL = "https://www.e-sad.gov.pl/api2/EpuWS.EpuService.svc";
        URL myURL = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myURL.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Encoding", "gzip,deflate");
        con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        con.setRequestProperty("SOAPAction", "http://e-sad.gov.pl/2010/03/26/epu/EpuService/ZlozWnioskiEgzekucyjne");
        con.setRequestProperty("Host", "www.e-sad.gov.pl");
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setDoOutput(true);
        con.setDoInput(true);

        String xml = buildSOAPRequest();
        byte[] XMLBytes = xml.getBytes(StandardCharsets.UTF_8);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        output.write(XMLBytes);
        output.close();

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String respString = readResponse(response, reader);
            writeResponse(respString, con.getResponseCode(), con.getResponseMessage());

        } catch (IOException e) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            String respString = readResponse(response, reader);
            reader.close();
            writeResponse(respString, con.getResponseCode(), con.getResponseMessage());
        }
    }

    private String buildSOAPRequest() {
        String xmlSOAP = Constants.SOAPENV_BEG +
                Constants.USER +
                motions.XMLWithoutDeclaration() +
                Constants.SOAPENV_END;
        return xmlSOAP;
    }

    private String readResponse(StringBuilder response, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private void writeResponse(String response, int respCode, String respMessage) throws IOException {
        File jarPath = new File(EPUSender.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String CSVPath = jarPath.getParentFile().getAbsolutePath();
        PrintWriter writer = new PrintWriter(new FileWriter(CSVPath + "/odpowiedz.txt"));
        writer.println(response);
        writer.println("\nResp Code:"+ respCode);
        writer.println("Resp Message:"+ respMessage);
        writer.close();
    }
}