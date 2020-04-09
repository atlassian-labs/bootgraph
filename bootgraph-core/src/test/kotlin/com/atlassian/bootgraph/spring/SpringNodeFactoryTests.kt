package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.Node
import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.InternalNode
import com.atlassian.bootgraph.api.mapper.BeanMapper
import com.atlassian.bootgraph.api.matcher.BeanMatcher
import com.atlassian.bootgraph.api.mapper.ExternalBeanMapper
import com.atlassian.bootgraph.api.mapper.InternalBeanMapper
import com.atlassian.bootgraph.api.matcher.AnnotationMatcher
import com.atlassian.bootgraph.api.matcher.BeanNameMatcher
import com.atlassian.bootgraph.api.matcher.QualifierMatcher
import com.atlassian.bootgraph.api.matcher.TypeMatcher
import com.atlassian.bootgraph.graphviz.ExportConfiguration
import com.atlassian.bootgraph.graphviz.GraphVizModelExporter
import com.atlassian.bootgraph.graphviz.OutputFormat
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ConfigurableApplicationContext
import java.util.Optional

@SpringBootTest(properties = [
    "spel.mainComponentName=foo",
    "spel.inputComponentName=bar",
    "spel.outputComponentName=baz",
    "spel.inputLabel=bar->foo",
    "spel.outputLabel=foo->baz"
]
)
internal class SpringNodeFactoryTests {

    @Autowired
    private lateinit var applicationContext: ConfigurableApplicationContext

    @Test
    fun basicMapping() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(testBeanMatcher(), testBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals("test application", model.name)
        assertEquals(1, model.getInternalNodes().size)
        assertEquals("TestBean", model.getInternalNodes()[0].name)
    }

    @Test
    fun basicMappingWithInputAndOutput() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(testBeanMatcher(), testBeanMapperWithInputAndOutput())

        val model = factory.createApplicationModel()

