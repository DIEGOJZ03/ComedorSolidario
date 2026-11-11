package com.comedorsolidario.config;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
@Configuration @EnableWebMvc
@ComponentScan({"com.comedorsolidario.controller.logistica", "com.comedorsolidario.service.logistica"})
public class LogisticaConfig {
    @Bean public InternalResourceViewResolver vistas() {
        InternalResourceViewResolver v=new InternalResourceViewResolver(); v.setPrefix("/views/logistica/"); v.setSuffix(".jsp"); return v;
    }
}
