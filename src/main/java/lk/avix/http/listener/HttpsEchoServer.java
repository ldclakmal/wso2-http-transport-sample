package lk.avix.http.listener;

import org.apache.log4j.BasicConfigurator;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

import static org.wso2.transport.http.netty.contract.Constants.HTTPS_SCHEME;

/**
 * An HTTP server which implemented using wso2 http-transport.
 */
public class HttpsEchoServer {

    private static final int SERVER_PORT = 9191;
    private static final String KEYSTORE_PATH = "/home/wso2/projects/http-transport-sample/src/main/resources/keystore/wso2carbon.jks";
    private static final String KEYSTORE_PASS = "wso2carbon";

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        ListenerConfiguration listenerConfiguration = getListenerConfiguration();
        ServerConnector connector = factory
                .createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new EchoMessageListener());
        future.sync();
    }

    private static ListenerConfiguration getListenerConfiguration() {
        ListenerConfiguration listenerConfiguration = ListenerConfiguration.getDefault();
        listenerConfiguration.setPort(SERVER_PORT);
        listenerConfiguration.setScheme(HTTPS_SCHEME);
        listenerConfiguration.setKeyStoreFile(KEYSTORE_PATH);
        listenerConfiguration.setKeyStorePass(KEYSTORE_PASS);
        return listenerConfiguration;
    }
}
