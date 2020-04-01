package com.atlassian.bootgraph.spring

import org.springframework.stereotype.Component

@Component
@InputNodeWithDependencyAnnotation
class InputBeanWithDependency(val dependency: InputBeanDependency)
