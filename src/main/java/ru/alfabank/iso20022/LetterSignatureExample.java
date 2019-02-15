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

public class LetterSignatureExample {
    private static final Logger log = LoggerFactory.getLogger(LetterSignatureExample.class);

    private static final String LETTER_SIGNATURE_ELEMENT_XPATH = "/*[local-name()='Document' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:auth.026.001.01']" +
            "/*[local-name()='CcyCtrlReqOrLttr' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:auth.026.001.01']" +
            "/*[local-name()='SplmtryData' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:auth.026.001.01']" +
            "/*[local-name()='Envlp' and namespace-uri()='urn:iso:std:iso:20022:tech:xsd:auth.026.001.01']";

    private static final List<Certificate> certificates = Lists.newArrayList();

    public static void main(String[] args) throws Exception {
        SignatureService signatureService = new SignatureService();
        signatureService.init();

        String request = IOUtils.resourceToString("/letter_example.txt", Charset.forName("UTF-8"));
        log.info("Request:{}", request);
        String signedRequest = signatureService.sign(request, LETTER_SIGNATURE_ELEMENT_XPATH, certificates);
        log.info("Signed request:{}", signedRequest);
    }
}
