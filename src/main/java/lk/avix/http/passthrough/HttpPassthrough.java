package lk.avix.http.passthrough;

import com.beust.jcommander.Parameter;
import lk.avix.http.client.HttpClient;
import lk.avix.http.util.HttpUtil;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

/**
 * An HTTP passthrough service which implemented using wso2 http-transport.
 */
@SuppressWarnings("Duplicates")
public class HttpPassthrough {

    @Parameter(names = "--ssl", description = "Enable SSL", arity = 1)
    private static boolean ssl = false;

    @Parameter(names = "--http2", description = "Use HTTP/2 protocol instead of HTTP/1.1", arity = 1)
    private static boolean http2 = false;

    @Parameter(names = "--listener-port", description = "Listener Port")
    private static int listenerPort = 9090;

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

    @Parameter(names = "--keystore-path", description = "Keystore Path")
    private static String keystorePath = HttpClient.class.getResource("/keystore/wso2carbon.jks").getFile();

    @Parameter(names = "--keystore-pass", description = "Keystore Password")
    private static String keystorePass = "wso2carbon";

    public static void main(String[] args) throws InterruptedException {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        float httpVersion = http2 ? 2.0f : 1.1f;
        String scheme = ssl ? "https" : "http";

        ListenerConfiguration listenerConfiguration =
                HttpUtil.getListenerConfiguration(httpVersion, listenerPort, scheme, keystorePath, keystorePass);
        ServerConnector connector =
                factory.createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();

        SenderConfiguration senderConfiguration =
                HttpUtil.getSenderConfiguration(httpVersion, scheme, truststorePath, truststorePass);
        future.setHttpConnectorListener(
                new PassthroughMessageProcessor(senderConfiguration, serverHost, serverPort, serverPath));
        future.sync();
    }
}
