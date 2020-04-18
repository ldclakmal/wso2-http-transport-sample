package lk.avix.http.listener;

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
public class HttpEchoServer {

    public void doStart(float httpVersion, String scheme, int serverPort, String keystorePath, String keystorePass) {
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();

        ListenerConfiguration listenerConfiguration =
                HttpUtil.getListenerConfiguration(httpVersion, serverPort, scheme, keystorePath, keystorePass);
        ServerConnector connector =
                factory.createServerConnector(new ServerBootstrapConfiguration(new HashMap<>()), listenerConfiguration);
        ServerConnectorFuture future = connector.start();
        future.setHttpConnectorListener(new EchoMessageListener());
        try {
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
