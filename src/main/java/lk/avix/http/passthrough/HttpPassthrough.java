package lk.avix.http.passthrough;

import org.apache.log4j.BasicConfigurator;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

import static org.wso2.transport.http.netty.contract.Constants.HTTP_SCHEME;

/**
 * TODO: Class level comment
 */
public class HttpPassthrough {

    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        ListenerConfiguration listenerConfiguration = getListenerConfiguration();
        ServerConnector connector = factory
                .createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);

        SenderConfiguration senderConfiguration = getSenderConfiguration();
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new PassthroughMessageProcessor(senderConfiguration));
        future.sync();
    }

    private static ListenerConfiguration getListenerConfiguration() {
        ListenerConfiguration listenerConfiguration = ListenerConfiguration.getDefault();
        listenerConfiguration.setPort(SERVER_PORT);
        listenerConfiguration.setScheme(HTTP_SCHEME);
        return listenerConfiguration;
    }

    private static SenderConfiguration getSenderConfiguration() {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setScheme(Constants.HTTP_SCHEME);
        return senderConfiguration;
    }
}
