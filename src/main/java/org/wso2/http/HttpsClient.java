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

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.util.HashMap;

import static org.wso2.transport.http.netty.contract.Constants.HTTPS_SCHEME;

/**
 * An HTTPS client which implemented using wso2 http-transport.
 */
public class HttpsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpsClient.class);

    private static final int SERVER_PORT = 9095;
    private static final String SERVER_HOST = "127.0.0.1";
    private static final String SERVER_PATH = "/hello/sayHello";
    private static final String TRUSTSTORE_PATH = "/home/wso2/projects/http-transport-sample/src/main/resources/client-truststore.jks";
    private static final String TRUSTSTORE_PASS = "wso2carbon";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        HttpWsConnectorFactory factory = new DefaultHttpWsConnectorFactory();
        HttpClientConnector httpClientConnector = factory
                .createHttpClientConnector(new HashMap<>(), getSenderConfigurationForHttp());

        String payload = "Test value";
        String response = HttpUtil.sendPostRequest(httpClientConnector, Constants.HTTP_SCHEME, SERVER_HOST,
                SERVER_PORT, SERVER_PATH, payload);
        LOG.info("Response: {}", response);
    }

    private static SenderConfiguration getSenderConfigurationForHttp() {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setTrustStoreFile(TRUSTSTORE_PATH);
        senderConfiguration.setTrustStorePass(TRUSTSTORE_PASS);
        senderConfiguration.setScheme(HTTPS_SCHEME);
        return senderConfiguration;
    }
}
