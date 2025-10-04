package cng.ms_security.Controllers;

import cng.ms_security.Models.Role;
import cng.ms_security.Models.User;
import cng.ms_security.Models.UserRole;
import cng.ms_security.Repositories.UserRoleRepository;
import cng.ms_security.Repositories.UserRepository;
import cng.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {
    @Autowired
    private UserRoleRepository theUserRoleRepository;

    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private RoleRepository theRoleRepository;

    @GetMapping("")
    // Listar todos los usuarios
    public List<UserRole> find() {
        return this.theUserRoleRepository.findAll();
    }

    // Busca el usuario por id
    @GetMapping("{id}")
    public UserRole findById(@PathVariable String id) {
        UserRole theUserRole = this.theUserRoleRepository.findById(id).orElse(null);
        return theUserRole;
    }

    // roles del usuario
    @GetMapping("user/{userId}")
    public List<UserRole> getRolesByUser(@PathVariable String userId){
        return this.theUserRoleRepository.getRolesByUser(userId);
    }


    // usuarios del rol
    @GetMapping("role/{roleId}")
    public List<UserRole> getUserByRole(@PathVariable String roleId) {
        return this.theUserRoleRepository.getUsersByRole(roleId);
    }

    // Crea el usuario
    @PostMapping("user/{userId}/role/{roleId}")
    public UserRole create(@PathVariable String userId, @PathVariable String roleId) {
        User theUser= this.theUserRepository.findById(userId).orElse(null);
        Role theRole = this.theRoleRepository.findById(roleId).orElse(null);

        if (theUser != null && theRole != null){
            UserRole newUserRole = new UserRole();
            newUserRole.setUser(theUser);
            newUserRole.setRole(theRole);

            return this.theUserRoleRepository.save(newUserRole);
        }else{
            return null;
        }
    }

    // Elimina el usuario
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        UserRole theUserRole = this.theUserRoleRepository.findById(id).orElse(null);
        if (theUserRole != null) {
            this.theUserRoleRepository.delete(theUserRole);
        }
    }
}