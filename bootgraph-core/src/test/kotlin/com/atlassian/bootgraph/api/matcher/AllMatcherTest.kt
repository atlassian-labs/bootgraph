package com.atlassian.bootgraph.api.matcher

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext

internal class AllMatcherTest {

    @Test
    fun allMatch() {
        val matcher = AllMatcher(matching(), matching());
        kotlin.test.assertTrue(matcher.matches(Object(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

    @Test
    fun oneMatches(){
        val matcher = AllMatcher(matching(), notMatching());
        kotlin.test.assertFalse(matcher.matches(Object(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

    @Test
    fun noneMatch(){
        val matcher = AllMatcher(notMatching(), notMatching());
        kotlin.test.assertFalse(matcher.matches(Object(), "beanName", mock(BeanDefinition::class.java), mock(ConfigurableApplicationContext::class.java)))
    }

    fun matching(): BeanMatcher{
        return object : BeanMatcher {
            override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
                return true
            }
        }
    }

    fun notMatching(): BeanMatcher{
        return object : BeanMatcher {
            override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
                return false
            }
        }
    }

}