package lk.avix.http;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

/**
 * An HTTP client which implemented using wso2 http-transport.
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static final int SERVER_PORT = 9095;
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PATH = "/hello/sayHello";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        HttpClientConnector httpClientConnector = factory
                .createHttpClientConnector(new HashMap<>(), getSenderConfigurationForHttps());

        String payload = "Test value";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        String response = HttpUtil.sendPostRequest(httpClientConnector, Constants.HTTPS_SCHEME, SERVER_HOST,
                SERVER_PORT, SERVER_PATH, payload, headers);
        LOG.info("Response: {}", response);
    }

    private static SenderConfiguration getSenderConfigurationForHttps() {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setScheme(Constants.HTTP_SCHEME);
        return senderConfiguration;
    }
}
