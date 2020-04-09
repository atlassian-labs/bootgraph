package com.atlassian.bootgraph.api

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition

fun <T : Annotation> isBeanAnnotated(bean: Any, beanDefinition: BeanDefinition, annotationClazz: Class<T>): Boolean {
    return isBeanAnnotatedDirectly(bean, annotationClazz) || isFactoryMethodAnnotated(beanDefinition, annotationClazz)
}

private fun <T : Annotation> isBeanAnnotatedDirectly(bean: Any, annotationClazz: Class<T>): Boolean {
    return bean.javaClass.isAnnotationPresent(annotationClazz)
}

private fun <T : Annotation> isFactoryMethodAnnotated(beanDefinition: BeanDefinition, annotationClazz: Class<T>): Boolean {
    if (beanDefinition !is AnnotatedBeanDefinition) {
        return false
    }

    return beanDefinition.factoryMethodMetadata?.isAnnotated(annotationClazz.name) ?: false
}