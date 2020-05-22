package com.atlassian.bootgraph.graphviz

import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertFailsWith

internal class GraphVizModelExporterTests {

    @ParameterizedTest
    @MethodSource("parameters")
    fun exportTestApplications(graphModel: GraphModel, configBuilder: ExportConfiguration.Builder) {
        val exporter = GraphVizModelExporter(configBuilder.build())
        exporter.export(graphModel)
    }

    @Test
    fun exceptionOnUnknownFont() {
        assertFailsWith<UnknownFontException> {
            GraphVizModelExporter(unknownFontConfig("unknownFont").build())
        }
    }

    companion object {
        @JvmStatic
        fun parameters() = listOf(
                Arguments.of(inputFromExternalNode(), dotConfig("inputFromExternalNode")),
                Arguments.of(inputFromExternalNode(), pngConfig("inputFromExternalNode")),
                Arguments.of(inputFromExternalNode(), noLabelsDotConfig("noLabels")),
                Arguments.of(inputFromExternalNode(), noLabelsPngConfig("noLabels")),
                Arguments.of(outputToExternalNode(), dotConfig("outputToExternalNode")),
                Arguments.of(outputToExternalNode(), pngConfig("outputToExternalNode")),
                Arguments.of(inputAndOutput(), dotConfig("inputAndOutput")),
                Arguments.of(inputAndOutput(), pngConfig("inputAndOutput")),
                Arguments.of(nodesInClusters(), pngConfig("showNodesInClustersFalse").showNodesInClusters(false)),
                Arguments.of(nodesInClusters(), dotConfig("showNodesInClustersFalse").showNodesInClusters(false)),
                Arguments.of(nodesInClusters(), pngConfig("showNodesInClustersTrue").showNodesInClusters(true)),
                Arguments.of(nodesInClusters(), dotConfig("showNodesInClustersTrue").showNodesInClusters(true))
        )
    }
}

private fun inputFromExternalNode(): GraphModel {
    val a = ExternalNode("A")
    val b = InternalNode("B")
    a.addEdgeTo(b, "A to B")

    return GraphModel("my app")
            .addNodes(a, b)
}

private fun nodesInClusters(): GraphModel {
    val a = ExternalNode("A")
    val b = InternalNode("B", "cluster1")
    val c = InternalNode("C", "cluster1")
    val q = InternalNode("Q", "cluster1")
    val w = InternalNode("W", "cluster1")
    val d = InternalNode("D", "cluster3")
    val e = InternalNode("E", "cluster2")
    val f = InternalNode("F", "cluster2")
    val x = InternalNode("X", "cluster4")
    val y = InternalNode("Y", "cluster4")
    val z = InternalNode("Z", "cluster4")
    a.addEdgeTo(b, "A to B")
    a.addEdgeTo(c, "A to C")
    a.addEdgeTo(d, "A to D")
    a.addEdgeTo(e, "A to E")
    a.addEdgeTo(f, "A to F")
    c.addEdgeTo(x, "C to X")
    d.addEdgeTo(x, "D to X")
    b.addEdgeTo(y, "B to Y")

    return GraphModel("my app")
            .addNodes(a, b, c, d, e, f, q, w, x, y, z)
}

private fun outputToExternalNode(): GraphModel {
    val a = InternalNode("A")
    val b = ExternalNode("B")
    a.addEdgeTo(b, "A to B")

    return GraphModel("my app")
            .addNodes(a, b)
}

private fun inputAndOutput(): GraphModel {
    val a = ExternalNode("A")
    val b = InternalNode("B")
    val c = ExternalNode("C")
    a.addEdgeTo(b, "A to B")
    b.addEdgeTo(c, "B to C")

    return GraphModel("my app")
            .addNodes(a, b)
}

private fun unknownFontConfig(outputFileName: String): ExportConfiguration.Builder {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
            .fontName("this font does not exist")
}

private fun dotConfig(outputFileName: String): ExportConfiguration.Builder {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.dot")
            .outputFormat(OutputFormat.DOT)
}

private fun pngConfig(outputFileName: String): ExportConfiguration.Builder {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
}

private fun noLabelsDotConfig(outputFileName: String): ExportConfiguration.Builder {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.dot")
            .outputFormat(OutputFormat.DOT)
            .showLabelsOnArrows(false)
}

private fun noLabelsPngConfig(outputFileName: String): ExportConfiguration.Builder {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
            .showLabelsOnArrows(false)
}
