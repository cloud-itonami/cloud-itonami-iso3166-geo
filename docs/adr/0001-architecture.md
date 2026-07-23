# ADR-0001: Architecture — Georgia market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-23

## Context

`cloud-itonami-iso3166-geo` was published as a `:blueprint` (docs +
`blueprint.edn`, plus a country-level `culture.facts` catalog in an
earlier, unrelated batch) but carried ZERO `src/marketentry` or
`src/statute` content -- its `:public-sector/market-entry-compliance`
domain, declared in `blueprint.edn`, was unimplemented. This ADR closes
that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and, most directly,
`cloud-itonami-iso3166-arm` / `cloud-itonami-iso3166-aze` /
`cloud-itonami-iso3166-mda` (the simpler, no-`goyoukiki` shape this
blueprint also uses -- `blueprint.edn`'s `:required-technologies` does
not list `:ontology`, so this fork skips the `marketentry.goyoukiki`
real-tender-fact bridge JPN carries).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
JPN/ARM/AZE/MDA's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Georgia's own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`GEO-DFT-000000`, `GEO-SUB-000000`). No additional numeric
  threshold function -- Georgia's own procurement-method monetary
  thresholds (GEL 200 000 / GEL 5 000, Article 3(1)(p)/(q)/(r1)) select
  which PORTAL WORKFLOW a contracting authority uses, not a bidder-
  eligibility formula, so they don't naturally fit this namespace's
  shape (see the namespace docstring).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Flagship HARD check: `white-list-claim-unverified` -- and why it is a genuinely different SHAPE from ARM/AZE/MDA's flagships

Researching the Law of Georgia on Public Procurement (Law No 1388-IS,
adopted 20 April 2005, in force since 1 January 2006 -- this iteration
downloaded the State Procurement Agency's own hosted English
translation via curl+pdftotext, since a plain WebFetch on the PDF
returned only undecoded binary, the same fallback prior iterations of
this loop used for Armenia/Azerbaijan/Albania/South Korea) surfaced
Article 3(1), "Definition of terms", which defines TWO distinct
supplier registries the Agency maintains (Article 4(6)(j)/(j1)):

1. **Article 3(1)(l): the Black List** ("register of mala fide
   participants of the procurement") -- "mala fide persons, bidders and
   suppliers ... who may not participate in public procurement and be
   awarded a public procurement contract within one year after they are
   entered into the Black List." This is the SAME registry-membership,
   categorical-bar SHAPE Armenia's `ineligible-bidder-listed`,
   Azerbaijan's `unreliable-supplier-listed` and Moldova's
   `interdiction-list-violations` checks already establish for this
   family (a fourth instance). `marketentry.governor/black-list-
   violations` reuses this shape honestly -- Georgia's own law genuinely
   supports the identical mechanism -- but it is deliberately NOT the
   flagship.
2. **Article 3(1)(l1): the White List** ("register of qualified
   suppliers participating in procurements") -- suppliers who "meet the
   criteria determined by a subordinate normative act" and, once
   registered, "shall enjoy simplified procedures provided under a
   subordinate normative act." This is a POSITIVE entitlement, not an
   exclusion -- structurally the OPPOSITE of every list-membership check
   this fleet has implemented so far, all of which screen for a known
   BAR. A supplier falsely claiming (or an advisor falsely proposing to
   rely on) White List status to obtain a simplified procedure it was
   never actually granted is a distinct, real harm the Black List check
   cannot catch, because the ground truth being verified is a claimed
   BENEFIT, not a known disqualification.

`marketentry.governor/white-list-claim-unverified-violations` is this
vertical's FLAGSHIP: for `:filing/submit`, when the engagement's own
proposal declares `:claims-white-list-benefit? true`, it INDEPENDENTLY
checks `:white-list-verified?` and HARD-holds on a mismatch. Unlike
ARM/AZE/MDA's flagships (all UNCONDITIONAL -- evaluated on every
`:filing/submit`), this check is CONDITIONAL on the engagement's own
claim, because White List reliance is optional/discretionary (only an
engagement that actually invokes the simplified-procedure benefit needs
its membership independently re-verified) -- an honest divergence from
the unconditional shape, not an oversight; the genuine novelty this
check contributes is the claimed-BENEFIT-vs-known-BAR axis, not the
conditional/unconditional axis. `grep -rn "ineligible-bidder-listed\|
unreliable-supplier-listed\|interdiction-list" src/` against this repo
returns nothing -- the flagship's name and rule keyword
(`:white-list-claim-unverified`) were not copied from any sibling.

This iteration ALSO found, on the live portal's own navigation
(`procurement.gov.ge/en`), a third tier -- a "Warned Suppliers
List"/"Warned Suppliers Registry" -- that exists operationally but
whose specific statutory basis could NOT be located within Article 3's
defined-terms list (only Black List and White List are defined there).
This is an honest, disclosed gap: no `warned-list` check is implemented,
because this iteration could not independently confirm its legal basis
this session (see `marketentry.facts` namespace docstring).

### Other HARD checks (all unoverridable)

1. **spec-basis** -- never invent a jurisdiction's market-entry
   requirements (`marketentry.facts` G2 catalog: Unified Electronic
   System of Public Procurement, NAPR, identification number for GEO).
2. **evidence-incomplete** -- draft/submit require a full assessment
   checklist on file.
3. **black-list-listed** -- see above (standard, not flagship).
4. **white-list-claim-unverified** -- see above (FLAGSHIP).
5. **engagement-fee-mismatch** -- recompute `base-fee + monthly-rate ×
   monitoring-months` (ground-truth-recompute discipline).
6. **id-number-unverified** -- conditional on `:requires-id-number?`
   (the 9-digit identification number NAPR issues at the SAME moment as
   business registration -- confirmed via the OECD's own hosted Common
   Reporting Standard TIN documentation for Georgia, curl+pdftotext-
   verified after WebFetch again returned only undecoded binary on the
   PDF).
7. **already-drafted / already-submitted** -- dedicated booleans, never
   a `:status` value.

### `rep-spec-basis`: honestly nil, not fabricated

Unlike Armenia's Law on Procurement Article 6(1)(3) (bidder's-
representative disqualification, narrow but real) or Azerbaijan's
equivalent, this iteration read the Law of Georgia on Public
Procurement in full and found NO analogous provision extending
disqualification to a bidder's authorized representative.
`marketentry.facts/rep-spec-basis` returns nil for GEO -- the catalog
entry simply omits `:rep-owner-authority`, and `test/marketentry/
facts_test.clj` asserts this explicitly (`geo-has-no-rep-spec-basis`)
so a future iteration does not mistake the omission for an oversight.

