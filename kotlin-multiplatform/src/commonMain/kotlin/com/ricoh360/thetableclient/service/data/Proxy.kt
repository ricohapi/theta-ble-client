package com.ricoh360.thetableclient.service.data

/**
 * Proxy information to be used for the access point.
 */
data class Proxy(
    /**
     * true: use proxy false: do not use proxy
     */
    val use: Boolean,

    /**
     * Proxy server URL
     */
    val url: String? = null,

    /**
     * Proxy server port number: 0 to 65535
     */
    val port: Int? = null,

    /**
     * User ID used for proxy authentication
     */
    val userid: String? = null,

    /**
     * Password used for proxy authentication
     */
    val password: String? = null,
) {
    internal constructor(value: com.ricoh360.thetableclient.transferred.Proxy) : this(
        use = value.use,
        url = value.url,
        port = value.port,
        userid = value.userid,
        password = value.password,
    )

    companion object {
        val keyName: String
            get() = "proxy"
    }
}
