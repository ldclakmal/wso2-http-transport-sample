package lk.avix.http.client;

import lk.avix.http.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public void doStart(float httpVersion, String scheme, String serverHost, int serverPort,
                        String serverPath, String truststorePath, String truststorePass) {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        SenderConfiguration senderConfiguration =
                HttpUtil.getSenderConfiguration(httpVersion, scheme, truststorePath, truststorePass);
        HttpClientConnector httpClientConnector =
                factory.createHttpClientConnector(new HashMap<>(), senderConfiguration);

        String payload = "Test value!";
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", TEXT_PLAIN);

        String response = HttpUtil.sendPostRequest(httpClientConnector, scheme, serverHost, serverPort, serverPath,
                                                   payload, headerMap);
        LOG.info("Response: {}", response);
    }
}
