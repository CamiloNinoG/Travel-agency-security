package cng.ms_security.Services;

import cng.ms_security.Models.*;
import cng.ms_security.Repositories.PermissionRepository;
import cng.ms_security.Repositories.RolePermissionRepository;
import cng.ms_security.Repositories.UserRepository;
import cng.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ValidatorsService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PermissionRepository thePermissionRepository;
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    public boolean validationRolePermission(HttpServletRequest request,
                                            String url,
                                            String method) {
        boolean success = false;

        User theUser = this.getUser(request);


        if (theUser != null) {
            System.out.println("✅ Usuario autenticado: " + theUser.getEmail() + " | ID: " + theUser.get_id());
            System.out.println("➡️ URL original: " + url + " | Método: " + method);

            // 🔹 Quitar prefijo /api
            url = url.replaceFirst("^/api", "");
            System.out.println("🛠 URL sin prefijo /api: " + url);

            // Normalización de URL
            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");
            System.out.println("🔄 URL normalizada: " + url);

            Permission thePermission = this.thePermissionRepository.getPermission(url, method);

            System.out.println("🔎 Permiso encontrado: " + (thePermission != null ? thePermission.getUrl() : "❌ NULL"));
            List<UserRole> roles = this.theUserRoleRepository.getRolesByUser(theUser.get_id());
            System.out.println("👤 Roles asociados al usuario: " + roles.size());

            int i = 0;
            while (i < roles.size() && !success) {
                UserRole actual = roles.get(i);
                Role theRole = actual.getRole();
                System.out.println("➡️ Revisando rol: " + (theRole != null ? theRole.getName() : "❌ NULL"));

                if (theRole != null && thePermission != null) {
                    RolePermission theRolePermission = this.theRolePermissionRepository
                            .getRolePermission(theRole.get_id(), thePermission.get_id());

                    if (theRolePermission != null) {
                        System.out.println("✅ Permiso concedido por el rol: " + theRole.getName());
                        success = true;
                    } else {
                        System.out.println("❌ Rol " + theRole.getName() + " no tiene permiso para esta acción.");
                    }
                } else {
                    System.out.println("❌ Rol o permiso nulo, no se puede validar.");
                }
                i++;
            }
        } else {
            System.out.println("❌ No se pudo extraer usuario desde el token");
        }

        return success;
    }

    public User getUser(final HttpServletRequest request) {
        User theUser = null;
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("📥 Header Authorization: " + authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            System.out.println("🔑 Token extraído: " + token);

            User theUserFromToken = jwtService.getUserFromToken(token);

            if (theUserFromToken != null) {
                System.out.println("✅ Usuario extraído del token con ID: " + theUserFromToken.get_id());
                theUser = this.theUserRepository.findById(theUserFromToken.get_id())
                        .orElse(null);

                if (theUser != null) {
                    System.out.println("✅ Usuario encontrado en BD: " + theUser.getEmail());
                } else {
                    System.out.println("❌ Usuario no existe en BD con ID: " + theUserFromToken.get_id());
                }
            } else {
                System.out.println("❌ No se pudo extraer usuario desde el token (posible firma inválida o expirado).");
            }
        } else {
            System.out.println("❌ Header vacío o sin prefijo Bearer");
        }

        return theUser;
    }
}