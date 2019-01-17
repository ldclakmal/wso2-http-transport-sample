package lk.avix.http.passthrough;

import lk.avix.http.util.HttpUtil;
import org.apache.log4j.BasicConfigurator;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;
import java.util.Optional;

/**
 * An HTTP passthrough service which implemented using wso2 http-transport.
 */
public class HttpPassthrough {

    private static final int CLIENT_PORT = 9090;
    private static final int SERVER_PORT = 9191;

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        ListenerConfiguration listenerConfiguration = HttpUtil
                .getListenerConfiguration(CLIENT_PORT, Optional.empty(), Optional.empty());
        ServerConnector connector = factory
                .createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);

        SenderConfiguration senderConfiguration = HttpUtil.getSenderConfiguration(Optional.empty(), Optional.empty());
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new PassthroughMessageProcessor(senderConfiguration, SERVER_PORT));
        future.sync();
    }
}
