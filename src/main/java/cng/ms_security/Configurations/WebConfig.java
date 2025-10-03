package cng.ms_security.Configurations;

import cng.ms_security.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(securityInterceptor)
               .addPathPatterns("/api/**") //dice que siempre se intercepten las urls
               .excludePathPatterns("/api/public/**"); //solo el login esta excepto, porque es publica.
        // CREAR UN USUARIO - PERMISO - ROL
        // USUARIO -> AGREGAR ROLE -> AGEGAR PERMISO.

    }
}