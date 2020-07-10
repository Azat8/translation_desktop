package mkh.azat.api;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class API {
	static String url = "http://localhost:3001/translation";
	private void sendPost() throws UnsupportedEncodingException {
 
        
	}
	
	private void sendGet() {}
	
	public static void saveTranslation(HashMap<String, String> translationList) throws Exception {
		HttpPost post = new HttpPost(url);

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        
        Set set = translationList.entrySet();
        Iterator iterator = set.iterator();
        
        while(iterator.hasNext()) {        	
        	Map.Entry mentry = (Map.Entry)iterator.next();
        	urlParameters.add(new BasicNameValuePair(mentry.getKey().toString(), mentry.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));

        try {
        	CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post); 
			System.out.println(EntityUtils.toString(response.getEntity()));
		
        } catch (Exception e) {
        	e.printStackTrace();
        }

	}
}
