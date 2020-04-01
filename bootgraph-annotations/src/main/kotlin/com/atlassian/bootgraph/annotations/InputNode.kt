package com.atlassian.bootgraph.annotations

/**
 * Marker annotation for documentation purposes. Marks a Spring bean as being called by external
 * systems / components or taking input from them in another way.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class InputNode(

    /**
     * The name of the bean to be used in the documentation. Defaults to the name of the Spring bean.
     */
    val name: String = "",

    /**
     * The label for the connection between the external source and the annotated bean to be used in the documentation (for instance the message type in case of a
     * queue, or the type of request in case of a REST call). Defaults to no label.
     */
    val connectionLabel: String = "",

    /**
     * The name of the external system / component that calls this Spring bean to be used in the documentation.
     */
    val source: String
)
