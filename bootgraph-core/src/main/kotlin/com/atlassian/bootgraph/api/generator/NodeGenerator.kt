package com.atlassian.bootgraph.api.generator

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.Node
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

@FunctionalInterface
interface NodeGenerator {

    /**
     * Generates a node to be contributed to the graph.
     *
     * @return a node to be contributed to the graph (together with its connections to other nodes).
     */
    fun generateNode(factory: NodeFactory): Node
}
