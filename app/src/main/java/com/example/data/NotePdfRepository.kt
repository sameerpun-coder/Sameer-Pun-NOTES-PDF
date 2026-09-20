package com.example.data

import android.content.Context
import com.example.util.PdfFileManager
import com.example.util.SlidePresentationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotePdfRepository(
    private val notePdfDao: NotePdfDao,
    private val context: Context
) {
    val allNotes: Flow<List<NotePdf>> = notePdfDao.getAllNotes()
    val favoriteNotes: Flow<List<NotePdf>> = notePdfDao.getFavoriteNotes()
    val totalCount: Flow<Int> = notePdfDao.getTotalCount()

    fun searchNotes(query: String): Flow<List<NotePdf>> {
        return if (query.isBlank()) {
            notePdfDao.getAllNotes()
        } else {
            notePdfDao.searchNotes(query.trim())
        }
    }

    fun getNotesBySubject(subject: String): Flow<List<NotePdf>> {
        return notePdfDao.getNotesBySubject(subject)
    }

    fun getNotesBySubjectAndUnit(subject: String, unit: String): Flow<List<NotePdf>> {
        return notePdfDao.getNotesBySubjectAndUnit(subject, unit)
    }

    fun getNoteById(id: Long): Flow<NotePdf?> {
        return notePdfDao.getNoteById(id)
    }

    suspend fun insertNote(note: NotePdf): Long = withContext(Dispatchers.IO) {
        notePdfDao.insertNote(note)
    }

    suspend fun updateNote(note: NotePdf) = withContext(Dispatchers.IO) {
        notePdfDao.updateNote(note)
    }

    suspend fun deleteNote(note: NotePdf) = withContext(Dispatchers.IO) {
        PdfFileManager.deletePdfFile(note.filePath)
        notePdfDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long, filePath: String) = withContext(Dispatchers.IO) {
        PdfFileManager.deletePdfFile(filePath)
        notePdfDao.deleteNoteById(id)
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        notePdfDao.setFavorite(id, !currentStatus)
    }

    suspend fun prepopulateSamplesIfEmpty() = withContext(Dispatchers.IO) {
        // Standard PDF revision documents linked directly to syllabus chapters
        val sampleConfigs = listOf(
            SampleNoteDef(
                title = "Vectors, Dimensions & Units Study Guide",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Physical quantities, vectors and scalars",
                description = "Mastering SI units, dimensional formulas, dot and cross products, resolution of vectors, and error propagation.",
                content = """
                    I. Physical Quantities & Dimensions
                    • Fundamental Quantities: Length [L], Mass [M], Time [T], Electric Current [I], Temperature [K], Amount of Substance [mol], Luminous Intensity [cd].
                    • Dimensional Formulas:
                      - Velocity: [M⁰ L T⁻¹]
                      - Force: [M L T⁻²]
                      - Work & Energy: [M L² T⁻²]
                      - Pressure: [M L⁻¹ T⁻²]
                      - Planck's Constant: [M L² T⁻¹]
                    • Principle of Homogeneity: In any physical equation, the dimensions of every term on both sides must be identical.

                    II. Vectors & Operations
                    • Resultant of two vectors A and B: R = √(A² + B² + 2AB cos θ)
                    • Direction: tan α = (B sin θ) / (A + B cos θ)
                    • Scalar (Dot) Product: A · B = |A||B| cos θ. Orthogonal when A · B = 0.
                    • Vector (Cross) Product: A × B = |A||B| sin θ · n̂. Parallel when A × B = 0.
                    • Component Resolution: A_x = A cos θ, A_y = A sin θ.
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Kinematics 1D & 2D Formulas & Concepts",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Kinematics",
                description = "Equations of motion, projectile trajectories, resistive drag forces, and graph interpretations.",
                content = """
                    I. Kinematics Equations of Motion (Constant Acceleration)
                    • v = u + at
                    • s = ut + 0.5 a t²
                    • v² = u² + 2as
                    • Distance in nth second: s_n = u + 0.5 a (2n - 1)

                    II. Projectile Motion
                    • Time of Flight: T = (2 u sin θ) / g
                    • Maximum Height: H_max = (u² sin² θ) / (2g)
                    • Horizontal Range: R = (u² sin 2θ) / g (Maximum at θ = 45°)
                    • Equation of Trajectory: y = x tan θ - (g x²) / (2 u² cos² θ)

                    III. Relative Velocity & Graphs
                    • v_AB = v_A - v_B
                    • Area under v-t graph = Displacement
                    • Slope of v-t graph = Acceleration
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Rotational Dynamics & Moment of Inertia",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Rotational dynamics",
                description = "Formulas for moment of inertia, parallel/perpendicular axis theorems, torque, and conservation of angular momentum.",
                content = """
                    I. Rotational Kinematics & Inertia
                    • Angular Equations: ω = ω₀ + αt, θ = ω₀t + 0.5 αt², ω² = ω₀² + 2αθ
                    • Moment of Inertia I = ∑ m_i r_i² = ∫ r² dm
                    • Radius of Gyration: k = √(I / M)
                    • Uniform Rod (Center): I = (1/12) M L²
                    • Uniform Rod (End): I = (1/3) M L²
                    • Solid Cylinder / Disk: I = 0.5 M R²
                    • Solid Sphere: I = (2/5) M R²

                    II. Theorems & Torque
                    • Parallel Axes Theorem: I = I_cm + M d²
                    • Perpendicular Axes Theorem (Lamina): I_z = I_x + I_y
                    • Torque: τ = I α = dL / dt
                    • Rotational Kinetic Energy: K_rot = 0.5 I ω²
                    • Total Rolling Kinetic Energy: K_total = 0.5 M v² (1 + k² / R²)
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Reaction Mechanisms & Functional Groups",
                subject = SubjectCategory.CHEMISTRY.title,
                unit = "Organic Chemistry",
                chapter = "Haloalkanes & Haloarenes",
                description = "Nucleophilic substitution (SN1 vs SN2), electrophilic aromatic substitution, Markovnikov additions, and carbonyl reactions.",
                content = """
                    I. Nucleophilic Substitution (SN1 vs SN2)
                    • SN1: Two-step, carbocation intermediate, racemization, favored by tertiary halides and polar protic solvents. Rate = k[R-X].
                    • SN2: One-step concerted mechanism, backside attack with Walden inversion, favored by primary halides and polar aprotic solvents (DMF, DMSO). Rate = k[R-X][Nu⁻].

                    II. Elimination Reactions
                    • Saytzeff Rule: More substituted alkene is the major product.
                    • E1 vs E2: E1 proceeds via carbocation; E2 is concerted anti-periplanar.

                    III. Electrophilic Aromatic Substitution (EAS)
                    • Halogenation: FeBr3 + Br2 -> electrophile Br⁺
                    • Nitration: HNO3 + H2SO4 -> NO2⁺
                    • Friedel-Crafts: AlCl3 + R-Cl -> R⁺ carbocation
                    • Ortho/Para directors: -OH, -NH2, -OCH3, alkyl groups
                    • Meta directors: -NO2, -CN, -COOH, -CHO
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Human Cardiovascular & Neural System",
                subject = SubjectCategory.ZOOLOGY.title,
                unit = "Human Biology and Physiology",
                chapter = "Circulatory System & Blood",
                description = "Cardiac cycle stages, ECG waveform analysis, blood pressure regulation, and nerve action potential phases.",
                content = """
                    I. Cardiac Cycle & Circulation
                    • Total Cycle Duration: ~0.8 seconds (72 beats/min)
                    • Auricular Systole: 0.1s
                    • Ventricular Systole: 0.3s (Lubb sound - AV valves close)
                    • Complete Cardiac Diastole: 0.4s (Dubb sound - Semilunar valves close)
                    • Stroke Volume: ~70 mL; Cardiac Output = 70 mL * 72 = 5.04 L/min

                    II. Electrocardiogram (ECG) Waves
                    • P wave: Depolarization of atria
                    • QRS complex: Depolarization of ventricles (marks ventricular contraction)
                    • T wave: Repolarization of ventricles

                    III. Action Potential in Neurons
                    • Resting Membrane Potential: -70 mV maintained by Na+/K+ ATPase pump (3 Na+ out, 2 K+ in)
                    • Depolarization: Opening of voltage-gated Na+ channels -> overshoot to +30 mV
                    • Repolarization: Closure of Na+ channels, opening of voltage-gated K+ channels
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Cellular Organelles & Cell Division",
                subject = SubjectCategory.BOTANY.title,
                unit = "Cell Biology & Genetics",
                chapter = "Cell Structure & Organelles",
                description = "Comparison of plant vs animal cells, chloroplast thylakoid light reactions, and stages of Mitosis and Meiosis crossing over.",
                content = """
                    I. Plant Cell Ultrastructure
                    • Primary Cell Wall: Cellulose microfibrils, hemicellulose, pectin
                    • Middle Lamella: Calcium and Magnesium pectate
                    • Chloroplasts: Double membrane; Thylakoid grana (Light reaction / photophosphorylation), Stroma (Dark reaction / Calvin cycle)
                    • Vacuole: Surrounded by single semipermeable membrane (Tonoplast); maintains turgor pressure

                    II. Mitosis vs Meiosis
                    • Mitosis: Equational division producing 2 identical 2n daughter cells
                    • Meiosis I: Reductional division (2n -> n)
                    • Prophase I Sub-stages:
                      - Leptotene: Chromosome condensation
                      - Zygotene: Synapsis forming bivalents
                      - Pachytene: Crossing over mediated by Recombinase enzyme
                      - Diplotene: Chiasmata visible
                      - Diakinesis: Terminalization of chiasmata
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Number Series & Pattern Deductions",
                subject = SubjectCategory.MAT.title,
                unit = "Numerical Reasoning",
                chapter = "Number Series & Arithmetic",
                description = "Mastering sequences: geometric progression, prime shifts, alternating operations, and analogical reasoning techniques.",
                content = """
                    I. Core Series Patterns
                    • Prime Number Sequences: 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31...
                    • Squares & Cubes +/- n: (n² - 1), (n³ + 1), n³ - n
                    • Difference of Differences: When first difference is non-linear, take second order differences to reveal constant step.
                    • Fibonacci Variants: Each term equals sum of prior two or three terms plus a constant multiplier.

                    II. Alternating Operations
                    • Dual Series: Odd terms follow one pattern (*2), even terms follow another (+3).
                    • Direction and Distance: Always establish standard cardinal cross (N, S, E, W) and track displacement vectors.
                """.trimIndent()
            )
        )

        for (sample in sampleConfigs) {
            val result = PdfFileManager.createQuickPdfDocument(
                context = context,
                title = sample.title,
                subject = sample.subject,
                unit = sample.unit,
                notesContent = sample.content
            )
            if (result != null) {
                notePdfDao.insertNote(
                    NotePdf(
                        title = sample.title,
                        subject = sample.subject,
                        unit = sample.unit,
                        chapter = sample.chapter,
                        description = sample.description,
                        filePath = result.file.absolutePath,
                        fileName = result.fileName,
                        fileSizeBytes = result.fileSizeBytes,
                        pageCount = result.pageCount,
                        docType = "PDF",
                        isFavorite = (sample.subject == "Physics" || sample.subject == "Zoology"),
                        tags = "${sample.subject}, ${sample.unit}, ${sample.chapter}"
                    )
                )
            }
        }

        // Additional High-Yield PDF Study Guides
        val extraPdfs = listOf(
            SampleNoteDef(
                title = "Kinematics Practice Problems & Derivations",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Kinematics",
                description = "Graphical derivations of equations of motion, relative velocity, and projectile motion question bank.",
                content = """
                    I. Graphical Derivations
                    • Velocity-Time Graph: Slope represents instantaneous acceleration a = dv/dt. Area under curve represents displacement s = ∫ v dt.
                    • Acceleration-Time Graph: Area under curve gives change in velocity Δv.
                    
                    II. Key Relative Velocity Cases
                    • River-Boat Problem:
                      - Shortest Path (perpendicular across): sin θ = v_r / v_b; Resultant velocity = √(v_b² - v_r²).
                      - Shortest Time: Head directly across (θ = 90°); Time t = d / v_b; Drift = v_r * (d / v_b).
                    • Rain-Man Problem: v_rain/man = v_rain - v_man.
                    
                    III. Practice Numerical Tips
                    • When acceleration is not constant: Must use calculus a = v(dv/dx) = dv/dt.
                    • In projectile motion: Horizontal component remains constant (u_x = u cos θ); Vertical component undergoes uniform acceleration -g.
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Dynamics, Newton's Laws & Friction Guide",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Dynamics",
                description = "Comprehensive review of Newton's laws, static and kinetic friction coefficients, impulse-momentum theorem, and circular motion dynamics.",
                content = """
                    I. Newton's Laws of Motion
                    • First Law (Inertia): Bodies persist in state of rest or uniform rectilinear motion unless compelled by net external force.
                    • Second Law: Net Force = rate of change of momentum = m * a (for constant mass).
                    • Third Law: Action and Reaction are equal in magnitude, opposite in direction, and act on different bodies.
                    
                    II. Friction & Limiting Value
                    • Static Friction: Self-adjusting force up to limiting value f_s(max) = μ_s * N.
                    • Kinetic Friction: f_k = μ_k * N, where μ_k < μ_s.
                    • Angle of Repose (θ) = Angle of Friction (λ): tan θ = μ_s.
                    
                    III. Work, Energy & Momentum
                    • Work-Energy Theorem: Total work by all forces = ΔK = 1/2 m (v² - u²).
                    • Impulse: J = F_avg * Δt = Δp = m v - m u.
                    • Conservation of Momentum: If F_ext = 0, initial momentum = final momentum.
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Dynamics - Work, Power & Energy Notes",
                subject = SubjectCategory.PHYSICS.title,
                unit = "Mechanics",
                chapter = "Dynamics",
                description = "Conservative vs non-conservative forces, potential energy diagrams, collisions in 1D and 2D, and power formulas.",
                content = """
                    I. Work and Conservative Forces
                    • Work = ∫ F · dr = F s cos θ.
                    • Conservative Forces: Work done is path independent. F = -dU/dr. Examples: Gravitational, Electrostatic, Spring forces.
                    • Potential Energy: U = -∫ F · dr. Gravitational: U = mgh. Spring: U = 1/2 k x².
                    
                    II. Collisions
                    • Coefficient of Restitution e = (v₂ - v₁) / (u₁ - u₂).
                      - Perfectly Elastic: e = 1 (Momentum & Kinetic Energy conserved).
                      - Inelastic: 0 < e < 1 (Momentum conserved, KE lost as heat/sound).
                      - Perfectly Inelastic: e = 0 (Bodies stick together, maximum KE lost).
                    
                    III. Power
                    • Instantaneous Power P = dW/dt = F · v.
                    • Average Power P_avg = W_total / Δt.
                """.trimIndent()
            ),
            SampleNoteDef(
                title = "Cardiac Physiology & Hemodynamics",
                subject = SubjectCategory.ZOOLOGY.title,
                unit = "Human Biology and Physiology",
                chapter = "Circulatory System & Blood",
                description = "Detailed review of heart anatomy, conduction pathways (SA node to Purkinje), cardiac cycle events, and blood pressure regulation.",
                content = """
                    I. Cardiac Anatomy & Conduction
                    • Four Chambers: Right and Left Atria, Right and Left Ventricles.
                    • Pacemaker Sequence: SA Node (72 bpm) -> AV Node (40-60 bpm, 0.1s delay) -> Bundle of His -> Purkinje fibers.
                    • Heart Valves: Tricuspid (Right AV), Bicuspid/Mitral (Left AV), Aortic and Pulmonary Semilunar valves.
                    
                    II. Cardiac Cycle Stages (0.8s)
                    • Atrial Systole: 0.1s (P wave).
                    • Ventricular Systole: 0.3s (QRS wave; Lubb sound due to closure of AV valves).
                    • Joint Diastole: 0.4s (Dubb sound due to closure of semilunar valves).
                    • Stroke Volume = 70 mL, Cardiac Output = SV * HR = ~5.0 L/min.
                    
                    III. Blood Pressure & Regulation
                    • Standard Healthy BP: 120/80 mmHg.
                    • Vasomotor Center in Medulla Oblongata receives input from carotid sinus and aortic baroreceptors.
                """.trimIndent()
            )
        )

        for (sample in extraPdfs) {
            val result = PdfFileManager.createQuickPdfDocument(
                context = context,
                title = sample.title,
                subject = sample.subject,
                unit = sample.unit,
                notesContent = sample.content
            )
            if (result != null) {
                notePdfDao.insertNote(
                    NotePdf(
                        title = sample.title,
                        subject = sample.subject,
                        unit = sample.unit,
                        chapter = sample.chapter,
                        description = sample.description,
                        filePath = result.file.absolutePath,
                        fileName = result.fileName,
                        fileSizeBytes = result.fileSizeBytes,
                        pageCount = result.pageCount,
                        docType = "PDF",
                        isFavorite = true,
                        tags = "${sample.subject}, ${sample.unit}, ${sample.chapter}"
                    )
                )
            }
        }
    }

    suspend fun normalizeAllToPdf() = withContext(Dispatchers.IO) {
        notePdfDao.normalizeAllToPdf()
    }
}

private data class SampleNoteDef(
    val title: String,
    val subject: String,
    val unit: String,
    val chapter: String,
    val description: String,
    val content: String
)
