package com.atlassian.bootgraph.annotations

/**
 * Marker annotation for documentation purposes. Marks a Spring bean as calling external
 * systems / components.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OutputNode(

    /**
     * The name of the bean to be used in the documentation. Defaults to the name of the Spring bean.
     */
    val name: String = "",

    /**
     * The label for the connection between the annotated bean and the external target to be used in the documentation (for instance the message type in case of a call
     * to a queue, or the type of request in case of a REST call). Defaults to no label.
     */
    val connectionLabel: String = "",

    /**
     * The name of the external system / component that is called by the Spring bean to be used in the documentation.
     */
    val target: String
)
