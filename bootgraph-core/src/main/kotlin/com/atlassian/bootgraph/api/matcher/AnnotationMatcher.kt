package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Matches Spring beans that are annotated with a specified annotation. Will match if the class of
 * the bean is annotated or if the factory method is annotated (i.e. the method annotated with @Bean).
 */
open class AnnotationMatcher<T : Annotation>(
    private val annotationClazz: Class<T>
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        return applicationContext.findAnnotationOnBean(beanName, annotationClazz) != null
    }

}
