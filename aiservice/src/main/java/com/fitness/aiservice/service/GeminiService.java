package com.fitness.aiservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service

public class GeminiService {

    public final WebClient webClient; //constructor wise dependency injection , because We didn’t use Lombok’s @AllArgsConstructor because we only needed to inject one specific bean (WebClient.Builder) and preferred writing the constructor manually for simplicity and readability.


//    @Value("${gemini.api.url}")
//    private String geminiUrl;
//
//    @Value("${gemini.api.key}")
//    private String geminikey;

//    private final String uri = (geminiUrl+geminikey).trim();



    public GeminiService(WebClient.Builder webClientBuillder){
        this.webClient = webClientBuillder.build();
    }


//    Structure of request object , got from postman request
    public String getAnswer(String question){
        Map<String , Object> requestBody = Map.of(
                "contents",new Object[]{
                        Map.of(
                              "parts" , new Object[]{
                                      Map.of("text",question)
                                }
                        )
                }
        );

        String response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAU45TcQIemzusGW31tgxlSMusMhk6gwjM")
                .header("Content-Type" ,"application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)//mono is like single value represntation for multiple value
                .block();

        return response;
    }



}
