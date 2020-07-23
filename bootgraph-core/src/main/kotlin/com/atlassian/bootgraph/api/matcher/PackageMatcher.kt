package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Matches Spring beans that are in a specified package (i.e. their fully qualified name starts with the given package
 * name).
 */
open class PackageMatcher(
        private val packageName: String
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        return bean.javaClass.name.startsWith(packageName)
    }

}

