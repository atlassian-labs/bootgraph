package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.api.model.Node
import org.springframework.core.env.Environment
import java.util.Optional

class SpelEvaluator {

    /**
     * Takes a node and evaluates all Spring expressions in connected nodes and edge labels. Returns a new model with the
     * evaluated expressions.
     */
    fun evaluateSpringExpressions(originalModel: Node, factory: NodeFactory, environment: Environment): Node {
        val newModel = evaluateSpel(originalModel, factory, environment)

        for (inputConnection in originalModel.incomingEdges()) {
            val newInputNode = evaluateSpel(inputConnection.from, factory, environment)
            newModel.addEdgeFrom(newInputNode, evaluateSpelOptionally(inputConnection.label, environment))
        }

        for (outputConnection in originalModel.outgoingEdges()) {
            val newOutputNode = evaluateSpel(outputConnection.to, factory, environment)
            newModel.addEdgeTo(newOutputNode, evaluateSpelOptionally(outputConnection.label, environment))
        }

        return newModel
    }

    private fun evaluateSpel(model: Node, factory: NodeFactory, environment: Environment): Node {
        return if (model is InternalNode) factory.internalNode(evaluateSpel(model.name, environment))
        else factory.externalNode(evaluateSpel(model.name, environment))
    }

    private fun evaluateSpelOptionally(string: String?, environment: Environment): String? {
        if (string == null) {
            return null
        }
        return evaluateSpel(string, environment)
    }

    fun evaluateSpel(string: String, environment: Environment): String {
        return try {
            environment.resolvePlaceholders(string)
        } catch (e: Exception) {
            string
        }
    }
}