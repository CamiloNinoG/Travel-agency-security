package cng.ms_security.Configurations;

import cng.ms_security.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

/*
        registry.addInterceptor(securityInterceptor)
               .addPathPatterns("/api/**") //dice que siempre se intercepten las urls
               .excludePathPatterns("/api/public/**"); //solo el login esta excepto, porque es publica.
        */
        // CREAR UN USUARIO - PERMISO - ROL
        // USUARIO -> AGREGAR ROLE -> AGEGAR PERMISO.

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // importante si usas cookies o auth
    }
}
