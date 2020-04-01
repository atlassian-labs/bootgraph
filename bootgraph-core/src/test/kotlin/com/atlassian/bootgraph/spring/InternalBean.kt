package com.atlassian.bootgraph.spring

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("qualifier")
class InternalBean