package ada.tech.tenthirty.tvpackages.application.service;

import org.springframework.stereotype.Service;

@Service
public class GetOpenInvoice {
  private final HttpPayment httpPayment;

  public GetOpenInvoice(HttpPayment httpPayment) {
    this.httpPayment = httpPayment;
  }

  public boolean execute(String idUser) {
    return httpPayment.getPaymentStatus(idUser);
  }
}
