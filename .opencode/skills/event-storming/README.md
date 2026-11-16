# Skill Dossier: `event-storming-facilitator`

*Design documentation and rationale, for the skill-development project knowledge base.*

---

## Summary

`event-storming-facilitator` is a portable AI agent skill that conducts an Event Storming session — a collaborative domain-discovery workshop — through conversation. It elicits a business domain as a timeline of domain events, captures the language the domain experts actually use, surfaces where the knowledge is contested or unknown, and puts all results (intermediate and final) as sticky notes on a **Miro board** via the Miro MCP. It also produces a structured domain-model document that can feed downstream discovery, architecture, and requirements work.

**Prerequisite:** the Miro MCP server must be connected, and the user must name the target board (link or name) at the start of the session. If the board can't be found or accessed, the skill says so and lets the user correct it.

It is written to the open **Agent Skills / SKILL.md** standard (originally developed by Anthropic, since adopted across Claude Code, Codex, Gemini CLI, Cursor and others), so it is deliberately not tied to any single tool or vendor.

---

## Purpose and origin

The skill was created as a companion to an in-house **CSAF Facilitator** skill (Capability Statement Assessment Form), which elicits a capability's Business Jobs To Be Done and begins establishing its Ubiquitous Language. Event Storming is a natural sibling discovery technique: where CSAF interrogates *what a capability is for*, Event Storming maps *how the domain behaves over time*. Together they form a discovery suite assembled from our own portable parts rather than adopted wholesale from a framework such as BMAD.

The intended pairing is **CSAF first, Event Storming second**: CSAF frames the problem space (vision, capability, jobs, service-design wrap) and produces the initial canonical ubiquitous-language glossary; Event Storming then descends into the behaviour/design space (the event timeline and, at deeper levels, the DDD building blocks — commands, actors, policies, aggregates, bounded contexts), ingesting CSAF's glossary as a baseline and handing it back enriched. The two are complementary lenses (problem vs behaviour), not two zoom levels of the same model.

The motivating principle is **avoiding lock-in**: rather than buying into one spec-driven platform, we author our own role/persona/specialism skills in an open, portable format and compose them. This skill is a worked example of that approach.

---

## What Event Storming is (brief grounding)

Event Storming is a workshop technique created by Alberto Brandolini. Participants cover a wall with coloured sticky notes representing domain events (orange), commands (blue), actors (yellow), aggregates (pale yellow), policies (purple), external systems (pink), read models (green), and hotspots (red). It runs at three increasing levels of resolution:

- **Big Picture** — domain events on a timeline; broad, fast, exploratory.
- **Process Modelling** — adds commands, actors, policies, read models, external systems.
- **Software Design** — adds aggregates and tightens bounded contexts toward implementation.

The most valuable outputs are usually not the tidy parts of the model but the **hotspots** (disagreements and unknowns) and the **ubiquitous language** captured along the way.

---

## File structure

The skill is a four-file directory using the standard's progressive-disclosure pattern — lightweight in context by default, with detail loaded only when needed.

| File | Role | When it loads |
|---|---|---|
| `SKILL.md` | Frontmatter (name + triggering description) and the facilitation flow: persona, session framing, level selection, the phase-by-phase workshop, guardrails, and output handling. | Frontmatter is always in context (~100 tokens); the body loads when the skill triggers. |
| `references/notation-and-grammar.md` | The sticky-note vocabulary and colour conventions, the command→aggregate→event grammar, per-element question banks, input-reshaping examples, anti-patterns, group-vs-solo notes, and the shared glossary schema (columns, `Status` legend, and the CSAF round-trip rules). | On demand, while actually facilitating. |
| `references/miro-board.md` | How the Miro board acts as the wall: resolving/verifying the board (link or name), Miro MCP tool usage, element → sticky colour mapping, layout, write cadence, handling of user edits on the shared board, final output on the board, and failure handling. | At framing, before the first write to the board. |
| `assets/event-storm-output-template.md` | The structured domain-model document the facilitator fills in: timeline, commands/actors, read models, policies, external systems, aggregates, candidate bounded contexts, ubiquitous-language glossary, hotspots, next steps. | Used when producing the final output. |

