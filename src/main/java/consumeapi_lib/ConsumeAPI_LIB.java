/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consumeapi_lib;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.fluent.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author mike
 */
public class ConsumeAPI_LIB {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        POST("IND=2&FA=101001", "http://localhost", "3000/FA");
        POST("IND=3&FA=101000", "http://localhost", "3000/FA");
        POST("IND=1&FA=100000", "http://localhost", "3000/FA");
        String response = GET("2", "http://localhost", "3000/FA");
        System.out.println(response);
        Collection<String> keys = new ArrayList<String>();// anonymous subclass
        keys.add("2");
        keys.add("1");
        keys.add("3");
        //executeAsync(keys, 100, TimeUnit.MILLISECONDS);
        List<Future<String>> out = executeAsync(keys, 100, TimeUnit.MILLISECONDS, "http://127.0.0.1","3000/FA", 4);
        for (int i = 0; i < out.size(); i++) {
            try{
            System.out.println(out.get(i).get());
            }catch(Exception e){}
        }
    }

    public static String GET(String params, String HOST, String port) {
        String response = "undefined";
        //System.out.println(HOST + ":" + port + "/" + params);
        try {
            response = Request.Get(HOST + ":" + port + "/" + params)
                    .connectTimeout(50)
                    .socketTimeout(50)
                    .execute().returnContent().asString();
        } catch (Exception e) {
        }
        return response;
    }

    public static void POST(String JSON, String HOST, String port) {
        //HttpClient httpClient = new DefaultHttpClient(); //Deprecated
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        //System.out.println("POST  "+HOST + ":" + port);
        try {
            HttpPost request = new HttpPost(HOST + ":" + port);
            StringEntity params = new StringEntity(JSON);
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            // handle response here...
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown(); //Deprecated
        }
    }

    public static List<Future<String>> executeAsync(Collection<String> keys, long timeout, TimeUnit unit, String HOST, String PORT, int nThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        ConcurrentHashMap c = new ConcurrentHashMap();
        
        List<Task> tasks = new ArrayList<>(keys.size());
        for (String key : keys) {
            tasks.add(new Task(HOST, PORT, key));
        }

        List<Future<String>> out = null;
        try{
            out = executor.invokeAll(tasks, timeout, unit);
            executor.shutdown();
            boolean awaitTermination = executor.awaitTermination(timeout+50, unit);
        }
        catch(Exception e){}
        
        return out;
    }
    
}
