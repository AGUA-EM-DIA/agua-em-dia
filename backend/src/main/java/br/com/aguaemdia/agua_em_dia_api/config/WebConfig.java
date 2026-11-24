package br.com.aguaemdia.agua_em_dia_api.config;

import br.com.aguaemdia.agua_em_dia_api.service.ArmazenamentoImagemService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Publica a pasta de fotos em /uploads/**. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ArmazenamentoImagemService armazenamento;

    public WebConfig(ArmazenamentoImagemService armazenamento) {
        this.armazenamento = armazenamento;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(armazenamento.getPasta().toUri().toString())
                .setCachePeriod(3600);
    }
}
