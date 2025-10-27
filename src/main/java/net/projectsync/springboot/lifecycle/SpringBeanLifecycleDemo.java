package net.projectsync.springboot.lifecycle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class SpringBeanLifecycleDemo {
	
	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		accessSingletonBeans(context);
		accessPrototypeBeans(context);
		
		System.out.println("\n--- Closing context: singleton beans will be destroyed automatically ---");
		System.out.println("Note: Singleton bean being destroyed: " + context.getBean("singletonBean", MyBean.class).hashCode());
		context.close(); // triggers destruction of singleton beans only. steps 8 to 10 called once during application termination
	}

	private static void accessSingletonBeans(AbstractApplicationContext context) {

		System.out.println("\n--- Accessing Singleton Bean starts ---"); // steps 1 to 7 called only once during application startup. It doesn't matter how many times we request for bean
		MyBean singletonBean1 = context.getBean("singletonBean", MyBean.class);
		MyBean singletonBean2 = context.getBean("singletonBean", MyBean.class);
		System.out.println("Note: singletonBean already created during start up");
		System.out.println("--- Accessing Singleton Bean ends ---");

		System.out.println("\n--- Singleton Beans ready to use starts ---");
		System.out.println("singletonBean1 hashCode: " + singletonBean1.hashCode());
		System.out.println("singletonBean2 hashCode: " + singletonBean2.hashCode());
		// System.out.println(prototype1 != prototype2); // true
		System.out.println("--- Singleton Beans ready to use ends ---");

		System.out.println("\n--- SingletonBean destruction (Automatic)---");	
	}

	private static void accessPrototypeBeans(AbstractApplicationContext context) {

		System.out.println("\n--- Accessing Prototype Beans starts ---"); // steps 1 to 6 called each time we request for bean. It is not called during application start-up. Here it is called twice
		System.out.println("Note: PrototypeBean created on request");
		MyBean prototypeBean1 = (MyBean) context.getBean("prototypeBean");
		System.out.println();
		MyBean prototypeBean2 = (MyBean) context.getBean("prototypeBean");
		System.out.println("--- Accessing Prototype Beans ends ---");

		System.out.println("\n--- Prototype Beans ready to use starts ---");
		System.out.println("prototypeBean1 hashCode: " + prototypeBean1.hashCode());
		System.out.println("prototypeBean2 hashCode: " + prototypeBean2.hashCode());
		System.out.println("--- Prototype Beans ready to use ends ---");
		
		System.out.println("\n--- PrototypeBean destruction (Manual) for prototypeBean1 ---");
		destroyPrototypeBean(prototypeBean1);
		
		System.out.println("\n--- PrototypeBean destruction (Manual) for prototypeBean1 ---");
		destroyPrototypeBean(prototypeBean2);		
	}
	
	private static void destroyPrototypeBean(MyBean bean) {

		System.out.println("Note: Prototype bean being destroyed: " + bean.hashCode());
		bean.preDestroy();
	    bean.destroy();
	    bean.customDestroy();
	}	
}

/* 
1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: singletonBean with hashCode: 1620948027
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 1620948027
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
7. Custom init-method called
   [BPP] afterInitialization

--- Accessing Singleton Bean starts ---
Note: singletonBean already created during start up
--- Accessing Singleton Bean ends ---

--- Singleton Beans ready to use starts ---
singletonBean1 hashCode: 1620948027
singletonBean2 hashCode: 1620948027
--- Singleton Beans ready to use ends ---

--- SingletonBean destruction (Automatic)---

--- Accessing Prototype Beans starts ---
Note: PrototypeBean created on request
1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: prototypeBean with hashCode: 423583818
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 1620948027
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
   [BPP] afterInitialization

1. Bean instantiated (constructor)
2. BeanNameAware.setBeanName: prototypeBean with hashCode: 552936351
3. BeanFactoryAware.setBeanFactory
   Info: found singletonBean in container
4. ApplicationContextAware.setApplicationContext
   Info: Accessed singletonBean from context, hashCode: 1620948027
   [BPP] beforeInitialization
5. @PostConstruct called
6. InitializingBean.afterPropertiesSet
   [BPP] afterInitialization
--- Accessing Prototype Beans ends ---

--- Prototype Beans ready to use starts ---
prototypeBean1 hashCode: 423583818
prototypeBean2 hashCode: 552936351
--- Prototype Beans ready to use ends ---

--- PrototypeBean destruction (Manual) for prototypeBean1 ---
Note: Prototype bean being destroyed: 423583818
8. @PreDestroy called
9. DisposableBean.destroy called
10. Custom destroy-method called

--- PrototypeBean destruction (Manual) for prototypeBean1 ---
Note: Prototype bean being destroyed: 552936351
8. @PreDestroy called
9. DisposableBean.destroy called
10. Custom destroy-method called

--- Closing context: singleton beans will be destroyed automatically ---
Note: Singleton bean being destroyed: 1620948027
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
