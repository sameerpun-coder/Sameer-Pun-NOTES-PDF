package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SyllabusChapter(
    val name: String,
    val microTopics: List<String> = emptyList()
)

data class SyllabusUnit(
    val name: String,
    val marks: Int,
    val chapters: List<SyllabusChapter>
)

enum class SubjectCategory(
    val id: String,
    val title: String,
    val marks: Int,
    val accentColor: Color,
    val lightContainerColor: Color,
    val darkContainerColor: Color,
    val syllabusUnits: List<SyllabusUnit>
) {
    PHYSICS(
        id = "physics",
        title = "Physics",
        marks = 50,
        accentColor = Color(0xFF6366F1), // Indigo/Violet
        lightContainerColor = Color(0xFF1E1B4B),
        darkContainerColor = Color(0xFF0F172A),
        syllabusUnits = listOf(
            SyllabusUnit(
                name = "Mechanics",
                marks = 10,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Physical quantities, vectors and scalars",
                        microTopics = listOf(
                            "Precision, accuracy, significant figures",
                            "Dimensional analysis",
                            "Concept, laws and calculations of vectors and scalars"
                        )
                    ),
                    SyllabusChapter(
                        name = "Kinematics",
                        microTopics = listOf(
                            "Linear and projectile motions",
                            "In presence as well as absence of resistive force",
                            "Concepts, calculations and graphical treatment"
                        )
                    ),
                    SyllabusChapter(
                        name = "Dynamics",
                        microTopics = listOf(
                            "Newton's laws and equilibrium conditions",
                            "Force, impulse, momentum, torque",
                            "Work, energy and power",
                            "Linear motions, collisions, solid friction"
                        )
                    ),
                    SyllabusChapter(
                        name = "Rotational dynamics",
                        microTopics = listOf(
                            "Moment of inertia (rigid uniform rod only)",
                            "Radius of gyration",
                            "Torque, work, energy and power related to rotational motion"
                        )
                    ),
                    SyllabusChapter(
                        name = "Fluid statics and dynamics",
                        microTopics = listOf(
                            "Pressure, surface tension and energy",
                            "Capillary action",
                            "Newton, Stokes, Poiseuille, Bernoulli laws/principles related to fluids"
                        )
                    ),
                    SyllabusChapter(
                        name = "Gravitation",
                        microTopics = listOf(
                            "Newton's law of gravitation",
                            "Gravitational potential and intensity",
                            "Escape velocity and satellite motion"
                        )
                    ),
                    SyllabusChapter(
                        name = "Elasticity",
                        microTopics = listOf(
                            "Hooke's law and stress-strain curve",
                            "Elastic moduli (Young, Bulk, Shear)",
                            "Elastic potential energy"
                        )
                    ),
                    SyllabusChapter(
                        name = "Periodic motion & SHM",
                        microTopics = listOf(
                            "Simple harmonic motion characteristics",
                            "Energy in SHM",
                            "Simple pendulum and spring oscillations"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Heat and thermodynamics",
                marks = 6,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Thermal expansion & Thermometry",
                        microTopics = listOf(
                            "Temperature scales and calibration",
                            "Linear, superficial and cubical expansion",
                            "Apparent expansion of liquids"
                        )
                    ),
                    SyllabusChapter(
                        name = "Calorimetry & Change of state",
                        microTopics = listOf(
                            "Specific heat capacity",
                            "Latent heat of fusion and vaporization",
                            "Newton's law of cooling"
                        )
                    ),
                    SyllabusChapter(
                        name = "First and Second Laws of Thermodynamics",
                        microTopics = listOf(
                            "Isothermal and adiabatic processes",
                            "Work done in cyclic processes",
                            "Carnot engine and efficiency"
                        )
                    ),
                    SyllabusChapter(
                        name = "Heat Transfer",
                        microTopics = listOf(
                            "Conduction and thermal conductivity",
                            "Stefan's law of radiation",
                            "Wien's displacement law and blackbody radiation"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Waves and optics",
                marks = 8,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Wave motion & Sound",
                        microTopics = listOf(
                            "Progressive and stationary waves",
                            "Speed of sound in gases & Laplace correction",
                            "Doppler effect in sound",
                            "Vibration in organ pipes and strings"
                        )
                    ),
                    SyllabusChapter(
                        name = "Geometrical Optics",
                        microTopics = listOf(
                            "Refraction at spherical surfaces",
                            "Lens maker formula and thin lens combinations",
                            "Prism, dispersion and optical instruments"
                        )
                    ),
                    SyllabusChapter(
                        name = "Wave Optics",
                        microTopics = listOf(
                            "Huygens' principle and wavefronts",
                            "Interference of light & Young's double slit",
                            "Diffraction at single slit and polarization"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Electrostatics and capacitors",
                marks = 6,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Electric charges & Coulomb's Law",
                        microTopics = listOf(
                            "Quantization and conservation of charge",
                            "Coulomb's law in vector form",
                            "Electric field and electric dipole"
                        )
                    ),
                    SyllabusChapter(
                        name = "Gauss's Law & Electric Potential",
                        microTopics = listOf(
                            "Electric flux and Gauss's theorem",
                            "Electric potential due to point charge and dipole",
                            "Equipotential surfaces"
                        )
                    ),
                    SyllabusChapter(
                        name = "Capacitance & Dielectrics",
                        microTopics = listOf(
                            "Parallel plate capacitor",
                            "Combinations in series and parallel",
                            "Energy stored and effect of dielectrics"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Current electricity and magnetism",
                marks = 10,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Current, Resistance & DC Circuits",
                        microTopics = listOf(
                            "Ohm's law, drift velocity, resistivity",
                            "Kirchhoff's laws and applications",
                            "Wheatstone bridge and potentiometer"
                        )
                    ),
                    SyllabusChapter(
                        name = "Magnetic effects of current",
                        microTopics = listOf(
                            "Biot-Savart law and Ampere's circuital law",
                            "Force on moving charge & current-carrying wire",
                            "Galvanometer conversion to ammeter/voltmeter"
                        )
                    ),
                    SyllabusChapter(
                        name = "Electromagnetic Induction & AC",
                        microTopics = listOf(
                            "Faraday's laws and Lenz's law",
                            "Self and mutual inductance",
                            "LCR circuits, resonance and transformer"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Modern physics",
                marks = 10,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Photons & Photoelectric Effect",
                        microTopics = listOf(
                            "Quantum theory of radiation",
                            "Einstein's photoelectric equation",
                            "Work function and stopping potential"
                        )
                    ),
                    SyllabusChapter(
                        name = "Atomic Models & X-rays",
                        microTopics = listOf(
                            "Bohr's model of hydrogen atom",
                            "Energy levels and emission spectrum",
                            "Production and properties of X-rays"
                        )
                    ),
                    SyllabusChapter(
                        name = "Nuclear Physics & Radioactivity",
                        microTopics = listOf(
                            "Mass defect and binding energy",
                            "Laws of radioactive decay and half-life",
                            "Nuclear fission and fusion"
                        )
                    ),
                    SyllabusChapter(
                        name = "Semiconductors & Electronics",
                        microTopics = listOf(
                            "P-N junction diode characteristics",
                            "Half-wave and full-wave rectifiers",
                            "Logic gates (AND, OR, NOT, NAND, NOR)"
                        )
                    )
                )
            )
        )
    ),
    CHEMISTRY(
        id = "chemistry",
        title = "Chemistry",
        marks = 50,
        accentColor = Color(0xFF0D9488), // Teal
        lightContainerColor = Color(0xFF134E4A),
        darkContainerColor = Color(0xFF042F2E),
        syllabusUnits = listOf(
            SyllabusUnit(
                name = "Physical Chemistry",
                marks = 18,
                chapters = listOf(
                    SyllabusChapter(
                        name = "States of Matter & Gas Laws",
                        microTopics = listOf(
                            "Ideal gas equation (PV = nRT)",
                            "Kinetic molecular theory of gases",
                            "Dalton's law and Graham's diffusion law"
                        )
                    ),
                    SyllabusChapter(
                        name = "Atomic Structure & Chemical Bonding",
                        microTopics = listOf(
                            "Bohr's model, de Broglie relation, Heisenberg principle",
                            "Quantum numbers and orbital electronic configuration",
                            "Ionic, covalent, coordinate bonding and hybridization"
                        )
                    ),
                    SyllabusChapter(
                        name = "Chemical Thermodynamics & Energetics",
                        microTopics = listOf(
                            "Enthalpy, Hess's law of constant heat summation",
                            "Entropy and Gibbs free energy (ΔG = ΔH - TΔS)",
                            "Spontaneity criteria in reactions"
                        )
                    ),
                    SyllabusChapter(
                        name = "Chemical & Ionic Equilibrium",
                        microTopics = listOf(
                            "Law of mass action, Kp and Kc relations",
                            "Le Chatelier's principle and applications",
                            "pH calculations, buffer solutions and solubility product"
                        )
                    ),
                    SyllabusChapter(
                        name = "Electrochemistry & Chemical Kinetics",
                        microTopics = listOf(
                            "Electrochemical cells and Nernst equation",
                            "Faraday's laws of electrolysis",
                            "Rate of reaction, order and Arrhenius equation"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Inorganic Chemistry",
                marks = 14,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Periodic Classification & Periodicity",
                        microTopics = listOf(
                            "Modern periodic law and periodic trends",
                            "Ionization energy, electron affinity, electronegativity",
                            "Valency and atomic/ionic radii variations"
                        )
                    ),
                    SyllabusChapter(
                        name = "s-Block & p-Block Elements",
                        microTopics = listOf(
                            "Alkali and alkaline earth metals compounds",
                            "Boron, carbon, nitrogen and oxygen family",
                            "Halogens and noble gases chemistry"
                        )
                    ),
                    SyllabusChapter(
                        name = "d-Block & Coordination Compounds",
                        microTopics = listOf(
                            "Transition metals general electronic configurations",
                            "Catalytic properties and variable oxidation states",
                            "Werner's theory, ligands, IUPAC nomenclature"
                        )
                    ),
                    SyllabusChapter(
                        name = "Metallurgy & Metal Extraction",
                        microTopics = listOf(
                            "Concentration of ores, roasting and calcination",
                            "Extraction of Iron, Copper, Zinc and Silver",
                            "Principles of refining and smelting"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Organic Chemistry",
                marks = 14,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Basic Principles & Hydrocarbons",
                        microTopics = listOf(
                            "IUPAC nomenclature and isomerism",
                            "Inductive, resonance and electromeric effects",
                            "Alkanes, alkenes and alkynes addition reactions"
                        )
                    ),
                    SyllabusChapter(
                        name = "Haloalkanes & Haloarenes",
                        microTopics = listOf(
                            "SN1 and SN2 nucleophilic substitution",
                            "Elimination reactions (Saytzeff rule)",
                            "Reactions of chlorobenzene and Grignard reagent"
                        )
                    ),
                    SyllabusChapter(
                        name = "Alcohols, Phenols and Ethers",
                        microTopics = listOf(
                            "Distinction tests (Lucas test, Victor Meyer)",
                            "Acidity of phenols and electrophilic substitutions",
                            "Williamson ether synthesis"
                        )
                    ),
                    SyllabusChapter(
                        name = "Aldehydes, Ketones & Carboxylic Acids",
                        microTopics = listOf(
                            "Nucleophilic addition reactions",
                            "Aldol condensation and Cannizzaro reaction",
                            "Acidity of carboxylic acids and esterification"
                        )
                    ),
                    SyllabusChapter(
                        name = "Amines & Nitrogen Compounds",
                        microTopics = listOf(
                            "Classification and basicity of amines",
                            "Carbylamine test and Hinsberg test",
                            "Diazonium salts and coupling reactions"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Applied & Analytical Chemistry",
                marks = 4,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Polymers, Dyes & Biomolecules",
                        microTopics = listOf(
                            "Addition and condensation polymerization",
                            "Carbohydrates, proteins and nucleic acids",
                            "Classification of therapeutic drugs and dyes"
                        )
                    ),
                    SyllabusChapter(
                        name = "Volumetric & Qualitative Analysis",
                        microTopics = listOf(
                            "Standard solutions and titration indicators",
                            "Redox and acid-base volumetric calculations",
                            "Identification of acidic and basic radicals"
                        )
                    )
                )
            )
        )
    ),
    ZOOLOGY(
        id = "zoology",
        title = "Zoology",
        marks = 40,
        accentColor = Color(0xFFF43F5E), // Rose / Coral
        lightContainerColor = Color(0xFF881337),
        darkContainerColor = Color(0xFF4C0519),
        syllabusUnits = listOf(
            SyllabusUnit(
                name = "Human Biology and Physiology",
                marks = 14,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Digestive System & Nutrition",
                        microTopics = listOf(
                            "Histology of digestive canal",
                            "Enzymatic digestion of carbs, proteins, fats",
                            "Absorption of nutrients and liver functions"
                        )
                    ),
                    SyllabusChapter(
                        name = "Respiratory System & Gas Exchange",
                        microTopics = listOf(
                            "Mechanism of pulmonary breathing",
                            "Oxygen and carbon dioxide transport mechanisms",
                            "Respiratory volumes, capacities and regulation"
                        )
                    ),
                    SyllabusChapter(
                        name = "Circulatory System & Blood",
                        microTopics = listOf(
                            "Blood composition and coagulation cascade",
                            "Cardiac cycle, ECG and blood pressure",
                            "Double circulation and lymphatic system"
                        )
                    ),
                    SyllabusChapter(
                        name = "Excretory System & Osmoregulation",
                        microTopics = listOf(
                            "Nephron histology and urine formation",
                            "Counter-current multiplier mechanism",
                            "Hormonal regulation (ADH, RAAS)"
                        )
                    ),
                    SyllabusChapter(
                        name = "Nervous & Endocrine Coordination",
                        microTopics = listOf(
                            "Nerve impulse conduction and synapse",
                            "Central and peripheral nervous system",
                            "Pituitary, thyroid, adrenal hormones and feedback loops"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Animal Histology & Selected Animals",
                marks = 10,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Animal Tissues & Histology",
                        microTopics = listOf(
                            "Epithelial tissue types and functions",
                            "Connective tissue (bone, cartilage, blood)",
                            "Muscular and nervous tissues"
                        )
                    ),
                    SyllabusChapter(
                        name = "Earthworm Anatomy & Biology",
                        microTopics = listOf(
                            "Morphology, coelom and locomotion",
                            "Alimentary canal and typhlosole function",
                            "Reproductive and nephridial systems"
                        )
                    ),
                    SyllabusChapter(
                        name = "Frog Anatomy & Organ Systems",
                        microTopics = listOf(
                            "Digestive and respiratory adaptations",
                            "Circulatory and arterial/venous arches",
                            "Urinogenital system"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Animal Diversity and Classification",
                marks = 8,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Non-Chordata Classification",
                        microTopics = listOf(
                            "Protozoa, Porifera and Coelenterata features",
                            "Platyhelminthes, Aschelminthes and Annelida",
                            "Arthropoda, Mollusca and Echinodermata characters"
                        )
                    ),
                    SyllabusChapter(
                        name = "Chordata Classification",
                        microTopics = listOf(
                            "Protochordata (Urochordata, Cephalochordata)",
                            "Vertebrata classes (Pisces, Amphibia, Reptilia)",
                            "Aves and Mammalia key anatomical features"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Evolutionary Biology",
                marks = 4,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Origin of Life & Evolution Theories",
                        microTopics = listOf(
                            "Oparin-Haldane theory and Miller-Urey experiment",
                            "Lamarckism, Darwinism and Modern Synthetic Theory",
                            "Paleontological and embryological evidences"
                        )
                    ),
                    SyllabusChapter(
                        name = "Human Evolution",
                        microTopics = listOf(
                            "Dryopithecus, Ramapithecus to Australopithecus",
                            "Homo habilis, Homo erectus, Neanderthal man",
                            "Homo sapiens cranial capacity evolution"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Microbial Diseases & Immunology",
                marks = 4,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Immune System & Vaccines",
                        microTopics = listOf(
                            "Innate and acquired immunity",
                            "Humoral and cell-mediated immune responses",
                            "Antibody structure and vaccination principles"
                        )
                    ),
                    SyllabusChapter(
                        name = "Human Pathogens & Diseases",
                        microTopics = listOf(
                            "Malaria lifecycle (Plasmodium vivax)",
                            "Bacterial, viral and fungal infectious diseases",
                            "AIDS, cancer and drug abuse"
                        )
                    )
                )
            )
        )
    ),
    BOTANY(
        id = "botany",
        title = "Botany",
        marks = 40,
        accentColor = Color(0xFF10B981), // Emerald
        lightContainerColor = Color(0xFF064E3B),
        darkContainerColor = Color(0xFF022C22),
        syllabusUnits = listOf(
            SyllabusUnit(
                name = "Plant Anatomy & Physiology",
                marks = 14,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Plant Tissues & Internal Structure",
                        microTopics = listOf(
                            "Meristematic and permanent plant tissues",
                            "Internal structure of monocot and dicot stems",
                            "Internal structure of roots and dorsiventral leaves"
                        )
                    ),
                    SyllabusChapter(
                        name = "Water Relations & Transpiration",
                        microTopics = listOf(
                            "Diffusion, osmosis and water potential (Ψw)",
                            "Mechanism of stomatal opening and closing",
                            "Ascent of sap (Cohesion-tension theory)"
                        )
                    ),
                    SyllabusChapter(
                        name = "Photosynthesis (Light & Dark)",
                        microTopics = listOf(
                            "Photosystems I and II, photophosphorylation",
                            "Calvin cycle (C3) and Hatch-Slack pathway (C4)",
                            "Photorespiration and factors affecting rate"
                        )
                    ),
                    SyllabusChapter(
                        name = "Cellular Respiration in Plants",
                        microTopics = listOf(
                            "Glycolysis in cytoplasm",
                            "Krebs cycle in mitochondrial matrix",
                            "Electron transport chain and ATP synthesis"
                        )
                    ),
                    SyllabusChapter(
                        name = "Plant Growth Regulators",
                        microTopics = listOf(
                            "Auxins, gibberellins and cytokinins functions",
                            "Ethylene and abscisic acid (stress hormone)",
                            "Photoperiodism and vernalization"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Cell Biology & Genetics",
                marks = 10,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Cell Structure & Organelles",
                        microTopics = listOf(
                            "Prokaryotic vs eukaryotic cell architecture",
                            "Endomembrane system, mitochondria, plastids",
                            "Chromosome structure, nucleosome and centromere"
                        )
                    ),
                    SyllabusChapter(
                        name = "Cell Division (Mitosis & Meiosis)",
                        microTopics = listOf(
                            "Cell cycle phases (G1, S, G2, M)",
                            "Stages of mitosis and cytokinesis",
                            "Meiosis I (crossing over in pachytene) and significance"
                        )
                    ),
                    SyllabusChapter(
                        name = "Mendelian Genetics & Inheritance",
                        microTopics = listOf(
                            "Monohybrid and dihybrid cross ratios",
                            "Incomplete dominance and codominance",
                            "Linkage, recombination and sex-linked traits"
                        )
                    ),
                    SyllabusChapter(
                        name = "Molecular Genetics & DNA",
                        microTopics = listOf(
                            "DNA double helix structure (Watson-Crick)",
                            "Semiconservative DNA replication",
                            "Central dogma: transcription and translation"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Biodiversity & Plant Diversity",
                marks = 12,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Microorganisms & Cryptogams",
                        microTopics = listOf(
                            "Bacteria structure, Gram staining and reproduction",
                            "Viruses (Bacteriophage and TMV structure)",
                            "Algae and Fungi life cycles and economic uses"
                        )
                    ),
                    SyllabusChapter(
                        name = "Bryophytes & Pteridophytes",
                        microTopics = listOf(
                            "Riccia and Marchantia morphology and reproduction",
                            "Funaria gametophyte and sporophyte",
                            "Pteris and Selaginella anatomy and heterospory"
                        )
                    ),
                    SyllabusChapter(
                        name = "Gymnosperms & Angiosperms",
                        microTopics = listOf(
                            "Cycas and Pinus morphology and life cycle",
                            "Structure of flower, megasporogenesis, microsporogenesis",
                            "Double fertilization and endosperm development"
                        )
                    ),
                    SyllabusChapter(
                        name = "Families of Angiosperms",
                        microTopics = listOf(
                            "Fabaceae (Papilionaceae) floral formula and diagram",
                            "Solanaceae characteristic features and economic importance",
                            "Brassicaceae and Poaceae (Gramineae) description"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Ecology & Applied Botany",
                marks = 4,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Ecosystem Dynamics & Ecology",
                        microTopics = listOf(
                            "Ecosystem structure, abiotic and biotic factors",
                            "Food chain, food web and ecological pyramids",
                            "Plant succession (hydrosere and xerosere)"
                        )
                    ),
                    SyllabusChapter(
                        name = "Applied Botany & Conservation",
                        microTopics = listOf(
                            "Economic plants (medicinal, timber, oil)",
                            "Plant tissue culture and recombinant DNA in crops",
                            "Biodiversity threats and in-situ/ex-situ conservation"
                        )
                    )
                )
            )
        )
    ),
    MAT(
        id = "mat",
        title = "MAT",
        marks = 20,
        accentColor = Color(0xFFA855F7), // Purple
        lightContainerColor = Color(0xFF581C87),
        darkContainerColor = Color(0xFF3B0764),
        syllabusUnits = listOf(
            SyllabusUnit(
                name = "Verbal Reasoning",
                marks = 5,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Analogies & Word Association",
                        microTopics = listOf(
                            "Synonym and antonym relationship pairs",
                            "Part-to-whole and cause-effect word pairs",
                            "Contextual vocabulary inferences"
                        )
                    ),
                    SyllabusChapter(
                        name = "Syllogisms & Deductive Logic",
                        microTopics = listOf(
                            "Venn diagram method for categorical syllogisms",
                            "Statement and conclusion validation",
                            "Statement and assumption reasoning"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Numerical Reasoning",
                marks = 5,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Number Series & Arithmetic",
                        microTopics = listOf(
                            "Arithmetic and geometric number series",
                            "Alternating and difference of differences series",
                            "Missing numbers in patterns and matrices"
                        )
                    ),
                    SyllabusChapter(
                        name = "Mathematical Operations & Speed Math",
                        microTopics = listOf(
                            "Symbol substitution and operator precedence",
                            "Percentage, ratio and average quick calculations",
                            "Data sufficiency tests"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Logical Sequencing",
                marks = 5,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Direction Sense & Blood Relations",
                        microTopics = listOf(
                            "Compass directions and displacement calculations",
                            "Coded and descriptive family tree relations",
                            "Facing directions and shadow puzzles"
                        )
                    ),
                    SyllabusChapter(
                        name = "Coding-Decoding & Ranking",
                        microTopics = listOf(
                            "Letter shifting and position replacement codes",
                            "Linear and circular seating arrangement",
                            "Order, ranking and calendar calculations"
                        )
                    )
                )
            ),
            SyllabusUnit(
                name = "Spatial Relation & Abstract Reasoning",
                marks = 5,
                chapters = listOf(
                    SyllabusChapter(
                        name = "Figure Series & Matrix Reasoning",
                        microTopics = listOf(
                            "Rotation and translation of geometric figures",
                            "Pattern continuation and matrix completion",
                            "Odd figure out / grouping of images"
                        )
                    ),
                    SyllabusChapter(
                        name = "Mirror, Water & Paper Folding",
                        microTopics = listOf(
                            "Horizontal and vertical axis reflections",
                            "Paper folding and punch hole pattern prediction",
                            "Cube and dice spatial projections"
                        )
                    )
                )
            )
        )
    );

    val units: List<String>
        get() = syllabusUnits.map { it.name }

    fun getIcon(): ImageVector {
        return when (this) {
            PHYSICS -> Icons.Default.Bolt
            CHEMISTRY -> Icons.Default.Science
            ZOOLOGY -> Icons.Default.Pets
            BOTANY -> Icons.Default.Eco
            MAT -> Icons.Default.Calculate
        }
    }

    companion object {
        fun fromTitle(title: String): SubjectCategory {
            return entries.find { it.title.equals(title, ignoreCase = true) } ?: PHYSICS
        }

        fun getDefaultChapters(unit: String): List<String> {
            for (subject in entries) {
                val foundUnit = subject.syllabusUnits.find { it.name.equals(unit, ignoreCase = true) }
                if (foundUnit != null) {
                    return foundUnit.chapters.map { it.name }
                }
            }
            return listOf("General Chapter Notes", "Key Formulas & Review", "Past Questions")
        }

        fun getChapterDefinition(subjectName: String, unitName: String, chapterName: String): SyllabusChapter? {
            val subject = entries.find { it.title.equals(subjectName, ignoreCase = true) } ?: return null
            val unit = subject.syllabusUnits.find { it.name.equals(unitName, ignoreCase = true) } ?: return null
            return unit.chapters.find { it.name.equals(chapterName, ignoreCase = true) }
        }
    }
}
