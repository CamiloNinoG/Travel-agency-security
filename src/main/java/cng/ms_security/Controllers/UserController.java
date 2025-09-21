package cng.ms_security.Controllers;

import cng.ms_security.Models.Session;
import cng.ms_security.Models.User;
import cng.ms_security.Repositories.SessionRepository;
import cng.ms_security.Repositories.UserRepository;
import cng.ms_security.Services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
        @Autowired
        private UserRepository theUserRepository;
        @Autowired
        private SessionRepository theSessionRepository;

        @Autowired
        private EncryptionService theEncryptionService;

        @GetMapping("")
        public List<User> find(){
            System.out.println(">>> Entró al controlador GET /api/users <<<");
            return this.theUserRepository.findAll();
        }

        @GetMapping("{id}")
        public User findById(@PathVariable String id){
            User theUser=this.theUserRepository.findById(id).orElse(null);
            return theUser;
        }

        @PostMapping
        public User create(@RequestBody User newUser){
            // VALIDAR QUE EL USUARIO NO EXISTA CON  EL MISMO CORREO, consulta en la db.
            newUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
            return this.theUserRepository.save(newUser);
        }

        @PutMapping("{id}")
        public User update(@PathVariable String id, @RequestBody User newUser){
            User actualUser=this.theUserRepository.findById(id).orElse(null);

            if(actualUser!=null){
                actualUser.setName(newUser.getName());
                actualUser.setEmail(newUser.getEmail());
                actualUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
                this.theUserRepository.save(actualUser);
                return actualUser;
            }
            else{
                return null;
            }
        }

        @DeleteMapping("{id}")
        public void delete(@PathVariable String id){
            User theUser=this.theUserRepository.findById(id).orElse(null);

            if (theUser!=null){
                this.theUserRepository.delete(theUser);
            }
        }

        @PostMapping("{userId}/session/{sessionId}")
        public void matchSession(@PathVariable String userId, @PathVariable String sessionId){
            Session theSession=this.theSessionRepository.findById(sessionId).orElse(null);
            User theUser=this.theUserRepository.findById(userId).orElse(null);

            if(theUser !=null && theSession != null){

                theSession.setUser(theUser);
                this.theSessionRepository.save(theSession);
            }
        }

    }

