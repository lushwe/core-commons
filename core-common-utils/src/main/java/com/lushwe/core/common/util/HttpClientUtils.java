package com.lushwe.core.common.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.io.DefaultHttpResponseParserFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 说明：HttpClient工具类
 *
 * @author Jack Liu
 * @date 2019-12-10 23:34
 * @since 0.1
 */
public class HttpClientUtils {


    private static PoolingHttpClientConnectionManager manager = null;
    private static CloseableHttpClient httpClient = null;

    private HttpClientUtils() {

    }

    /**
     * 获取HttpClient
     *
     * 单例模式-懒汉式
     *
     * @return
     */
    public static synchronized CloseableHttpClient getHttpClient() {

        if (httpClient == null) {

            // 注册访问协议相关的Socket工厂
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                    .build();

            // 创建连接工厂，配置写请求/解析响应处理器
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory =
                    new ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE,
                            DefaultHttpResponseParserFactory.INSTANCE);
            // DNS解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
            // 创建池化连接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);
            // 默认Socket配置
            SocketConfig defaultSocketConfig = SocketConfig.custom()
                    .setTcpNoDelay(true)
                    .build();
            manager.setDefaultSocketConfig(defaultSocketConfig);
            // 整个连接池的最大连接数
            manager.setMaxTotal(300);
            // 每个路由最大连接数
            manager.setDefaultMaxPerRoute(200);
            // 连接不活跃多长时间进行验证，默认2s，当前配置5s
            manager.setValidateAfterInactivity(5 * 1000);

            // 默认请求配置
            // setConnectTimeout 连接超时时间，2s
            // setSocketTimeout 等待数据超时时间，5s
            // setConnectionRequestTimeout 从连接池获取连接的等待超时时间
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(2 * 1000)
                    .setSocketTimeout(5 * 1000)
                    .setConnectionRequestTimeout(2 * 1000)
                    .build();

            // 创建HttpClient
            // setConnectionManagerShared 连接池不是共享模式
            // evictIdleConnections 定期回收空闲连接
            // evictExpiredConnections 定期回收过期连接
            // setConnectionTimeToLive 连接存活时间，如果不设置，则根据长连接信息决定
            // setDefaultRequestConfig 默认请求配置
            // setConnectionReuseStrategy 连接重用策略，即是否能用KeepAlive
            // setKeepAliveStrategy 长连接配置，即获取长连接生产多长时间
            // setRetryHandler 设置重试次数，默认3次，当前是禁用（根据需要开启）
            httpClient = HttpClients.custom()
                    .setConnectionManager(manager)
                    .setConnectionManagerShared(false)
                    .evictIdleConnections(60, TimeUnit.SECONDS)
                    .evictExpiredConnections()
                    .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                    .build();


            // JVM停止或重启时，关闭连接池释放掉连接（跟数据库连接池类似）
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        return httpClient;
    }
}
