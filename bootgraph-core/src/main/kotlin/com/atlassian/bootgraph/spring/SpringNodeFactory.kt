package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.api.generator.NodeGenerator
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.mapper.BeanMapper
import com.atlassian.bootgraph.api.matcher.BeanMatcher
import com.atlassian.bootgraph.api.mapper.InputNodeMapper
import com.atlassian.bootgraph.api.mapper.OutputNodeMapper
import com.atlassian.bootgraph.api.matcher.InputNodeMatcher
import com.atlassian.bootgraph.api.matcher.OutputNodeMatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ConfigurableApplicationContext

class SpringNodeFactory private constructor(
        private val applicationName: String,
        private val applicationContext: ConfigurableApplicationContext
) {

    companion object {

        /**
         * Returns a SpringModelFactory with some default matchers and mappers.
         */
        fun defaultInstance(applicationName: String, applicationContext: ConfigurableApplicationContext): SpringNodeFactory {
            return SpringNodeFactory(applicationName, applicationContext)
                    .addMapper(OutputNodeMatcher(), OutputNodeMapper())
                    .addMapper(InputNodeMatcher(), InputNodeMapper())
        }

        /**
         * Returns a "naked" SpringModelFactory with no matchers and no mappers. Use this factory method only if you
         * don't want the default features (like in tests)!
         */
        fun nakedInstance(applicationName: String, applicationContext: ConfigurableApplicationContext): SpringNodeFactory {
            return SpringNodeFactory(applicationName, applicationContext)
        }

        val logger: Logger = LoggerFactory.getLogger(SpringNodeFactory::class.java)
    }

    private val mappers: MutableMap<BeanMatcher, BeanMapper> = HashMap()

    private val generators: MutableList<NodeGenerator> = ArrayList()

    private val spelEvaluator = SpelEvaluator()

    @Suppress("UNCHECKED_CAST")
    fun addMapper(matcher: BeanMatcher, mapper: BeanMapper): SpringNodeFactory {
        mappers[matcher] = mapper as BeanMapper
        return this
    }

    fun addGenerator(generator: NodeGenerator): SpringNodeFactory {
        generators.add(generator)
        return this
    }

    fun createApplicationModel(): GraphModel {
        val graphModel = GraphModel(applicationName)
        val nodeFactory = NodeFactory(graphModel, applicationContext.environment)
        val beanNameToModel = HashMap<String, Node>()

        for (beanName in applicationContext.beanFactory.beanNamesIterator) {

            try {
                val beanDefinition = applicationContext.beanFactory.getBeanDefinition(beanName)
                val bean = applicationContext.getBean(beanName)

                for ((matcher, mapper) in mappers) {
                    if (matcher.matches(bean, beanName, beanDefinition, applicationContext)) {
                        logger.debug("matcher '{}' MATCHED bean '{}'", matcher.javaClass.simpleName, beanName)
                        val beanNode = mapper.mapToGraph(bean, beanName, beanDefinition, applicationContext, nodeFactory)

                        if (!beanNode.isPresent()) {
                            logger.debug("mapper '{}' DID NOT MAP bean '{}' to graph node", mapper.javaClass.simpleName, beanName)
                            continue
                        } else {
                            logger.info("mapper '{}' MAPPED bean '{}' to graph node '{}'", mapper.javaClass.simpleName, beanName, beanNode.get())
                        }

                        val evaluatedBeanNode = spelEvaluator.evaluateSpringExpressions(beanNode.get(), nodeFactory, applicationContext.environment)
                        graphModel.addNode(evaluatedBeanNode)
                        beanNameToModel[beanName] = evaluatedBeanNode
                    }
                }
            } catch (e: NoSuchBeanDefinitionException) {
                continue
            }
        }

        connectDependentNodes(beanNameToModel)

        for (generator in generators) {
            val node = generator.generateNode(nodeFactory)
            val evaluatedBeanNode = spelEvaluator.evaluateSpringExpressions(node, nodeFactory, applicationContext.environment)
            graphModel.addNode(evaluatedBeanNode)
        }

        return graphModel
    }

    private fun connectDependentNodes(beanNameToModel: HashMap<String, Node>) {
        for ((beanName, model) in beanNameToModel.entries) {
            val thisNode = beanNameToModel[beanName]
                    ?: throw IllegalStateException("cannot find bean $beanName!")
            val dependentNodes = HashSet<Node>()
            findDependentNodes(beanName, beanNameToModel, dependentNodes)
            for (dependentNode in dependentNodes) {
                dependentNode.addEdgeTo(thisNode, "")
            }
        }
    }

    /**
     * Adds all Nodes that are dependent on the Spring bean with the specified name to the specified collection recursively.
     */
    private fun findDependentNodes(beanName: String, beanNameToModel: HashMap<String, Node>, found: MutableCollection<Node>) {
        // TODO: throw exception, log a warning, or re-name graph node when the bean is a Mockito mock because
        // in that case getDependentBeans() will return an empty list and we should make the user aware of this!

        for (dependentBeanName in applicationContext.beanFactory.getDependentBeans(beanName)) {
            beanNameToModel[dependentBeanName]?.let {
                if (it != beanNameToModel[beanName]) {
                    // Multiple Spring beans can represent the same Node.
                    // We don't want arrows from a Node to itself, thus this if statement.
                    found.add(it)
                }
            }
            // TODO: make it configurable to include transitive dependencies or not
            // findDependentNodes(dependentBeanName, beanNameToModel, found)
        }
    }
}
