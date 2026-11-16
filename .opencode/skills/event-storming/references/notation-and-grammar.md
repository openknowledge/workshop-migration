# Event Storming — Notation, Grammar, and Question Banks

Read this before facilitating. It defines the sticky-note vocabulary, the grammar that connects the pieces, the questions that surface each element, and the failure modes to watch for. The colour conventions are the standard Brandolini palette. On the Miro board each element becomes a sticky in the closest available named sticky colour — see `references/miro-board.md` §3 for the exact mapping, text rules, and layout.

## Contents
1. The elements (sticky-note types)
2. The core grammar
3. Question banks by element
4. Reshaping bad input
5. Anti-patterns and failure modes
6. Group vs solo facilitation notes
7. The shared glossary (ubiquitous language)

---

## 1. The elements

| Element | Colour (convention) | What it is | Grammar form |
|---|---|---|---|
| **Domain Event** | Orange | Something meaningful that happened in the domain | Past tense: "Order Placed" |
| **Command** | Blue | An intent/decision that aims to cause an event | Imperative: "Place Order" |
| **Actor** | Small yellow | The person/role issuing a command | Noun (a role): "Dispatcher" |
| **Aggregate** | Large pale yellow | The entity that receives a command and emits the event; enforces consistency | Noun: "Order" |
| **Policy / Reaction** | Lilac/purple | Reactive rule: when an event occurs, automatically trigger a command | "Whenever X, then Y" |
| **External System** | Pink | A system outside this domain that is called or notified | Noun: "Payment Gateway" |
| **Read Model** | Green | The information an actor reads to decide on a command | Noun phrase: "Available Stock view" |
| **Hotspot** | Red | A problem, disagreement, risk, or unknown | A question or flagged tension |
| **Pivotal Event** | Orange + vertical marker | A domain event that marks a phase change in the process; sits on context seams | A normal event, emphasised |

## 2. The core grammar

The pieces connect in a repeatable sentence. Use it to test whether the model hangs together:

> An **actor** issues a **command**, optionally informed by a **read model**; the command is handled by an **aggregate**, which produces a **domain event**; a **policy** may react to that event by triggering the next **command**; **external systems** may issue commands or receive the effects of events.

Two reusable templates:
- Command → Aggregate → Event: *"Place Order" → Order → "Order Placed"*
- Policy: *"Whenever **Order Placed**, then **Reserve Stock**"*

If an event has no plausible command or trigger, that's a gap — ask about it. If a command never leads to an event, it probably isn't business-meaningful — question it.

## 3. Question banks by element

Use these to elicit each element. Ask conversationally and one thread at a time; these are prompts, not a script to read verbatim.

**Domain events (Phase A)**
- "What are the things that happen in this process? Tell me the outcomes, in past tense."
- "Then what happens? And before that?"
- "What's the very first thing that kicks this off? What's the last thing that signals it's done?"
- "What happens when it goes wrong — what's the unhappy path?"

**Timeline (Phase B)**
- "Does this happen before or after that?"
- "Is there anything that has to happen between these two?"
- "Does it always go this way, or are there branches?"

**Pivotal events & hotspots (Phase C)**
- "Which of these feels like a real turning point — after which the process is in a different phase?"
- "Where does it get messy, or where do people disagree about how it works?"
- "What here is a 'well, it depends'?"

**Commands & actors (Phase D)**
- "What did someone *do* to make this event happen?"
- "Who does that — which role?"
- "Is this triggered by a person, by the clock, or by another event?"

**Read models (Phase E)**
- "Before they do that, what do they need to look at to decide?"
- "What information would be on the screen in front of them?"

**Policies & external systems (Phase F)**
- "When this event happens, does something automatically have to follow?"
- "Is that rule always true, or are there exceptions?" (exceptions are hotspots)
- "Does anything outside your control get involved here — another system, a third party?"

**Aggregates (Phase G, design level)**
- "What's the *thing* this command acts on — the noun that has to stay consistent?"
- "Who's the guardian that decides whether this command is allowed?"

**Bounded contexts & language (Phase H)**
- "Does the word for this change as we move along the timeline?"
- "Do the same people own this whole stretch, or does it hand off to a different team/area here?"

## 4. Reshaping bad input

Participants naturally speak in UI and CRUD terms. Reshape, don't reject — and show your working so they learn the grammar:

**Example 1**
Input: "The user clicks Submit on the booking form."
Reshape: "So the actor issues a **Book** command — and the outcome we care about is the event **Booking Confirmed**. Is that right, or can a submit fail and produce something like **Booking Rejected**?"

