package com.atlassian.bootgraph.spring

import com.atlassian.bootgraph.annotations.InputNode
import com.atlassian.bootgraph.annotations.OutputNode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TestConfiguration {

    @Bean
    @OutputNode(name = "outputBean", connectionLabel = "outputBean connection", target = "outputBean target")
    open fun outputBean(): OutputBean {
        return OutputBean()
    }

    @Bean
    @InputNode(name = "inputBean", connectionLabel = "inputBean connection", source = "inputBean source")
    open fun inputBean(): InputBean {
        return InputBean()
    }
}
