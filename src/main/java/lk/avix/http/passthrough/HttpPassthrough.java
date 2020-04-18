package lk.avix.http.passthrough;

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
public class HttpPassthrough {

    public void doStart(float httpVersion, String scheme, int listenerPort, String serverHost, int serverPort,
                        String serverPath, String keystorePath, String keystorePass, String truststorePath,
                        String truststorePass) {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        ListenerConfiguration listenerConfiguration =
                HttpUtil.getListenerConfiguration(httpVersion, listenerPort, scheme, keystorePath, keystorePass);
        ServerConnector connector =
                factory.createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();

        SenderConfiguration senderConfiguration =
                HttpUtil.getSenderConfiguration(httpVersion, scheme, truststorePath, truststorePass);
        future.setHttpConnectorListener(
                new PassthroughMessageProcessor(senderConfiguration, serverHost, serverPort, serverPath));
        try {
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
