package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment

internal class SpelEvaluatorTest {

    @Test
    fun evaluatedBeansHaveTheSameFields() {
        val evaluator = SpelEvaluator()

        val originalNode = InternalNode("node", "cluster");
        val environment = MockEnvironment()
        val nodeFactory = NodeFactory(GraphModel("root"), environment)

        val evaluatedNode = evaluator.evaluateSpringExpressions(originalNode, nodeFactory, environment)

        assertThat(evaluatedNode).isEqualToComparingFieldByField(originalNode)
    }

}