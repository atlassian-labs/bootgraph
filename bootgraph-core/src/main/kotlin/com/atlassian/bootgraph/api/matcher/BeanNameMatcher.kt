package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Matches beans that have a specified name.
 */
class BeanNameMatcher(
    private val beanName: String
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        return this.beanName == beanName
    }
}
