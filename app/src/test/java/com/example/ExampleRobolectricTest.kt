package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.seed.ActivitySeedData
import com.example.data.seed.PathwaySeedData
import com.example.data.seed.ScreenFreeSeedData
import org.junit.Assert.assertEquals
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
    assertEquals("KIDSPARK", appName)
  }

  @Test
  fun `verify activity catalog seed data contains 100 activities`() {
    val activities = ActivitySeedData.activities
    assertEquals(100, activities.size)
  }

  @Test
  fun `verify future skills pathways contains 10 pathways`() {
    val pathways = PathwaySeedData.pathways
    assertEquals(10, pathways.size)
  }

  @Test
  fun `verify screen free adventure contains 30 missions`() {
    val missions = ScreenFreeSeedData.missions
    assertEquals(30, missions.size)
  }
}
