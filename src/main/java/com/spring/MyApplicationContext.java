package com.spring;

import javax.annotation.Resource;
import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public MyApplicationContext(Class configClass) throws Exception {
        this.configClass = configClass;

        // 1、扫描
        doScan(configClass, beanDefinitionMap);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            // 如果是单例bean, 则直接创建bean
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    /**
     * 创建bean
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    public Object createBean(String beanName, BeanDefinition beanDefinition) throws Exception {
        // 拿到对应的class 对象
        Class aClass = beanDefinition.getType();
        Object instance = null;
        try {
            instance = aClass.getConstructor().newInstance();
            // 拿到所有的属性
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class) || field.isAnnotationPresent(Resource.class)){
                    field.setAccessible(true);
                    // 简化处理，直接根据属性的名称去拿
                    field.set(instance, getBean(field.getName()));
                }
            }

            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            if (instance instanceof InitializingBean) {
                ((InitializingBean)instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

        } catch (InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }


    /**
     * 获取bean
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) throws Exception {

        // 不存在抛出异常
        if (!beanDefinitionMap.containsKey(beanName)){
            throw new ClassNotFoundException();
        }

        // 拿到bean 定义
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String scope = beanDefinition.getScope();
        // 判断是否是单例bean
        if ("singleton".equals(scope)){
            Object singletonBean = singletonObjects.get(beanName);
            if (null == singletonBean){
                singletonBean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, singletonBean);
            }
            return singletonBean;
        }else if ("prototype".equals(scope)){
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }else {
            throw new ClassNotFoundException();
        }
    }


    private void doScan(Class configClass, Map<String, BeanDefinition> beanDefinitionMap) {

        if(configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            // 拿到注解的值，并且将 .  换成 /,  磁盘路径
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");
            // System.out.println("ComponentScan  path   : "  +  path);

            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            String filePath = resource.getFile();
            // System.out.println("resource  file path  " + filePath);
            File file = new File(filePath);

            if (file.isDirectory()){
                for (File listFile : file.listFiles()) {
                    //  /Users/qinjian/workspace/qj-spring/target/classes/com/qj/service/CartService.class
                    String absolutePath = listFile.getAbsolutePath();
                    absolutePath = absolutePath.substring( absolutePath.indexOf("com"),
                            absolutePath.indexOf(".class")).replace("/", ".");
                    // System.out.println("resource  file absolutePath  " + absolutePath);

                    try {
                        // 加载类
                        Class<?> loadClass = classLoader.loadClass(absolutePath);
                        // 判断是否加了@Service
                        if ( loadClass.isAnnotationPresent(Service.class) ){

                            if (BeanPostProcessor.class.isAssignableFrom(loadClass)){
                                BeanPostProcessor instance = (BeanPostProcessor) loadClass.getConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }

                            // System.out.println( "loadClass"   +  loadClass);
                            Service serviceAnnotation = loadClass.getAnnotation(Service.class);
                            String beanName = serviceAnnotation.value();
                            if ("".equals(beanName)) {
                                beanName = Introspector.decapitalize(loadClass.getSimpleName());
                            }

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(loadClass);
                            // 判断是否加了@Scope
                            if (loadClass.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = loadClass.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                beanDefinition.setScope(value);
                            }else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    }catch (ClassNotFoundException | NoSuchMethodException classNotFoundException){
                        classNotFoundException.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}