/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.http.listener.DefaultHttpConnectorListener;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.wso2.transport.http.netty.contract.Constants.HTTP_SCHEME;

/**
 * An HTTP client which implemented using wso2 http-transport.
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static final int SERVER_PORT = 9090;
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PATH = "";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        HttpClientConnector httpClientConnector = factory.createHttpClientConnector(new HashMap<>(), getSenderConfigs());
        sendPostRequest(httpClientConnector, SERVER_HOST, SERVER_PORT, SERVER_PATH);
    }

    private static SenderConfiguration getSenderConfigs() {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setScheme(HTTP_SCHEME);
        return senderConfiguration;
    }

    private static void sendPostRequest(HttpClientConnector httpClientConnector, String serverHost,
                                        int serverPort, String serverPath) {
        try {
            String testValue = "Test Message";
            HttpCarbonMessage msg = createHttpPostReq(serverHost, serverPort, serverPath, testValue);

            CountDownLatch latch = new CountDownLatch(1);
            DefaultHttpConnectorListener listener = new DefaultHttpConnectorListener(latch);
            HttpResponseFuture responseFuture = httpClientConnector.send(msg);
            responseFuture.setHttpConnectorListener(listener);

            latch.await(30, TimeUnit.SECONDS);

            HttpCarbonMessage response = listener.getHttpResponseMessage();
            String responsePayload = new BufferedReader(
                    new InputStreamReader(new HttpMessageDataStreamer(response).getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
            LOG.info("Response: {}", responsePayload);
        } catch (Exception e) {
            LOG.error("Exception occurred while running client", e);
        }
    }

    private static HttpCarbonMessage createHttpPostReq(String serverHost, int serverPort, String serverPath,
                                                       String payload) {
        HttpCarbonMessage httpPostRequest = new HttpCarbonMessage(
                new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, serverPath));
        httpPostRequest.setProperty(Constants.PROTOCOL, Constants.HTTP_SCHEME);
        httpPostRequest.setProperty(Constants.HTTP_PORT, serverPort);
        httpPostRequest.setProperty(Constants.HTTP_HOST, serverHost);
        httpPostRequest.setProperty(Constants.HTTP_METHOD, Constants.HTTP_POST_METHOD);

        ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes(Charset.forName("UTF-8")));
        httpPostRequest.addHttpContent(new DefaultLastHttpContent(Unpooled.wrappedBuffer(byteBuffer)));

        return httpPostRequest;
    }
}