---

## Design rationale (key decisions and the reasoning behind them)

### 1. Conversational facilitation on a real (Miro) wall
Event Storming is fundamentally a physical, parallel, many-people-at-a-wall activity. An agent in a chat or terminal cannot reproduce a shared whiteboard by itself — an earlier version of this skill simulated the wall as running text. The skill now uses a real, shared wall instead: a **Miro board** the user names (link or name), written to via the Miro MCP. The conversation remains the facilitation channel; the board is where results live:
- **The agent holds the model and mirrors it on the board** as colour-coded stickies (mapping and layout in `references/miro-board.md`). Chat carries short deltas and one question per turn, not reprints of the wall.
- **The agent drives the rhythm**, asking for one thing at a time and batching where natural — and writing to the board in batches.
- **The board is shared**, so people can edit it directly; the agent re-reads it at phase boundaries and treats user edits as the source of truth.
- **No silent fallbacks.** If the board can't be found or accessed, or a write fails, the agent says so, so the user can change the board or fix access.
- The markdown output document (especially the full `UBIQUITOUS_LANGUAGE.md`) is still produced, because it is the handoff currency to CSAF and downstream work, and a sticky can't carry a full glossary definition.

The transferable lesson: when porting a human, collaborative, or visual method into a skill, translate its *mechanics* honestly — and where a real shared artifact exists and is reachable through a tool, use it instead of simulating it.

### 2. Three levels with a sensible default
The skill supports all three Brandolini levels but defaults to Big Picture and only descends to Software Design on a well-understood slice. This keeps sessions from sprawling and prevents premature solutioning — the depth matches the goal rather than always running to the deepest setting.

### 3. Capture glossary and hotspots *continuously*
The ubiquitous-language glossary and the hotspot list are maintained throughout every phase, not assembled at the end. The reasoning: ambiguous domain language and unresolved uncertainty are the dominant sources of downstream "plausible but wrong" output from AI agents. Front-loading and continuously capturing these is the discovery-time equivalent of what the "living spec" tools try to achieve continuously through implementation — and it fits a "don't buy a system" stance because it is a one-time discovery investment rather than ongoing machinery.

### 4. The expert holds the knowledge; the agent never invents it
A hard rule in the skill: the agent extracts and organises, but never silently bakes in a guessed domain fact. Where it must propose something to keep momentum, it labels it as an assumption to confirm. This protects the integrity of the output and keeps the human firmly in the role of domain authority.

### 5. Composability over monolith — and a shared, round-tripping glossary
The output document is structured to hand off, and the glossary handoff is now explicitly **CSAF → Event Storming → CSAF**. A prior CSAF Facilitator session produces the initial canonical `UBIQUITOUS_LANGUAGE.md`; this skill ingests it as a baseline, extends and pressure-tests it during the storm, and hands the *same file* back enriched — inherited terms preserved, `New`/`Refined` terms added, and `Contested` ones flagged for the CSAF owner to adjudicate. The two skills therefore share **one glossary file and one schema** rather than each maintaining its own and converting lossily at the seam.

The schema is CSAF's four columns (Term / Canonical Definition / Rejected Synonyms / Notes) plus an additive `Status` column (`Inherited` / `Refined` / `New` / `Contested`) that CSAF safely ignores. Two rules reconcile the skills' opposing instincts — CSAF *resolves* language to a canonical term, Event Storming refuses to *merge* concepts prematurely: (a) only write into "Rejected Synonyms" on the participant's explicit confirmation; (b) flag, never overrule, an inherited term — if behaviour contradicts it, mark it `Contested` and raise a hotspot rather than silently rewriting a decision the earlier room made. Bounded contexts and policies feed architecture and PRD work. The skill does one job well and connects to others, rather than absorbing adjacent responsibilities.

