package com.atlassian.bootgraph.api.model

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

open class Node(

        /**
         * The name of the node. This is the unique identifier for a node within the model.
         */
        val name: String,

        /**
         * A node can either be internal or external. Internal nodes are considered to be part of
         * the application that is graphed. External nodes are outside of the application but may interact
         * with internal nodes.
         */
        val isExternal: Boolean,

        /**
         * Optional name of a cluster to put this node into. All nodes with the same cluster name will be but into a
         * box representing this cluster.
         */
        val cluster: String?

) {

    constructor(name: String, isExternal: Boolean) :
            this(name, isExternal, null)


    /**
     * The inputs to this node from other nodes.
     */
    internal val inputs: MutableMap<Node, Edge> = HashMap()

    /**
     * The outputs from this node to other nodes.
     */
    internal val outputs: MutableMap<Node, Edge> = HashMap()

    fun addEdgeTo(toNode: Node): Node {
        return addEdgeTo(toNode, null)
    }

    fun addEdgeTo(toNode: Node, connectionLabel: String?): Node {
        val edge = Edge(this, toNode, connectionLabel)
        outputs[toNode] = edge
        toNode.inputs[this] = edge
        return this;
    }

    fun hasEdgeTo(toNode: Node): Boolean {
        return outputs.containsKey(toNode)
    }

    fun addEdgeFrom(fromNode: Node, connectionLabel: String?): Node {
        val edge = Edge(fromNode, this, connectionLabel)
        inputs[fromNode] = edge
        fromNode.outputs[this] = edge
        return this
    }

    fun addEdgeFrom(fromNode: Node): Node {
        return addEdgeFrom(fromNode, null)
    }

    fun hasEdgeFrom(fromNode: Node): Boolean {
        return inputs.containsKey(fromNode)
    }

    fun incomingEdges(): Collection<Edge> {
        return inputs.values
    }

    fun outgoingEdges(): Collection<Edge> {
        return outputs.values
    }

    fun isInCluster(): Boolean {
        return cluster != null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    /**
     * Copies this Node into a new one without the connections to other nodes.
     */
    fun shallowCopy(): Node {
        return if (isExternal) {
            ExternalNode(name, cluster)
        } else {
            InternalNode(name, cluster)
        }
    }

    override fun toString(): String {
        return name
    }

    fun toDebugString(): String {
        val stringBuilder = StringBuilder(String.format("  node '%s'", name))

        if (inputs.isEmpty() && outputs.isEmpty()) {
            stringBuilder.append(" with no edges\n")
        } else {
            stringBuilder.append(" with edges:\n")
        }

        val indent = getConnectedNodeWithLongestName()?.name?.length ?: 0

        for ((node, edge) in inputs) {
            stringBuilder.append(String.format("    %-${indent}s <-- %s\n", name, node.name))
        }
        for ((node, edge) in outputs) {
            stringBuilder.append(String.format("    %-${indent}s --> %s\n", name, node.name))
        }

        return stringBuilder.toString()
    }

    private fun getConnectedNodeWithLongestName(): Node? {
        val allNodes = HashSet<Node>()
        allNodes.addAll(inputs.keys)
        allNodes.addAll(outputs.keys)

        return allNodes.stream()
                .max(Comparator.comparingInt { node -> node.name.length })
                .orElseGet { null }
    }
}
