package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail
import org.junit.jupiter.api.Test

internal class InputPathFilterTests {

    @Test
    fun filtersInputPath() {
        val model = applicationModelWithPathFromAToD()
        val filteredModel = InputPathFilter("A").applyTo(model)

        assertTrue(filteredModel.containsNode("A"))
        assertTrue(filteredModel.containsNode("B"))
        assertTrue(filteredModel.containsNode("C"))
        assertTrue(filteredModel.containsNode("D"))
        assertTrue(filteredModel.containsNode("E"))
        assertNotNull(filteredModel.getEdge("A", "B"))
        assertNotNull(filteredModel.getEdge("B", "C"))
        assertNotNull(filteredModel.getEdge("C", "D"))
        assertNotNull(filteredModel.getEdge("C", "E"))

        assertFalse(filteredModel.containsNode("X"))
        assertFalse(filteredModel.containsNode("Y"))
        assertFalse(filteredModel.containsNode("Z"))
    }

    @Test
    fun breaksOnCycle() {
        val model = applicationModelWithCycle()
        val filteredModel = InputPathFilter("A").applyTo(model)

        assertTrue(filteredModel.containsNode("A"))
        assertTrue(filteredModel.containsNode("B"))
        assertTrue(filteredModel.containsNode("C"))
        assertTrue(filteredModel.containsNode("D"))
        assertTrue(filteredModel.containsNode("E"))
        assertNotNull(filteredModel.getEdge("A", "B"))
        assertNotNull(filteredModel.getEdge("B", "C"))
        assertNotNull(filteredModel.getEdge("C", "D"))
        assertNotNull(filteredModel.getEdge("C", "E"))

        assertFalse(filteredModel.containsNode("X"))
        assertFalse(filteredModel.containsNode("Y"))
        assertFalse(filteredModel.containsNode("Z"))
    }

    private fun applicationModelWithPathFromAToD(): GraphModel {

        // nodes in the path
        val a = ExternalNode("A")
        val b = InternalNode("B")
        val c = InternalNode("C")
        val d = InternalNode("D")
        val e = ExternalNode("E")
        a.addEdgeTo(b)
        b.addEdgeTo(c)
        c.addEdgeTo(d)
        c.addEdgeTo(e)

        // nodes not in the path
        val x = ExternalNode("X")
        val y = InternalNode("Y")
        val z = InternalNode("Z")
        x.addEdgeTo(y)
        x.addEdgeTo(b)
        y.addEdgeTo(z)
        y.addEdgeTo(c)

        val model = GraphModel("application")
        model.addNodes(a, b, c, d, e, x, y, z)
        return model
    }

    private fun applicationModelWithCycle(): GraphModel {
        val model = applicationModelWithPathFromAToD()
        val a = model.getNode("A") ?: fail("expected graph to contain node 'A' ")
        val c = model.getNode("C") ?: fail("expected graph to contain node 'C' ")

        c.addEdgeTo(a)

        return model
    }
}
