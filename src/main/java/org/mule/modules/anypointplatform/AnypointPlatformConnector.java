package org.mule.modules.anypointplatform;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.modules.anypointplatform.config.ConnectorConfig;
import org.mule.modules.anypointplatform.config.DefaultResponseHandler;

@Connector(name="anypoint-api-platform", friendlyName="Anypoint API Platform")
public class AnypointPlatformConnector {
    private static String API_PLATFROM_BASE_URI = "https://anypoint.mulesoft.com/apiplatform";
	
    @Config
    ConnectorConfig config;
    
    protected String getUriFor(String resource) {
    	return API_PLATFROM_BASE_URI + resource;
    }

    /**
     * Gets all the apis from the api platform
     *
     * {@sample.xml ../../../doc/anypoint-platform-connector.xml.sample anypoint-platform:greet}
     *
     * @return this list of apis
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @Processor
    public JSONObject listApis() throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet getAPIsRequest = new HttpGet(this.getUriFor("/repository/apis"));
        getAPIsRequest.addHeader("Authorization", "Bearer " + this.config.getSessionAccessToken());
        String sessionResponseBody = httpClient.execute(getAPIsRequest, new DefaultResponseHandler());
        JSONObject response = new JSONObject(sessionResponseBody);
        return response;
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}