**Example 2**
Input: "We create a record in the customers table."
Reshape: "At this level let's stay out of the database. What's the business outcome — is it **Customer Registered**? Who triggered it and why?"

**Example 3**
Input: "There's a status field that can be pending, active, or closed."
Reshape: "Those status changes are probably events: **Application Submitted**, **Application Approved**, **Application Closed**. What causes each transition?"

## 5. Anti-patterns and failure modes

- **The flat, tidy timeline.** Real domains have branches, retries, and unhappy paths. If everything is a clean straight line, you haven't probed the edges — ask about failure and exceptions.
- **Premature solutioning.** Schemas, classes, and API design at exploration level kill discovery. Park them as hotspots and return to "what happens?".
- **Facilitator ventriloquism.** Filling the wall with your own plausible events. Anything the participant didn't say is a labelled assumption until they confirm it.
- **Silent synonym merging.** Treating "client", "customer", and "account holder" as the same without checking. Each might be a distinct concept — or a naming hotspot.
- **Resolving hotspots too early.** A hotspot captured is worth more than a hotspot argued into a fake consensus. Record the tension and move on.
- **Commands masquerading as events (and vice versa).** "Place Order" (intent, imperative) is a command; "Order Placed" (outcome, past tense) is the event. Keep them distinct.
- **Boil-the-ocean scope.** Without a goal the timeline sprawls forever. Re-anchor on the session's purpose and storm a slice.

## 6. Group vs solo facilitation notes

- **Solo expert:** you get depth and consistency but a single viewpoint — flag that blind spots are likely and mark anything they're unsure of as a hotspot.
- **Group (relayed through the user):** you get conflict, which is gold. When two views differ, don't pick a winner — record both as a hotspot attributed to "the room". Watch for the loudest-voice effect; explicitly ask whether quieter participants would phrase it differently.

## 7. The shared glossary (ubiquitous language)

The ubiquitous-language glossary is a **shared artifact**, not one this skill owns alone. It is written as `UBIQUITOUS_LANGUAGE.md` and is the handoff currency between this skill and the CSAF (Capability Statement Assessment Form) Facilitator. In the expected workflow — **CSAF first, Event Storming second** — CSAF produces the initial *canonical* glossary, you ingest it as a baseline and **extend** it, then hand the same file back enriched. Keep the filename and the columns identical so it round-trips.

### Schema

| Term | Canonical Definition | Rejected Synonyms / Ambiguities | Notes | Status |
|---|---|---|---|---|

The first four columns are exactly CSAF's format; `Status` is an **additive** column CSAF does not read, so adding it never breaks the upstream skill. A worked illustration:

| Term | Canonical Definition | Rejected Synonyms / Ambiguities | Notes | Status |
|---|---|---|---|---|
| Sighting | A confirmed observation of a target vehicle logged by a spotter | "spot", "hit" (informal) | from CSAF | Inherited |
| Provisional Sighting | A logged observation not yet confirmed against the watchlist | — | timeline revealed "Sighting" splits into provisional vs confirmed — CSAF treated it as one | New |
| Watchlist | *(CSAF: the set of target vehicles)* | — | spotters say "the list", ops say "the board" — may be two views, not one term | Contested → hotspot H3 |

### Status values
- **Inherited** — carried from the incoming glossary unchanged.
- **Refined** — inherited, but you sharpened or corrected the definition. Record what changed in Notes.
- **New** — first surfaced during this Event Storming session.
- **Contested** — the domain's behaviour, as it appeared on the timeline, conflicts with the term or its inherited definition. Describe the tension in Notes and cross-reference the matching hotspot (§9 of the output document). You **flag** this; you do not resolve it — the session that set the canonical term owns the decision to change it.

If you are starting with no incoming glossary, every row is simply `New` and the column does no extra work — but keep it, so the document is ready to hand to CSAF or a later storm.

### Two rules that keep both philosophies intact
CSAF resolves language to a single canonical term and records what it beat; Event Storming refuses to merge concepts prematurely. Both hold at once if you observe:

1. **Only fill *Rejected Synonyms / Ambiguities* on explicit confirmation.** When the participant clearly says "X and Y are the same — call it X", record Y as rejected. Otherwise the two stay as separate rows (or one `Contested` row) with the tension noted — never collapsed on your own judgement.
2. **Flag, never overrule, inherited terms.** A canonical term you were handed was decided by the people in the earlier room. If behaviour contradicts it, mark it `Contested` and raise a hotspot; don't quietly redefine, merge, or delete it.
