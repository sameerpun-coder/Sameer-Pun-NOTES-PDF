package com.example

import com.example.data.SlideItem
import com.example.data.SubjectCategory
import com.example.util.SlidePresentationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testSubjectCategoriesAndUnits() {
        val physics = SubjectCategory.PHYSICS
        assertEquals("Physics", physics.title)
        assertTrue(physics.units.contains("Mechanics"))
        assertTrue(physics.units.contains("Waves and optics"))
        assertTrue(physics.units.contains("Electrostatics and capacitors"))

        // Verify Mechanics chapters
        val mechanics = physics.syllabusUnits.find { it.name == "Mechanics" }
        assertNotNull(mechanics)
        val chapterNames = mechanics!!.chapters.map { it.name }
        assertTrue(chapterNames.contains("Physical quantities, vectors and scalars"))
        assertTrue(chapterNames.contains("Kinematics"))
        assertTrue(chapterNames.contains("Dynamics"))
        assertTrue(chapterNames.contains("Rotational dynamics"))

        val chemistry = SubjectCategory.CHEMISTRY
        assertEquals("Chemistry", chemistry.title)
        assertTrue(chemistry.units.contains("Organic Chemistry"))
        assertTrue(chemistry.units.contains("Physical Chemistry"))

        val zoology = SubjectCategory.ZOOLOGY
        assertEquals("Zoology", zoology.title)
        assertTrue(zoology.units.contains("Human Biology and Physiology"))

        val botany = SubjectCategory.BOTANY
        assertEquals("Botany", botany.title)
        assertTrue(botany.units.contains("Plant Anatomy & Physiology") || botany.units.contains("Cell Biology & Genetics"))

        val mat = SubjectCategory.MAT
        assertEquals("MAT", mat.title)
        assertTrue(mat.units.contains("Numerical Reasoning") || mat.units.contains("Logical & Analytical Reasoning"))
    }

    @Test
    fun testSlideItemSerializationAndDeserialization() {
        val originalSlides = listOf(
            SlideItem(
                slideNumber = 1,
                title = "Thermodynamics Laws",
                bullets = listOf(
                    "First law: ΔU = Q - W",
                    "Second law: Entropy of isolated system never decreases"
                ),
                formulaOrCallout = "dQ = dU + dW",
                presenterNotes = "State function vs path function"
            ),
            SlideItem(
                slideNumber = 2,
                title = "Carnot Cycle",
                bullets = listOf(
                    "Reversible thermodynamic cycle",
                    "Efficiency η = 1 - T_C / T_H"
                ),
                formulaOrCallout = "η = (Q_in - Q_out) / Q_in",
                presenterNotes = "Maximum theoretical efficiency"
            )
        )

        val json = SlidePresentationHelper.serializeSlides(originalSlides)
        assertTrue(json.isNotBlank())
        assertTrue(json.contains("Thermodynamics Laws"))
        assertTrue(json.contains("Carnot Cycle"))

        val deserialized = SlidePresentationHelper.deserializeSlides(json)
        assertEquals(2, deserialized.size)
        assertEquals("Thermodynamics Laws", deserialized[0].title)
        assertEquals(2, deserialized[0].bullets.size)
        assertEquals("Carnot Cycle", deserialized[1].title)
        assertEquals("η = (Q_in - Q_out) / Q_in", deserialized[1].formulaOrCallout)
    }
}
