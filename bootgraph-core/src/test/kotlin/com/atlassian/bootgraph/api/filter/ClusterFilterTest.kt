package com.atlassian.bootgraph.api.filter

import assertk.assertThat
import com.atlassian.bootgraph.api.containsEdge
import com.atlassian.bootgraph.api.containsNodes
import com.atlassian.bootgraph.api.doesntContainNodes
import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import org.junit.jupiter.api.Test

internal class ClusterFilterTest {

    @Test
    fun filtersClusterNodes() {
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

        val originalModel = GraphModel("my app")
                .addNodes(a, b, c, d, e, f, q, w, x, y, z)

        val filteredModel = originalModel.filter(ClusterFilter())

        assertThat(filteredModel).doesntContainNodes("B",
                "C",
                "D",
                "E",
                "F",
                "Q",
                "W",
                "X",
                "Y",
                "Z"
        )

        assertThat(filteredModel).containsNodes("A",
                "cluster1",
                "cluster2",
                "cluster3",
                "cluster4")

        assertThat(filteredModel).containsEdge("A", "cluster1")
        assertThat(filteredModel).containsEdge("A", "cluster2")
        assertThat(filteredModel).containsEdge("A", "cluster3")
        assertThat(filteredModel).containsEdge("cluster1", "cluster4")
        assertThat(filteredModel).containsEdge("cluster3", "cluster4")

    }

}