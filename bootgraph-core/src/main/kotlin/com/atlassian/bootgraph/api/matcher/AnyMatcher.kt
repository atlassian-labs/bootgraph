package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Combines multiple matchers and matches only if any of them matches.
 */
open class AnyMatcher(
        private vararg val matchers: BeanMatcher
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        for (matcher in matchers) {
            if (matcher.matches(bean, beanName, beanDefinition, applicationContext)) {
                return true
            }
        }
        return false
    }

}

