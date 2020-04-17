package lk.avix.http.client;

import lk.avix.http.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

import static org.wso2.transport.http.netty.contract.Constants.TEXT_PLAIN;

/**
 * An HTTP client which implemented using wso2 http-transport.
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final float HTTP_VERSION = (Integer.parseInt(System.getProperty("version", "1")) == 1)
            ? Constants.HTTP_1_1 : Constants.HTTP_2_0;
    private static final String SERVER_SCHEME = SSL ? Constants.HTTPS_SCHEME : Constants.HTTP_SCHEME;
    private static final String SERVER_HOST = System.getProperty("host", "localhost");
    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "9191"));
    private static final String SERVER_PATH = System.getProperty("path", "/hello/sayHello");
    private static final String TRUSTSTORE_PATH = System.getProperty("truststorepath",
            HttpClient.class.getResource("/truststore/client-truststore.jks").getFile());
    private static final String TRUSTSTORE_PASS = System.getProperty("truststorepass", "wso2carbon");

    public static void main(String[] args) {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        SenderConfiguration senderConfiguration =
                HttpUtil.getSenderConfiguration(HTTP_VERSION, SERVER_SCHEME, TRUSTSTORE_PATH, TRUSTSTORE_PASS);
        HttpClientConnector httpClientConnector =
                factory.createHttpClientConnector(new HashMap<>(), senderConfiguration);

        String payload = "Test value!";
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", TEXT_PLAIN);

        String response = HttpUtil.sendPostRequest(httpClientConnector, SERVER_SCHEME, SERVER_HOST, SERVER_PORT,
                SERVER_PATH, payload, headerMap);
        LOG.info("Response: {}", response);
    }
}
