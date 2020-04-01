package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.api.model.Node
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

/**
 * Maps a bean into a simple internal graph node using the bean name as a graph label. Use this matcher to include beans into
 * the graph without any special requirements.
 */
class InternalBeanMapper : BeanMapper {

    override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {
        return Optional.of(factory.internalNode(beanName))
    }

}