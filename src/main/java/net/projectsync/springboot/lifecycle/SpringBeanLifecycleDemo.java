package net.projectsync.springboot.lifecycle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class SpringBeanLifecycleDemo {
	
	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		printSingletonBeanLogs(context);
		printPrototypeBeanLogs(context);
		
		context.close(); // triggers destruction of singleton beans only. steps 8 to 10 called once during application termination
	}

	private static void printSingletonBeanLogs(AbstractApplicationContext context) {

		System.out.println("\n--- Accessing Singleton Bean starts ---"); // steps 1 to 7 called only once during application startup. It doesn't matter how many times we request for bean
		MyBean singletonBean1 = (MyBean) context.getBean("singletonBean");
		MyBean singletonBean2 = (MyBean) context.getBean("singletonBean");
		System.out.println("Note: singletonBean already created during start up");
		System.out.println("--- Accessing Singleton Bean ends ---");

		System.out.println("\n--- Beans ready to use starts ---");
		System.out.println("singletonBean1 hashCode: " + singletonBean1.hashCode());
		System.out.println("singletonBean2 hashCode: " + singletonBean2.hashCode());
		// System.out.println(prototype1 != prototype2); // true
		System.out.println("--- Beans ready to use ends ---");

		System.out.println("\n--- SingletonBean destruction (Automatic)---");	
	}

	private static void printPrototypeBeanLogs(AbstractApplicationContext context) {

		System.out.println("\n--- Accessing Prototype Beans starts ---"); // steps 1 to 6 called each time we request for bean. It is not called during application start-up. Here it is called twice
		MyBean prototypeBean1 = (MyBean) context.getBean("prototypeBean");
		System.out.println();
		MyBean prototypeBean2 = (MyBean) context.getBean("prototypeBean");
		System.out.println("--- Accessing Prototype Beans ends ---");

		System.out.println("\n--- Beans ready to use starts ---");
		System.out.println("prototypeBean1 hashCode: " + prototypeBean1.hashCode());
		System.out.println("prototypeBean2 hashCode: " + prototypeBean2.hashCode());
		System.out.println("--- Beans ready to use ends ---");
		
		System.out.println("\n--- PrototypeBean destruction (Manual) for prototypeBean1 ---");
		destroyPrototypeBean(prototypeBean1);
		
		System.out.println("\n--- PrototypeBean destruction (Manual) for prototypeBean1 ---");
		destroyPrototypeBean(prototypeBean2);		
	}
	
	private static void destroyPrototypeBean(MyBean bean) {

		bean.preDestroy();
	    bean.destroy();
	    bean.customDestroy();
	}
	
	// Or, if your bean implements DisposableBean and you want a generic call:
	/*
	if (prototype instanceof DisposableBean) {
	    ((DisposableBean) prototype).destroy();
	}
	*/
	
}

/* 
1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: singletonBean with hashCode: 201576232
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 201576232
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
7. Custom init-method called
   [BPP] afterInitialization

--- Accessing Singleton Bean starts ---
Note: singletonBean already created during start up
--- Accessing Singleton Bean ends ---

--- Beans ready to use starts ---
singletonBean1 hashCode: 201576232
singletonBean2 hashCode: 201576232
--- Beans ready to use ends ---

--- SingletonBean destruction (Automatic)---

--- Accessing Prototype Beans starts ---
1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: prototypeBean with hashCode: 1810742349
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 201576232
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
   [BPP] afterInitialization

1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: prototypeBean with hashCode: 154319946
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 201576232
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
   [BPP] afterInitialization
--- Accessing Prototype Beans ends ---

--- Beans ready to use starts ---
prototypeBean1 hashCode: 1810742349
prototypeBean2 hashCode: 154319946
--- Beans ready to use ends ---

--- PrototypeBean destruction (Manual) for prototypeBean1 ---
8. @PreDestroy called
9. DisposableBean.destroy called
10. Custom destroy-method called

--- PrototypeBean destruction (Manual) for prototypeBean1 ---
8. @PreDestroy called
9. DisposableBean.destroy called
10. Custom destroy-method called
8. @PreDestroy called
9. DisposableBean.destroy called
10. Custom destroy-method called

 */



/*
| Step | Method / Callback                                   | Called By                                                    | Bean Scope            | Purpose / Significance                                                                           | Example / Effect in Code                           |               |
| ---- | --------------------------------------------------- | ------------------------------------------------------------ | --------------------- | ------------------------------------------------------------------------------------------------ | -------------------------------------------------- | ------------- |
| 1    | **Constructor** `MyBean()`                          | Spring instantiates bean                                     | Singleton & Prototype | Creates the bean instance; sets default state or initial data                                    | Initializes `data` and `randomValue`               |               |
| 2    | `setBeanName(String beanName)`                      | Spring container (`BeanNameAware`)                           | Singleton & Prototype | Informs the bean of its name in the container; allows name-dependent logic                       | Updates `data` to `"Data set in setBeanName"`      |               |
| 3    | `setBeanFactory(BeanFactory beanFactory)`           | Spring container (`BeanFactoryAware`)                        | Singleton & Prototype | Gives access to BeanFactory; can inspect or fetch other beans; perform container-dependent setup | Checks for `singletonBean`, modifies `randomValue` |               |
| 4    | `setApplicationContext(ApplicationContext context)` | Spring container (`ApplicationContextAware`)                 | Singleton & Prototype | Gives full ApplicationContext access; can fetch beans, access environment, publish events        | Accesses `singletonBean`, updates `data`           |               |
| 5    | `@PostConstruct` `postConstruct()`                  | Spring container after dependencies are injected             | Singleton & Prototype | Called after all injections; used for setup that depends on injected properties                  | Prints current `data` and `randomValue`            |               |
| 6    | `afterPropertiesSet()`                              | Spring container (`InitializingBean`)                        | Singleton & Prototype | Provides callback for initialization logic or validation after properties are set                | Validates `randomValue`, warns if invalid          |               |
| 7    | Custom init method `customInit()`                   | Spring container if defined via `@Bean(initMethod="...")`    | Singleton & Prototype | Final initialization step; any setup required before bean is ready                               | Updates `data` to `"...                            | initialized"` |
| 8    | `@PreDestroy` `preDestroy()`                        | Spring container on `context.close()`                        | Singleton only        | Cleanup before destruction; resource release                                                     | Logs cleanup message                               |               |
| 9    | `destroy()`                                         | Spring container (`DisposableBean`)                          | Singleton only        | Interface-based destruction; cleanup logic                                                       | Releases resources like connections                |               |
| 10   | Custom destroy method `customDestroy()`             | Spring container if defined via `@Bean(destroyMethod="...")` | Singleton only        | Last destruction step; can perform additional cleanup                                            | Final cleanup message                              |               |


Notes:
 - Singleton beans: All steps (1–10) are executed automatically by Spring.
 - Prototype beans: Steps 8–10 are not automatic; you need to call preDestroy(), destroy(), and customDestroy() manually if cleanup is needed.
 - Step order is exactly the Spring bean lifecycle sequence.
 - The data / randomValue examples show how bean state can change at each lifecycle stage.
*/
