package com.atlassian.bootgraph.graphviz

class ExportConfiguration private constructor(
        val outputFilePath: String,
        val outputFormat: OutputFormat,
        val showConnectionLabels: Boolean,
        val arrowFormat: ArrowFormat,
        val fontName: String?,
        val nodeWidthInInches: Float? = null,
        val nodeHeightInInches: Float? = null,
        val widthInPixels: Int? = null,
        val heightInPixels: Int? = null,
        val showNodesInClusters: Boolean
) {

    /**
     * Builds a default configuration.
     */
    constructor() : this(
            Builder()
    )

    private constructor(builder: Builder) : this(
            builder.outputFilePath ?: "target/bootgraph/bootgraph.png",
            builder.outputFormat ?: OutputFormat.PNG,
            builder.showLabelsOnArrows ?: true,
            builder.arrowFormat ?: ArrowFormat.SPLINE,
            FontFinder().findFont(builder.fontName),
            builder.nodeWidthInInches,
            builder.nodeHeightInInches,
            builder.widthInPixels,
            builder.heightInPixels,
            builder.showNodesInClusters ?: true
    )

    class Builder {
        var outputFilePath: String? = null
            private set

        var outputFormat: OutputFormat? = null
            private set

        var showLabelsOnArrows: Boolean? = null
            private set

        var arrowFormat: ArrowFormat? = null
            private set

        var fontName: String? = null
            private set

        var nodeWidthInInches: Float? = null
            private set

        var nodeHeightInInches: Float? = null
            private set

        var widthInPixels: Int? = null
            private set

        var heightInPixels: Int? = null
            private set

        var showNodesInClusters: Boolean? = true
            private set

        /**
         * The path to the file into which to export the graph.
         * Defaults to "target/bootgraph/bootgraph.png".
         */
        fun outputFilePath(outputFilePath: String) = apply { this.outputFilePath = outputFilePath }

        /**
         * The output file format into which the graph should be exported.
         * Defaults to PNG.
         */
        fun outputFormat(outputFormat: OutputFormat) = apply { this.outputFormat = outputFormat }

        /**
         * Set this to false to hide the labels on connection arrows since they may cause a graph to become cluttered.
         * Defaults to TRUE.
         */
        fun showLabelsOnArrows(showLabelsOnArrows: Boolean) = apply { this.showLabelsOnArrows = showLabelsOnArrows }

        /**
         * Format of the arrows between nodes.
         * Defaults to SPLINE (curved arrows).
         */
        fun arrowFormat(arrowFormat: ArrowFormat) = apply { this.arrowFormat = arrowFormat }

        /**
         * Name of the font to use for labels.
         * Defaults to "Arial".
         */
        fun fontName(fontName: String) = apply { this.fontName = fontName }

        /**
         * If set, all nodes will have this width in inches. Causes long labels to overflow.
         * If left unset, each node will be sized individually so that the label text fits.
         */
        fun nodeWidthInInches(nodeWidthInInches: Float) = apply { this.nodeWidthInInches = nodeWidthInInches }

        /**
         * If set, all nodes will have this height in inches. Causes long labels to overflow.
         * If left unset, each node will be sized individually so that the label text fits.
         */
        fun nodeHeightInInches(nodeHeightInInches: Float) = apply { this.nodeHeightInInches = nodeHeightInInches }

        /**
         * The width of the exported graph image. If no width is specified, GraphViz will determine a fitting width.
         * Increase the width for a better resolution.
         */
        fun widthInPixels(widthInPixels: Int) = apply { this.widthInPixels = widthInPixels }

        /**
         * The height of the exported graph image. If no height is specified, GraphViz will determine a fitting height.
         * Increase the height for a better resolution.
         */
        fun heightInPixels(heightInPixels: Int) = apply { this.heightInPixels = heightInPixels }

        /**
         * If true (default) nodes with a cluster name are shown. If false, only the cluster will be shown.
         * Arrows to / from beans with a cluster name are drawn to / from the cluster, then.
         */
        fun showNodesInClusters(showNodesInClusters: Boolean) = apply { this.showNodesInClusters = showNodesInClusters }

        fun build(): ExportConfiguration = ExportConfiguration(this)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }
}
