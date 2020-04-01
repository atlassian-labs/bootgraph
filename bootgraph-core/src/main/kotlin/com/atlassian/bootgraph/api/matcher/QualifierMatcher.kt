package com.atlassian.bootgraph.api.matcher

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

/**
 * Matches beans that has a specified @Qualifier annotation.
 */
class QualifierMatcher(
    private val qualifierString: String
) : BeanMatcher {

    override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
        val qualifierAnnotation = applicationContext.findAnnotationOnBean(beanName, Qualifier::class.java)
                ?: return false

        return qualifierAnnotation.value == qualifierString
    }
}
