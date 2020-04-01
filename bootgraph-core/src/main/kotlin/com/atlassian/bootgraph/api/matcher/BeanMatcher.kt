package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

@FunctionalInterface
interface BeanMatcher {

    fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean
}
