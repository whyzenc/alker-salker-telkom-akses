package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MasterData
import com.example.data.model.InspectionStatus
import com.example.data.repository.InspectionRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Alker Sarker", appName)
    }

    @Test
    fun `test repository database and csv generation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = InspectionRepository(context)
        val defaultPeriod = MasterData.periods[0]

        // Test login
        val loginResult = repository.login("940218", "telkom")
        assertTrue(loginResult.isSuccess)
        assertEquals("Ahmad Fauzan", loginResult.getOrNull()?.name)

        // Test CSV export
        val csv = repository.generateRekapCsv(defaultPeriod.id)
        assertTrue(csv.contains("Periode,NIK,Nama Teknisi,STO,Nama Alat"))
        assertTrue(csv.contains("Ahmad Fauzan"))
    }
}
