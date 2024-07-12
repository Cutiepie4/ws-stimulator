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
public class EncodeIsdnController {

    static final String USERNAME = "admin";
    static final String PASSWORD = "123";
    static final String SUBID = "57d6b0d6518068d7feedf08e6490c3ab71e28da9cf2d8b4bc76e6c6972b6d4f3";

    @PostMapping(value = "/encode-isdn", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleEncodeIsdnRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList encodeList = document.getElementsByTagNameNS("*", "encode");
            if (encodeList.getLength() == 0) {
                return createErrorResponse("Invalid XML format");
            }

            Element encodeElement = (Element) encodeList.item(0);

            String isdn = getElementTextContent(encodeElement, "isdn");
            String username = getElementTextContent(encodeElement, "username");
            String password = getElementTextContent(encodeElement, "password");
            String transid = getElementTextContent(encodeElement, "transid");

            if (isdn == null || username == null || password == null || transid == null) {
                return createErrorResponse("Missing required fields");
            }

            // Validate username and password
            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                return createSuccessResponse(transid);
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

    private String createSuccessResponse(String transid) {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <ns2:encodeResponse xmlns:ns2=\"http://service.ws.mp.viettel.com/\">\n" +
                "            <return>\n" +
                "                <date>" + date + "</date>\n" +
                "                <desc>Success</desc>\n" +
                "                <errcode>0</errcode>\n" +
                "                <transid>" + transid + "</transid>\n" +
                "                <subid>" + SUBID + "</subid>\n" +
                "            </return>\n" +
                "        </ns2:encodeResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    private String createErrorResponse(String message) {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <ns2:encodeResponse xmlns:ns2=\"http://service.ws.mp.viettel.com/\">\n" +
                "            <return>\n" +
                "                <date>" + date + "</date>\n" +
                "                <desc>" + message + "</desc>\n" +
                "                <errcode>1</errcode>\n" +
                "                <transid>ERROR</transid>\n" +
                "                <subid></subid>\n" +
                "            </return>\n" +
                "        </ns2:encodeResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }
}
