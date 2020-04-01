package com.atlassian.bootgraph.graphviz

import guru.nidi.graphviz.attribute.GraphAttr

enum class ArrowFormat(
    val graphVizFormat: GraphAttr.SplineMode
) {
    LINE(GraphAttr.SplineMode.LINE),
    SPLINE(GraphAttr.SplineMode.SPLINE),
    POLYLINE(GraphAttr.SplineMode.POLYLINE),
    ORTHO(GraphAttr.SplineMode.ORTHO),
    CURVED(GraphAttr.SplineMode.CURVED),
    NONE(GraphAttr.SplineMode.NONE)
}
