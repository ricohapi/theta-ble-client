package com.ricoh360.thetableclient.transferred

import com.ricoh360.thetableclient.service.data.NumberAsIntSerializer
import kotlinx.serialization.Serializable

/**
 * Proxy information to be used for the access point.
 */
@Serializable
internal data class Proxy(
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
    @Serializable(with = NumberAsIntSerializer::class)
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
    internal constructor(value: com.ricoh360.thetableclient.service.data.Proxy) : this(
        use = value.use,
        url = value.url,
        port = value.port,
        userid = value.userid,
        password = value.password,
    )
}
