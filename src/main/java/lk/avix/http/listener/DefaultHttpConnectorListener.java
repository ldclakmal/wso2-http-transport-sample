package lk.avix.http.listener;

import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.util.concurrent.CountDownLatch;

/**
 * A connector listener for HTTP.
 */
public class DefaultHttpConnectorListener implements HttpConnectorListener {

    private HttpCarbonMessage httpMessage;
    private Throwable throwable;
    private CountDownLatch latch;

    public DefaultHttpConnectorListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onMessage(HttpCarbonMessage httpMessage) {
        this.httpMessage = httpMessage;
        latch.countDown();
    }

    @Override
    public void onError(Throwable throwable) {
        this.throwable = throwable;
        latch.countDown();
    }

    public HttpCarbonMessage getHttpResponseMessage() {
        return httpMessage;
    }

    public Throwable getHttpErrorMessage() {
        return throwable;
    }
}

