package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.annotations.InputNode
import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.annotations.OutputNode
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

class OutputNodeMapper : BeanMapper {

    override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {

        val annotation = applicationContext.findAnnotationOnBean(beanName, OutputNode::class.java)
                ?: throw IllegalStateException("cannot find annotation @OutputNode on class ${bean.javaClass}")

        val node = InternalNode(if (annotation.name.isBlank()) beanName else annotation.name)
        val outputNode = factory.externalNode(annotation.target)
        node.addEdgeTo(outputNode, annotation.connectionLabel)
        return Optional.of(node)
    }

}
