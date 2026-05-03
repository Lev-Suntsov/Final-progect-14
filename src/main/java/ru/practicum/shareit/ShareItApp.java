package ru.practicum.shareit;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ShareItApp {
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.getConnector().setPort(8080);

		Context tomcatContext = tomcat.addContext("", null);

		AnnotationConfigWebApplicationContext applicationContext =
				new AnnotationConfigWebApplicationContext();
		applicationContext.scan("ru.practicum");
		applicationContext.setServletContext(tomcatContext.getServletContext());
		applicationContext.refresh();

		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		Wrapper dispatcherWrapper =
				Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet);
		dispatcherWrapper.addMapping("/");
		dispatcherWrapper.setLoadOnStartup(1);

		tomcat.start();
	}

}
