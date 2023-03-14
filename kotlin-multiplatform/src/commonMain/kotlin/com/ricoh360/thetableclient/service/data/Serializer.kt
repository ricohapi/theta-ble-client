package com.ricoh360.thetableclient.service.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * floating point number to/from Long serializer
 */
internal object NumberAsIntSerializer : KSerializer<Int> {
    /**
     * serial descriptor
     */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NumberAsIntSerializer", PrimitiveKind.INT)

    /**
     * serialize value with encoder
     * @param encoder encoder object
     * @param value value to encode
     */
    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }

    /**
     * deserialize value with decoder and return decoded value
     * @param decoder decoder object
     * @return decoded value
     */
    override fun deserialize(decoder: Decoder): Int {
        val value = decoder.decodeDouble()
        return value.toInt()
    }
}

/**
 * floating point number to/from List<Int> serializer
 */
@kotlinx.serialization.ExperimentalSerializationApi
internal object NumbersAsIntsSerializer : KSerializer<List<Int>> {
    /**
     * serial descriptor
     */
    override val descriptor: SerialDescriptor = listSerialDescriptor<Int>()

    /**
     * Double serializer to decode number
     */
    private val doubleSerializer = Double.serializer()

    /**
     * Long serializer to encode Int
     */
    private val intSerializer = Int.serializer()

    /**
     * serialize value with encoder
     * @param encoder encoder object
     * @param value value to encode
     */
    override fun serialize(encoder: Encoder, value: List<Int>) {
        val composite = encoder.beginCollection(descriptor, value.size)
        val iterator = value.iterator()
        for (index in value.indices) {
            composite.encodeSerializableElement(
                descriptor, index, intSerializer, iterator.next()
            )
        }
        composite.endStructure(descriptor)
    }

    /**
     * deserialize value with decoder and return decoded value
     * @param decoder decoder object
     * @return decoded value
     */
    override fun deserialize(decoder: Decoder): List<Int> {
        val result = mutableListOf<Int>()
        val compositeDecoder = decoder.beginStructure(descriptor)
        while (true) {
            val index = compositeDecoder.decodeElementIndex(descriptor)
            if (index == CompositeDecoder.DECODE_DONE) {
                break
            }
            result.add(decoder.decodeSerializableValue(doubleSerializer).toInt())
        }
        compositeDecoder.endStructure(descriptor)
        return result
    }
}
