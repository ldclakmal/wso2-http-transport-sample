package lk.avix.http.passthrough;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnectorException;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class for https pass-through message processor.
 */
public class PassthroughMessageProcessor implements HttpConnectorListener {

    private static final Logger LOG = LoggerFactory.getLogger(PassthroughMessageProcessor.class);

    private ExecutorService executor = Executors.newFixedThreadPool(16);
    private HttpClientConnector clientConnector;
    private int serverPort;
    private String serverHost;

    PassthroughMessageProcessor(SenderConfiguration senderConfiguration, String serverHost, int serverPort) {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory();
        clientConnector = httpWsConnectorFactory.createHttpClientConnector(new HashMap<>(), senderConfiguration);
    }

    @Override
    public void onMessage(HttpCarbonMessage httpRequestMessage) {
        executor.execute(() -> {
            httpRequestMessage.setProperty(Constants.HTTP_HOST, serverHost);
            httpRequestMessage.setProperty(Constants.HTTP_PORT, serverPort);
            try {
                HttpResponseFuture future = clientConnector.send(httpRequestMessage);
                future.setHttpConnectorListener(new HttpConnectorListener() {
                    @Override
                    public void onMessage(HttpCarbonMessage httpResponse) {
                        executor.execute(() -> {
                            try {
                                httpRequestMessage.respond(httpResponse);
                            } catch (ServerConnectorException e) {
                                LOG.error("Error occurred during message notification : {}", e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LOG.error("Error occurred during message notification: {}", throwable.getMessage());
                    }
                });
            } catch (Exception e) {
                LOG.error("Error occurred during message processing: ", e);
            }
        });
    }

    @Override
    public void onError(Throwable throwable) {
        LOG.error("Error occurred during message notification: {}", throwable.getMessage());
    }
}
