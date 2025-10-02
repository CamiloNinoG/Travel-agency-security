package cng.ms_security.Controllers;

import cng.ms_security.Models.Permission;
import cng.ms_security.Models.Session;
import cng.ms_security.Models.User;
import cng.ms_security.Repositories.SessionRepository;
import cng.ms_security.Repositories.UserRepository;
import cng.ms_security.Services.EncryptionService;
import cng.ms_security.Services.FAService;
import cng.ms_security.Services.JwtService;
// import cng.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.time.ZoneId;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;
    @Autowired
    private SessionRepository theSessionRepository;
    @Autowired
    private FAService theFAService;
/*
    private ValidatorsService theValidatorsService;

    @PostMapping("permissions-validation")
    public boolean permissionsValidation(final HttpServletRequest request,
                                         @RequestBody Permission thePermission) {
        boolean success=this.theValidatorsService.validationRolePermission(request,thePermission.getUrl(),thePermission.getMethod());
        return success;
    }*/

    @PostMapping("login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String,Object> theResponse=new HashMap<>();
        User theActualUser=this.theUserRepository.getUserByEmail(theNewUser.getEmail());

        if(theActualUser!=null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){
            String code = theFAService.generateCode();

            Session session = new Session();
            session.setUser(theActualUser);
            session.setCode2FA(code);
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(60);
            Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
            session.setExpiration(expirationDate);

            Session savedSession = theSessionRepository.save(session);

            try{
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://localhost:5000/api/v1/send-email";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HashMap<String, String> emailRequest = new HashMap<>();
                emailRequest.put("to", theActualUser.getEmail());
                emailRequest.put("subject", "Tu código de verificación 2FA");
                emailRequest.put("message", "Hola, tu código de verificación es: " + code);

                HttpEntity<HashMap<String, String>> requestEntity = new HttpEntity<>(emailRequest, headers);

                restTemplate.postForObject(url, requestEntity, String.class);
            }catch(Exception e){
                System.out.println("Error enviando correo: "+e.getMessage());
            }

            theResponse.put("sessionId", savedSession.get_id());
            theResponse.put("status", "pending-2fa");
            return theResponse;

        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return  theResponse;
        }
    }

    @PostMapping("verify-2fa")
    public HashMap<String,Object> verify2FA(@RequestBody HashMap<String, String> payload,
                                            final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();

        String sessionId = payload.get("sessionId");
        String code = payload.get("code");

        Session theSession = theSessionRepository.findById(sessionId).orElse(null);

        if (theSession == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no encontrada");
            return theResponse;
        }

        if (theSession.getExpiration().before(new java.util.Date())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión expirada");
            return theResponse;
        }

        if (!theSession.getCode2FA().equals(code)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Código incorrecto");
            return theResponse;
        }

        User theUser = theSession.getUser();
        String token = theJwtService.generateToken(theUser);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = now.format(formatter);

        theSession.setToken(token);
        theSessionRepository.save(theSession);

        try{
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:5000/api/v1/send-email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HashMap<String, String> emailRequest = new HashMap<>();
            emailRequest.put("to", theUser.getEmail());
            emailRequest.put("subject", "Nuevo acceso a la App");
            emailRequest.put("message", "Hola, " + theUser.getName() + ". Se detectó un nuevo inicio de sesión en tu cuenta el día " + fechaHora + ". Si no fuiste tu, cambia tu contraseña de inmediato");

            HttpEntity<HashMap<String, String>> requestEntity = new HttpEntity<>(emailRequest, headers);

            restTemplate.postForObject(url, requestEntity, String.class);
        }catch(Exception e){
            System.out.println("Error enviando correo: "+e.getMessage());
        }

        theUser.setPassword("");
        theResponse.put("token", token);
        theResponse.put("user", theUser);
        theResponse.put("status", "success");
        return theResponse;
    }
}
