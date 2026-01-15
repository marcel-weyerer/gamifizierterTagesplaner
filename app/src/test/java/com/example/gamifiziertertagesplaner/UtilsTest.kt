package com.example.gamifiziertertagesplaner

import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.util.buildTimeString
import com.example.gamifiziertertagesplaner.util.calculateTaskPoints
import com.example.gamifiziertertagesplaner.util.computePoints
import com.google.firebase.Timestamp
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TaskUtilsTest {

  private var oldLocale: Locale? = null
  private var oldTimeZone: TimeZone? = null

  @Before
  fun setUp() {
    // Make time formatting deterministic across machines
    oldLocale = Locale.getDefault()
    oldTimeZone = TimeZone.getDefault()

    Locale.setDefault(Locale.GERMANY)
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  }

  @After
  fun tearDown() {
    oldLocale?.let { Locale.setDefault(it) }
    oldTimeZone?.let { TimeZone.setDefault(it) }
  }

  // CalculateTaskPoints Tests

  @Test
  fun `calculateTaskPoints - high priority, 0 minutes returns base points only`() {
    val points = calculateTaskPoints(priority = 1, durationMinutes = 0)
    assertEquals(20, points)
  }

  @Test
  fun `calculateTaskPoints - medium priority, 60 minutes adds 16 points`() {
    val points = calculateTaskPoints(priority = 2, durationMinutes = 60)
    assertEquals(10 + 6, points)
  }

  @Test
  fun `calculateTaskPoints - low priority, 30 minutes adds 8 points`() {
    val points = calculateTaskPoints(priority = 3, durationMinutes = 30)
    assertEquals(5 + 3, points)
  }

  @Test
  fun `calculateTaskPoints - unknown priority uses else base 5`() {
    val points = calculateTaskPoints(priority = 999, durationMinutes = 0)
    assertEquals(5, points)
  }

  // ComputePoints Tests

  @Test
  fun `computePoints - empty list returns zeros`() {
    val summary = computePoints(emptyList())
    assertEquals(0, summary.totalPoints)
    assertEquals(0, summary.receivedPoints)
  }

  @Test
  fun `computePoints - totals all points and counts received only for done tasks`() {
    val tasks = listOf(
      Task(points = 10, state = 0), // done
      Task(points = 5, state = 1),  // not done
      Task(points = 7, state = 0)   // done
    )

    val summary = computePoints(tasks)
    assertEquals(22, summary.totalPoints)
    assertEquals(17, summary.receivedPoints)
  }

  // BuildTimeString Tests

  @Test
  fun `buildTimeString - no startTime and no duration returns empty string`() {
    val task = Task(startTime = null, duration = null)
    assertEquals("", buildTimeString(task))
  }

  @Test
  fun `buildTimeString - duration only returns formatted hours and minutes`() {
    val task = Task(startTime = null, duration = 125)   // 2h 5min
    assertEquals("02h 05min", buildTimeString(task))
  }

  @Test
  fun `buildTimeString - startTime only returns time plus Uhr`() {
    val start = timestampUtc(hour = 9, minute = 30)
    val task = Task(startTime = start, duration = null)

    assertEquals("09:30 Uhr", buildTimeString(task))
  }

  @Test
  fun `buildTimeString - startTime and duration returns start and end time`() {
    val start = timestampUtc(hour = 9, minute = 30)
    val task = Task(startTime = start, duration = 45)

    // 09:30 + 45min = 10:15
    assertEquals("09:30 - 10:15", buildTimeString(task))
  }

  @Test
  fun `buildTimeString - startTime and duration crosses hour boundary correctly`() {
    val start = timestampUtc(hour = 23, minute = 50)
    val task = Task(startTime = start, duration = 20)

    // 23:50 + 20min = 00:10 (next day)
    assertEquals("23:50 - 00:10", buildTimeString(task))
  }

  // Helper
  private fun timestampUtc(hour: Int, minute: Int): Timestamp {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
      set(Calendar.YEAR, 2020)
      set(Calendar.MONTH, Calendar.JANUARY)
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, hour)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return Timestamp(cal.time)
  }
}
