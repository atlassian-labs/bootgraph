package com.atlassian.bootgraph.api.model

/**
 * A system, application, or component that is outside of the modelled application.
 */
class ExternalNode(name: String, cluster: String?) : Node(name, isExternal = true, cluster = cluster) {

    constructor(name: String) : this(name, null)

}
