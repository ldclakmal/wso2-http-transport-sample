package lk.avix.http.client;

import lk.avix.http.util.HttpUtil;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

import static org.wso2.transport.http.netty.contract.Constants.HTTPS_SCHEME;

/**
 * An HTTPS client which implemented using wso2 http-transport.
 */
public class HttpsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpsClient.class);

    private static final int SERVER_PORT = 9095;
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PATH = "/hello/sayHello";
    private static final String TRUSTSTORE_PATH = "/home/chanaka/ballerina/bre/security/ballerinaTruststore.p12";
    private static final String TRUSTSTORE_PASS = "ballerina";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        HttpClientConnector httpClientConnector = factory
                .createHttpClientConnector(new HashMap<>(), getSenderConfigurationForHttp());

        String payload = "Test value";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        String response = HttpUtil.sendPostRequest(httpClientConnector, Constants.HTTP_SCHEME, SERVER_HOST,
                SERVER_PORT, SERVER_PATH, payload, headers);
        LOG.info("Response: {}", response);
    }

    private static SenderConfiguration getSenderConfigurationForHttp() {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setTrustStoreFile(TRUSTSTORE_PATH);
        senderConfiguration.setTrustStorePass(TRUSTSTORE_PASS);
        senderConfiguration.setScheme(HTTPS_SCHEME);
        return senderConfiguration;
    }
}
