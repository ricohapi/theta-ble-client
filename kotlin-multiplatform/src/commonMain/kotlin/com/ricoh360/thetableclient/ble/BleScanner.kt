package com.ricoh360.thetableclient.ble

import com.benasher44.uuid.uuidFrom
import com.juul.kable.Filter
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.ricoh360.thetableclient.BleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

internal const val THETA_NAME_LENGTH = 8

internal abstract class BleScanner(
    val timeout: Int,
    val name: String?,
) {

    abstract fun init()
    abstract val advertisements: Flow<BleAdvertisement>

    fun isThetaName(name: String): Boolean {
        if (name.length != THETA_NAME_LENGTH) {
            return false
        }
        return name.toCharArray().all {
            it.isDigit()
        }
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
                if (!advertisement.uuids.isEmpty()) {
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
