package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.AuthenticatedUser;


public class TransferService
{

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();
    public AuthenticatedUser user = null;
    

    
	public TransferService(String url)
	{
		
		BASE_URL = url + "transfers";
	}
	
	//public Transfer makeEntity(Transfer transfer)
	private HttpEntity<Transfer> makeEntity(Transfer transfer)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		if(user != null)
		{
			headers.setBearerAuth(user.getToken());
		}
		
		HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
		
		return entity;
	}
	
	
	public Transfer send(Transfer transfer)
	{
		String url = BASE_URL + "/transfers/" + transfer.getAmount();
		
		Transfer transferAmount = new Transfer();
		
		HttpEntity<Transfer> entity = makeEntity(transferAmount);
		Transfer amount = restTemplate.postForObject(url, entity, Transfer.class);
		
		return amount;
	}
	
	
//	public Transfer getAccount (Account account)
//	{
//		return account;
//	
//	}
	
	
}
