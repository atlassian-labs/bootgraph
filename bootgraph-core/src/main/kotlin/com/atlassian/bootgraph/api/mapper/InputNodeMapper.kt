package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.annotations.InputNode
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

class InputNodeMapper : BeanMapper {

    override fun mapToGraph(
            bean: Any,
            beanName: String,
            beanDefinition: BeanDefinition,
            applicationContext: ConfigurableApplicationContext,
            factory: NodeFactory): Optional<Node> {

        val annotation: InputNode = getAnnotationFromBean(bean)
                ?: getAnnotationFromFactoryMethod(beanDefinition)
                ?: throw IllegalStateException("cannot find annotation @InputNode on class ${bean.javaClass}")

        val node = InternalNode(if (annotation.name.isBlank()) beanName else annotation.name)
        val inputNode = factory.externalNode(annotation.source)
        node.addEdgeFrom(inputNode, annotation.connectionLabel)
        return Optional.of(node)
    }

    private fun getAnnotationFromFactoryMethod(beanDefinition: BeanDefinition): InputNode? {
        if (beanDefinition !is AnnotatedBeanDefinition) {
            return null
        }

        // Spring's MethodMetaData doesn't provide access to the actual annotation, so we're re-building it from the
        // primitive attributes.

        val annotationAttributes = beanDefinition.factoryMethodMetadata?.getAnnotationAttributes(InputNode::class.java.name)
                ?: throw java.lang.IllegalStateException("could not find @InputNode annotation on bean ${beanDefinition.beanClassName}")

        val name = annotationAttributes["name"] as String
        val source = annotationAttributes["source"] as String
        val connectionLabel = annotationAttributes["connectionLabel"] as String

        return InputNode::class.constructors.first().call(name, connectionLabel, source)
    }

    private fun getAnnotationFromBean(bean: Any): InputNode? {
        return bean.javaClass.getAnnotation(InputNode::class.java)
    }

}
