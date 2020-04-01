package com.atlassian.bootgraph.graphviz

import com.atlassian.bootgraph.api.model.GraphModel
import guru.nidi.graphviz.attribute.Font
import guru.nidi.graphviz.attribute.GraphAttr.splines
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.attribute.Style
import guru.nidi.graphviz.engine.Graphviz.fromGraph
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.model.Link
import guru.nidi.graphviz.model.MutableGraph
import java.io.File

class GraphVizModelExporter(
        private val config: ExportConfiguration
) {

    fun export(graphModel: GraphModel) {

        val mainGraph = mutGraph().setDirected(true)

        mainGraph.graphAttrs().add(splines(config.arrowFormat.graphVizFormat))

        mainGraph.graphAttrs().add(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))

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
        mainGraph.add(applicationCluster(graphModel))
        addInputConnections(graphModel, mainGraph)
        addOutputConnections(graphModel, mainGraph)

        exportToFile(mainGraph, config)
    }

    private fun addInputConnections(graphModel: GraphModel, mainGraph: MutableGraph) {
        for (targetNode in graphModel.getInternalNodes()) {
            for (inputEdge in targetNode.incomingEdges()) {

                val link = if (config.showConnectionLabels && inputEdge.label !== null)
                    Link.to(mutNode(targetNode.name)).with(Label.of(inputEdge.label))
                else Link.to(mutNode(targetNode.name))

                mainGraph.add(mutNode(inputEdge.from.name)
                        .addLink(link))
            }
        }
    }

    private fun addOutputConnections(graphModel: GraphModel, mainGraph: MutableGraph) {
        for (sourceNode in graphModel.getInternalNodes()) {
            for (outputEdge in sourceNode.outgoingEdges()) {

                val link = if (config.showConnectionLabels && outputEdge.label !== null)
                    Link.to(mutNode(outputEdge.to.name)).with(Label.of(outputEdge.label))
                else Link.to(mutNode(outputEdge.to.name))

                mainGraph.add(mutNode(sourceNode.name)
                        .addLink(link))
            }
        }
    }

    private fun inputCluster(graphModel: GraphModel): MutableGraph {
        val cluster = mutGraph("input components")
        cluster.graphAttrs().add(Style.INVIS)
        cluster.isCluster = true
        return cluster
    }

    private fun outputCluster(graphModel: GraphModel): MutableGraph {
        val cluster = mutGraph("output components")
        cluster.graphAttrs().add(Style.INVIS)
        cluster.isCluster = true
        return cluster
    }

    private fun applicationCluster(graphModel: GraphModel): MutableGraph {
        val applicationCluster = mutGraph("application")
        applicationCluster.graphAttrs().add(Label.of("${graphModel.name}\\l"))
        applicationCluster.isCluster = true

        val inputCluster = inputCluster(graphModel)
        val outputCluster = outputCluster(graphModel)

        for (node in graphModel.getInternalNodes()) {
            if (!node.isExternal && node.inputs.keys.any { it.isExternal }) {
                inputCluster.add(node(node.name))
            } else if (!node.isExternal && node.outputs.keys.any { it.isExternal }) {
                outputCluster.add(node(node.name))
            } else {
                applicationCluster.add(node(node.name))
            }
        }

        applicationCluster.add(inputCluster)
        applicationCluster.add(outputCluster)

        return applicationCluster
    }

    private fun exportToFile(graph: MutableGraph, config: ExportConfiguration) {
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
