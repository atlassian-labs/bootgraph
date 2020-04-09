package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.annotations.OutputNode
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.api.model.Node
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

class OutputNodeMapper : BeanMapper {

    override fun mapToGraph(
            bean: Any,
            beanName: String,
            beanDefinition: BeanDefinition,
            applicationContext: ConfigurableApplicationContext,
            factory: NodeFactory): Optional<Node> {

        val annotation: OutputNode = getAnnotationFromBean(bean)
                ?: getAnnotationFromFactoryMethod(beanDefinition)
                ?: throw IllegalStateException("cannot find annotation @OutputNode on class ${bean.javaClass}")

        val node = InternalNode(if (annotation.name.isBlank()) beanName else annotation.name)
        val outputNode = factory.externalNode(annotation.target)
        node.addEdgeTo(outputNode, annotation.connectionLabel)
        return Optional.of(node)
    }

    private fun getAnnotationFromFactoryMethod(beanDefinition: BeanDefinition): OutputNode? {
        if (beanDefinition !is AnnotatedBeanDefinition) {
            return null
        }

        // Spring's MethodMetaData doesn't provide access to the actual annotation, so we're re-building it from the
        // primitive attributes.

        val annotationAttributes = beanDefinition.factoryMethodMetadata?.getAnnotationAttributes(OutputNode::class.java.name)
                ?: throw java.lang.IllegalStateException("could not find @OutputNode annotation on bean ${beanDefinition.beanClassName}")

        val name = annotationAttributes["name"] as String
        val target = annotationAttributes["target"] as String
        val connectionLabel = annotationAttributes["connectionLabel"] as String

        return OutputNode::class.constructors.first().call(name, connectionLabel, target)
    }

    private fun getAnnotationFromBean(bean: Any): OutputNode? {
        return bean.javaClass.getAnnotation(OutputNode::class.java)
    }

}
