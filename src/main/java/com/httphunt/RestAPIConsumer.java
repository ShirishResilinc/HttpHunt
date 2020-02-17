package com.httphunt;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import com.httphunt.exceptions.FailureStepException;
import com.httphunt.models.FailureMessage;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RestAPIConsumer {
	static {
        System.setProperty("https.protocols", "TLSv1.2");
    }
	public static String DEFAULT_USER_ID = "_Xf0C_Lb"; 
	private String userId = System.getProperty("userId");
	private final String baseUrl = "https://http-hunt.thoughtworks-labs.net/challenge";
	private Client client;

    Client getJerseyHTTPSClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = getSslContext();

        return ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .build();
    }

    private SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManager[] keyManagers = null;
        TrustManager[] trustManager = {new NoOpTrustManager()};
        SecureRandom secureRandom = new SecureRandom();
        sslContext.init(keyManagers, trustManager, secureRandom);
        SSLContext.setDefault(sslContext);

        return sslContext;
    }
    
	public RestAPIConsumer() {
		try {
				// create client on constructing object
				// for more details on constructing jersy client please follow documentation
				this.client = getJerseyHTTPSClient();
			} catch (NoSuchAlgorithmException | KeyManagementException  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Performs a get request API call
	 * @param url - input url
	 * @return - response from server
	 * @throws FailureStepException
	 */
	public Response get(String url) throws FailureStepException {
		System.out.println("Sending Get Request to url: " + this.baseUrl + url);
		if (this.userId == null){
			this.userId = DEFAULT_USER_ID;
		}
		final Response response = this.client.target(UriBuilder.fromPath(this.baseUrl + url).build())
				.request(MediaType.APPLICATION_JSON).header("userId", this.userId).get();
		System.out.println("Response recieved for url: " + this.baseUrl + url);
		// Check status from server if its 412 read message and throw exception
		if (response.getStatus() == 412) {
			FailureMessage failureResponse = response.readEntity(FailureMessage.class);
			System.out.println("Get api not responding!!! Server says " + failureResponse.getMessage());
			throw new FailureStepException(failureResponse.getMessage());	
		}
		return response;
	}
	
	/**
	 * Performs post request API call
	 * @param url - input url
	 * @param body - input request body
	 * @return - response from server
	 * @throws FailureStepException
	 */
	public Response post(String url, Entity<?> body) throws FailureStepException {
		System.out.println("Sending Post Request to url: " + this.baseUrl + url);
		if (this.userId == null){
			this.userId = DEFAULT_USER_ID;
		}
		final Response response = this.client.target(this.baseUrl + url).request(MediaType.APPLICATION_JSON).header("userId", this.userId).post(body);
		System.out.println("Response recieved for url: " + this.baseUrl + url);	
		// Check status from server if its 412 or 406 read message and throw exception
		// Else consider it successfull
		if (response.getStatus() == 412 || response.getStatus() == 406) {
			FailureMessage failureResponse = response.readEntity(FailureMessage.class);
			System.out.println("Input to post is wrong!!! Server says " + failureResponse.getMessage());
			throw new FailureStepException(failureResponse.getMessage());	
		} else {
			System.out.println("POST request is successfull");
		}
		return response;
	}
}
