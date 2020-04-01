package com.atlassian.bootgraph.api.mapper

import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.spring.SpelEvaluator
import org.springframework.core.env.Environment
import java.lang.IllegalStateException

class NodeFactory(
    val model: GraphModel,
    val environment: Environment
) {

    private val spelEvaluator = SpelEvaluator()

    fun internalNode(name: String): Node {
        val evaluatedName = spelEvaluator.evaluateSpel(name, environment)
        return model.getNode(evaluatedName) ?: InternalNode(evaluatedName)
    }

    fun externalNode(name: String): Node {
        val evaluatedName = spelEvaluator.evaluateSpel(name, environment)
        return model.getNode(evaluatedName) ?: ExternalNode(evaluatedName)
    }

    fun getExistingNode(name: String): Node {
        val evaluatedName = spelEvaluator.evaluateSpel(name, environment)
        return model.getNode(evaluatedName) ?: throw IllegalStateException("Node $evaluatedName doesn't exist.")
    }
}
