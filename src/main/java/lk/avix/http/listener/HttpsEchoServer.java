package lk.avix.http.listener;

import lk.avix.http.util.HttpUtil;
import org.apache.log4j.BasicConfigurator;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;
import java.util.Optional;

/**
 * An HTTPS server which implemented using wso2 http-transport.
 */
public class HttpsEchoServer {

    private static final int SERVER_PORT = 9191;
    private static final String KEYSTORE_PATH = "/home/wso2/projects/http-transport-sample/src/main/resources/keystore/wso2carbon.jks";
    private static final String KEYSTORE_PASS = "wso2carbon";

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        ListenerConfiguration listenerConfiguration = HttpUtil.getListenerConfiguration(SERVER_PORT, Optional.of(KEYSTORE_PATH), Optional.of(KEYSTORE_PASS));
        ServerConnector connector = factory
                .createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new EchoMessageListener());
        future.sync();
    }


}
