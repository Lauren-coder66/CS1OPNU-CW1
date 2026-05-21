# CS1OPNU — Project Reflection

**Module Code:** CS1OPNU  
**Assignment report Title:** Project Reflection  
**Student Number :** *[202483710049]*  
**Actual hrs spent for the assignment:** 12  
**Which Artificial Intelligence tools used :** Cursor AI (code assistance and debugging support)

---

## 1. Introduction

For Coursework 1 (CW1), I developed a **multi-player text-based adventure game** in Java as part of the Object-Oriented Programming module (CS1OPNU). The project requires players to explore interconnected rooms, collect items, solve puzzles, interact with non-player characters (NPCs), and cooperate or compete to reach a clear win condition—obtaining the *golden artifact* after unlocking the vault with a *rusty key*.

The implementation follows modular object-oriented design. Core packages include `model` (entities such as `Player`, `Room`, `Item`), `logic` (`GameEngine` for game rules), `cli` (`GameCLI` for command-line interaction), and dedicated packages for three required design patterns: `singleton`, `observer`, and `factory`. The game supports local multi-player play through commands such as `join`, `switch`, `go`, `take`, `give`, and `use`. Unit tests written with JUnit 5 verify movement, item handling, puzzle solving, observer notifications, and the win condition.

This report reflects on two central themes required by Coursework 2 (CW2): the role of **AI tools** in my development process, and the **effectiveness and challenges** of applying the Singleton, Observer, and Factory patterns in the project. It also discusses ethical and legal considerations relevant to AI-assisted software development and to a game that handles player state but no personal data.2. Analysis of AI Support in Software Development (25%)

### 2.1 How AI Tools Were Used

I used **Cursor AI** throughout CW1, in line with the module policy that AI may support (but not replace) student work. Typical uses included:

- **Project scaffolding:** Generating the initial Maven structure, package layout, and boilerplate classes (`pom.xml`, `Main.java`, model classes).
- **Pattern implementation:** Drafting `GameState` (Singleton), `GameEventPublisher` and `PlayerObserver` (Observer), and `GameObjectFactory` (Factory), then integrating them into `GameEngine`.
- **Debugging:** When unit tests failed—for example, the vault puzzle logic or a missing `events.subscribe()` call in `addPlayer`—AI helped trace symptoms and suggest fixes.
- **Documentation:** Assisting with `README.md` command lists and this reflection report structure.
- **Environment issues:** Guidance on Git errors (wrong repository root, missing commits) and Java version compatibility (targeting Java 8).

I reviewed, tested, and modified all AI-generated code. For instance, I corrected the initial Git repository location (`src/main` instead of project root) manually after understanding the error messages.

### 2.2 Benefits of Using AI Tools

AI tools significantly **reduced setup time**. Creating six linked rooms, item templates, NPC dialogue, and a command parser by hand would have taken longer; AI provided a working baseline that I could refine.

They also improved **learning efficiency**. When implementing the Observer pattern, AI-generated examples clarified how `subscribe`, `notify`, and `drainMessages` fit together. I could then focus on domain-specific behaviour, such as notifying other players when someone picks up an item.

Another benefit was **faster iteration on tests**. JUnit tests for Singleton sharing, Factory templates, and vault unlocking helped confirm correctness quickly. AI suggested test cases I might have overlooked, such as verifying that inactive players still receive observer messages.

### 2.3 Challenges and Limitations

Despite the benefits, several **limitations** appeared:

1. **Subtle logic bugs:** AI once omitted `events.subscribe(observer)` in `addPlayer`, so notifications never reached other players. Tests exposed this; the fix was simple but easy to miss without running tests.
2. **Over-generated structure:** Early output included Python files alongside Java; I had to delete redundant code and align everything with the Java requirement.
3. **Environment assumptions:** Suggestions sometimes assumed Java 17 or PowerShell `&&` syntax, which did not match my JDK 8 and Windows setup.
4. **Understanding risk:** Copying AI code without reading it would weaken learning. I deliberately ran `mvn test` and played the CLI game to validate behaviour.
5. **Academic integrity:** I must ensure the submitted work reflects my understanding. AI supported the process; design decisions, testing, and this reflection are my own responsibility.

