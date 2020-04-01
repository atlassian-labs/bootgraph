package com.atlassian.bootgraph.api

import com.atlassian.bootgraph.graphviz.ExportConfiguration
import com.atlassian.bootgraph.graphviz.GraphVizModelExporter
import com.atlassian.bootgraph.api.filter.GraphModelFilter
import com.atlassian.bootgraph.api.generator.EdgeGenerator
import com.atlassian.bootgraph.api.generator.ExternalNodeGenerator
import com.atlassian.bootgraph.api.generator.InternalNodeGenerator
import com.atlassian.bootgraph.api.generator.NodeGenerator
import com.atlassian.bootgraph.api.mapper.BeanMapper
import com.atlassian.bootgraph.spring.SpringNodeFactory
import com.atlassian.bootgraph.api.mapper.InternalBeanMapper
import com.atlassian.bootgraph.api.matcher.BeanMatcher
import com.atlassian.bootgraph.api.matcher.BeanNameMatcher
import com.atlassian.bootgraph.api.matcher.QualifierMatcher
import com.atlassian.bootgraph.api.matcher.TypeMatcher
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext

/**
 * Entrypoint to the API for creating a graph from a Spring Boot application context.
 */
class BootGraph private constructor(
        val applicationName: String,
        val mappers: MutableMap<BeanMatcher, BeanMapper>,
        val filter: GraphModelFilter? = null,
        val applicationContext: ConfigurableApplicationContext,
        val exportConfig: ExportConfiguration,
        val generators: List<NodeGenerator>
) {

    private constructor (builder: Builder) : this(
            builder.applicationName ?: throw IllegalArgumentException("Missing property 'applicationName'!"),
            builder.mappers,
            builder.filter,
            builder.applicationContext ?: throw IllegalArgumentException("Missing property 'applicationContext'!"),
            builder.exportConfig ?: ExportConfiguration(),
            builder.generators
    )

    class Builder {
        var applicationName: String? = null
            private set

        var filter: GraphModelFilter? = null
            private set

        var applicationContext: ConfigurableApplicationContext? = null
            private set

        var exportConfig: ExportConfiguration? = null
            private set

        val mappers: MutableMap<BeanMatcher, BeanMapper> = HashMap()

        val generators: MutableList<NodeGenerator> = ArrayList()

        /**
         * Defines the application name to show in the export.
         */
        fun applicationName(applicationName: String) =
                apply { this.applicationName = applicationName }

        /**
         * Adds a matcher and mapper pair to the configuration. A matcher defines which beans should be considered
         * to be included in the exported graph. A mapper maps these beans into a model that can be represented as
         * a graph.
         * By default, the configuration will contain some matchers and mappers that evaluate the BootGraph annotations
         * like @InputNode and @OutputNode.
         */
        fun mapper(matcher: BeanMatcher, mapper: BeanMapper) =
                apply { this.mappers[matcher] = mapper }

        /**
         * Adds a NodeGenerator to the configuration. A NodeGenerator contributes nodes to the graph that are not
         * a Spring bean. Use this to add nodes to the graph to visualize connections to external systems or components.
         */
        fun generator(generator: NodeGenerator) =
                apply { this.generators.add(generator) }

        /**
         * Include all beans of the specified as nodes in the graph.
         *
         * This is convenience for <code>mapper(TypeMatcher, InternalBeanMapper)</code>.
         */
        fun <T : Any> includeBeansOfType(beanClass: Class<T>) =
                apply {
                    val matcher = TypeMatcher(beanClass)
                    val mapper = InternalBeanMapper()
                    mapper(matcher, mapper)
                }

        /**
         * Include the bean with the specified bean name as a node in the graph.
         *
         * This is convenience for <code>mapper(BeanNameMatcher, InternalBeanMapper)</code>.
         */
        fun includeBeanWithName(beanName: String) =
                apply {
                    val matcher = BeanNameMatcher(beanName)
                    val mapper = InternalBeanMapper()
                    mapper(matcher, mapper)
                }

        /**
         * Include the bean with the specified qualifier as a node in the graph.
         *
         * This is convenience for <code>mapper(QualifierMatcher, InternalBeanMapper)</code>.
         *
         * @see Spring @Qualifier annotation
         */
        fun includeBeanWithQualifier(qualifier: String) =
                apply {
                    val matcher = QualifierMatcher(qualifier)
                    val mapper = InternalBeanMapper()
                    mapper(matcher, mapper)
                }

        /**
         * Adds a custom external node to the graph. "External" means that the node will be displayed outside
         * of the application in the graph.
         *
         * This is convenience for <code>generator(ExternalNodeGenerator)</code>.
         */
        fun withExternalNode(name: String) =
                apply {
                    generator(ExternalNodeGenerator(name))
                }

        /**
         * Adds a custom internal node to the graph. "Internal" means that the node will be displayed as
         * part of the application in the graph.
         *
         * This is convenience for <code>generator(InternalNodeGenerator)</code>.
         */
        fun withInternalNode(name: String) =
                apply {
                    generator(InternalNodeGenerator(name))
                }

        /**
         * Adds a custom edge between to nodes, identified by their names.
         *
         * This is convenience for <code>generator(EdgeGenerator)</code>.
         */
        fun withEdge(fromNode: String, toNode: String, label: String) =
                apply {
                    generator(EdgeGenerator(fromNode, toNode, label))
                }

        /**
         * Adds a custom edge between to nodes, identified by their names.
         *
         * This is convenience for <code>generator(EdgeGenerator)</code>.
         */
        fun withEdge(fromNode: String, toNode: String) =
                apply {
                    generator(EdgeGenerator(fromNode, toNode, null))
                }


        /**
         * Adds a filter to the configuration. A filter allows to reduce the graph to only show nodes that are
         * relevant for a specific view of the application.
         * There is no filter by default.
         */
        fun filter(filter: GraphModelFilter) =
                apply { this.filter = filter }

        /**
         * Adds the Spring application context to the configuration. The application context is the source from which
         * the graph is created. You can control which parts of the application context are considered for a graph
         * export by adding filters and mappers to this configuration.
         */
        fun applicationContext(applicationContext: ConfigurableApplicationContext) =
                apply { this.applicationContext = applicationContext }

        /**
         * Allows to define an export configuration that controls the graph output. Can be left empty, then a default
         * configuration will be used.
         * @see ExportConfiguration.Builder
         */
        fun exportConfig(exportConfig: ExportConfiguration) =
                apply { this.exportConfig = exportConfig }

        fun build() = BootGraph(this)

        fun export() {
            BootGraph(this).export()
        }
    }

    /**
     * Exports the graph using the provided configuration.
     */
    fun export() {
        val factory = SpringNodeFactory.defaultInstance(applicationName, applicationContext)
        for ((matcher, mapper) in mappers) {
            factory.addMapper(matcher, mapper)
        }

        for (generator in generators) {
            factory.addGenerator(generator)
        }

        var graphModel = factory.createApplicationModel()

        filter?.let {
            graphModel = graphModel.filter(it)
        }

        val exporter = GraphVizModelExporter(exportConfig)

        logger.debug("about to export the following graph: {}", graphModel.toDebugString())
        exporter.export(graphModel)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }

        private val logger = LoggerFactory.getLogger(BootGraph::class.java)
    }

}