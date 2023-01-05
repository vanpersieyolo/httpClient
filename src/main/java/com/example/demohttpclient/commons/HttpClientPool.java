package com.example.demohttpclient.commons;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

public class HttpClientPool {
    private static final int DEFAULT_MAX_TOTAL_CONN = 500;
    private static final int DEFAULT_MAX_PER_ROUTE_CONN = 50;

    private static final int CONNECT_TIMEOUT = 20 * 1000; // 10 seconds
    private static final int WAIT_TIMEOUT = 20 * 1000;
    private static final int READ_TIMEOUT = 20 * 1000;

    private static final int DEFAULT_KEEP_ALIVE_MILLISECONDS = (5 * 60 * 1000);
    private static int keepAliveValue = DEFAULT_KEEP_ALIVE_MILLISECONDS;

    private static ConnectionKeepAliveStrategy keepAliveStrategyObj = new ConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement header = it.nextElement();
                String param = header.getName();
                String value = header.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return keepAliveValue;
        }
    };

    private static PoolingHttpClientConnectionManager pool = null;
    private static CloseableHttpClient httpclient = null;

    static {
        pool = new PoolingHttpClientConnectionManager();
        pool.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE_CONN);
        pool.setMaxTotal(DEFAULT_MAX_TOTAL_CONN);

        // config timeout and cookie
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(WAIT_TIMEOUT).setSocketTimeout(READ_TIMEOUT).setCookieSpec(CookieSpecs.STANDARD)
                .build();

        httpclient = HttpClients.custom().setKeepAliveStrategy(keepAliveStrategyObj).setConnectionManager(pool)
                .setDefaultRequestConfig(config).build();
        IdleConnMon idleConnectionMonitor = new IdleConnMon(pool);
        idleConnectionMonitor.start();
    }

    private HttpClientPool() {

    }

    public static synchronized CloseableHttpClient getHttpclient() {
        return httpclient;
    }

    public static class IdleConnMon extends Thread {

        private final HttpClientConnectionManager pool;
        private volatile boolean shutdown;

        public IdleConnMon(HttpClientConnectionManager pool) {
            super();
            this.pool = pool;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        pool.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 60 sec
                        pool.closeIdleConnections(60, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
                shutdown();
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }
}
