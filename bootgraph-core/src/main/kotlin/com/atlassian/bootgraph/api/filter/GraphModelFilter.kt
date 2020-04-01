package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.GraphModel

interface GraphModelFilter {

    fun applyTo(originalModel: GraphModel): GraphModel
}
