package com.atlassian.bootgraph.graphviz

import guru.nidi.graphviz.engine.Format

enum class OutputFormat(
    val graphVizFormat: Format
) {
    PNG(Format.PNG),
    DOT(Format.DOT);
}
