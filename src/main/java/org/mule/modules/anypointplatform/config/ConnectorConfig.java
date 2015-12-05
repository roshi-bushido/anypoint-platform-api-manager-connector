package org.mule.modules.anypointplatform.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;

@ConnectionManagement(friendlyName = "Anypoint API Platform Configuration")
public class ConnectorConfig {
    private static String ANYPOINT_ACCOUNT_LOGIN = "https://anypoint.mulesoft.com/accounts/login";
    private static String ANYPOINT_SESSION_LOGIN = "https://anypoint.mulesoft.com/apiplatform/session";
    
    private String anypointUsername;
    private String anypointPassword;
    private String accountAccessToken;
    private String sessionAccessToken;

    /**
     * Connect
     *
     * @param username A username
     * @param password A password
     * @throws ConnectionException
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String username, @Password String password) throws ConnectionException {
    	this.anypointUsername = username;
    	this.anypointPassword = password;
    	
        CloseableHttpClient httpClient = HttpClients.createDefault();

        List<NameValuePair> anypointAccountForm = new ArrayList<NameValuePair>();
        anypointAccountForm.add(new BasicNameValuePair("username", this.anypointUsername));
        anypointAccountForm.add(new BasicNameValuePair("password", this.anypointPassword));

        try {
            HttpPost anypointAccountLoginPost = new HttpPost(ANYPOINT_ACCOUNT_LOGIN);
            HttpEntity accountEntity = new UrlEncodedFormEntity(anypointAccountForm);
            anypointAccountLoginPost.setEntity(accountEntity);

            String accountResponseBody = httpClient.execute(anypointAccountLoginPost, new DefaultResponseHandler());
            JSONObject account = new JSONObject(accountResponseBody);
            	
            this.accountAccessToken = account.getString("access_token");
            
            List<NameValuePair> anypointSessionForm = new ArrayList<NameValuePair>();
            anypointSessionForm.add(new BasicNameValuePair("token", this.accountAccessToken));

            HttpPost anypointSessionLoginPost = new HttpPost(ANYPOINT_SESSION_LOGIN);
            HttpEntity sessionEntity = new UrlEncodedFormEntity(anypointSessionForm);
            anypointSessionLoginPost.setEntity(sessionEntity);

            String sessionResponseBody = httpClient.execute(anypointSessionLoginPost, new DefaultResponseHandler());
            JSONObject session = new JSONObject(sessionResponseBody);
            this.sessionAccessToken = session.getString("token");
        } catch (Exception e) {
        	throw new ConnectionException(org.mule.api.ConnectionExceptionCode.INCORRECT_CREDENTIALS, e.getMessage(), e.getMessage(), e);
        }
    }

    /**
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
    	this.sessionAccessToken = null;
    	this.accountAccessToken = null;
    }

    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {
    	return (!StringUtils.isEmpty(this.accountAccessToken) && !StringUtils.isEmpty(this.sessionAccessToken));
    }

    /**
     * Are we connected
     */
    @ConnectionIdentifier
    public String connectionId() {
        return this.accountAccessToken;
    }

	public String getAnypointUsername() {
		return anypointUsername;
	}

	public void setAnypointUsername(String anypointUsername) {
		this.anypointUsername = anypointUsername;
	}

	public String getAnypointPassword() {
		return anypointPassword;
	}

	public void setAnypointPassword(String anypointPassword) {
		this.anypointPassword = anypointPassword;
	}

	public String getSessionAccessToken() {
		return sessionAccessToken;
	}
	
}