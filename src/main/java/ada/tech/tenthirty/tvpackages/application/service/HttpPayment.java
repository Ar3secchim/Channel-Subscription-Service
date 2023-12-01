package ada.tech.tenthirty.tvpackages.application.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Configuration
public class HttpPayment {
  private String URL; 

  @Bean
  WebClient webClient(){
      return WebClient.builder().baseUrl(URL)
              .build();
  }

  public Boolean getPaymentStatus(String idUser) {
    URL = "http://localhost:3001/";

    try {
      WebClient webClient = webClient();
    
      List<Map<String, String>> paymentList = webClient.get()
              .uri(uriBuilder -> uriBuilder.path("/payment").build(idUser))
              .retrieve()
              .onStatus(
                      status -> !status.is2xxSuccessful(),
                      response -> {
                          throw new ResponseStatusException(response.statusCode(), "Error retrieving promotions");
                      }
              )
              .bodyToFlux(new ParameterizedTypeReference<List<Map<String, String>>>() {}).blockFirst();

      if (paymentList != null) {
          return paymentList.isEmpty();
      } else {
          System.out.println("Error: Promotion is null");
      }
    } catch (ResponseStatusException ex) {
      throw new ResponseStatusException(ex.getStatusCode(), ex.getMessage());
    }
    return null;
  }
}
