package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Matches beans that are of a specified type.
 */
class TypeMatcher<T : Any>(
    private val clazz: Class<T>
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        return clazz.isInstance(bean)
    }
}
