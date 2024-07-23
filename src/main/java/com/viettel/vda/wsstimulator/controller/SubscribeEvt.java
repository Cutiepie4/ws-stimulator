package com.viettel.vda.wsstimulator.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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
public class SubscribeEvt {

    @PostMapping(value = "/subscribe-evt", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(soapXml)));

            NodeList subscribeEvtList = document.getElementsByTagNameNS("*", "SubscribeEvt");
            if (subscribeEvtList.getLength() == 0) {
                return createErrorResponse("Lỗi hệ thống");
            }

            Element subscribeEvtElement = (Element) subscribeEvtList.item(0);

            String phoneNumber = getElementTextContent(subscribeEvtElement, "phoneNumber");
            String portalAccount = getElementTextContent(subscribeEvtElement, "portalAccount");
            String portalPwd = getElementTextContent(subscribeEvtElement, "portalPwd");
            String role = getElementTextContent(subscribeEvtElement, "role");
            String tradeMark = getElementTextContent(subscribeEvtElement, "tradeMark");

            if(phoneNumber == null && portalAccount == null && portalPwd == null && role == null && tradeMark == null) {
                return createErrorResponse("Lỗi hệ thống");
            }

            if ("1".equals(phoneNumber)) {
                return createSuccessResponse("301009");
            } else if ("2".equals(phoneNumber)) {
                return createSuccessResponse("310010");
            } else if ("3".equals(phoneNumber)) {
                return createSuccessResponse("301505");
            } else if ("4".equals(phoneNumber)) {
                return createSuccessResponse("100006");
            } else if ("5".equals(phoneNumber)) {
                return createErrorResponse("Lỗi hệ thống");
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
                "      <ns1:subscribePlusResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://listener.webservice.interfaces.vcrbt.viettel.com\">\n" +
                "         <subscribePlusReturn href=\"#id0\"/>\n" +
                "      </ns1:subscribePlusResponse>\n" +
                "      <multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xsi:type=\"ns2:com.viettel.vcrbt.interfaces.webservice.listener.SubscribeResp\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns2=\"http://soapinterop.org/xsd\">\n" +
                "         <eventClassName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <operationID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <resultCode xsi:type=\"xsd:string\">0</resultCode>\n" +
                "         <resultInfo xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <returnCode xsi:type=\"xsd:string\">" + returnCode + "</returnCode>\n" +
                "      </multiRef>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private String createErrorResponse(String errorMessage) {
        String errorCode = getErrorCode(errorMessage);
        return "<?xml version=\"1.0\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ns1:subscribePlusResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://listener.webservice.interfaces.vcrbt.viettel.com\">\n" +
                "         <subscribePlusReturn href=\"#id0\"/>\n" +
                "      </ns1:subscribePlusResponse>\n" +
                "      <multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xsi:type=\"ns2:com.viettel.vcrbt.interfaces.webservice.listener.SubscribeResp\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns2=\"http://soapinterop.org/xsd\">\n" +
                "         <eventClassName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <operationID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <resultCode xsi:type=\"xsd:string\">1</resultCode>\n" +
                "         <resultInfo xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\n" +
                "         <returnCode xsi:type=\"xsd:string\">" + errorCode + "</returnCode>\n" +
                "      </multiRef>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private String getErrorCode(String errorMessage) {
        switch (errorMessage) {
            case "Đăng ký thành công":
                return "000000";
            case "Đăng ký thành công do đã đăng ký dịch vụ rồi":
                return "301009";
            case "Đăng ký không thành công do tài khoản không đủ tiền":
                return "310010";
            case "Đăng ký không thành công do thuê bao thuộc Blacklist hoặc Homephone":
                return "301505";
            case "Đăng ký không thành công do lỗi kết nối Database":
                return "100006";
            default:
                return "Lỗi hệ thống";
        }
    }
}
