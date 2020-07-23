package com.atlassian.bootgraph.api.matcher

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

internal class PackageMatcherTest {

    @Test
    fun matchesPackage() {
        val matcher = PackageMatcher("com.atlassian.bootgraph.api.matcher")
        kotlin.test.assertTrue(matcher.matches(TestBean(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

    @Test
    fun matchesParentPackage() {
        val matcher = PackageMatcher("com.atlassian.bootgraph")
        kotlin.test.assertTrue(matcher.matches(TestBean(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

    @Test
    fun doesntMatchPackage() {
        val matcher = PackageMatcher("com.atlassian.api")
        kotlin.test.assertFalse(matcher.matches(TestBean(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

}