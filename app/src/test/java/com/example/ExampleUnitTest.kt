package com.example

import com.example.data.model.Man1ClassDirectory
import com.example.data.model.QrParseResult
import com.example.data.model.UserAccount
import com.example.data.model.WasteCommodityCatalog
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testMan1Yogyakarta27Classes() {
        val all27 = Man1ClassDirectory.all27Classes
        assertEquals("Total kelas MAN 1 Yogyakarta harus 27 kelas", 27, all27.size)

        val grade10 = all27.filter { it.grade == 10 }
        val grade11 = all27.filter { it.grade == 11 }
        val grade12 = all27.filter { it.grade == 12 }

        assertEquals(9, grade10.size)
        assertEquals(9, grade11.size)
        assertEquals(9, grade12.size)

        assertTrue(grade10.any { it.name == "X PK 1" })
        assertTrue(grade10.any { it.name == "X PK 2" })
        assertTrue(grade11.any { it.name == "XI PK 1" })
        assertTrue(grade11.any { it.name == "XI PK 2" })
        assertTrue(grade12.any { it.name == "XII PK 1" })
        assertTrue(grade12.any { it.name == "XII PK 2" })
    }

    @Test
    fun test42CommoditiesAndPrices() {
        val items = WasteCommodityCatalog.items
        assertEquals("Harus ada 42 komoditas sesuai lampiran harga resmi", 42, items.size)

        // Botol PET (B-01): Jual 3500, Beli 2800
        val pet = WasteCommodityCatalog.findByCode("B-01")
        assertNotNull(pet)
        assertEquals(3500L, pet!!.priceSellBsi)
        assertEquals(2800L, pet.priceBuyCustomer)

        // Kardus (A-07): Jual 1600, Beli 1280
        val kardus = WasteCommodityCatalog.findByCode("A-07")
        assertNotNull(kardus)
        assertEquals(1600L, kardus!!.priceSellBsi)
        assertEquals(1280L, kardus.priceBuyCustomer)

        // Kaleng (D-09): Jual 1500, Beli 1200
        val kaleng = WasteCommodityCatalog.findByCode("D-09")
        assertNotNull(kaleng)
        assertEquals(1500L, kaleng!!.priceSellBsi)
        assertEquals(1200L, kaleng.priceBuyCustomer)

        // Minyak Jelantah (F-07): Jual 5000, Beli 4000
        val jelantah = WasteCommodityCatalog.findByCode("F-07")
        assertNotNull(jelantah)
        assertEquals(5000L, jelantah!!.priceSellBsi)
        assertEquals(4000L, jelantah.priceBuyCustomer)
    }

    @Test
    fun testQrPayloadEncodingAndParsingWithPhone() {
        val user = UserAccount(
            id = "BST-MAN1-X-A-12345",
            name = "Ahmad Fauzan",
            className = "X A",
            phoneNumber = "081234567890",
            balance = 0L
        )

        val payload = user.toQrPayload()
        assertEquals("BST-MAN1-X-A-12345|Ahmad Fauzan|X A|081234567890", payload)

        val parseResult = UserAccount.fromQrPayload(payload)
        assertTrue(parseResult is QrParseResult.Success)
        val success = parseResult as QrParseResult.Success
        assertEquals(user.id, success.id)
        assertEquals(user.name, success.name)
        assertEquals(user.className, success.className)
        assertEquals(user.phoneNumber, success.phoneNumber)
    }
}
