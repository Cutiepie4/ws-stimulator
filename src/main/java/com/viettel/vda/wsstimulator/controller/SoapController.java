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
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class SoapController {

    static String USERNAME = "admin";

    static String PASSWORD = "123";

    @PostMapping(value = "/sms", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String handleSoapRequest(@RequestBody String soapXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new java.io.StringReader(soapXml)));

            NodeList usernameList = document.getElementsByTagName("username");
            NodeList passwordList = document.getElementsByTagName("password");

            if (usernameList.getLength() > 0 && passwordList.getLength() > 0) {
                String username = usernameList.item(0).getTextContent().trim();
                String password = passwordList.item(0).getTextContent().trim();

                if (username.equals(USERNAME) && password.equals(PASSWORD)) {
                    return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            " <S:Body>\n" +
                            " <ns2:mtResponse xmlns:ns2=\"http://ws.mp.viettel.com/\">\n" +
                            " <return>\n" +
                            " <date>yyyyMMddHHmmss</date>\n" +
                            " <errcode>0</errcode>\n" +
                            "<desc>Success</desc >\n" +
                            "<transid>test-123</transid >\n" +
                            "<balance>1</balance >\n" +
                            " </return>\n" +
                            " </ns2:mtResponse>\n" +
                            " </S:Body>\n" +
                            "</S:Envelope>";
                } else {
                    return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            " <S:Body>\n" +
                            " <ns2:mtResponse xmlns:ns2=\"http://ws.mp.viettel.com/\">\n" +
                            " <return>\n" +
                            " <date>yyyyMMddHHmmss</date>\n" +
                            " <errcode>1</errcode>\n" +
                            "<desc>Fail</desc >\n" +
                            " </return>\n" +
                            " </ns2:mtResponse>\n" +
                            " </S:Body>\n" +
                            "</S:Envelope>";
                }
            } else {
                return "<S:Envelope xmlns:S=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\">\\n\" +\n" +
                        "                            \" <S:Body>\\n\" +\n" +
                        "                            \" <ns2:mtResponse xmlns:ns2=\\\"http://ws.mp.viettel.com/\\\">\\n\" +\n" +
                        "                            \" <return>\\n\" +\n" +
                        "                            \" <date>yyyyMMddHHmmss</date>\\n\" +\n" +
                        "                            \" <errcode>1</errcode>\\n\" +\n" +
                        "                            \"<desc>Fail</desc >\\n\" +\n" +
                        "                            \" </return>\\n\" +\n" +
                        "                            \" </ns2:mtResponse>\\n\" +\n" +
                        "                            \" </S:Body>\\n\" +\n" +
                        "                            \"</S:Envelope>";
            }

        } catch (ParserConfigurationException | SAXException | java.io.IOException e) {
            e.printStackTrace();
            return "<S:Envelope xmlns:S=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\">\\n\" +\n" +
                    "                            \" <S:Body>\\n\" +\n" +
                    "                            \" <ns2:mtResponse xmlns:ns2=\\\"http://ws.mp.viettel.com/\\\">\\n\" +\n" +
                    "                            \" <return>\\n\" +\n" +
                    "                            \" <date>yyyyMMddHHmmss</date>\\n\" +\n" +
                    "                            \" <errcode>1</errcode>\\n\" +\n" +
                    "                            \"<desc>Fail</desc >\\n\" +\n" +
                    "                            \" </return>\\n\" +\n" +
                    "                            \" </ns2:mtResponse>\\n\" +\n" +
                    "                            \" </S:Body>\\n\" +\n" +
                    "                            \"</S:Envelope>";
        }
    }

    @GetMapping("")
    public String index() {
        return "Welcome to the VDA Simulator!";
    }
}