### `statute.facts` (second, orthogonal catalog)

Three Georgian statutes, each located via a secondary route (Revenue
Service / NAPR pages, or a plain web search for the law's name plus
`matsne.gov.ge`) and then INDEPENDENTLY VERIFIED by directly fetching
the resulting `matsne.gov.ge/en/document/view/<id>` page and confirming
title/law-number/date matched -- `matsne.gov.ge`'s own `/search`
endpoint returned a programmatic "access denied" response (not a bot-
detection/CAPTCHA challenge; no browser-automation bypass was
attempted, per this loop's hard prohibition on that class of
workaround), so this verify-by-fetch method was used instead of direct
search:

- Tax Code of Georgia -- Law No 3591, adopted 17 September 2010
  (matsne document 1043717).
- Organic Law of Georgia -- Labour Code of Georgia -- Law No 4113-რს,
  adopted 17 December 2010 (matsne document 1155567).
- Law of Georgia on Entrepreneurs -- Law No 875-Vრს-Xმპ, adopted 2
  August 2021 (matsne document 5230186) -- this iteration specifically
  checked which of TWO same-titled laws (a 1994 original, No 578-IS,
  and this 2021 recodification) is current, and cites the current one.

This iteration also specifically searched for (and could NOT find) a
"Law of Georgia on Investment Activity Promotion and Guarantees" via
the same method -- an honest, disclosed gap, not a fabricated fourth
entry.

## Consequences

- `src/` now genuinely exists with real, tested, WebFetch/curl-cited
  content for this blueprint's declared domain (`:public-sector/
  market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (an earlier, unrelated batch) is
  untouched, as is `schema/culture.edn` and `data/culture-tx.edn`.
- The Warned Suppliers Registry (found operationally, not legally
  grounded this session) is a genuine, disclosed extension point for a
  future iteration that can independently locate and read its
  subordinate-normative-act basis.
- The Law on Investment Activity Promotion and Guarantees (searched
  for, not found) is a genuine, disclosed extension point for a future
  iteration with working `matsne.gov.ge` search access or a different
  secondary-source lead.
- Sibling country blueprints can continue forking JPN/ARM/AZE/MDA/GEO
  and swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself evidence (following ARM's own
  ADR-0001, which made the same point) that the flagship check should
  be chosen from real, currency-checked research, not copied by rote,
  and that a country's law finding the SAME general mechanism as a
  sibling (Black List) does not preclude also finding a genuinely
  DIFFERENT one (White List) worth making the flagship instead.
