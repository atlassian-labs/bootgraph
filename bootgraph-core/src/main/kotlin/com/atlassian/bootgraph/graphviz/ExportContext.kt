package com.atlassian.bootgraph.graphviz

import guru.nidi.graphviz.model.MutableGraph

/**
 * Provides state for an export process.
 */
class ExportContext {

    val clusters: MutableMap<String, MutableGraph> = HashMap()

    fun addCluster(name: String, cluster: MutableGraph) {
        clusters[name] = cluster
    }

    fun getCluster(name: String): MutableGraph {
        return clusters[name]
                ?: throw IllegalStateException(String.format("cluster with name '%s' not in context!", name))
    }

    fun clusterExists(name: String?): Boolean {
        if (name == null) {
            return false
        }
        return clusters.containsKey(name)
    }

}