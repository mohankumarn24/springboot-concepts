package net.projectsync.springboot.lifecycle;

import org.springframework.beans.factory.config.BeanPostProcessor;

class MyBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (bean instanceof MyBean) {
			System.out.println("   [BPP] beforeInitialization");
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean instanceof MyBean) {
			System.out.println("   [BPP] afterInitialization");
		}
		return bean;
	}
}
