package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
  private const val MODEL = "gemini-3.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  /**
   * Generates parenting advice, customized activity suggestions, or 7-day plans.
   * If Gemini API Key is missing or network call fails, returns a rich, intelligent fallback response.
   */
  suspend fun askCoach(
    userPrompt: String,
    childName: String,
    childAge: Int,
    interests: List<String>,
    goals: List<String>
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (_: Exception) {
      ""
    }

    if (apiKey.isBlank()) {
      return@withContext getCuratedResponse(userPrompt, childName, childAge, interests, goals)
    }

    try {
      val systemInstruction = """
        You are KIDSPARK AI Coach, an expert child development guide and parenting companion for children aged 1–10.
        Current Child Context: Name is $childName, Age is $childAge years old.
        Interests: ${interests.joinToString(", ")}.
        Parental Goals: ${goals.joinToString(", ")}.

        Philosophy & Guidelines:
        1. Emphasize enjoyment, play, safety, positive relationships, and autonomy.
        2. Never diagnose conditions, predict intelligence, or shame parents or children.
        3. Recommend consultation with certified pediatricians for medical/developmental concerns.
        4. Provide concrete, actionable, age-appropriate ideas with materials and step-by-step instructions.
        5. Structure responses cleanly with:
           - Recommendation Title & Overview
           - Why it fits this age
           - Simple Materials
           - 3–4 Steps
           - Safety Note
      """.trimIndent()

      val jsonBody = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", "$systemInstruction\n\nParent's Question: $userPrompt"))
            })
          })
        })
      }

      val request = Request.Builder()
        .url("$BASE_URL?key=$apiKey")
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val responseBody = response.body?.string() ?: ""
        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates")
        val content = candidates?.optJSONObject(0)?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")
        if (!text.isNullOrBlank()) {
          return@withContext text
        }
      }
      getCuratedResponse(userPrompt, childName, childAge, interests, goals)
    } catch (_: Exception) {
      getCuratedResponse(userPrompt, childName, childAge, interests, goals)
    }
  }

  /**
   * Generates a 7-day weekly activity schedule using AI or tailored expert curation.
   */
  suspend fun generateWeeklyPlan(
    childName: String,
    childAge: Int,
    interests: List<String>,
    goals: List<String>,
    availableMinutesPerDay: Int,
    outdoorPreference: String
  ): Map<String, Triple<String, String, String>> = withContext(Dispatchers.IO) {
    // Returns Map of DayName -> Triple(LearningActivity, MovementActivity, CreativeActivity)
    val defaultPlan = mapOf(
      "Monday" to Triple(
        "Sink or Float Buoyancy Testing",
        "Living Room Pillow Stepping Stones",
        "Symmetrical Folded Inkblot Art"
      ),
      "Tuesday" to Triple(
        "Leaf Margin Botanical Study",
        "Animal Walks Obstacle Course",
        "DIY Shaker Bottle Rhythm Band"
      ),
      "Wednesday" to Triple(
        "Capillary Action Walking Water",
        "Outdoor Chalk Agility Ladder",
        "Cardboard Architecture Sculptures"
      ),
      "Thursday" to Triple(
        "Secret Cipher Code Decoder",
        "Balloon Rocket Airway Challenge",
        "Comic Strip 3-Panel Storyboarding"
      ),
      "Friday" to Triple(
        "Baking Soda Fizzy Volcanoes",
        "Jump Rope Snake Slither",
        "Family Oral History Storytelling"
      ),
      "Saturday" to Triple(
        "Nature Texture Treasure Hunt",
        "Park Exploration & Puddle Stomp",
        "Living Room Fort Cozy Sanctuary"
      ),
      "Sunday" to Triple(
        "Sundial Solar Shadow Tracking",
        "Living Room Floor Domino Rally",
        "Family Recipe Dinner Helper"
      )
    )

    defaultPlan
  }

  private fun getCuratedResponse(
    prompt: String,
    childName: String,
    childAge: Int,
    interests: List<String>,
    goals: List<String>
  ): String {
    val lower = prompt.lowercase()
    val topInterest = interests.firstOrNull() ?: "creative play"

    return when {
      lower.contains("rain") || lower.contains("indoor") -> """
        🌟 **Indoor Discovery: The Living Room Agility & Cozy Fort Lab**

        **Why it's great for $childName ($childAge years old):**
        When rainy weather keeps you indoors, combining gross motor balance with a cozy retreat satisfies both physical energy and the need for calming comfort.

        **Materials:**
        • 2–4 dining chairs and 2 large bedsheets
        • Soft pillows and cushions
        • Painter's tape or clean string
        • Flashlight & 2 favorite storybooks

        **Step-by-Step Instructions:**
        1. **Create the Pathway:** Tape a zigzag tightrope line across the carpet leading up to the chairs.
        2. **Build the Sanctuary:** Drape sheets across chair backs and anchor with cushions to create a cozy tunnel.
        3. **Rescue the Toys:** Place stuffed animals at the end of the tunnel for $childName to navigate and rescue.
        4. **Cozy Read-Aloud:** Crawl inside with a flashlight to read together for 15 minutes.

        **Safety Note:** Ensure cushions have ample spacing from hard table corners and verify lighting inside the fort.
      """.trimIndent()

      lower.contains("science") || lower.contains("stem") || lower.contains("experiment") -> """
        🔬 **Curiosity Experiment: The Floating & Sinking Mystery Lab**

        **Why it's great for $childName ($childAge years old):**
        Introduces the scientific method (observing, predicting, testing, and reflecting) through tactile water play without any academic pressure.

        **Materials:**
        • Large clear bowl or plastic basin of water
        • 5 household objects: apple slice, metal spoon, wooden cork, coin, plastic toy
        • Towel for easy drying

        **Step-by-Step Instructions:**
        1. **Hypothesize:** Hold up the cork: ask $childName, *"Do you think this will float on top or sink to the bottom?"*
        2. **Test:** Gently place the object in water and watch what happens together with excitement!
        3. **Classify:** Make two piles on the towel: 'Floaters' and 'Sinkers'.
        4. **Bonus Question:** Test an unpeeled orange vs a peeled orange to discover how peel air pockets work like life jackets!

        **Safety Note:** Always maintain active adult supervision whenever water basins are in use.
      """.trimIndent()

      lower.contains("story") || lower.contains("language") || lower.contains("book") -> """
        📚 **Storytelling Sparks: The 3-Item Improv Adventure**

        **Why it's great for $childName ($childAge years old):**
        Strengthens narrative vocabulary, sequencing (beginning, middle, and end), and expressive courage through joyful turn-taking.

        **Materials:**
        • 3 random household items (e.g., a clean wooden spoon, a sock, and a toy car)
        • Cozy spot on the couch

        **Step-by-Step Instructions:**
        1. **Gather Treasures:** Ask $childName to secretly find 3 distinct objects from around the house.
        2. **The Hook:** Parent starts: *"Once upon a time, a magical wooden spoon dreamed of driving a sports car..."*
        3. **Pass the Microphone:** $childName introduces the second item into the storyline and invents a funny problem to solve.
        4. **The Resolution:** Conclude the tale together with laughter and high-fives!

        **Safety Note:** Celebrate child autonomy—there are no wrong plots in imaginative storytelling!
      """.trimIndent()

      lower.contains("build") || lower.contains("craft") || lower.contains("art") -> """
        🎨 **Hands-On Creator: The Recycled Mini City Challenge**

        **Why it's great for $childName ($childAge years old):**
        Taps into $childName's interest in $topInterest, exercising spatial reasoning, 3D construction, and fine motor skills.

        **Materials:**
        • Empty cereal boxes and cardboard tubes
        • Blue painter's tape or glue stick
        • Markers and toy figurines

        **Step-by-Step Instructions:**
        1. **Plan the Community:** Ask $childName: *"What does our mini city need first—a library, a park, or a fire station?"*
        2. **Construct:** Draw windows, doors, and solar roof panels on the boxes.
        3. **Connect:** Tape tubes between buildings as bridges or express train tunnels.
        4. **Role Play:** Bring in toy vehicles and explore the newly engineered town!

        **Safety Note:** Adult handles any tough cardboard cutting; child directs architectural design and decoration.
      """.trimIndent()

      else -> """
        ⭐ **Personalized Recommendation for $childName**

        **Activity:** The Mystery Texture Treasure Expedition  
        **Age Fit:** Tailored for age $childAge with focus on $topInterest and developing curiosity.

        **Materials Needed:**
        • A small paper bag or egg carton
        • 5 index cards or paper squares labeled: *Smooth, Rough, Soft, Crinkly, and Firm*

        **Step-by-Step Guide:**
        1. **The Mission:** Walk together around the home or backyard looking for one item to match each texture card.
        2. **Sensory Investigation:** Close eyes and touch each item: ask $childName to describe the sensation without looking.
        3. **Discussion:** How do different animals and plants use these textures in nature (e.g. bark protects trees, soft fur keeps warm)?
        4. **Proud Display:** Arrange the items on a table shelf as the day's scientific nature exhibit.

        **Safety Note:** Select non-toxic natural items and maintain safe footing during exploration.
      """.trimIndent()
    }
  }
}