### 6. Open standard for portability
Authored to the core SKILL.md format (YAML frontmatter with `name` + `description`, markdown body, optional `references/` and `assets/`) and avoiding tool-specific features, so it runs across compatible agents without modification. The triggering `description` is written to be slightly "pushy" and to fire on intent ("help me map how orders flow…") as well as on the explicit term, because skills tend to under-trigger otherwise.

---

## Known limitations and tradeoffs

- **Depends on the Miro MCP.** Without a connected Miro MCP and an accessible board, the skill can't do what it is built for; it asks the user how to proceed rather than silently falling back to text. Board layout is generated, so expect to tidy it by hand now and then.
- **No live group energy.** A text agent cannot reproduce the real-time argument and parallel ideation of many people at a wall. The skill leans into what it *can* do well — disciplined facilitation, grammar enforcement, and refusing to smooth over hotspots — and treats the agent as facilitator, not as a replacement for the domain experts.
- **Single-viewpoint risk in solo mode.** When interviewing one expert, blind spots are likely; the skill flags this and pushes uncertain points into hotspots.
- **Glossary handoff is one-directional by design, but only half-tested.** The skill now *ingests* a CSAF glossary and hands an enriched one back, but the round-trip (especially that CSAF can still read a file carrying an unfamiliar `Status` column, and that `Contested` flags are actionable for the CSAF owner) has not yet been exercised end to end. See next steps.
- **Partial portability.** The plain-markdown core travels across agents well; anything later added that relies on a specific agent's advanced features (e.g. context forking) may need light per-tool adjustment.
- **Not yet validated.** The skill is a first draft and has not been run through structured test cases (see next steps).

---

## Installation and use

Place the `event-storming-facilitator/` directory in the agent's skills location — e.g. `.claude/skills/` (project) or `~/.claude/skills/` (personal), with equivalent paths for Codex, Gemini CLI, etc. Trigger it by asking the agent to run an Event Storming session or to help map/discover a domain. The agent first asks for the Miro board (link or name) and verifies access, then frames scope and goal, checks for an incoming CSAF glossary, picks a level, facilitates the phases while putting the results on the board as stickies, and offers to save the completed output document.

---

## Validation status and suggested next steps

**Status:** draft, authored but not yet eval-tested. The CSAF→ES glossary handoff has been designed and wired into the skill files but not yet run.

Recommended next steps, roughly in order:
1. **Run realistic test sessions** — at minimum one familiar domain (e.g. the Vehicle Spotter sighting-logging flow) and one from a live business domain — and review where the facilitation feels thin, too rigid, or lets bad input through.
2. **Iterate the persona and guardrails** based on those runs; tune the triggering description if it over- or under-fires.
3. **Confirm the CSAF handoff (both legs).** Verify that a CSAF `UBIQUITOUS_LANGUAGE.md` ingests cleanly as a baseline at framing, and that the enriched file handed back is still readable by anyone working from CSAF's four-column format (the `Status` column should be safely ignorable). Confirm `Contested` flags carry enough context to be actionable for the CSAF owner. If the round-trip exposes a genuine gap in CSAF's format, capture it as evidence-based feedback for the CSAF author rather than changing CSAF on a hunch.
4. **Decide on a sharing model** — version these skills in a private repo so CSAF, Event Storming, and future role-skills evolve together like any other code.

---

## Transferable patterns for future skills in this project

This skill illustrates several patterns worth reusing when authoring others:

- **Translate mechanics, not just form**, when porting a human/visual/collaborative method into a conversational agent.
- **Maintain state explicitly** when the original method relied on a shared external artifact (a wall, a board, a document) — and write it to the real artifact when a tool allows it, treating that artifact (and the people editing it) as the source of truth.
- **Capture the drift-prone artifacts continuously** (language, assumptions, open questions) rather than only at the end.
- **Keep the human as the authority**; have the agent label its own contributions as proposals.
- **Design for handoff** so skills compose into a suite instead of overlapping — and where two skills share an artifact (here, the glossary), give them **one schema and one file** with an additive marker column, rather than two formats and a lossy conversion at the seam.
- **Write to the open standard and to intent**, with progressive disclosure (lean `SKILL.md`, detail in `references/`, output scaffolding in `assets/`).
