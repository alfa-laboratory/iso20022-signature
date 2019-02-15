/*
 * Программный код, содержащийся в этом файле, является примером и предназначен
 * для целей обучения.
 * Данный код не может быть непосредственно использован
 * для защиты информации. АО "Альфа-Банк" не несет никакой
 * ответственности за функционирование этого кода.
 */

package ru.alfabank.iso20022.model;

import com.google.common.collect.Maps;

import java.util.Map;

public enum SignatureAlgorithm {
    GOST_2012_256("urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102012-gostr34112012-256"
            , "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34112012-256", "GOST3410DH_2012_256"),
    GOST_2012_512("urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102012-gostr34112012-512", "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34112012-512", "GOST3410DH_2012_512"),
    GOST_2001("urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102001-gostr3411", "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr3411", "GOST3410DHEL");

    private String signatureMethod;
    private String digestMethod;
    private String keyAlgorithm;

    private static Map<String, SignatureAlgorithm> signatureAlgorithmsMap = Maps.newHashMap();

    static {
        for (SignatureAlgorithm signatureAlgorithm : SignatureAlgorithm.values()) {
            signatureAlgorithmsMap.put(signatureAlgorithm.keyAlgorithm, signatureAlgorithm);
        }
    }

    private SignatureAlgorithm(String signatureMethod, String digestMethod, String keyAlgorithm) {
        this.signatureMethod = signatureMethod;
        this.digestMethod = digestMethod;
        this.keyAlgorithm = keyAlgorithm;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public String getDigestMethod() {
        return digestMethod;
    }

    public static SignatureAlgorithm getSignatureAlgorithmByKey(String keyAlgorithm) {
        return signatureAlgorithmsMap.get(keyAlgorithm);
    }

}
