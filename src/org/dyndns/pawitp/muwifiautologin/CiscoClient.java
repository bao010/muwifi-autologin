package org.dyndns.pawitp.muwifiautologin;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Client for MU-WiFi system running on Aruba Networks
public class CiscoClient implements LoginClient {

    // These are not regex
    static final String LOGIN_FAIL_PATTERN = "<INPUT TYPE=\"hidden\" NAME=\"err_flag\" SIZE=\"16\" MAXLENGTH=\"15\" VALUE=\"1\">";

    static final String FORM_USERNAME = "username";
    static final String FORM_PASSWORD = "password";
    static final String FORM_URL = "https://1.1.1.1/login.html";

    private DefaultHttpClient mHttpClient;

    public CiscoClient() {
        mHttpClient = Utils.createHttpClient(MySSLSocketFactory.MODE_TRUST_ALL);
    }

    public void login(String username, String password) throws IOException, LoginException {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(FORM_USERNAME, username));
        formparams.add(new BasicNameValuePair(FORM_PASSWORD, password));

        // Magic values
        formparams.add(new BasicNameValuePair("buttonClicked", "4"));
        formparams.add(new BasicNameValuePair("err_flag", "0"));
        formparams.add(new BasicNameValuePair("redirect_url", ""));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        HttpPost httppost = new HttpPost(FORM_URL);
        httppost.setEntity(entity);
        HttpResponse response = mHttpClient.execute(httppost);
        String strRes = EntityUtils.toString(response.getEntity());

        if (strRes.contains(LOGIN_FAIL_PATTERN)) {
            // login fail (extracted message from server)
            throw new LoginException("The User Name and Password combination you have entered is invalid. Please try again.");
        } else {
            // login successful
        }
    }

    public void logout() throws IOException, LoginException {
        throw new LoginException("Unsupported Operation");
    }

}