### 2.4 Overall Impact on Your Learning and Development

AI acted as a **pair-programming assistant** rather than a substitute for thinking. It accelerated repetitive tasks and highlighted patterns, but I still needed to understand *why* Singleton suits global game state, *why* Observer decouples event broadcast from players, and *why* Factory centralises room creation.

The experience reinforced that professional developers should treat AI as a tool: verify output, write tests, and maintain accountability. I became more disciplined about version control, directory structure, and running automated tests before submission—habits that will transfer directly to future modules and industry work.

---

---

## 3. Analysis of Software Patterns in the Project 

### 3.1 How the Patterns Were Used

#### Singleton — `GameState`

`GameState` uses a private constructor and `getInstance()` to provide a single shared instance holding rooms, players, active player, and game-over status. `WorldBuilder` populates rooms into this instance; `GameEngine` reads and updates player locations and inventory through it. A `reset()` method exists primarily for unit tests. This matches the requirement for consistent global game state across the session.

#### Observer — `GameEventPublisher` / `PlayerObserver`

Each player registers a `PlayerObserver` when joining. `GameEngine` calls `events.notify(message)` after actions such as movement, picking up items, or giving items to another player. The active player’s pending messages are appended to command output under “Updates”; other players accumulate messages until they act. The `exclude` parameter prevents a player from receiving their own “has joined” notification.

#### Factory — `GameObjectFactory`

Static methods `createItem`, `createNpc`, and `createRoom` build objects from template identifiers (e.g. `"entrance"`, `"rusty_key"`, `"guide"`). `WorldBuilder` loops through room templates and registers them in `GameState`. Adding a new room means extending the factory rather than scattering `new Room(...)` across the codebase.

### 3.2 Benefits of Using Software Patterns


| Pattern   | Benefit in this project                                                                                        |
| --------- | -------------------------------------------------------------------------------------------------------------- |
| Singleton | One authoritative source for world and player data; avoids passing large context objects through every method. |
| Observer  | Loose coupling between game logic and UI feedback; easy to add more listeners (e.g. logging) later.            |
| Factory   | Encapsulates world-building knowledge; consistent item/NPC definitions; simpler tests via known templates.     |


Patterns also made the project **easier to explain and assess**, which aligns with CW1’s explicit pattern requirements. Markers can locate each pattern in dedicated packages.

### 3.3 Challenges and Limitations

1. **Singleton and testing:** Global state can make tests order-dependent. `GameState.reset()` mitigates this but must be called in `@BeforeEach` to avoid cross-test pollution.
2. **Observer granularity:** All observers receive string messages. A richer design might use typed events (`ItemTakenEvent`), but that was beyond scope for a text CLI game.
3. **Factory growth:** `createRoom` uses a chain of `if` statements. For six rooms this is manageable; a larger game would benefit from registration maps or configuration files.
4. **Pattern overhead:** For a small student project, some patterns add indirection. Without discipline, they could feel like “pattern for pattern’s sake”; here they map clearly to stated requirements.
5. **Integration bugs:** As noted, forgetting `subscribe` broke the Observer chain—patterns only help when wired correctly end-to-end.

### 3.4 Overall Impact on Your Project

Patterns **structured** the codebase and separated concerns: models stay simple data holders; `GameEngine` owns rules; patterns sit in named packages. Maintenance—such as adding a new exit or item template—became localised.

Trade-offs include slightly more files and learning curve for readers unfamiliar with the patterns. Overall, for a multi-player adventure with shared state and broadcast updates, Singleton, Observer, and Factory were **appropriate and effective**, not merely checkbox exercises.

---

## 4. Ethical and Legal Considerations 

