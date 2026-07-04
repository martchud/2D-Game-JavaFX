# Thief Escape 🏃💰🚓

A 2D top down maze chase game built in **Java** and **JavaFX**. You play as a thief who must collect every bag of stolen cash scattered across a city grid and reach the exit before the pursuing police catch you.

The game was developed covering the full software lifecycle: requirements gathering, use case analysis, UI mockups, and UML design (Phase 1); a working implementation (Phase 2); and a dedicated testing and quality assurance phase with unit tests, integration tests, and coverage analysis (Phase 3).

![Java](https://img.shields.io/badge/Java-18-orange) ![JavaFX](https://img.shields.io/badge/GUI-JavaFX-blue) ![Maven](https://img.shields.io/badge/Build-Maven-red) ![JUnit](https://img.shields.io/badge/Tests-JUnit%204-green)

---

## 🎮 Gameplay

You control the thief with the **arrow keys** on a 20 × 15 tile city map (800 × 600 px window, 40 px tiles). The city is enclosed by walls and filled with buildings and trees that block movement for both you and the police.

**Objective:** collect **all** the money bills on the map, then escape through the exit door on the right edge while a live score counter and match timer run in the HUD.

### Entities

| Sprite | Entity | Effect |
|:---:|---|---|
| <img src="project/phase2/src/main/resources/Objects/Thief.jpg" width="32"/> | **Thief (Player)** | Controlled with arrow keys. Moves 5 px per game tick, 5× faster than the police, so escape is always possible with good routing. |
| <img src="project/phase2/src/main/resources/Objects/police.png" width="32"/> | **Police (Enemy) × 3** | Actively hunt the player every tick using a greedy pursuit AI. Touching one is an instant **game over**. |
| <img src="project/phase2/src/main/resources/Objects/Money.png" width="32"/> | **Money Bill (Reward) × 3** | **+10 points** each. Collecting all three is required to win. |
| <img src="project/phase2/src/main/resources/Objects/Diamond_.png" width="32"/> | **Diamond (Bonus) × 4** | **+20 points** each. Optional, high-value pickups that only exist during randomized time windows, they vanish partway through the match and briefly reappear, rewarding players who grab them early. |
| <img src="project/phase2/src/main/resources/Objects/bomb.png" width="32"/> | **Bomb (Punishment) × 5** | **−20 points** on contact. If your score drops below zero, you lose. Bombs sit on tempting paths, forcing route trade offs. |
| <img src="project/phase2/src/main/resources/Objects/door.png" width="32"/> | **Exit Door** | The win tile. Standing on it with every money bill collected triggers the victory screen. |
| <img src="project/phase2/src/main/resources/Objects/tree.png" width="32"/> | **Walls / Buildings / Trees** | Static barriers (5 visual variants) that block both the player and the police, shaping chase routes and choke points. |

### Win / Lose Conditions

- 🏆 **Win** — collect all 3 money bills and reach the exit door → victory screen.
- ❌ **Lose** — get caught by a police officer, **or** let your score go negative from bomb hits → game-over screen.

### Controls

| Key | Action |
|---|---|
| ↑ / ↓ / ← / → | Move up / down / left / right (hold to keep moving) |

---

## 🛠️ Technical Highlights

Everything below is done using plain JavaFX, the project deliberately avoids game engines to demonstrate the underlying mechanics.

**Fixed timestep game loop.** The core loop is built on JavaFX's `AnimationTimer` with a nanosecond delta time accumulator, decoupling game logic from the display's refresh rate and locking updates to a consistent **24 ticks per second**. Each tick runs `update()` (input, AI, collisions, win check) followed by `render()` (full frame redraw).

**Tile map driven level construction.** The level is defined as a single 15 × 20 `int` matrix in which each code (0–11) maps to a game object. On startup, the map is scanned once and every wall, building, tree, reward, bomb, enemy, bonus, exit, and the player spawn are instantiated at their grid coordinates. New levels can be created by editing one 2D array, no rendering or logic changes required.

**Object oriented entity hierarchy.** All game objects extend a common `GameComponent` base class that owns position state, a `Rectangle2D` bounding box, and an `intersects()` test:

```
GameComponent
├── Player                        (input handling, movement, collision resolution, scoring)
├── Enemy                         (pursuit AI)
├── Reward / Bonus / Punishment   (collectibles & hazards)
├── Exit                          (win condition check)
└── Barrier
    ├── Wall
    ├── Building1 / Building2 / Building3
    └── Tree
```

This keeps collision logic generic while each subclass supplies only its own sprite and behavior.

**Predictive AABB collision detection.** Collision is axis aligned, bounding box based via JavaFX's `Rectangle2D.intersects()`, with a twist: the player's and enemies' bounding boxes are projected **one tick ahead** (`position + velocity`). A move into a barrier is detected and cancelled *before* it happens by zeroing velocity, which prevents entities from ever clipping into walls, no need for position rollback.

**Enemy pursuit AI.** Each police officer independently evaluates the player's coordinates every tick and steps toward them on both axes (greedy chase), while still respecting barrier collisions. Because the player moves 5 px/tick vs the enemy's 1 px/tick, the threat comes from being cornered by multiple officers, not raw speed.

**Timed bonus mechanic, designed for testability.** Diamond visibility is driven by two randomized thresholds (`ThreadLocalRandom`) checked against the elapsed match clock. While hidden, diamonds are neither rendered nor collectible. Crucially, the visibility state is exposed through a single `checkBonus` flag rather than being buried inside the animation loop, so the appear/disappear behavior can be unit tested deterministically by driving the flag directly, no `AnimationTimer` needed in tests.

**Event driven input.** Keyboard handling uses JavaFX `Scene` key pressed/key released events to set and clear velocity, giving smooth hold to move motion rather than one press one step input.

**Canvas rendering + HUD.** Each frame is drawn immediate mode onto a JavaFX `Canvas` via `GraphicsContext` — background, collectibles, enemies, barriers, exit, player, with a live HUD overlay showing the current **score** (top right) and a **M:SS match timer** (top left). Win and loss each swap to a dedicated end screen stage.

---

## 📁 Project Structure

```
2D-Game-JavaFX/
└── project/
    ├── Design/
    │   └── CMPT276_Group23_PHASE1.pdf           # Phase 1: requirements, use cases, UI mockups, UML
    └── phase2/                                  # Implementation (Maven project)
        ├── pom.xml
        └── src/
            ├── main/
            │   ├── java/
            │   │   ├── game/
            │   │   │   └── Main.java            # Entry point, game loop, map builder, renderer
            │   │   └── classes/
            │   │       ├── GameComponent.java   # Base entity: position + AABB collision
            │   │       ├── Player.java          # Input, movement, collision resolution, scoring
            │   │       ├── Enemy.java           # Police pursuit AI
            │   │       ├── Reward.java          # Money bill (+10)
            │   │       ├── Bonus.java           # Diamond (+20, timed)
            │   │       ├── Punishment.java      # Bomb (−20)
            │   │       ├── Exit.java            # Win condition check
            │   │       ├── ScoreUI.java         # Score model
            │   │       └── barrier/             # Barrier + Wall, Building1–3, Tree
            │   └── resources/
            │       ├── Objects/                 # Sprites (thief, police, money, diamond, bomb, …)
            │       └── game/                    # Background, win & lose screens
            └── test/java/                       # JUnit test suite (unit + integration)
```

---

## 🚀 Building & Running

### Requirements

- **JDK:** Java SE 18 (project language level 16)
- **Maven:** 3.8.4 or newer
- JavaFX libraries are resolved through Maven (OpenJFX)

### Download

```bash
git clone https://github.com/martchud/2D-Game-JavaFX.git
cd 2D-Game-JavaFX/project/phase2
```

### Run the game

```bash
mvn exec:java
```

Or open the project in IntelliJ IDEA (preconfigured), set the SDK to JDK 18, and run `game.Main`.

### Run the tests

```bash
mvn test
```

### Generate Javadocs

```bash
mvn javadoc:javadoc
```

The generated docs land in `target/site/apidocs` open `index.html` in a browser.

---

## 🧪 Testing & Quality Assurance

Phase 3 added a JUnit suite spanning unit tests for every game component, integration tests for the interactions that drive game state, and line/branch coverage measurement with a >80% coverage target for packages and classes.

### Unit tests

- **All entity classes** (Player, Enemy, every barrier type, Reward, Bonus, Punishment, Exit, GameComponent): verify each object constructs correctly, lands in its intended maze cell with the expected x/y coordinates, and reports the correct bounding box.
- **Movement:** Player and Enemy are verified moving in all four directions with their assigned speeds.
- **Scoring:** Player cash tracking, plus `ScoreUI` initialization, increment, and decrement.
- **Main:** the enemy/bomb/reward/barrier/bonus collections are populated and every object's placement is validated.

### Integration tests

Five interactions were identified as the game's critical component boundaries and tested across collision and non-collision cases:

| Interaction | Cases covered |
|---|---|
| Player × Enemy | Collision → game stops, lose state set (hitboxes allow approach to within 35 px, the player's bounding-box size); no collision → game continues |
| Player × Reward | Collision → bill removed, cash +10; no collision → cash unchanged, reward stays in place |
| Player × Bonus | Collision → cash +20; no collision → unchanged |
| Player × Bomb | Score driven negative (0 → −20) → lose state; score reduced but non-negative (20 → 0) → game continues; no collision |
| Win condition | All rewards collected + standing on the exit → win state; all rewards collected but away from the door → game keeps running |

### Notable findings

- **Testing caught a real bug:** `statusUpdateCheck()` in `Player` set the game state back to *running* instead of *lose* when the score went negative. The backup check silently disagreed with the main collision logic, unit tests exposed it and it was fixed.
- **Deterministic testing of randomized behavior:** rather than spinning up an `AnimationTimer` to wait for diamonds to disappear, tests drive the `checkBonus` visibility flag directly and assert that hidden bonuses can't be collected, imitating the runtime condition without timing flakiness.

---

## 📐 Design Documentation

The Phase 1 design document ([`project/Design/CMPT276_Group23_PHASE1.pdf`](project/Design/CMPT276_Group23_PHASE1.pdf)) was produced before any code was written and includes:

- **Game specification** — theme, characters, enemies, rewards/punishments, barriers, and board rules
- **Five detailed use cases** (claiming rewards, claiming bonuses, enemy attack, punishment, barrier collision) with actors, preconditions, triggers, scenarios, exceptions, priorities, and usage frequency
- **UI mockups** for the main gameplay states and end screens
- **UML class diagram** of the planned architecture

The implementation realizes this design, with gameplay values tuned during development (for ex score scaled to ±10/±20), and Phase 3 closed the loop with the test suite and QA analysis summarized above.

---

## 🔮 Possible Improvements

- Cache sprite `Image` objects at load time instead of constructing them per render call, to cut per frame allocation.
- Upgrade enemy AI from greedy chase to **BFS/A\* pathfinding** so police can route around barriers instead of stalling against them.
- Add a restart button on the win/lose screens, pause functionality, and sound effects.
- Grid snapped movement for tighter, Pac Man style controls.
- Multiple maps / difficulty levels — the tile map system already makes new levels a one array change.

---

## 👥 Credits

Built by **Group 23** — Martin, Khanh, Lingxuan, and Lin — for CMPT 276 (Introduction to Software Engineering). Design, implementation, and testing work was divided equally across the team.
