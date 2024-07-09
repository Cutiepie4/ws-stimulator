package com.viettel.vda.wsstimulator.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class ISignController {

    static final String USERNAME = "admin";
    static final String PASSWORD = "123";

    @PostMapping(value = "/isign", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapISignRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList isignList = document.getElementsByTagNameNS("*", "isign");
            if (isignList.getLength() == 0) {
                return createErrorResponse("Invalid XML format", true);
            }

            Element isignElement = (Element) isignList.item(0);
            String subscriber = getElementTextContent(isignElement, "subscriber");
            String companyId = getElementTextContent(isignElement, "companyId");
            String signature = getElementTextContent(isignElement, "signature");

            System.out.println("subscriber: " + subscriber);
            System.out.println("companyId: " + companyId);
            System.out.println("signature: " + signature);

            String username = getElementTextContent(isignElement, "username");
            String password = getElementTextContent(isignElement, "password");
            if (username == null || password == null) {
                return createErrorResponse("Username or password not found", true);
            }

            String namespaceUri = isignElement.getNamespaceURI();
            if (namespaceUri == null) {
                return createErrorResponse("Namespace URI not found", true);
            }

            String isignType = extractIsignType(namespaceUri);

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                return createResponse("0", "Success", isignType, true);
            } else {
                return createResponse("1", "Wrong username or password", isignType, true);
            }
        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return createErrorResponse("Error processing request", true);
        }
    }

    @PostMapping(value = "/ncdn", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapNcdnRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList ncdnList = document.getElementsByTagNameNS("*", "ncdn");
            if (ncdnList.getLength() == 0) {
                return createErrorResponse("Invalid XML format", false);
            }

            Element ncdnElement = (Element) ncdnList.item(0);
            String subscriber = getElementTextContent(ncdnElement, "subscriber");
            String planCode = getElementTextContent(ncdnElement, "planCode");
            System.out.println("subscriber: " + subscriber);
            System.out.println("planCode: " + planCode);
            String username = getElementTextContent(ncdnElement, "username");
            String password = getElementTextContent(ncdnElement, "password");

            if (username == null || password == null) {
                return createErrorResponse("Username or password not found", false);
            }

            String namespaceUri = ncdnElement.getNamespaceURI();
            if (namespaceUri == null) {
                return createErrorResponse("Namespace URI not found", false);
            }

            String ncdnType = extractIsignType(namespaceUri);

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                return createResponse("0", "Success", ncdnType, false);
            } else {
                return createResponse("1", "Wrong username or password", ncdnType, false);
            }
        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return createErrorResponse("Error processing request", false);
        }
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private String extractIsignType(String namespaceUri) {
        String[] namespaceParts = namespaceUri.split("/");
        return namespaceParts[namespaceParts.length - 1];
    }

    private String createResponse(String errcode, String desc, String type, boolean isISign) {
        String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String namespace = "http://ws.mp.viettel.com/" + (type != null ? type : "unknown");

        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                " <S:Body>\n" +
                " <ns2:" + (isISign ? "isign" : "ncdn") +  "Response xmlns:ns2=\"" + namespace + "\">\n" +
                " <return>\n" +
                " <date>" + currentDate + "</date>\n" +
                " <errcode>" + errcode + "</errcode>\n" +
                " <desc>" + desc + "</desc>\n" +
                " </return>\n" +
                " </ns2:" + (isISign ? "isign" : "ncdn") +"Response>\n" +
                " </S:Body>\n" +
                "</S:Envelope>";
    }

    private String createErrorResponse(String message, boolean isISign) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                " <S:Body>\n" +
                " <ns2:" + (isISign ? "isign" : "ncdn") +  "Response xmlns:ns2=\"http://ws.mp.viettel.com/unknown\">\n" +
                " <return>\n" +
                " <date>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</date>\n" +
                " <errcode>1</errcode>\n" +
                " <desc>" + message + "</desc>\n" +
                " </return>\n" +
                " </ns2:" + (isISign ? "isign" : "ncdn") +"Response>\n" +
                " </S:Body>\n" +
                "</S:Envelope>";
    }
}
