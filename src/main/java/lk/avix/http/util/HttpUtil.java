package lk.avix.http.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lk.avix.http.listener.DefaultHttpConnectorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.wso2.transport.http.netty.contract.Constants.HTTPS_SCHEME;
import static org.wso2.transport.http.netty.contract.Constants.HTTP_SCHEME;

/**
 * HTTP client utilities.
 */
public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    public static String sendPostRequest(HttpClientConnector httpClientConnector, String serverScheme, String serverHost,
                                         int serverPort, String serverPath, String payload, HashMap<String, String> headers) {
        try {
            HttpCarbonMessage msg = createHttpPostReq(serverScheme, serverHost, serverPort, serverPath, payload);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                msg.setHeader(entry.getKey(), entry.getValue());
            }

            CountDownLatch latch = new CountDownLatch(1);
            DefaultHttpConnectorListener listener = new DefaultHttpConnectorListener(latch);
            HttpResponseFuture responseFuture = httpClientConnector.send(msg);
            responseFuture.setHttpConnectorListener(listener);

            latch.await(30, TimeUnit.SECONDS);

            HttpCarbonMessage response = listener.getHttpResponseMessage();
            String responsePayload = new BufferedReader(
                    new InputStreamReader(new HttpMessageDataStreamer(response).getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
            return responsePayload;
        } catch (Exception e) {
            LOG.error("Exception occurred while running client", e);
            return "";
        }
    }

    private static HttpCarbonMessage createHttpPostReq(String serverScheme, String serverHost, int serverPort,
                                                       String serverPath, String payload) {
        HttpCarbonMessage httpPostRequest = new HttpCarbonMessage(
                new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, serverPath));
        httpPostRequest.setProperty(Constants.PROTOCOL, serverScheme);
        httpPostRequest.setProperty(Constants.HTTP_HOST, serverHost);
        httpPostRequest.setProperty(Constants.HTTP_PORT, serverPort);
        httpPostRequest.setProperty(Constants.TO, serverPath);
        httpPostRequest.setProperty(Constants.HTTP_METHOD, Constants.HTTP_POST_METHOD);

        ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes(Charset.forName("UTF-8")));
        httpPostRequest.addHttpContent(new DefaultLastHttpContent(Unpooled.wrappedBuffer(byteBuffer)));

        return httpPostRequest;
    }

    public static SenderConfiguration getSenderConfiguration(Optional<String> truststorePath,
                                                             Optional<String> truststorePass) {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setScheme(Constants.HTTP_SCHEME);
        if (truststorePath.isPresent() && truststorePass.isPresent()) {
            senderConfiguration.setScheme(Constants.HTTPS_SCHEME);
            senderConfiguration.setTrustStoreFile(truststorePath.get());
            senderConfiguration.setTrustStorePass(truststorePass.get());
            // Enable following property if the SERVER_HOST is an IP address. Since this is not recommended,
            // please provide an valid host name.
            // senderConfiguration.setHostNameVerificationEnabled(false);
        }
        return senderConfiguration;
    }

    public static ListenerConfiguration getListenerConfiguration(int serverPort, Optional<String> keystorePath,
                                                                 Optional<String> keystorePass) {
        ListenerConfiguration listenerConfiguration = ListenerConfiguration.getDefault();
        listenerConfiguration.setPort(serverPort);
        listenerConfiguration.setScheme(HTTP_SCHEME);
        if (keystorePath.isPresent() && keystorePass.isPresent()) {
            listenerConfiguration.setScheme(HTTPS_SCHEME);
            listenerConfiguration.setKeyStoreFile(keystorePath.get());
            listenerConfiguration.setKeyStorePass(keystorePass.get());
        }

        return listenerConfiguration;
    }
}
