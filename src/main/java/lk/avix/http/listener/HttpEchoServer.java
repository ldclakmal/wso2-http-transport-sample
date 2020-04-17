package lk.avix.http.listener;

import lk.avix.http.util.HttpUtil;
import org.wso2.transport.http.netty.contract.Constants;
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

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final float HTTP_VERSION = (Integer.parseInt(System.getProperty("version", "1")) == 1)
            ? Constants.HTTP_1_1 : Constants.HTTP_2_0;
    private static final String SERVER_SCHEME = SSL ? Constants.HTTPS_SCHEME : Constants.HTTP_SCHEME;
    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "9191"));
    private static final String KEYSTORE_PATH = System.getProperty("keystorepath",
            HttpEchoServer.class.getResource("/keystore/wso2carbon.jks").getFile());
    private static final String KEYSTORE_PASS = System.getProperty("keystorepass", "wso2carbon");

    public static void main(String[] args) throws InterruptedException {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        ListenerConfiguration listenerConfiguration =
                HttpUtil.getListenerConfiguration(HTTP_VERSION, SERVER_PORT, SERVER_SCHEME, KEYSTORE_PATH, KEYSTORE_PASS);
        ServerConnector connector =
                factory.createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new EchoMessageListener());
        future.sync();
    }
}
