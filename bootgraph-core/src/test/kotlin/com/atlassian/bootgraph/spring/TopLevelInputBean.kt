package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.annotations.InputNode
import org.springframework.stereotype.Component

@Component
@InputNode(connectionLabel = "topLevelInputBean connection", source = "topLevelInputBean source")
class TopLevelInputBean
