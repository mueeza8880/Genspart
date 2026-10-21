package com.example.data.seed

import com.example.data.model.FutureSkillsPathway
import com.example.data.model.PathwayProject

object PathwaySeedData {
  val pathways: List<FutureSkillsPathway> = listOf(
    FutureSkillsPathway(
      id = "young_scientist",
      title = "Young Scientist",
      subtitle = "Observation, hypothesis & discovering the natural world",
      iconEmoji = "🔬",
      accentColorHex = "#087F8C",
      description = "Nurtures the instinct to wonder, ask 'why?', notice patterns, and test ideas through gentle experimentation without pressure.",
      relatedEverydaySkills = listOf("Curious observation", "Pattern recognition", "Cause and effect", "Formulating questions"),
      ageGuidance = "Ages 3–10: Starts with sensory discovery of leaves and water, progressing to student-directed weather logs and experiments.",
      projects = listOf(
        PathwayProject("sci_p1", "The Floating & Sinking Investigation", "Ages 3–5", "Test everyday objects in a water tub to discover buoyancy.", "Tub of water, plastic spoon, rock, wooden block, coin", "20 mins"),
        PathwayProject("sci_p2", "Backyard Nature Detective Journal", "Ages 5–7", "Sketch three different types of leaves and note textures.", "Paper notebook, colored pencils, magnifying glass", "30 mins"),
        PathwayProject("sci_p3", "Kitchen Shadow & Light Lab", "Ages 7–10", "Explore how distance affects shadow size using a flashlight and toys.", "Flashlight, white cardboard, toy figurines, ruler", "25 mins")
      ),
      reflectionQuestions = listOf(
        "What unexpected question did your child ask today about how things work?",
        "Did your child test something twice to see if the outcome repeated?",
        "How did your child respond when a guess turned out differently than expected?"
      ),
      challengeChecklist = listOf(
        "Notice and describe clouds three days in a row",
        "Perform a safe ice-melting race with warm vs cool water",
        "Collect five natural textures (smooth pebble, rough bark, soft petal)",
        "Ask 'What do you think will happen next?' before an everyday event"
      )
    ),

    FutureSkillsPathway(
      id = "future_technologist",
      title = "Future Technologist",
      subtitle = "Computational thinking, sequencing & algorithmic play",
      iconEmoji = "💻",
      accentColorHex = "#1E6091",
      description = "Introduces logical sequencing, step-by-step algorithms, debugging everyday mistakes, and systems thinking—completely screen-free.",
      relatedEverydaySkills = listOf("Step-by-step logic", "Debugging & patience", "Directional awareness", "Spatial decomposition"),
      ageGuidance = "Ages 4–10: Teaches the logic behind code through floor mazes, recipe ordering, and symbol decoding.",
      projects = listOf(
        PathwayProject("tech_p1", "Parent Robot Floor Maze", "Ages 4–6", "Child gives step commands ('Forward 2, Turn Right') to guide parent through living room.", "Painter's tape or cushions for obstacles", "20 mins"),
        PathwayProject("tech_p2", "Secret Code Cipher Wheel", "Ages 7–9", "Create a rotating alphabet wheel to encrypt and decode friendly family notes.", "Paper plates, brass fastener, markers", "35 mins"),
        PathwayProject("tech_p3", "Binary Bead Bracelet", "Ages 8–10", "Represent initials using 2 colors of beads (representing 1s and 0s).", "Two colors of beads, string", "30 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child break down a big task into smaller steps?",
        "How did your child react when an instruction produced an unexpected result?",
        "Can your child explain the sequence of their morning routine?"
      ),
      challengeChecklist = listOf(
        "Create a visual 4-step recipe card for making a sandwich",
        "Play a game where someone follows only exact spoken rules",
        "Design a treasure hunt map with clear direction markers",
        "Debug an intentionally broken Lego tower to find the weak joint"
      )
    ),

    FutureSkillsPathway(
      id = "creative_artist",
      title = "Creative Artist",
      subtitle = "Visual expression, color harmony & imaginative storytelling",
      iconEmoji = "🎨",
      accentColorHex = "#FF8A65",
      description = "Fosters creative confidence and emotional release through tactile mediums, mixed textures, and personal artistic voice.",
      relatedEverydaySkills = listOf("Visual experimentation", "Fine motor precision", "Emotional expression", "Aesthetic appreciation"),
      ageGuidance = "Ages 2–10: From finger painting and collage to mixed-media landscape studies.",
      projects = listOf(
        PathwayProject("art_p1", "Texture Collage Board", "Ages 2–5", "Glue foil, fabric scraps, leaves, and string onto sturdy cardboard.", "Cardboard, child-safe glue, scraps of ribbon/paper", "25 mins"),
        PathwayProject("art_p2", "Recycled Cardboard Sculpture", "Ages 5–8", "Build an abstract 3D sculpture using cardboard slots without tape.", "Cereal boxes, child scissors, markers", "40 mins"),
        PathwayProject("art_p3", "Mood & Emotion Color Swatches", "Ages 7–10", "Paint abstract cards representing calm, energy, mystery, and joy.", "Watercolor paints, paper, brushes", "30 mins")
      ),
      reflectionQuestions = listOf(
        "What feelings or stories did your child express while making art?",
        "Did your child enjoy the process itself without worrying about perfection?",
        "Which color combinations did your child gravitate toward?"
      ),
      challengeChecklist = listOf(
        "Mix two primary colors to invent a brand-new color swatch",
        "Draw something using only circular shapes",
        "Create an art gallery corner to display two favorite creations",
        "Paint using an unconventional tool (sponge, cotton swab, fork)"
      )
    ),

    FutureSkillsPathway(
      id = "confident_communicator",
      title = "Confident Communicator",
      subtitle = "Expressive language, active listening & narrative craft",
      iconEmoji = "🎙️",
      accentColorHex = "#6B4E9E",
      description = "Builds vocabulary, conversational empathy, storytelling courage, and body language awareness in everyday family interactions.",
      relatedEverydaySkills = listOf("Story structure", "Listening without interruption", "Clear articulation", "Expressing personal needs"),
      ageGuidance = "Ages 2–10: From puppet conversations and silly rhyming to family interviews and story podcasts.",
      projects = listOf(
        PathwayProject("com_p1", "Sock Puppet Family Theater", "Ages 3–6", "Host a 5-minute puppet show with a beginning, middle, and problem solved.", "Clean socks, markers, buttons, sofa stage", "25 mins"),
        PathwayProject("com_p2", "Family Historian Interview", "Ages 6–9", "Child asks three prepared questions to a grandparent or parent about childhood.", "Notepad, pencil, mock cardboard microphone", "30 mins"),
        PathwayProject("com_p3", "Spontaneous 'Once Upon a Time' Pass", "Ages 5–10", "Take turns adding one sentence each to create an unpredictable adventure.", "None needed, just imagination", "15 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child listen attentively when someone else spoke?",
        "What descriptive adjectives did your child use to convey excitement?",
        "Did your child feel comfortable presenting their thoughts aloud?"
      ),
      challengeChecklist = listOf(
        "Share one interesting fact at family dinnertime",
        "Describe a favorite toy to someone whose eyes are closed",
        "Ask a family member 'How was your day?' and listen closely",
        "Invent a new word and define what it means"
      )
    ),

    FutureSkillsPathway(
      id = "young_innovator",
      title = "Young Innovator",
      subtitle = "Inventing solutions, design thinking & creative reuse",
      iconEmoji = "💡",
      accentColorHex = "#FFD166",
      description = "Teaches children to see problems as invitations to invent, repurpose household items, and iterate when things break.",
      relatedEverydaySkills = listOf("Divergent thinking", "Adaptability", "User empathy", "Iterative prototyping"),
      ageGuidance = "Ages 4–10: Inspires building contraptions, designing contraptions for toys, and improving daily family routines.",
      projects = listOf(
        PathwayProject("inn_p1", "The Egg or Ball Soft Landing Capsule", "Ages 6–10", "Build a capsule that cushions a dropped object using paper cups and tissue.", "Paper cups, rubber bands, cotton balls, bubble wrap", "35 mins"),
        PathwayProject("inn_p2", "Everyday Problem Fixer", "Ages 5–8", "Identify something annoying (like a rolling pencil) and invent a paper holder for it.", "Cardboard tubes, tape, markers", "25 mins"),
        PathwayProject("inn_p3", "Dream Vehicle Blueprint", "Ages 4–7", "Design a vehicle that travels over land, water, and snow.", "Large paper, colored pencils", "25 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child think of more than one way to solve the challenge?",
        "When a piece failed, did your child adjust rather than give up?",
        "What unique features did your child incorporate into their invention?"
      ),
      challengeChecklist = listOf(
        "Find three unusual uses for an empty cardboard tube",
        "Redesign a breakfast placemat with functional zones",
        "Build a ramp that makes a marble change direction twice",
        "Test an idea, write down what failed, and try version 2"
      )
    ),

    FutureSkillsPathway(
      id = "caring_leader",
      title = "Caring Leader",
      subtitle = "Empathy, collaboration, responsibility & conflict resolution",
      iconEmoji = "🤝",
      accentColorHex = "#2E7D5B",
      description = "Nurtures emotional intelligence, cooperative play, inclusive habits, and caring for people, pets, and our surroundings.",
      relatedEverydaySkills = listOf("Empathy & perspective-taking", "Teamwork & sharing", "Caring for living things", "Fairness"),
      ageGuidance = "Ages 2–10: Focuses on sharing roles, family responsibilities, recognizing peers' feelings, and acts of kindness.",
      projects = listOf(
        PathwayProject("lead_p1", "Family Kindness Secret Mission", "Ages 4–8", "Perform one helpful surprise for a sibling or parent without being asked.", "Post-it notes, colored pens", "20 mins"),
        PathwayProject("lead_p2", "Pet or Houseplant Chief Caretaker", "Ages 5–10", "Take daily responsibility for water, sunlight check, or brushing a pet.", "Watering can, gentle brush, checklist", "15 mins daily"),
        PathwayProject("lead_p3", "Cooperative Game Rule Designer", "Ages 6–10", "Modify a competitive game so that everyone wins together.", "Board game or deck of cards", "30 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child notice how another person was feeling today?",
        "Did your child offer assistance spontaneously to someone?",
        "How did your child handle a disagreement during play?"
      ),
      challengeChecklist = listOf(
        "Write or draw a thank-you note for a neighbor or teacher",
        "Invite someone else to make the first choice in a game",
        "Help pack up shared toys without being reminded twice",
        "Give a sincere compliment to a friend or sibling"
      )
    ),

    FutureSkillsPathway(
      id = "active_explorer",
      title = "Active Explorer",
      subtitle = "Spatial awareness, physical agility, nature navigation & endurance",
      iconEmoji = "🧭",
      accentColorHex = "#087F8C",
      description = "Encourages outdoor curiosity, bodily coordination, balance, environmental appreciation, and safe physical resilience.",
      relatedEverydaySkills = listOf("Gross motor coordination", "Map reading", "Balance & spatial control", "Resilience outdoors"),
      ageGuidance = "Ages 2–10: From living room obstacle courses to neighborhood nature orienteering.",
      projects = listOf(
        PathwayProject("exp_p1", "Living Room Agility Safari", "Ages 2–6", "Crawl under tables, step over pillows, and balance along a taped line.", "Pillows, painter's tape, low chairs", "25 mins"),
        PathwayProject("exp_p2", "Neighborhood Compass & Tree Map", "Ages 6–9", "Draw a simple map of your street identifying three distinct trees.", "Clipboard, paper, colored pencils", "40 mins"),
        PathwayProject("exp_p3", "Micro-Habitat Square Inch Study", "Ages 7–10", "Observe one square foot of grass for 10 minutes and list all creatures seen.", "String, four sticks, magnifying glass", "30 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child test their physical balance or body control today?",
        "What caught your child's eye outside that adults usually miss?",
        "How did being outdoors affect your child's calm and focus?"
      ),
      challengeChecklist = listOf(
        "Balance on one foot for 10 seconds (with both feet)",
        "Find a bug, watch where it travels, and describe its movements",
        "Walk backward in a straight line for 15 steps safely",
        "Spend 45 continuous minutes outdoors in nature or a park"
      )
    ),

    FutureSkillsPathway(
      id = "young_problem_solver",
      title = "Young Problem Solver",
      subtitle = "Analytical deduction, puzzle strategy & critical reflection",
      iconEmoji = "🧩",
      accentColorHex = "#D85D36",
      description = "Builds perseverance, deductive reasoning, logic games, and the ability to look at dilemmas from multiple angles.",
      relatedEverydaySkills = listOf("Deductive reasoning", "Patience with frustration", "Categorization", "Trial and error"),
      ageGuidance = "Ages 3–10: From sorting buttons by attributes to tangrams, sudoku, and scavenger deduction.",
      projects = listOf(
        PathwayProject("prob_p1", "Attribute Sorting Challenge", "Ages 3–5", "Sort a mixed bowl of buttons or toy cars by color, then by size, then by wheel type.", "Mixed small safe toys, sorting bowls", "20 mins"),
        PathwayProject("prob_p2", "Homemade Tangram Puzzle Kit", "Ages 5–8", "Cut a square into 7 geometric shapes and create animal silhouettes.", "Cardstock, scissors, ruler, envelope", "30 mins"),
        PathwayProject("prob_p3", "Deduction Mystery Clue Hunt", "Ages 7–10", "Follow 4 rhyming riddle clues where each leads to the next location.", "Index cards, pen, hidden small reward", "30 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child stick with a tricky puzzle when stuck?",
        "What strategy did your child use to organize their clues?",
        "Did your child ask clarifying questions to understand better?"
      ),
      challengeChecklist = listOf(
        "Complete a jigsaw puzzle or tangram without adult hands taking over",
        "Solve a simple 20-questions guessing game",
        "Sort books on a shelf by an inventive criteria (height, spine color)",
        "Explain the logic of how a riddle was solved"
      )
    ),

    FutureSkillsPathway(
      id = "curious_reader",
      title = "Curious Reader",
      subtitle = "Literacy joy, comprehension depth, character empathy & world curiosity",
      iconEmoji = "📚",
      accentColorHex = "#6B4E9E",
      description = "Transforms reading from a chore into a lifelong sanctuary of imagination, cultural exposure, and deep empathy for varied lives.",
      relatedEverydaySkills = listOf("Phonemic awareness", "Reading comprehension", "Character perspective", "Visual vocabulary"),
      ageGuidance = "Ages 1–10: Starts with picture book cuddling and sound repetition to independent chapter discussions.",
      projects = listOf(
        PathwayProject("read_p1", "Cozy Reading Fort Sanctuary", "Ages 2–6", "Build a blanket tent with flashlights specifically designated for story hour.", "Blankets, pillows, chairs, cozy books", "30 mins"),
        PathwayProject("read_p2", "Alternative Book Ending Workshop", "Ages 6–9", "Rewrite or redraw the last page of a familiar favorite book.", "Drawing paper, colored pencils, favorite book", "30 mins"),
        PathwayProject("read_p3", "Character Passport & Travel Log", "Ages 7–10", "Create an imaginary passport for a book character with their favorite sayings.", "Small blank booklet, markers, stickers", "35 mins")
      ),
      reflectionQuestions = listOf(
        "Which book character did your child connect with most this week?",
        "Did your child ask what an unfamiliar word meant in the story?",
        "What picture or illustration prompted the most conversation?"
      ),
      challengeChecklist = listOf(
        "Read together for 20 uninterrupted minutes in a special spot",
        "Predict what will happen before turning the page",
        "Visit a local library or swap books with a neighbor",
        "Act out the voice of a silly animal or character"
      )
    ),

    FutureSkillsPathway(
      id = "creative_builder",
      title = "Creative Builder",
      subtitle = "Structural physics, 3D design, balance & mechanical curiosity",
      iconEmoji = "🏗️",
      accentColorHex = "#D4A017",
      description = "Fosters hands-on understanding of gravity, weight distribution, levers, bridges, and architectural balance.",
      relatedEverydaySkills = listOf("Spatial visualization", "Physical structural stability", "Hand-eye coordination", "Precision assembly"),
      ageGuidance = "Ages 2–10: From mega-block towers and magnetic tiles to truss bridges and cardboard geodesic domes.",
      projects = listOf(
        PathwayProject("bld_p1", "The Tallest Unstable Tower Experiment", "Ages 3–6", "Build as high as possible, count blocks, observe why it leans.", "Wooden or foam blocks, measuring tape", "25 mins"),
        PathwayProject("bld_p2", "Spaghetti & Marshmallow Truss Bridge", "Ages 6–9", "Construct triangles and test if toy cars can roll across.", "Raw spaghetti noodles, mini marshmallows, cardstock roadway", "40 mins"),
        PathwayProject("bld_p3", "Cardboard Marble Rollercoaster", "Ages 7–10", "Fold cardboard strips into tracks with loops, banking, and drops.", "Cardboard, masking tape, marbles, scissors", "45 mins")
      ),
      reflectionQuestions = listOf(
        "Did your child analyze why the structure collapsed before rebuilding?",
        "What geometric shapes (triangles, squares) did your child choose?",
        "How long did your child remain engrossed in assembling pieces?"
      ),
      challengeChecklist = listOf(
        "Build a bridge that spans across two chairs without sagging",
        "Create a structure taller than the child's knee",
        "Use three different materials in one architectural design",
        "Reinforce a weak corner using diagonal bracing"
      )
    )
  )
}
