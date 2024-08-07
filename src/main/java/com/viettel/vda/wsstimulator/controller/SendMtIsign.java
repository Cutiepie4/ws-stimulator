package com.viettel.vda.wsstimulator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class SendMtIsign {

    static final String USERNAME = "admin";
    static final String PASSWORD = "123";

    @PostMapping(value = "/send-mt-isign", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList moRequestList = document.getElementsByTagNameNS("*", "moRequest");
            if (moRequestList.getLength() == 0) {
                return createErrorResponse("Invalid XML format");
            }

            Element moRequestElement = (Element) moRequestList.item(0);

            String username = getElementTextContent(moRequestElement, "username");
            String password = getElementTextContent(moRequestElement, "password");
            String source = getElementTextContent(moRequestElement, "source");
            String dest = getElementTextContent(moRequestElement, "dest");
            String method = getElementTextContent(moRequestElement, "method");
            String content = getElementTextContent(moRequestElement, "content");

            if (username == null || password == null || source == null || dest == null || method == null || content == null) {
                return createErrorResponse("JSON input error");
            }

            // Validate username and password
            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                try {
                    String msisdn = extractMsisdnFromContent(content);
                    String errorCode = extractErrorCodeFromContent(content);
                    return createSuccessResponse(msisdn, errorCode);
                } catch (Exception e) {
                    return createErrorResponse("JSON input error");
                }
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

    private String extractMsisdnFromContent(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode msisdnNode = rootNode.path("msisdn");
        if (msisdnNode.isMissingNode()) {
            throw new Exception("Missing msisdn");
        }
        return msisdnNode.asText();
    }

    private String extractErrorCodeFromContent(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode errorCodeNode = rootNode.path("error_code");
        if (errorCodeNode.isMissingNode()) {
            throw new Exception("Missing error_code");
        }
        return errorCodeNode.asText();
    }

    private String createSuccessResponse(String msisdn, String errorCode) {
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <mtRequestResponse xmlns=\"http://mtws/xsd\">\n" +
                "            <return>0</return>\n" +
                "            <desc>Send MT OK</desc>\n" +
                "        </mtRequestResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    private String createErrorResponse(String message) {
        return "<?xml version=\"1.0\"?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "    <S:Body>\n" +
                "        <mtRequestResponse xmlns=\"http://mtws/xsd\">\n" +
                "            <return>1</return>\n" +
                "            <desc>" + message + "</desc>\n" +
                "        </mtRequestResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }
}
