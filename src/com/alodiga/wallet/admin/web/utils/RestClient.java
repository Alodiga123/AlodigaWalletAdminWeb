package com.alodiga.wallet.admin.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.alodiga.wallet.respuestas.Response;
import com.alodiga.wallet.rest.request.UpdateTopNotificationRequest;
import com.alodiga.wallet.rest.request.UserRequest;
import com.alodiga.wallet.rest.response.UserListResponse;
import com.alodiga.wallet.rest.response.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 

public class RestClient {
	
	public String consumeRest(String urlRest,Object object) {
		String respose = "";
		try {
			 
			URL url = new URL(urlRest);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
 
			Gson gson = new Gson();
			String input = gson.toJson(object);
 
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
 
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) 
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			
 
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
 
			String output = null;
			while ((output = br.readLine()) != null) {
				respose = respose.concat(output);
				System.out.println(output);
			}
 
			conn.disconnect();
 
		} catch (MalformedURLException e) {
 
			e.printStackTrace();
		} catch (IOException e) {
 
			e.printStackTrace();
 
		}
		return respose;
	}
	
	public Object getResponse(String metod, Object request, Class clazz) {
		RestClient rest = new RestClient();
		String jsonComplejo = rest.consumeRest("http://localhost:8080/AlodigaWallet/wallet/"+metod, request);
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateFormatter()).create();
		Object response = gson.fromJson(jsonComplejo, clazz);
		return response;
	}
	
	public static Object getTestResponse(String metod, Object request, Class clazz) {
		RestClient rest = new RestClient();
		String jsonComplejo = rest.consumeRest("http://localhost:8080/AlodigaWallet/wallet/"+metod, request);
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateFormatter()).create();
		Object response = gson.fromJson(jsonComplejo, clazz);
		return response;
	}
	
	public static void main(String[] args) {
		UserRequest userRequest = new UserRequest();
		userRequest.setLogin("yalmea");
		userRequest.setEmail("mgraterol@alodiga.com");
		UserResponse userResponse = (UserResponse) getTestResponse("loadUserByLogin",userRequest,UserResponse.class);
		System.out.println("Usuario:"+userResponse.getFirstName() +" "+userResponse.getLastName());
		
		userResponse = (UserResponse) getTestResponse("loadUserByEmail",userRequest,UserResponse.class);
		System.out.println("Usuario:"+userResponse.getFirstName() +" "+userResponse.getLastName());
	
		UserListResponse userListResponse = (UserListResponse) getTestResponse("getUsers",userRequest,UserListResponse.class);
		for(UserResponse response:userListResponse.getUserResponses()) {			
			System.out.println("Usuarios:"+response.getFirstName() +" "+response.getLastName());
		}
		
		UpdateTopNotificationRequest notificationRequest = new UpdateTopNotificationRequest();
		notificationRequest.setIds("1,3");
		Response response =(Response) getTestResponse("updateUserNotifications",notificationRequest,Response.class);
		System.out.println("Response:"+response.toString());
	}

}
