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
public class UnsubscribeEvt {

    @PostMapping(value = "/unsubscribe-evt", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList unsubscribeEvtList = document.getElementsByTagNameNS("*", "UnSubscribeEvt");
            if (unsubscribeEvtList.getLength() == 0) {
                return createErrorResponse("Lỗi hệ thống");
            }

            Element unsubscribeEvtElement = (Element) unsubscribeEvtList.item(0);

            String deleteFlag = getElementTextContent(unsubscribeEvtElement, "deleteFlag");
            String phoneNumber = getElementTextContent(unsubscribeEvtElement, "phoneNumber");
            String portalAccount = getElementTextContent(unsubscribeEvtElement, "portalAccount");
            String portalPwd = getElementTextContent(unsubscribeEvtElement, "portalPwd");
            String role = getElementTextContent(unsubscribeEvtElement, "role");

            if (phoneNumber == null && portalAccount == null && portalPwd == null && role == null && deleteFlag == null) {
                return createErrorResponse("301015");
            }

            if ("0".equals(phoneNumber)) {
                return createErrorResponse("301001");
            } else if ("1".equals(phoneNumber)) {
                return createErrorResponse("301015");
            }
            return createSuccessResponse("000000");

        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return createErrorResponse("Lỗi hệ thống");
        }
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private String createSuccessResponse(String returnCode) {
        return "<?xml version=\"1.0\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ns1:unsubscribeResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://listener.webservice.interfaces.vcrbt.viettel.com\">\n" +
                "         <unsubscribeReturn href=\"#id0\"/>\n" +
                "      </ns1:unsubscribeResponse>\n" +
                "      <multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xsi:type=\"ns2:com.viettel.vcrbt.interfaces.webservice.listener.UnsubscribeResp\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns2=\"http://soapinterop.org/xsd\">\n" +
                "         <eventClassName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <operationID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <resultCode xsi:type=\"xsd:string\">0</resultCode>\n" +
                "         <resultInfo xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <returnCode xsi:type=\"xsd:string\">" + returnCode + "</returnCode>\n" +
                "      </multiRef>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private String createErrorResponse(String errorCode) {
        return "<?xml version=\"1.0\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ns1:unsubscribeResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://listener.webservice.interfaces.vcrbt.viettel.com\">\n" +
                "         <unsubscribeReturn href=\"#id0\"/>\n" +
                "      </ns1:unsubscribeResponse>\n" +
                "      <multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xsi:type=\"ns2:com.viettel.vcrbt.interfaces.webservice.listener.UnsubscribeResp\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns2=\"http://soapinterop.org/xsd\">\n" +
                "         <eventClassName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <operationID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <resultCode xsi:type=\"xsd:string\">1</resultCode>\n" +
                "         <resultInfo xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <returnCode xsi:type=\"xsd:string\">" + errorCode + "</returnCode>\n" +
                "      </multiRef>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

}
