/*
 * Программный код, содержащийся в этом файле, является примером и предназначен
 * для целей обучения.
 * Данный код не может быть непосредственно использован
 * для защиты информации. АО "Альфа-Банк" не несет никакой
 * ответственности за функционирование этого кода.
 */

package ru.alfabank.iso20022.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.CryptoPro.JCPxml.xmldsig.JCPXMLDSigInit;
import ru.alfabank.iso20022.model.Certificate;
import ru.alfabank.iso20022.model.SignatureAlgorithm;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public class SignatureService {
    private static final String SIGNATURE_FILTER_XML_ELEMENT = "//ds:Signature";

    private KeyStore ks;
    private DocumentBuilderFactory dbf;

    public void init() throws Exception {
        //Инициализация JCP и загрузка keystore (достаточно 1го вызова при старте приложения)
        JCPXMLDSigInit.init();
        ks = KeyStore.getInstance("HDImageStore");
        ks.load(null, null);

        dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(true);
        dbf.setNamespaceAware(true);
    }

    public String sign(String request, String signedElementXpath, List<Certificate> certificates) throws Exception {
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document doc = documentBuilder.parse(new InputSource(new StringReader(request)));

        //Получение элемента SngtrSt для наложения подписи
        Node signatureNode = getSignatureNode(doc, signedElementXpath);

        //Процесс наложения подписи всеми указанными сертификатами.
        for (Certificate certificate : certificates) {
            sign(certificate.getAlias(), certificate.getPassword(), doc, signatureNode);
        }

        //Конвертация документа в xml строку
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(baos));

        return new String(baos.toByteArray());
    }

    private Node getSignatureNode(Document doc, String signedElementXpath) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList link = (NodeList) xpath.evaluate(signedElementXpath
                , doc, XPathConstants.NODESET);
        if (link.item(0) == null) {
            throw new RuntimeException(String.format("Signature element with xpath: %s doesnt exist", signedElementXpath));
        } else {
            return link.item(0);
        }
    }

    private XMLSignature sign(String alias, String password, Document doc, Node element) throws Exception {
        //Получение секретного ключа и сертификата из HDImageStore keystore
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, StringUtils.isNotBlank(password) ? password.toCharArray() : null);
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        if (privateKey == null || cert == null) {
            throw new RuntimeException(String.format("Key or cert with alias: %s doesnt exist", alias));
        }
        return sign(privateKey, cert, doc, element);
    }

    private XMLSignature sign(PrivateKey privateKey, X509Certificate cert, Document doc, Node element) throws Exception {
        XMLSignature sig = new XMLSignature(doc, "", getSignatureMethod(privateKey));
        element.appendChild(sig.getElement());
        sig.addDocument("", buildTransforms(doc), getDigestMethod(privateKey));
        sig.addKeyInfo(cert);
        sig.sign(privateKey);
        return sig;
    }

    private String getSignatureMethod(PrivateKey privateKey) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getSignatureAlgorithmByKey(privateKey.getAlgorithm());
        if (signatureAlgorithm == null) {
            throw new RuntimeException(String.format("Unsupported key type %s", privateKey.getAlgorithm()));
        }
        return signatureAlgorithm.getSignatureMethod();
    }

    private String getDigestMethod(PrivateKey privateKey) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getSignatureAlgorithmByKey(privateKey.getAlgorithm());
        if (signatureAlgorithm == null) {
            throw new RuntimeException(String.format("Unsupported key type %s", privateKey.getAlgorithm()));
        }
        return signatureAlgorithm.getDigestMethod();
    }

    private Transforms buildTransforms(Document doc) throws Exception {
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        //фильтр для игнорирования уже существующих подписей при наложении второй и последующих
        XPath2FilterContainer xpathC = XPath2FilterContainer.newInstanceSubtract(doc, SIGNATURE_FILTER_XML_ELEMENT);
        xpathC.setXPathNamespaceContext("dsig-xpath", Transforms.TRANSFORM_XPATH2FILTER);
        Element node = xpathC.getElement();
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, node);
        return transforms;
    }
}