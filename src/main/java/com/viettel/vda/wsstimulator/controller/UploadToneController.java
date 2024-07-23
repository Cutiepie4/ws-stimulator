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
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UploadToneController {

    private AtomicLong toneIdGenerator = new AtomicLong(1);

    @PostMapping(value = "/upload-tone", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList uploadToneEvtList = document.getElementsByTagNameNS("*", "UploadToneEvt");
            if (uploadToneEvtList.getLength() == 0) {
                return createErrorResponse();
            }

            Element uploadToneEvtElement = (Element) uploadToneEvtList.item(0);

            String role = getElementTextContent(uploadToneEvtElement, "role");
            String toneName = getElementTextContent(uploadToneEvtElement, "toneName");
            String partnerID = getElementTextContent(uploadToneEvtElement, "partnerID");
            String toneValidDay = getElementTextContent(uploadToneEvtElement, "toneValidDay");
            String resourceServiceType = getElementTextContent(uploadToneEvtElement, "resourceServiceType");
            String uploadType = getElementTextContent(uploadToneEvtElement, "uploadType");
            String portalAccount = getElementTextContent(uploadToneEvtElement, "portalAccount");
            String portalPwd = getElementTextContent(uploadToneEvtElement, "portalPwd");
            String priceGroupID = getElementTextContent(uploadToneEvtElement, "priceGroupID");

            if (isValidRequest(role, toneName, partnerID, toneValidDay, resourceServiceType, uploadType, portalAccount, portalPwd, priceGroupID)) {
                long toneId = toneIdGenerator.getAndIncrement();
                return createSuccessResponse(toneName, toneId);
            } else {
                return createErrorResponse();
            }
        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return createErrorResponse();
        }
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private boolean isValidRequest(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String createSuccessResponse(String toneName, long toneId) {
        return "<?xml version=\"1.0\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ns:UploadToneResponse xmlns:ns=\"http://vcrbt.com/\">\n" +
                "         <return>0</return>\n" +
                "         <toneCode>" + toneName + "</toneCode>\n" +
                "         <toneID>" + toneId + "</toneID>\n" +
                "      </ns:UploadToneResponse>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private String createErrorResponse() {
        return "<?xml version=\"1.0\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ns:UploadToneResponse xmlns:ns=\"http://vcrbt.com/\">\n" +
                "         <return>1</return>\n" +
                "         <toneCode>null</toneCode>\n" +
                "         <toneID>null</toneID>\n" +
                "      </ns:UploadToneResponse>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}