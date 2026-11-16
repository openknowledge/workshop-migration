# Event Storm — [Domain / Process Name]

> **Miro board:** [board name + link — the primary home of the results; every section below should also exist there as stickies]
> **Session goal:** [why we stormed this]
> **Level reached:** [Big Picture | Process Modelling | Software Design]
> **Mode:** [single expert | group via facilitator]
> **As-is / to-be:** [are we modelling how it works today, how it's envisioned, or a mix? note which parts are which]
> **Glossary baseline:** [none — built fresh | extended from a CSAF UBIQUITOUS_LANGUAGE.md (note its date/source)]
> **Date / participants:** [fill in]

## 1. Domain event timeline

Events in chronological order. Note branches and unhappy paths inline. Mark pivotal events with **[PIVOTAL]**.

1. [Event Placed] — past tense
2. [Event Received]
3. ...

Branches / alternative paths:
- After [event], if [condition] then [alternative event] instead of [normal event].

Unhappy paths:
- When [step] fails: [what happens] → [resulting event / hotspot].

## 2. Commands and actors

| Command (imperative) | Actor / trigger | Resulting event |
|---|---|---|
| [Place Order] | [Customer] | [Order Placed] |
| [Reserve Stock] | (policy-triggered) | [Stock Reserved] |

## 3. Read models

What actors look at before they act.

| Read model | Used by | To decide |
|---|---|---|
| [Available Stock view] | [Dispatcher] | whether to [Confirm Order] |

## 4. Policies (reactive rules)

| Whenever (event) | Then (command) | Notes / exceptions |
|---|---|---|
| [Payment Failed] | [Notify Customer] | [exception: ... → hotspot] |

## 5. External systems

| System | Role in this domain |
|---|---|
| [Payment Gateway] | takes payment, emits [Payment Received] |

## 6. Aggregates (design level only)

| Aggregate | Handles commands | Emits events |
|---|---|---|
| [Order] | [Place Order, Cancel Order] | [Order Placed, Order Cancelled] |

## 7. Candidate bounded contexts

Clusters where language or ownership changes. Pivotal events usually sit on the seams.

- **[Context name]** — covers [stretch of timeline]; owned by [team/area]; distinct language: [terms].
- Seam between [Context A] and [Context B] sits at pivotal event **[event]**.

## 8. Ubiquitous language glossary

The domain's own words, in the domain's own definitions. This is the shared `UBIQUITOUS_LANGUAGE.md` — if a prior CSAF session produced one, this section **is** that file carried forward and enriched (do not start a second glossary). Keep the schema and filename identical so it round-trips between the skills.

| Term | Canonical Definition | Rejected Synonyms / Ambiguities | Notes | Status |
|---|---|---|---|---|
| [Term] | [definition in the domain's words] | [only when a synonym collapse was explicitly confirmed] | [context, or the tension if contested] | [Inherited / Refined / New / Contested] |

**Status:**
- `Inherited` — carried from the incoming glossary unchanged.
- `Refined` — inherited, but the definition was sharpened or corrected (say what changed in Notes).
- `New` — first surfaced during this session.
- `Contested` — the domain's behaviour conflicts with the term or its inherited definition; describe the tension in Notes and cross-reference the matching hotspot in §9. Flagged, never overruled — the session that set the canonical term owns the decision to change it.

Never write into *Rejected Synonyms / Ambiguities* on your own initiative — only when the participant explicitly confirms that one term replaces another. Unconfirmed tensions stay `Contested` with a hotspot.

## 9. Hotspots and open questions

Everything unresolved, contested, or assumed about the *current* domain. Do not delete these — they are the agenda for the next conversation.

- ❓ [Open question or disagreement, with context]
- ⚠️ [Assumption made to keep momentum — needs confirmation]
- 🔥 [Known problem / risk surfaced during the session]

## 10. Parking lot — out-of-scope opportunities

Ideas, features, and "wouldn't it be nice" that surfaced but are **not** part of the process as it stands. Kept here so they aren't lost and can feed design/PRD work later — distinct from §9 hotspots, which are open questions about the *current* domain rather than aspirations on top of it.

- 💡 [Future feature or opportunity — with the context that prompted it]
- 🔭 [Aspirational / to-be capability, esp. one that depends on scale or future work]

## 11. Suggested next steps

- [e.g. storm the unhappy paths / exception branches not yet explored]
- [e.g. deeper Process-level storm on the contested slice]
- [e.g. return the enriched `UBIQUITOUS_LANGUAGE.md` to the CSAF owner — especially any `Contested` terms needing adjudication — and feed bounded contexts into architecture work]
