package com.lushwe.core.common.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * 说明：TODO 写点注释吧
 *
 * @author Jack Liu
 * @date 2019-12-11 00:05
 * @since 0.1
 */
public class HttpClientUtilsTest {


    @Test
    public void test() throws IOException {

        String result = null;

        HttpResponse response = null;

        try {
            HttpGet get = new HttpGet("https://api.github.com/users/lushwe");

            response = HttpClientUtils.getHttpClient().execute(get);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            } else {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }

        System.out.println(result);
    }
}
