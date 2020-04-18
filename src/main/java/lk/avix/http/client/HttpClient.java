package lk.avix.http.client;

import com.beust.jcommander.Parameter;
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

    @Parameter(names = "--ssl", description = "Enable SSL", arity = 1)
    private static boolean ssl = false;

    @Parameter(names = "--http2", description = "Use HTTP/2 protocol instead of HTTP/1.1", arity = 1)
    private static boolean http2 = false;

    @Parameter(names = "--server-host", description = "Server Host")
    private static String serverHost = "localhost";

    @Parameter(names = "--server-port", description = "Server Port")
    private static int serverPort = 9191;

    @Parameter(names = "--server-path", description = "Server Path")
    private static String serverPath = "/hello/sayHello";

    @Parameter(names = "--truststore-path", description = "Truststore Path")
    private static String truststorePath = HttpClient.class.getResource("/truststore/client-truststore.jks").getFile();

    @Parameter(names = "--truststore-pass", description = "Truststore Password")
    private static String truststorePass = "wso2carbon";

    public static void main(String[] args) {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        float httpVersion = http2 ? 2.0f : 1.1f;
        String scheme = ssl ? "https" : "http";

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
