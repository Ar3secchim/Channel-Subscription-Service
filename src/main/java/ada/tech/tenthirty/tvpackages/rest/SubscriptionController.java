package ada.tech.tenthirty.tvpackages.rest;

import ada.tech.tenthirty.tvpackages.payloads.SubscriptionRequest;
import ada.tech.tenthirty.tvpackages.payloads.UpdateSubscriptionRequest;
import ada.tech.tenthirty.tvpackages.payloads.response.SubscriptionResponse;
import ada.tech.tenthirty.tvpackages.service.AddPackageSubscription;
import ada.tech.tenthirty.tvpackages.service.CreateSubscription;
import ada.tech.tenthirty.tvpackages.utils.SubscriptionConvert;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/")
@RequiredArgsConstructor
public class SubscriptionController {
    private final AddPackageSubscription addPackageSubscription;
    private final CreateSubscription createSubscription;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> creatSubscription(@RequestBody SubscriptionRequest subscription) {
        SubscriptionResponse subscriptionResponse = SubscriptionConvert.toResponse(
                createSubscription.execute(subscription)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionResponse);
    }

    @PostMapping("/update")
    public Object addNewPackage(@RequestBody UpdateSubscriptionRequest updateSubscription){
        return addPackageSubscription.execute(
                updateSubscription.getIdUser(), updateSubscription.getIdPackage()
        );
    }
}
