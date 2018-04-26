package com.zenwherk.api.service;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Service
public class MailingService {

    private static final Logger logger = LoggerFactory.getLogger(MailingService.class);

    private static final String DOMAIN_NAME = "sandboxe7e4c1c7890d4e039f2650f3926d9e13.mailgun.org";
    private static final String API_KEY = "key-bd16316378685cc0838a47ed9bb33ad8";


    public JsonNode sendSimpleMessage(String toMail, String subject, String message) throws Exception {

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        HttpResponse<JsonNode> request =
                Unirest.post("https://api.mailgun.net/v3/"
                        + DOMAIN_NAME
                        + "/messages" +
                        "?from=davidzaratetrujillo@gmail.com" +
                        "&to=" + toMail +
                        "&subject=" + subject+
                        "&text=" + message)
                        .basicAuth("api", API_KEY)
                        .asJson();
        System.out.println(request.getBody().toString());

        return request.getBody();
    }
}