### 4.1 Reflect on ethical concerns related to your use of AI tools

**Over-reliance on AI-generated content:** If I had submitted AI output without review, I would not demonstrate my own OOP skills. I mitigated this by testing gameplay, reading generated code, and fixing bugs myself.

**Biases in AI suggestions:** AI may favour certain idioms (e.g. Java 17 features) or English-only strings. I adapted code to Java 8 and kept user-facing text consistent. Biases in puzzle design (Western fantasy tropes: keys, vaults) should be acknowledged; future versions could diversify settings and accessibility.

**Academic integrity and originality:** The module allows AI to *support* work. My obligation is honest disclosure (this report), personal understanding, and not presenting others’ or AI’s work as solely my own invention without engagement. The reflection itself is written to demonstrate critical analysis, not merely description.

### 4.2 Data Handling and Privacy

This project is a **local CLI game** with no network persistence, database, or collection of real personal data. Player names are typed at runtime and stored only in memory.

If the game were extended online, I would need to consider:

- **GDPR** and lawful basis for processing any account or chat data.
- Minimising data collection (e.g. no unnecessary logging of real names).
- Secure storage and consent for multi-player services.

For the current scope, privacy risk is low; ethical focus shifts to fair play mechanics and clear rules rather than data protection.

### 4.3 Broader Ethical and Legal Implications

**Potential misuse:** A text adventure has limited harm, but any game with competitive elements could encourage cheating if extended with scoring or online leaderboards. Design should promote cooperation as intended.

**Accessibility and inclusivity:** The CLI assumes sighted users comfortable with English commands. Improvements could include command aliases, screen-reader-friendly output, and optional language packs.

**Licensing and intellectual property:** The project uses standard JDK and Maven libraries under their licences. AI-generated code should be treated as assistance; I remain responsible for compliance with university policy. Third-party assets (art, music) were not used; all descriptions are original text.

---

## 5. Conclusion

This CW2 reflection analysed a Java multi-player text adventure developed for CW1, focusing on **Cursor AI** as a development aid and on **Singleton, Observer, and Factory** as structural patterns.

**Key findings:** AI improved speed and suggested useful tests and structure, but required careful verification and caused occasional integration mistakes. Design patterns organised global state, event notification, and world construction effectively, with manageable trade-offs in complexity and test setup.

**Learning:** I strengthened skills in OOP packaging, automated testing, Git workflow, and critical use of AI. I understand that patterns solve specific problems—not every project needs every pattern.

**Recommendations for similar projects:**

1. Write tests early, especially for Observer wiring and puzzle logic.
2. Keep AI output in small, reviewable chunks; run `mvn test` after each integration step.
3. Document AI use transparently, as in this report.
4. Consider typed events or configuration-driven factories if the world scales beyond a handful of rooms.
5. Plan accessibility and ethical implications before adding networked or data-collecting features.

These practices will support more maintainable, honest, and user-respecting software in future coursework and professional development.

---

## References

Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.

Oracle Corporation (n.d.) *The Java Tutorials*. Available at: [https://docs.oracle.com/javase/tutorial/](https://docs.oracle.com/javase/tutorial/) (Accessed: 21 May 2026).

University of Nottingham (n.d.) *Academic Integrity and Artificial Intelligence*. Available at: [https://www.nottingham.ac.uk/studyingeffectively/referencing/integrity.aspx](https://www.nottingham.ac.uk/studyingeffectively/referencing/integrity.aspx) (Accessed: 21 May 2026).

JUnit Team (n.d.) *JUnit 5 User Guide*. Available at: [https://junit.org/junit5/docs/current/user-guide/](https://junit.org/junit5/docs/current/user-guide/) (Accessed: 21 May  2026).

Maven Project (n.d.) *Maven – Welcome to Apache Maven*. Available at: [https://maven.apache.org/](https://maven.apache.org/) (Accessed: 21 May 2026).

---

