package managementapp.managementapp.services;

import managementapp.managementapp.models.ActivationToken;
import managementapp.managementapp.models.UserApp;
import managementapp.managementapp.repositories.ActivationTokenRepository;
import managementapp.managementapp.repositories.UserAppRepository;
import managementapp.managementapp.security.HashingAlogorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ActivationTokenService {
    @Autowired
    ActivationTokenRepository activationTokenRepository;
    @Autowired
    UserAppRepository userAppRepository;
    @Autowired
    LogService LogService;

    public UUID generateActivationToken(UserApp userToRegister){
        UUID token = UUID.randomUUID();
        ActivationToken activationToken = ActivationToken.builder()
                .id(UUID.randomUUID())
                .hmacValue(HashingAlogorithm.calculateHmac(token.toString()))
                .userApp(userToRegister)
                .dateTimeStart(LocalDateTime.now())
                .dateTimeEnd(LocalDateTime.now().plusDays(1))
                .activated(false)
                .build();
        activationTokenRepository.save(activationToken);
        LogService.INFO("Generated activation token for user: " + userToRegister.getUsername());
        return token;
    }


    public ResponseEntity<?> activate(UUID token){
        ActivationToken activationToken = activationTokenRepository.findByHmacValue(HashingAlogorithm.calculateHmac(token.toString()));
        if(activationToken == null){
            LogService.ERROR("Invalid activation token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid token");
        }
        if(activationToken.getActivated()){
            LogService.INFO("Account is already active.");
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Account is already active.");
        }
        if(activationToken.isExpired()){
            LogService.WARN("Token has expired");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token has expired");
        }
        UserApp userApp = userAppRepository.findById(activationToken.getUserApp().getId()).orElse(null);
        if(userApp == null){
            LogService.ERROR("User does not exist.");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User does not exist.");
        }
        userApp.setActive(true);
        userAppRepository.save(userApp);
        activationToken.setActivated(true);
        activationTokenRepository.save(activationToken);
        LogService.INFO("Activated account for user: " + userApp.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isExpired(ActivationToken activationToken){
        return LocalDateTime.now().isAfter(activationToken.getDateTimeEnd());
    }
}
