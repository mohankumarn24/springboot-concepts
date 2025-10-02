package net.projectsync.springboot.lifecycle;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

class MyBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

	public MyBean() {
		System.out.println("1. Bean instantiated (constructor)");
	}

	// -------------------- Aware Callbacks --------------------
	// It allows the bean to know its name as defined in the Spring container
	@Override
	public void setBeanName(String beanName) {
		System.out.println("2. BeanNameAware.setBeanName: " + beanName + " with hashCode: " + this.hashCode());
	}

	// Access the BeanFactory that created the bean
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		System.out.println("3. BeanFactoryAware.setBeanFactory");
	    if(beanFactory.containsBean("singletonBean")) {
	        System.out.println("   Info: found singletonBean in container");
	    }
	}

	// Gives the bean access to the full Spring ApplicationContext, which is a more advanced container than BeanFactory
	@Override
	public void setApplicationContext(ApplicationContext context) {
		System.out.println("4. ApplicationContextAware.setApplicationContext");
        if(context.containsBean("singletonBean")) {
            MyBean other = context.getBean("singletonBean", MyBean.class);
            System.out.println("   Info: Accessed singletonBean from context, hashCode: " + other.hashCode());
        }
	}

	// -------------------- Initialization --------------------
	@PostConstruct
	public void postConstruct() {
		System.out.println("5. @PostConstruct called");
	}

	// Perform initialization logic that depends on Spring-injected properties
	@Override
	public void afterPropertiesSet() {
		System.out.println("6. InitializingBean.afterPropertiesSet");
	}

	public void customInit() {
		System.out.println("7. Custom init-method called");
	}

	// -------------------- Destruction --------------------
	@PreDestroy
	public void preDestroy() {
		System.out.println("8. @PreDestroy called");
	}

	// Provides a callback when the bean is about to be destroyed. Only singleton beans are destroyed automatically by the Spring container
	@Override
	public void destroy() {
		System.out.println("9. DisposableBean.destroy called");
	}

	public void customDestroy() {
		System.out.println("10. Custom destroy-method called");
	}
}
