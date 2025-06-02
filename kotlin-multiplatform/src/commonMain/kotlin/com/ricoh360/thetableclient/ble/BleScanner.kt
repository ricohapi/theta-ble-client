package com.ricoh360.thetableclient.ble

import com.juul.kable.Filter
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

internal abstract class BleScanner(
    val timeout: Int,
    val name: String?,
) {

    abstract fun init()
    abstract val advertisements: Flow<BleAdvertisement>

    fun isThetaName(name: String): Boolean {
        // 12345678 or AA12345678
        val regex = Regex("^[A-Z]{2}[0-9]{8}\$|^[0-9]{8}\$")
        return name.matches(regex)
    }

    suspend fun scan(): List<BleAdvertisement> {

        val advertisementMap = mutableMapOf<String, BleAdvertisement>()
        if (name != null) {
            val advertisement = withTimeoutOrNull(timeout.toLong()) {
                advertisements.firstOrNull()
            }
            advertisement?.let {
                advertisementMap[it.name] = it
            }
        } else {
            withTimeoutOrNull(timeout.toLong()) {
                advertisements.collect {
                    if (isThetaName(it.name)) {
                        advertisementMap[it.name] = it
                    }
                }
            }
        }
        val advertisementList = advertisementMap.map {
            it.value
        }
        return advertisementList
    }
}

internal class BleScannerImpl internal constructor(
    timeout: Int,
    name: String?,
) : BleScanner(timeout, name) {
    lateinit var scanner: Scanner

    override fun init() {
        scanner = Scanner {
            logging {
                level = Logging.Level.Data
            }
            filters = name?.let {
                listOf(Filter.Name(it))
            }
        }
    }

    override val advertisements: Flow<BleAdvertisement>
        get() = flow {
            scanner.advertisements.collect { advertisement ->
                if (advertisement.uuids.isNotEmpty()) {
                    advertisement.name?.let {
                        emit(newAdvertisement(advertisement))
                    }
                }
            }
        }
}

internal fun getScanner(timeout: Int, name: String?): BleScanner {
    return BleScannerImpl(timeout, name)
}
