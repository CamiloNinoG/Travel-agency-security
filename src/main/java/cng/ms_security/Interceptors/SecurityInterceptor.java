package cng.ms_security.Interceptors;

import cng.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private ValidatorsService validatorService;

    @Override
    // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("➡️ Interceptando: " + request.getMethod() + " " + request.getRequestURI());

        boolean success = this.validatorService.validationRolePermission(
                request, request.getRequestURI(), request.getMethod()
        );

        if (!success) {
            System.out.println("⛔ Acceso denegado en Interceptor");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.getWriter().write("No tienes permisos para acceder a esta ruta");
            return false; // corta la cadena -> no llega al controller
        }

        System.out.println("✅ Acceso permitido, pasa al Controller");
        return true;
    }

    @Override
    // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        System.out.println("✔️ postHandle ejecutado");
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        System.out.println("🔚 afterCompletion ejecutado");
    }
}