package com.atlassian.bootgraph.api.model

/**
 * A class or component that is part of the modelled application.
 */
class InternalNode(name: String) : Node(name, isExternal = false)
