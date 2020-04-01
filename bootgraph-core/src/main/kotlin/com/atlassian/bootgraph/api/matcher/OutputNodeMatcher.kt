package com.atlassian.bootgraph.api.matcher

import com.atlassian.bootgraph.annotations.OutputNode

/**
 * Matches beans annotated with the @OutputNode annotation.
 */
class OutputNodeMatcher : AnnotationMatcher<OutputNode>(annotationClazz = OutputNode::class.java)