        assertEquals(1, model.getInternalNodes().size)
        assertEquals(2, model.getExternalNodes().size)
    }

    @Test
    fun basicMappingWithInputAndOutputEvaluatingSpel() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(testBeanMatcher(), testBeanMapperWithSpelExpressions())

        val model = factory.createApplicationModel()

        assertEquals(1, model.getInternalNodes().size)
        assertEquals(2, model.getExternalNodes().size)

        val mainNode = model.getInternalNodes()[0]
        val inputNode = model.getExternalNodesWithEdgeToInternal()[0]
        val outputNode = model.getExternalNodesWithEdgeFromInternal()[0]
        val inputEdge = mainNode.incomingEdges().iterator().next()
        val outputEdge = mainNode.outgoingEdges().iterator().next()

        assertEquals("foo", mainNode.name)
        assertEquals("bar", inputNode.name)
        assertEquals("baz", outputNode.name)
        assertEquals("bar->foo", inputEdge.label)
        assertEquals("foo->baz", outputEdge.label)
    }

    @Test
    fun typeMatching() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(testBeanTypeMatcher(), testBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals("test application", model.name)
        assertEquals(1, model.getInternalNodes().size)
        assertEquals("TestBean", model.getInternalNodes()[0].name)
    }

    @Test
    fun annotationMatching() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(testAnnotationMatcher(), testBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals("test application", model.name)
        assertEquals(1, model.getInternalNodes().size)
        assertEquals("TestBean", model.getInternalNodes()[0].name)
    }

    @Test
    fun inputNodeAndOutputNodeAnnotationMatching() {

        val factory = SpringNodeFactory.defaultInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
        // we're expecting the @OutputNode and @InputNode matchers and mappers to be configured by default

        val model = factory.createApplicationModel()

        assertNotNull(model.getNode("topLevelInputBean"))
        assertNotNull(model.getNode("topLevelOutputBean"))
        assertNotNull(model.getNode("outputBean"))
        assertNotNull(model.getNode("inputBean"))

        val exporter = GraphVizModelExporter(ExportConfiguration.Builder()
                .outputFilePath("target/graphviz/inputNodeOutputNode.png")
                .outputFormat(OutputFormat.PNG)
                .build())
        exporter.export(model)
    }

    @Test
    fun dependencyBetweenInputAndOutputBean() {

        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)
                .addMapper(inputBeanWithDependencyMatcher(), inputBeanWithDependencyMapper())

        val model = factory.createApplicationModel()

        val inputBeanWithDependency = model.getNode("inputBeanWithDependency")
                ?: fail("expecting component with name 'inputBeanWithDependency' in the model")
        val inputBeanDependency = model.getNode("inputBeanDependency")
                ?: fail("expecting component with name 'inputBeanDependency' in the model")

        assertTrue(inputBeanWithDependency.hasEdgeTo(inputBeanDependency))
    }

    @Test
    fun internalAndExternalMapper(){
        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)

        factory.addMapper(TypeMatcher(InternalBean::class.java), InternalBeanMapper())
        factory.addMapper(TypeMatcher(ExternalBean::class.java), ExternalBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals(2, model.getNodes().size)
        assertNotNull(model.getNode("internalBean"))
        assertNotNull(model.getNode("externalBean"))
    }

    @Test
    fun beanNameMatcher(){
        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)

        factory.addMapper(BeanNameMatcher("internalBean"), InternalBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals(1, model.getNodes().size)
        assertNotNull(model.getNode("internalBean"))
    }

    @Test
    fun qualifierMatcher(){
        val factory = SpringNodeFactory.nakedInstance(
                applicationName = "test application",
                applicationContext = applicationContext)

        factory.addMapper(QualifierMatcher("qualifier"), InternalBeanMapper())

        val model = factory.createApplicationModel()

        assertEquals(1, model.getNodes().size)
        assertNotNull(model.getNode("internalBean"))
    }

    private fun testAnnotationMatcher(): BeanMatcher {
        return AnnotationMatcher(TestAnnotation::class.java)
    }

    private fun testBeanTypeMatcher(): BeanMatcher {
        return TypeMatcher(TestBean::class.java)
    }

    private fun inputBeanWithDependencyMatcher(): BeanMatcher {
        return AnnotationMatcher(InputNodeWithDependencyAnnotation::class.java)
    }

    private fun inputBeanWithDependencyMapper(): BeanMapper {
        return object : BeanMapper {
            override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {
                return Optional.of(InternalNode(beanName))
            }
        }
    }

    private fun testBeanMatcher(): BeanMatcher {
        return object : BeanMatcher {
            override fun matches(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext): Boolean {
                return bean.javaClass.simpleName == "TestBean"
            }
        }
    }

    private fun testBeanMapper(): BeanMapper {
        return object : BeanMapper {
            override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {
                return Optional.of(InternalNode("TestBean"))
            }
        }
    }

    private fun testBeanMapperWithInputAndOutput(): BeanMapper {
        return object : BeanMapper {
            override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {
                val model = InternalNode("TestBean")
                model.addEdgeFrom(ExternalNode("InputBean"), "input")
                model.addEdgeTo(ExternalNode("OutputBean"), "output")
                return Optional.of(model)
            }
        }
    }

    private fun testBeanMapperWithSpelExpressions(): BeanMapper {
        return object : BeanMapper {
            override fun mapToGraph(bean: Any, beanName: String, beanDefinition: BeanDefinition, applicationContext: ConfigurableApplicationContext, factory: NodeFactory): Optional<Node> {
                val model = InternalNode("\${spel.mainComponentName}")
                model.addEdgeFrom(ExternalNode("\${spel.inputComponentName}"), "\${spel.inputLabel}")
                model.addEdgeTo(ExternalNode("\${spel.outputComponentName}"), "\${spel.outputLabel}")
                return Optional.of(model)
            }
        }
    }
}
