package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class StartupBean {

    @PostConstruct
    public void init() {
        DefaultApi defaultApi = new DefaultApi();

        GameInputDto gameInput = new GameInputDto();
        gameInput.setGroupName("YaDo");

        try {
            GameDto response = defaultApi.gamePost(gameInput);
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("Fehler beim API-Aufruf: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
