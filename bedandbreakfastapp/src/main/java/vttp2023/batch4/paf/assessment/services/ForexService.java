package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class ForexService {

	final String BASE_API_URL = "https://api.frankfurter.app/latest";
	RestTemplate restTemplate = new RestTemplate();

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		//https://api.frankfurter.app/latest?amount=10&from=GBP&to=USD
		StringBuilder sb = new StringBuilder();
		sb.append(BASE_API_URL)
			.append("?")
			.append("amount=")
			.append(amount)
			.append("&")
			.append("from=")
			.append(from)
			.append("&to=")
			.append(to);

		ResponseEntity<String> result = restTemplate.getForEntity(sb.toString(), String.class);
		//System.out.println(result);
		if(result.getStatusCode()== HttpStatusCode.valueOf(200)){
			String body = result.getBody(); 
			StringReader stringReader = new StringReader(body);
			JsonReader jsonReader = Json.createReader(stringReader);
			JsonObject jsonObject = jsonReader.readObject();
			JsonObject rates = jsonObject.getJsonObject("rates");
			JsonNumber jsonNumber = rates.getJsonNumber(to.toUpperCase());
			Float currency = Float.parseFloat(jsonNumber.toString());
			return currency;
		}

		return -1000f;
	}
}
