package ada.tech.tenthirty.tvpackages.application.service;

import ada.tech.tenthirty.tvpackages.domain.Subscription;
import ada.tech.tenthirty.tvpackages.infra.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSubscriptionById {
  private final SubscriptionRepository subscriptionRepository;

  public Subscription execute(String subId) {
    return subscriptionRepository.findById(subId).orElseThrow();
  }
}
