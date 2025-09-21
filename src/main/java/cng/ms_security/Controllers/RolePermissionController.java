package cng.ms_security.Controllers;

import cng.ms_security.Models.Permission;
import cng.ms_security.Models.Role;
import cng.ms_security.Models.RolePermission;
import cng.ms_security.Repositories.RoleRepository;
import cng.ms_security.Repositories.RolePermissionRepository;
import cng.ms_security.Repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/role-permission")
public class RolePermissionController {
    
        @Autowired
        private RolePermissionRepository theRolePermissionRepository;

        @Autowired
        private PermissionRepository thePermissionRepository;

        @Autowired
        private RoleRepository theRoleRepository;

        @GetMapping("")
        // Listar todos los usuarios
        public List<RolePermission> find() {
            return this.theRolePermissionRepository.findAll();
        }

        // Busca el usuario por id
        @GetMapping("{id}")
        public RolePermission findById(@PathVariable String id) {
            RolePermission theRolePermission = this.theRolePermissionRepository.findById(id).orElse(null);
            return theRolePermission;
        }

        @GetMapping("permission/{permissionId}")
        public List<RolePermission> getRolesByPermission(@PathVariable String permissionId){
            return this.theRolePermissionRepository.getRolesByPermission(permissionId);
        }

        @GetMapping("role/{roleId}")
        public List<RolePermission> getPermissionByRole(@PathVariable String roleId) {
            return this.theRolePermissionRepository.getPermissionsByRole(roleId);
        }

        // Crea el usuario
        @PostMapping("permission/{permissionId}/role/{roleId}")
        public RolePermission create(@PathVariable String permissionId, @PathVariable String roleId) {
            Permission thePermission= this.thePermissionRepository.findById(permissionId).orElse(null);
            Role theRole = this.theRoleRepository.findById(roleId).orElse(null);

            if (thePermission != null && theRole != null){
                RolePermission newRolePermission = new RolePermission();
                newRolePermission.setPermission(thePermission);
                newRolePermission.setRole(theRole);

                return this.theRolePermissionRepository.save(newRolePermission);
            }else{
                return null;
            }
        }

        // Elimina el usuario
        @DeleteMapping("{id}")
        public void delete(@PathVariable String id) {
            RolePermission theRolePermission = this.theRolePermissionRepository.findById(id).orElse(null);
            if (theRolePermission != null) {
                this.theRolePermissionRepository.delete(theRolePermission);
            }
        }
    }

