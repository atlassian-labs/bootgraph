package com.atlassian.bootgraph

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.MessageSourceResolvable
import org.springframework.core.ResolvableType
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.io.ProtocolResolver
import org.springframework.core.io.Resource
import java.util.Locale

class FakeApplicationContext : ConfigurableApplicationContext {
    override fun getMessage(p0: String, p1: Array<Any>?, p2: String?, p3: Locale): String? {
        throw NotImplementedError()
    }

    override fun getMessage(p0: String, p1: Array<Any>?, p2: Locale): String {
        throw NotImplementedError()
    }

    override fun getMessage(p0: MessageSourceResolvable, p1: Locale): String {
        throw NotImplementedError()
    }

    override fun isRunning(): Boolean {
        throw NotImplementedError()
    }

    override fun setId(p0: String) {
        throw NotImplementedError()
    }

    override fun isActive(): Boolean {
        throw NotImplementedError()
    }

    override fun addApplicationListener(p0: ApplicationListener<*>) {
        throw NotImplementedError()
    }

    override fun getBeanFactory(): ConfigurableListableBeanFactory {
        throw NotImplementedError()
    }

    override fun getResource(p0: String): Resource {
        throw NotImplementedError()
    }

    override fun getId(): String? {
        throw NotImplementedError()
    }

    override fun getClassLoader(): ClassLoader? {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBeanProvider(p0: Class<T>): ObjectProvider<T> {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBeanProvider(p0: ResolvableType): ObjectProvider<T> {
        throw NotImplementedError()
    }

    override fun getBeanNamesForType(p0: ResolvableType): Array<String> {
        throw NotImplementedError()
    }

    override fun getBeanNamesForType(p0: ResolvableType, p1: Boolean, p2: Boolean): Array<String> {
        throw NotImplementedError()
    }

    override fun getBeanNamesForType(p0: Class<*>?): Array<String> {
        throw NotImplementedError()
    }

    override fun getBeanNamesForType(p0: Class<*>?, p1: Boolean, p2: Boolean): Array<String> {
        throw NotImplementedError()
    }

    override fun getBeanNamesForAnnotation(p0: Class<out Annotation>): Array<String> {
        throw NotImplementedError()
    }

    override fun containsLocalBean(p0: String): Boolean {
        throw NotImplementedError()
    }

    override fun setEnvironment(p0: ConfigurableEnvironment) {
        throw NotImplementedError()
    }

    override fun getBeanDefinitionCount(): Int {
        throw NotImplementedError()
    }

    override fun getAutowireCapableBeanFactory(): AutowireCapableBeanFactory {
        throw NotImplementedError()
    }

    override fun addProtocolResolver(p0: ProtocolResolver) {
        throw NotImplementedError()
    }

    override fun getBeansWithAnnotation(p0: Class<out Annotation>): MutableMap<String, Any> {
        throw NotImplementedError()
    }

    override fun getParentBeanFactory(): BeanFactory? {
        throw NotImplementedError()
    }

    override fun start() {
        throw NotImplementedError()
    }

    override fun refresh() {
        throw NotImplementedError()
    }

    override fun close() {
        throw NotImplementedError()
    }

    override fun setParent(p0: ApplicationContext?) {
        throw NotImplementedError()
    }

    override fun getParent(): ApplicationContext? {
        throw NotImplementedError()
    }

    override fun getBeanDefinitionNames(): Array<String> {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBeansOfType(p0: Class<T>?): MutableMap<String, T> {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBeansOfType(p0: Class<T>?, p1: Boolean, p2: Boolean): MutableMap<String, T> {
        throw NotImplementedError()
    }

    override fun getBean(p0: String): Any {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBean(p0: String, p1: Class<T>): T {
        throw NotImplementedError()
    }

    override fun getBean(p0: String, vararg p1: Any?): Any {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBean(p0: Class<T>): T {
        throw NotImplementedError()
    }

    override fun <T : Any?> getBean(p0: Class<T>, vararg p1: Any?): T {
        throw NotImplementedError()
    }

    override fun isPrototype(p0: String): Boolean {
        throw NotImplementedError()
    }

    override fun getType(p0: String): Class<*>? {
        throw NotImplementedError()
    }

    override fun getType(p0: String, p1: Boolean): Class<*>? {
        throw NotImplementedError()
    }

    override fun getDisplayName(): String {
        throw NotImplementedError()
    }

    override fun registerShutdownHook() {
        throw NotImplementedError()
    }

    override fun isTypeMatch(p0: String, p1: ResolvableType): Boolean {
        throw NotImplementedError()
    }

    override fun isTypeMatch(p0: String, p1: Class<*>): Boolean {
        throw NotImplementedError()
    }

    override fun containsBeanDefinition(p0: String): Boolean {
        throw NotImplementedError()
    }

    override fun publishEvent(p0: Any) {
        throw NotImplementedError()
    }

    override fun isSingleton(p0: String): Boolean {
        throw NotImplementedError()
    }

    override fun getStartupDate(): Long {
        throw NotImplementedError()
    }

    override fun containsBean(p0: String): Boolean {
        throw NotImplementedError()
    }

    override fun getEnvironment(): ConfigurableEnvironment {
        throw NotImplementedError()
    }

    override fun stop() {
        throw NotImplementedError()
    }

    override fun addBeanFactoryPostProcessor(p0: BeanFactoryPostProcessor) {
        throw NotImplementedError()
    }

    override fun getApplicationName(): String {
        throw NotImplementedError()
    }

    override fun getResources(p0: String): Array<Resource> {
        throw NotImplementedError()
    }

    override fun <A : Annotation?> findAnnotationOnBean(p0: String, p1: Class<A>): A? {
        throw NotImplementedError()
    }

    override fun getAliases(p0: String): Array<String> {
        throw NotImplementedError()
    }
}