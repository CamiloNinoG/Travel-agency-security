package cng.ms_security.Controllers;


import cng.ms_security.Models.Session;
import cng.ms_security.Models.User;
import cng.ms_security.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping ("/api/sessions")

public class SessionController {
    @Autowired
    private SessionRepository theSessionRepository;

    @GetMapping("")
    public List<Session> find(){
        return this.theSessionRepository.findAll();
    }

    @GetMapping("{id}")
    public Session findById(@PathVariable String id){
        Session theSession=this.theSessionRepository.findById(id).orElse(null);
        return theSession;
    }
    @PostMapping
    public Session create(@RequestBody Session newSession){
        return this.theSessionRepository.save(newSession);
    }

    @PutMapping("{id}")
    public Session update(@PathVariable String id, @RequestBody Session newSession){
        Session actualSession=this.theSessionRepository.findById(id).orElse(null);
        if(actualSession!=null){
            actualSession.setExpiration(newSession.getExpiration());
            actualSession.setToken(newSession.getToken());
            actualSession.setCode2FA(newSession.getCode2FA());
            this.theSessionRepository.save(actualSession);
            return actualSession;
        }else{
            return null;
        }
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        Session theSession=this.theSessionRepository.findById(id).orElse(null);
        if (theSession!=null){
            this.theSessionRepository.delete(theSession);
        }
    }

    @PostMapping("{id}/resend-code")
    public ResponseEntity<?> resendCode(@PathVariable String id) {
        Session theSession = this.theSessionRepository.findById(id).orElse(null);

        if (theSession == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sesión no encontrada");
        }

        if (theSession.getExpiration() != null && theSession.getExpiration().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El código ya expiró");
        }

        User theUser = theSession.getUser();
        if (theUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La sesión no tiene un usuario asociado");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:5000/api/v1/send-email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HashMap<String, String> emailRequest = new HashMap<>();
            emailRequest.put("to", theUser.getEmail());
            emailRequest.put("subject", "Reenvio código de verificación 2FA");
            emailRequest.put("message", "Hola, recuerda que este es tu codigo de verificación: " + theSession.getCode2FA());

            HttpEntity<HashMap<String, String>> requestEntity = new HttpEntity<>(emailRequest, headers);
            restTemplate.postForObject(url, requestEntity, String.class);

            return ResponseEntity.ok("Código reenviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar correo: " + e.getMessage());
        }
    }

}