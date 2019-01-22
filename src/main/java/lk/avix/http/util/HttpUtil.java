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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.wso2.transport.http.netty.contract.Constants.HTTPS_SCHEME;

/**
 * HTTP client utilities.
 */
public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    public static String getSampleResponse(HttpClientConnector httpClientConnector, String serverScheme,
                                           String serverHost, int serverPort, String serverPath) {
        String payload = "Test value!";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return HttpUtil.sendPostRequest(httpClientConnector, serverScheme, serverHost, serverPort, serverPath,
                payload, headers);
    }

    private static String sendPostRequest(HttpClientConnector httpClientConnector, String serverScheme,
                                          String serverHost, int serverPort, String serverPath, String payload,
                                          HashMap<String, String> headers) {
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
            return new BufferedReader(new InputStreamReader(
                    new HttpMessageDataStreamer(response).getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
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

    public static SenderConfiguration getSenderConfiguration(float httpVersion, String scheme, String truststorePath,
                                                             String truststorePass) {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setScheme(scheme);
        senderConfiguration.setHttpVersion(String.valueOf(httpVersion));
        if (scheme.equals(HTTPS_SCHEME)) {
            senderConfiguration.setTrustStoreFile(truststorePath);
            senderConfiguration.setTrustStorePass(truststorePass);
            // Enable following property if the SERVER_HOST is an IP address. Since this is not recommended,
            // please provide an valid host name.
            // senderConfiguration.setHostNameVerificationEnabled(false);
        }
        return senderConfiguration;
    }

    public static ListenerConfiguration getListenerConfiguration(float httpVersion, int port, String scheme,
                                                                 String keystorePath, String keystorePass) {
        ListenerConfiguration listenerConfiguration = ListenerConfiguration.getDefault();
        listenerConfiguration.setScheme(scheme);
        listenerConfiguration.setVersion(String.valueOf(httpVersion));
        listenerConfiguration.setPort(port);
        if (scheme.equals(HTTPS_SCHEME)) {
            listenerConfiguration.setKeyStoreFile(keystorePath);
            listenerConfiguration.setKeyStorePass(keystorePass);
        }

        return listenerConfiguration;
    }
}
