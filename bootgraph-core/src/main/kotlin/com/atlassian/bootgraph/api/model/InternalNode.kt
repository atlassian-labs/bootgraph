package com.atlassian.bootgraph.api.model

/**
 * A class or component that is part of the modelled application.
 */
class InternalNode(name: String, cluster: String?) : Node(name, isExternal = false, cluster = cluster){

    constructor(name: String) : this (name, null)

}
