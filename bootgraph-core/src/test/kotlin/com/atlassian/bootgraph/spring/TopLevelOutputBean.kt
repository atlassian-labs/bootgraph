package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.annotations.OutputNode
import org.springframework.stereotype.Component

@Component
@OutputNode(connectionLabel = "topLevelOutputBean connection", target = "topLevelOutputBean target")
class TopLevelOutputBean
