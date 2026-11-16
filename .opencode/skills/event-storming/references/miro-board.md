# Event Storming on Miro — Board Mechanics

Read this at framing, before you write anything to the board. The Miro board **is the wall**: every result of the session — intermediate and final — lands there as sticky notes. This file defines how to find the board, how to talk to it through the Miro MCP, what each sticky looks like, and how to lay it out.

## Contents
1. Resolving and verifying the board
2. Working with the Miro MCP tools
3. Sticky-note mapping
4. Layout
5. Cadence: when to write, what to say in chat
6. The board is shared: user edits win
7. Final output on the board
8. Failure handling

---

## 1. Resolving and verifying the board

The user must tell you which board to use — either a **link** (`https://miro.com/app/board/...`) or the board's **name**. Never pick a board on your own, and never create a new one unless the user explicitly asks (board creation needs their confirmation).

1. **No board given yet** → ask for it as part of framing (Step 1). Don't start eliciting events until you have a board, or the user has explicitly chosen to go on without one (see §8).
2. **Link given** → use it as-is.
3. **Name given** → look it up with `content_search` (fall back to `board_search_boards` if it isn't available).
   - Exactly one clear match → confirm it back to the user in one line (name + link) and continue.
   - Several plausible matches → list them (name, owner, last modified, link) and ask which one. Don't guess.
   - No match → say so plainly: "I couldn't find a board called '…' that I have access to." Ask the user to check the name, send the link, or make sure the board is shared with the account connected to the Miro MCP.
4. **Verify access before relying on it.** Do a cheap read (e.g. `canvas_search` with `result_mode: "overview"`). If it succeeds, you also learn what's already on the board (see §4). If it fails (not found, no permission, wrong account, MCP not connected), **report the failure and its apparent cause to the user** and wait for them to fix it — they may send another link, a different name, or grant access themselves.
5. **Do not change sharing or access on your own.** `board_share` / `board_role_update` change who can see the board; only use them if the user explicitly asks.

Remember the verified board URL for the whole session; every Miro call needs it.

## 2. Working with the Miro MCP tools

- **Load the authoring instructions first.** Before the first write, call `canvas_get_canvas_composer_skill` (no arguments, then follow the `step` it names — `design`, then `dsl` for creating; `edit` for changing existing items) and follow the SVG format it returns. Reuse those instructions for the rest of the conversation instead of re-fetching. Omit optional parameters you don't need rather than passing `null`.
- **Create** stickies and frames with `canvas_create_from_svg`.
- **Change** (rename, recolour, move, re-order) with `canvas_update_from_svg`, matching on the `data-miro-id` values from the previous `result_svg`.
- **Read** with `canvas_search` (find things / get an overview) and `canvas_read_as_svg` (read an area or specific items).
- **Keep an id map.** After every write, remember which model item (event, command, term, hotspot, …) corresponds to which `data-miro-id`. When the participant renames or reorders something, **update the existing sticky** — never add a duplicate and leave the old one behind.
- **Deleting is destructive and not undoable.** Only delete a sticky when the participant has asked for it or confirmed it. Never delete or move anything on the board that you didn't create unless the user explicitly asks.
- **Check the result.** A write call returns `success`, `created_count` and possibly `failed_items`. Only tell the user something is "on the board" when the call actually reported success for it.

The tool names above are those of the Miro MCP server. Some harnesses expose MCP tools through a code-execution wrapper rather than as direct tool calls — use whichever mechanism your harness provides.

## 3. Sticky-note mapping

One element = one sticky. Sticky colours come from a fixed named palette, so pick the **closest** named colour to the Brandolini convention and check the name against the format instructions from §2. Use the same colour for the same element type everywhere on the board.

| Element | Sticky colour | Text on the sticky |
|---|---|---|
| Domain Event | orange | Past tense, participant's own words: `Order Placed` |
| Pivotal Event | orange + a visible marker | `Order Placed` with a `PIVOTAL` marker (prefix or a small adjacent marker shape — be consistent) |
| Command | light blue | Imperative: `Place Order` |
| Actor | yellow | A role: `Dispatcher` |
| Aggregate | light yellow | A noun: `Order` |
| Policy | violet | `Whenever Payment Failed, then Notify Customer` |
| External System | pink | `Payment Gateway` |
| Read Model | light green / green | `Available Stock view` |
| Hotspot / open question | red | The question or tension, prefixed `?` — attribute conflicting views ("room: A says…, B says…") |
| Assumption (your proposal, unconfirmed) | red, prefixed `ASSUMPTION:` | Plus the item it concerns. Remove the prefix only when the participant confirms it |
| Glossary term | cyan (or the nearest free colour) | `Term: short definition` plus its Status (`Inherited` / `Refined` / `New` / `Contested`) |
| Parking-lot idea | gray | The out-of-scope idea, with the context that prompted it |

Text rules:
- **Short.** A sticky holds a thought, not a paragraph — aim for roughly 3–7 words. Split rather than cram. Longer definitions (glossary) stay in full in the markdown glossary (see §7); the sticky carries the term and a one-line gist.
- **Verbatim.** Keep the participant's phrasing. If you reshape something into grammar ("Submit click" → `Booking Confirmed`), the sticky carries the reshaped, *confirmed* version.
- **Mark provenance.** Anything the participant didn't say stays flagged (`ASSUMPTION:`) until they confirm. As-is vs to-be: tag to-be / aspirational items `TO-BE` so they are never mistaken for current behaviour.
- **Bounded contexts** are not stickies: they are frames (or labelled areas) drawn around a stretch of the timeline, titled with the context name. The stickies inside stay stickies.

## 4. Layout

Use the board's own conventions from the format instructions (spacing scale, one sticky size, frames with titles inside). On top of that:

- **Look before you write.** The board may already contain content. Read the overview first, put the session in a **free area** (or a frame you create for it), and never overlap or overwrite what is already there. If the user pointed at a specific frame (`?moveToWidget=`), work inside it.
- **One dedicated area per session**, with a title such as `Event Storm — <domain / process>` so anyone opening the board can tell what it is.
- **Timeline runs left to right.** Domain events in one row, in chronological order. Branches and unhappy paths go on a **separate row below** the happy path, aligned under the event they branch from.
- **Process-level elements sit around their event**, following the grammar (actor → command → aggregate → event): commands to the left of / just above the event they cause, actors above their command, read models next to the command they inform, policies between an event and the command it triggers, external systems alongside, aggregates beneath. Use one consistent arrangement for the whole board.
- **Hotspots touch the thing they concern.** Place each red sticky next to (or overlapping a corner of) the sticky it is about, so the tension is visible in context rather than in a separate list. Also keep a **Hotspots / open questions** frame as the running list.
- **Separate frames for the non-timeline artifacts:** Glossary, Hotspots / open questions, Parking lot. Don't mix them into the timeline.
- **Leave room.** Inserting an event in the middle of the timeline means shifting what follows it to the right (update the existing stickies' positions); leave generous space so later phases (commands, policies, …) have room to grow around the events.
- **Keep it readable.** Few, consistent sizes; aligned edges; nothing touching by accident; no decoration.

## 5. Cadence: when to write, what to say in chat

The board is the artifact; the chat is the conversation. Don't duplicate one in the other.

- **Write in batches, not sticky by sticky.** After the participant gives you a handful of things, reshape them, then put them on the board in one write. A single turn → at most a couple of Miro calls.
- **Chat stays lean.** After a write, say in one or two lines what changed ("Added 6 events, flagged 1 hotspot on the payment step") and ask **one** clear next question. Don't reprint the whole wall as text; point to the board instead. Share the board link at phase boundaries.
- **Reshaping is still shown in chat.** The "you said X → I'd write it as Y, right?" exchange happens in text *before* the sticky is written, because the sticky should carry the confirmed wording. If you're writing something provisionally, mark it `ASSUMPTION:`.
- **Phase boundaries** (end of A, B, C, …): tidy the board — fix ordering, add pivotal markers, make sure every phase's output is visibly there — and give the participant a short text summary plus the link, so they can step back and look at the whole wall.
- **Walking the timeline** (Phase H) is read back in chat as a narrative, from the board's current state.

## 6. The board is shared: user edits win

A real board can be edited by people while you work. Treat the board, not your memory, as the source of truth for what is on it:

- At **phase boundaries**, and whenever the participant says they changed something on the board, **re-read the session area** (`canvas_read_as_svg` on that area, or `canvas_search`) and sync your model to it. If someone renamed, moved, added, or removed a sticky, adopt that — don't overwrite it with your older version.
- If a user-made change conflicts with something you recorded (e.g. a renamed term that was in the glossary), flag it as a hotspot instead of silently picking a side.
- Stickies added by others that you don't recognise: take them in as the participant's input (ask a clarifying question if the grammar is off) rather than deleting them.

## 7. Final output on the board

At wrap-up (Phase H), make sure the board contains **every section** of the output document as stickies, using the same structure as `assets/event-storm-output-template.md`:

- Timeline (with pivotal events, branches, unhappy paths) — the main strip
- Commands + actors, read models, policies, external systems, aggregates (as far as the chosen level reached)
- Candidate bounded contexts — frames around the relevant stretches, seams at the pivotal events
- Ubiquitous-language glossary — one sticky per term with its Status
- Hotspots / open questions — still all there; none deleted to look tidy
- Parking lot — the out-of-scope ideas
- Suggested next steps — a small group of stickies (or a short frame)

Then:
- Give the participant the **board link** and a short summary of what is where.
- **Still produce the markdown document** from the template. The board is the primary place for results, but the structured document — in particular the full `UBIQUITOUS_LANGUAGE.md` with its five-column schema — is what round-trips to the CSAF Facilitator and downstream design work, and a sticky can't carry a full glossary definition. Offer to save it as a file.

## 8. Failure handling

- **Miro MCP not available / not connected.** Say so up front, tell the user it has to be connected for the board to be used, and ask how they want to proceed. Don't quietly fall back.
- **Board can't be found or accessed** (see §1). Flag it, say what you tried, and wait for the user to give you a different link/name or to fix access.
- **A write fails midway** (`failed_items`, an error, or a timeout). Tell the user which items didn't land, retry once if it looks transient, and otherwise ask. Keep those items in your model so they aren't lost; never claim they're on the board.
- **The user wants to continue without the board.** That's their call. Continue the session in text (use the compact running-list rendering: delta per turn, full list at phase boundaries), keep a list of everything that hasn't been written to Miro, and offer to sync it all as soon as a working board is available.
- **Never silently go text-only.** Whenever the board is not receiving the results, say so in that turn.
