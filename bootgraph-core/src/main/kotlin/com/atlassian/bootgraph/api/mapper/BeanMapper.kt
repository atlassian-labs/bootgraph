package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.api.model.Node
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

@FunctionalInterface
interface BeanMapper {

    /**
     * Maps a given Spring bean to a graph. The returned Node and all connecting nodes will
     * be contributed to the graph that is created by BootGraph.
     *
     * @return a Node with potentially connected nodes to be added to the graph. Return an empty Optional if the mapper
     * should not contribute anything to the graph.
     */
    fun mapToGraph(
            bean: Any,
            beanName: String,
            beanDefinition: BeanDefinition,
            applicationContext: ConfigurableApplicationContext,
            factory: NodeFactory): Optional<Node>
}
