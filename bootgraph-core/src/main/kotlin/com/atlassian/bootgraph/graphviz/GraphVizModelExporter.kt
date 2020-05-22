package com.atlassian.bootgraph.graphviz

import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.Node
import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.attribute.GraphAttr.COMPOUND
import guru.nidi.graphviz.attribute.GraphAttr.splines
import guru.nidi.graphviz.engine.Graphviz.fromGraph
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.model.Link
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import java.io.File

class GraphVizModelExporter(
        private val config: ExportConfiguration
) {

    fun export(graphModel: GraphModel) {

        val context = ExportContext()

        val mainGraph = mutGraph().setDirected(true)

        mainGraph.graphAttrs().add(splines(config.arrowFormat.graphVizFormat))

        mainGraph.graphAttrs().add(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
        mainGraph.graphAttrs().add(COMPOUND)

        config.fontName?.let {
            mainGraph.graphAttrs().add(Font.name(it))
            mainGraph.linkAttrs().add(Font.name(it))
            mainGraph.nodeAttrs().add(Font.name(it))
        }

        config.nodeWidthInInches?.let {
            mainGraph.nodeAttrs().add("fixedsize", "true")
            mainGraph.nodeAttrs().add("width", it.toString())
        }

        config.nodeHeightInInches?.let {
            mainGraph.nodeAttrs().add("fixedsize", "true")
            mainGraph.nodeAttrs().add("height", it.toString())
        }

        mainGraph.nodeAttrs().add(Shape.RECTANGLE)
        mainGraph.add(applicationCluster(graphModel, context))
        addInputConnections(graphModel, mainGraph, context)
        addOutputConnections(graphModel, mainGraph, context)

        exportToFile(mainGraph)
    }

    private fun addInputConnections(graphModel: GraphModel, mainGraph: MutableGraph, context: ExportContext) {
        for (targetNode in graphModel.getInternalNodes()) {
            for (inputEdge in targetNode.incomingEdges()) {

                val from = toGraphvizNode(inputEdge.from)
                val to = toGraphvizNode(targetNode)

                val link =
                        if (config.showConnectionLabels && inputEdge.label !== null)
                            Link.to(to).with(Label.of(inputEdge.label))
                        else Link.to(to)

                if (!config.showNodesInClusters) {
                    if (inputEdge.from.cluster != null) {
                        makeLinkSourceCluster(link, inputEdge.from.cluster)
                    }
                    if (targetNode.cluster != null) {
                        makeLinkTargetCluster(link, targetNode.cluster)
                    }
                }

                mainGraph.add(from
                        .addLink(link))
            }
        }
    }

    private fun addOutputConnections(graphModel: GraphModel, mainGraph: MutableGraph, context: ExportContext) {
        for (sourceNode in graphModel.getInternalNodes()) {
            for (outputEdge in sourceNode.outgoingEdges()) {

                val from = toGraphvizNode(sourceNode)
                val to = toGraphvizNode(outputEdge.to)

                val link =
                        if (config.showConnectionLabels && outputEdge.label !== null)
                            Link.to(to).with(Label.of(outputEdge.label))
                        else Link.to(to)

                if (!config.showNodesInClusters) {
                    if (sourceNode.cluster != null) {
                        makeLinkSourceCluster(link, sourceNode.cluster)
                    }
                    if (outputEdge.to.cluster != null) {
                        makeLinkTargetCluster(link, outputEdge.to.cluster)
                    }
                }

                mainGraph.add(from
                        .addLink(link))
            }
        }
    }

    private fun makeLinkTargetCluster(link: Link, clusterName: String) {
        link.add(Attributes.attr("lhead", "cluster_${clusterName}"))
    }

    private fun makeLinkSourceCluster(link: Link, clusterName: String) {
        link.add(Attributes.attr("ltail", "cluster_${clusterName}"))
    }

    private fun inputCluster(): MutableGraph {
        val cluster = mutGraph("input components")
        cluster.graphAttrs().add(Style.INVIS)
        cluster.isCluster = true
        return cluster
    }

    private fun outputCluster(): MutableGraph {
        val cluster = mutGraph("output components")
        cluster.graphAttrs().add(Style.INVIS)
        cluster.isCluster = true
        return cluster
    }

    private fun nodeCluster(node: Node, context: ExportContext): MutableGraph? {

        if (node.cluster == null) {
            return null;
        }

        // create cluster if it doesn't exist
        if (!context.clusterExists(node.cluster)) {
            val cluster = mutGraph(node.cluster)
            cluster.graphAttrs().add(Label.of(node.cluster))
            cluster.graphAttrs().add(Style.SOLID)
            cluster.isCluster = true
            context.addCluster(node.cluster, cluster)
        }

        return context.getCluster(node.cluster)
    }

    private fun toGraphvizNode(node: Node): MutableNode {
        val mutNode = mutNode(node.name)

        if (node.cluster != null && !config.showNodesInClusters) {
            mutNode.add(Style.INVIS)
            mutNode.add(Attributes.attr("fixedsize", "true"))
            mutNode.add(Attributes.attr("width", "0"))
            mutNode.add(Attributes.attr("height", "0"))
        }

        return mutNode
    }

    private fun addNodeOrCluster(node: Node, subGraph: MutableGraph, context: ExportContext) {
        val cluster = nodeCluster(node, context)

        if (cluster != null) {
            cluster.add(toGraphvizNode(node))
            subGraph.add(cluster)
        } else {
            subGraph.add(toGraphvizNode(node))
        }
    }

    private fun applicationCluster(graphModel: GraphModel, context: ExportContext): MutableGraph {
        val applicationCluster = mutGraph("application")
        applicationCluster.graphAttrs().add(Label.of(graphModel.name))
        applicationCluster.isCluster = true

        val inputCluster = inputCluster()
        val outputCluster = outputCluster()

        for (node in graphModel.getInternalNodes()) {

            if (!node.isExternal && node.inputs.keys.any { it.isExternal }) {
                addNodeOrCluster(node, inputCluster, context)
            } else if (!node.isExternal && node.outputs.keys.any { it.isExternal }) {
                addNodeOrCluster(node, outputCluster, context)
            } else {
                addNodeOrCluster(node, applicationCluster, context)
            }
        }

        applicationCluster.add(inputCluster)
        applicationCluster.add(outputCluster)

        return applicationCluster
    }

    private fun exportToFile(graph: MutableGraph) {
        val graphViz = fromGraph(graph)

        config.widthInPixels?.let {
            graphViz.width(it)
        }

        config.heightInPixels?.let {
            graphViz.height(it)
        }

        graphViz.render(config.outputFormat.graphVizFormat)
                .toFile(File(config.outputFilePath))
    }
}
