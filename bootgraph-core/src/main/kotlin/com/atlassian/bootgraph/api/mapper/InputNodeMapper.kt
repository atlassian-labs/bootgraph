package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.annotations.InputNode
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

class InputNodeMapper : BeanMapper {

    override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {

        val annotation = applicationContext.findAnnotationOnBean(beanName, InputNode::class.java)
                ?: throw IllegalStateException("cannot find annotation @InputNode on class ${bean.javaClass}")

        val node = InternalNode(if (annotation.name.isBlank()) beanName else annotation.name)
        val inputNode = factory.externalNode(annotation.source)
        node.addEdgeFrom(inputNode, annotation.connectionLabel)
        return Optional.of(node)
    }

}
