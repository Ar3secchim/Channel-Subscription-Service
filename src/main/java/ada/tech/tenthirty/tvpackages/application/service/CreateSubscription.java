package ada.tech.tenthirty.tvpackages.application.service;

import ada.tech.tenthirty.tvpackages.domain.Package;
import ada.tech.tenthirty.tvpackages.domain.Subscription;
import ada.tech.tenthirty.tvpackages.domain.User;
import ada.tech.tenthirty.tvpackages.application.payloads.SubscriptionRequest;
import ada.tech.tenthirty.tvpackages.infra.queue.NotifyInvoiceProducer;
import ada.tech.tenthirty.tvpackages.infra.queue.ScheduleTechnicalVisitProducer;
import ada.tech.tenthirty.tvpackages.infra.queue.payloads.NotifyInvoiceRequest;
import ada.tech.tenthirty.tvpackages.infra.queue.payloads.ScheduleTechnicalVisitRequest;
import ada.tech.tenthirty.tvpackages.infra.repository.SubscriptionRepository;
import ada.tech.tenthirty.tvpackages.infra.repository.UserRepository;
import ada.tech.tenthirty.tvpackages.utils.FormatDate;
import ada.tech.tenthirty.tvpackages.utils.SubscriptionConvert;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSubscription {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ScheduleTechnicalVisitProducer technicalVisitQueue;
    private final NotifyInvoiceProducer notifyInvoiceQueue;


    public Subscription execute(SubscriptionRequest subscriptionRequest) {
        User user = new User();
        user.setTransactionId(subscriptionRequest.getUserId());
        userRepository.save(user);

        Subscription subscription = SubscriptionConvert.toEntity(subscriptionRequest);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);

        NotifyInvoice(subscriptionRequest.getUserId(), subscription.getListPackage());
        ScheduleTechnicalVisit(subscriptionRequest.getTransactionId(), true);
        return subscription;
    }

    private void ScheduleTechnicalVisit(String idUser, Boolean newUser){
        ScheduleTechnicalVisitRequest scheduleTechnicalVisitRequest = new ScheduleTechnicalVisitRequest();
        scheduleTechnicalVisitRequest.setIdUser(idUser);
        scheduleTechnicalVisitRequest.setNewUSer(newUser);

        try {
            technicalVisitQueue.execute(scheduleTechnicalVisitRequest);
        } catch (JsonProcessingException e) {
            log.error("Não foi possível enviar a mensagem ao destinatário", e);
            throw new RuntimeException(e);
        }
    }

    private void  NotifyInvoice(String idUser, List<Package> listPackage){
        NotifyInvoiceRequest notifyInvoiceProducer = new NotifyInvoiceRequest();

        notifyInvoiceProducer.setIdUser(idUser);
        notifyInvoiceProducer.setIssueDate(FormatDate.dayMothYears());
        notifyInvoiceProducer.setListPackage(listPackage);

        try{
            notifyInvoiceQueue.execute(notifyInvoiceProducer);
        } catch (JsonProcessingException e) {
            log.error("Não foi possível enviar a mensagem ao destinatário", e);
            throw new RuntimeException(e);
        }
    }
}
