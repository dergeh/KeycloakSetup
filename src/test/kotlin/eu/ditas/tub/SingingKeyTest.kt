package eu.ditas.tub

import org.junit.Assert.*
import org.junit.Test

class SingingKeyTest {
    @Test
    fun test(){
        val pair = Crypto.buildKeyPair()

        val key = SingingKey(pair.public.encoded)

        print("${key.crc} with ${key.algorithm}")

    }
}