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
    fun exportTestApplications(graphModel: GraphModel, config: ExportConfiguration) {
        val exporter = GraphVizModelExporter(config)
        exporter.export(graphModel)
    }

    @Test
    fun exceptionOnUnknownFont() {
        assertFailsWith<UnknownFontException> {
            GraphVizModelExporter(unknownFontConfig("unknownFont"))
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
                Arguments.of(inputAndOutput(), pngConfig("inputAndOutput"))
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

private fun unknownFontConfig(outputFileName: String): ExportConfiguration {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
            .fontName("this font does not exist")
            .build()
}

private fun dotConfig(outputFileName: String): ExportConfiguration {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.dot")
            .outputFormat(OutputFormat.DOT)
            .build()
}

private fun pngConfig(outputFileName: String): ExportConfiguration {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
            .build()
}

private fun noLabelsDotConfig(outputFileName: String): ExportConfiguration {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.dot")
            .outputFormat(OutputFormat.DOT)
            .showLabelsOnArrows(false)
            .build()
}

private fun noLabelsPngConfig(outputFileName: String): ExportConfiguration {
    return ExportConfiguration.Builder()
            .outputFilePath("target/graphviz/$outputFileName.png")
            .outputFormat(OutputFormat.PNG)
            .showLabelsOnArrows(false)
            .build()
}
