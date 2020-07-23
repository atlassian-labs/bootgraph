package com.atlassian.bootgraph.api

import assertk.Assert
import assertk.assertions.support.expected
import com.atlassian.bootgraph.api.model.GraphModel

fun Assert<GraphModel>.containsNodes(vararg names: String) = given { actual ->
    val nodeNames = actual.getNodes()
            .map { it.name }
    for (name in names) {
        if (!nodeNames.contains(name)) {
            expected("graph to contain node with name '${name}', but it didn't. Nodes in the graph: ${nodeNames}")
        }
    }
}

fun Assert<GraphModel>.doesntContainNodes(vararg names: String) = given { actual ->
    val nodeNames = actual.getNodes()
            .map { it.name }
    for (name in names) {
        if (nodeNames.contains(name)) {
            expected("graph not to contain node with name '${name}', but it did. Nodes in the graph: ${nodeNames}")
        }
    }
}

fun Assert<GraphModel>.containsEdge(fromNodeName: String, toNodeName: String) = given { actual ->
    assertThat(actual).containsNodes(fromNodeName, toNodeName)
    val fromNode = actual.getNode(fromNodeName)
    val toNode = actual.getNode(toNodeName)
    if (!fromNode!!.hasEdgeTo(toNode!!)) {
        expected("graph to contain edge from '$fromNodeName' to '$toNodeName', but it didn't")
    }
}