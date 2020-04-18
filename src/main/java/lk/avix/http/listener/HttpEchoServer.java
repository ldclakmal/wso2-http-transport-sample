package lk.avix.http.listener;

import com.beust.jcommander.Parameter;
import lk.avix.http.client.HttpClient;
import lk.avix.http.util.HttpUtil;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

/**
 * An HTTP server which implemented using wso2 http-transport.
 */
@SuppressWarnings("Duplicates")
public class HttpEchoServer {

    @Parameter(names = "--ssl", description = "Enable SSL", arity = 1)
    private static boolean ssl = false;

    @Parameter(names = "--http2", description = "Use HTTP/2 protocol instead of HTTP/1.1", arity = 1)
    private static boolean http2 = false;

    @Parameter(names = "--server-host", description = "Server Host")
    private static String serverHost = "localhost";

    @Parameter(names = "--server-port", description = "Server Port")
    private static int serverPort = 9191;

    @Parameter(names = "--keystore-path", description = "Keystore Path")
    private static String keystorePath =
            HttpClient.class.getClassLoader().getResource("/keystore/wso2carbon.jks").getFile();

    @Parameter(names = "--keystore-pass", description = "Keystore Password")
    private static String keystorePass = "wso2carbon";

    public static void main(String[] args) throws InterruptedException {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        float httpVersion = http2 ? 2.0f : 1.1f;
        String serverScheme = ssl ? "https" : "http";

        System.out.println(serverScheme);

        ListenerConfiguration listenerConfiguration =
                HttpUtil.getListenerConfiguration(httpVersion, serverPort, serverScheme, keystorePath, keystorePass);
        ServerConnector connector =
                factory.createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new EchoMessageListener());
        future.sync();
    }
}
