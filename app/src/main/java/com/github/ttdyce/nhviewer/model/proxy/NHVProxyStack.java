package com.github.ttdyce.nhviewer.model.proxy;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class NHVProxyStack extends HurlStack {
    private String proxyHost;
    private int proxyPort;

    public NHVProxyStack(String proxyHost, int proxyPort) {
        super();
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        // Start the connection by specifying a proxy server
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(proxyHost, proxyPort ));//proxy server
        return (HttpURLConnection) url
                .openConnection(proxy);
    }
}
