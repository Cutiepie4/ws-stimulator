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

@RestController
public class ActiveIsignController {

    static final String USERNAME = "admin";
    static final String PASSWORD = "123";

    @PostMapping(value = "/activate-isign", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList mpsRegisterList = document.getElementsByTagNameNS("*", "mpsRegister");
            if (mpsRegisterList.getLength() == 0) {
                return createErrorResponse("Invalid XML format");
            }

            Element mpsRegisterElement = (Element) mpsRegisterList.item(0);

            String username = getElementTextContent(mpsRegisterElement, "username");
            String password = getElementTextContent(mpsRegisterElement, "password");
            String msisdn = getElementTextContent(mpsRegisterElement, "msisdn");
            String service = getElementTextContent(mpsRegisterElement, "service");
            String action = getElementTextContent(mpsRegisterElement, "action");
            String amount = getElementTextContent(mpsRegisterElement, "amount");
            String params = getElementTextContent(mpsRegisterElement, "params");
            String description = getElementTextContent(mpsRegisterElement, "description");
            String addday = getElementTextContent(mpsRegisterElement, "addday");

            if (username == null || password == null || msisdn == null || service == null || action == null || amount == null || params == null || description == null || addday == null) {
                return createErrorResponse("JSON input error");
            }

            // Validate username and password
            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                // Process the request based on service, amount, params, description, and addday
                return createSuccessResponse(msisdn, service, amount, params, description, addday);
            } else {
                return createErrorResponse("Authentication failed");
            }
        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return createErrorResponse("Error processing request");
        }
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private String createSuccessResponse(String msisdn, String service, String amount, String params, String description, String addday) {
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <mpsRegisterResponse xmlns=\"http://mpsRegisterws/xsd\">\n" +
                "            <return>0</return>\n" +
                "            <desc>" + "Success" + "</desc>\n" +
                "        </mpsRegisterResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    private String createErrorResponse(String message) {
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <mpsRegisterResponse xmlns=\"http://mpsRegisterws/xsd\">\n" +
                "            <return>1</return>\n" +
                "            <desc>" + message + "</desc>\n" +
                "        </mpsRegisterResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }
}
