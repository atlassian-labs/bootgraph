package com.atlassian.bootgraph.api.matcher

import com.atlassian.bootgraph.annotations.InputNode

/**
 * Matches beans annotated with the @InputNode annotation.
 */
class InputNodeMatcher : AnnotationMatcher<InputNode>(annotationClazz = InputNode::class.java)
