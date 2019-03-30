/*
 * Программный код, содержащийся в этом файле, является примером и предназначен
 * для целей обучения.
 * Данный код не может быть непосредственно использован
 * для защиты информации. АО "Альфа-Банк" не несет никакой
 * ответственности за функционирование этого кода.
 */

package ru.alfabank.iso20022;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alfabank.iso20022.model.Certificate;
import ru.alfabank.iso20022.services.SignatureService;

import java.nio.charset.Charset;
import java.util.List;

public class StatementSignatureExample {
    private static final Logger log = LoggerFactory.getLogger(StatementSignatureExample.class);

    private static final String STATEMENT_SIGNATURE_ELEMENT_XPATH = "/*[local-name()='Document' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:camt.060.001.03']" +
            "/*[local-name()='AcctRptgReq' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:camt.060.001.03']" +
            "/*[local-name()='SplmtryData' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:camt.060.001.03']" +
            "/*[local-name()='Envlp' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:camt.060.001.03']" +
            "/*[local-name()='SngtrSt' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:camt.060.001.03']";

    private static final List<Certificate> certificates = Lists.newArrayList(
    );

    public static void main(String[] args) throws Exception {
        SignatureService signatureService = new SignatureService();
        signatureService.init();

        String request = IOUtils.resourceToString("/statement_example.txt", Charset.forName("UTF-8"));
        log.info("Request:{}", request);
        String signedRequest = signatureService.sign(request, STATEMENT_SIGNATURE_ELEMENT_XPATH, certificates);
        log.info("Signed request:{}", signedRequest);
    }
}
