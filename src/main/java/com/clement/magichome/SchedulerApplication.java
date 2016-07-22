package com.clement.magichome;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * This is the main class that launch the spring application.
 * 
 * @author Clement_Soullard
 *
 */
@SpringBootApplication
public class SchedulerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}

	@Bean
	@SuppressWarnings("static-method")
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		// tomcat.addAdditionalTomcatConnectors(createConnector());
		// tomcat.addContextValves(createRemoteIpValves());
		// tomcat.setContextPath("/tvscheduler");
		// tomcat.getAdditionalTomcatConnectors();
		return tomcat;
	}

	// private static RemoteIpValve createRemoteIpValves() {
	// RemoteIpValve remoteIpValve = new RemoteIpValve();
	// remoteIpValve.setRemoteIpHeader("x-forwarded-for");
	// remoteIpValve.setProtocolHeader("x-forwarded-proto");
	// return remoteIpValve;
	// }

	private static Connector createConnector() {
		Connector connector = new Connector("AJP/1.3");
		connector.setPort(8009);
		return connector;
	}
}