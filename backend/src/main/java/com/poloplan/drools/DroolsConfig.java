package com.poloplan.drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración base de Drools. Carga {@code META-INF/kmodule.xml} del classpath
 * y expone un {@link KieContainer} reutilizable que los servicios usarán para
 * crear sesiones stateless.
 */
@Configuration
public class DroolsConfig {

  @Bean
  public KieContainer kieContainer() {
    KieServices kieServices = KieServices.Factory.get();
    return kieServices.getKieClasspathContainer();
  }
}
