package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.annotations.InputNode
import com.atlassian.bootgraph.annotations.OutputNode
import org.springframework.stereotype.Component

@Component
@OutputNode(connectionLabel = "topLevelInputAndOutputBean connection", target = "topLevelOutputBean target")
@InputNode(connectionLabel = "topLevelInputAndOutputBean connection", source = "topLevelInputBean source")
class TopLevelInputAndOutputBean
