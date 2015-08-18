package org.openlmis.web.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient extends RestTemplate {

    public RestClient(String username, String password) {
        setRequestFactory(new HttpComponentsClientHttpRequestFactory(createClient(username, password)));
    }

    private HttpClient createClient(String username, String password) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "UTF-8");

        Credentials credentials = new UsernamePasswordCredentials(username, password);
        DefaultHttpClient httpclient = new DefaultHttpClient(params);
        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

        return httpclient;
    }

}
