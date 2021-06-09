package com.example.util;

public class Constants {
    public static final String STATEMENT = "Treść oświadczenia";

    public static final String DEPUTYSHIP = "Treść pełnomocnictwa";

    public static final String SOAPENV_BEG = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:epu=\"http://e-sad.gov.pl/2010/03/26/epu/\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<epu:ZlozWnioskiEgzekucyjne>";

    public static final String USER = "<epu:userName>" + ApplicationProperties.getUsername() + "</epu:userName>" +
            "<epu:password>" + ApplicationProperties.getPassword() + "</epu:password>" +
            "<epu:apiKey>" + ApplicationProperties.getApiKey() + "</epu:apiKey>" +
            "<epu:listaWnioskowEgzekucyjnychXML><![CDATA[";

    public static final String SOAPENV_END = "]]></epu:listaWnioskowEgzekucyjnychXML>" +
            "</epu:ZlozWnioskiEgzekucyjne>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";
}
