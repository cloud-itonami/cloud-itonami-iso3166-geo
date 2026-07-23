# cloud-itonami-iso3166-geo

Open ISO 3166 Blueprint for **GEO**: Georgia --
**`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
local-content rules in Georgia, so the operator can win and service a
government contract without hiring a full in-house compliance
department.

## Official surface (WebFetch/curl-verified 2026-07-23)

- Procurement: Unified Electronic System of Public Procurement
  (`tenders.procurement.gov.ge`), operated by the State Procurement
  Agency (SPA) -- an independent LEPL ("Agency") established under the
  Law of Georgia on Public Procurement (Law No 1388-IS, adopted 20
  April 2005, in force since 1 January 2006, Article 4(1)). The Law
  defines TWO supplier registries the Agency maintains: the **Black
  List** (Article 3(1)(l), a 1-year categorical exclusion for "mala
  fide persons, bidders and suppliers") and the **White List** (Article
  3(1)(l1), qualified suppliers who "enjoy simplified procedures" --
  a positive benefit, not a bar). This repo's flagship governor check
  independently re-verifies the White List, not the Black List -- see
  `docs/adr/0001-architecture.md`.
- Business/tax identity: the National Agency of Public Registry (NAPR,
  `napr.gov.ge`), a Ministry-of-Justice-system LEPL. This repo
  specifically investigated, rather than assumed, whether business
  registration and taxpayer-identification-number issuance are one act
  or two: the OECD's own hosted Common Reporting Standard TIN
  documentation for Georgia states the 9-digit "taxpayer / enterprise
  identification number" is "assigned by the registering body ...
  (Public registry)" -- a SINGLE act by NAPR, cross-referenced by
  Article 66(9) of the Tax Code of Georgia (Law No 3591, adopted 17
  September 2010). NAPR's own fees-and-terms page confirms standard
  registration takes 1 working day (200 GEL), with a same-day expedited
  option (400 GEL) -- the real basis for Georgia's famously fast
  business registration.
- Registration procedure: Law of Georgia on Entrepreneurs (Law No
  875-Vრს-Xმპ, adopted 2 August 2021 -- a 2021 recodification of the
  1994 law of the same name; matsne.gov.ge document 5230186).

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `white-list-claim-unverified` (Law of Georgia on Public Procurement Article 3(1)(l1) -- a CONDITIONAL check independently re-verifying a claimed POSITIVE benefit, a genuinely different shape from prior siblings' unconditional bar-screening checks -- see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljc` -- Tax Code, Labour Code, Law on Entrepreneurs |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set — it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Georgia.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Georgian-licensed counsel
  or a registered agent where the law requires licensed representation.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `GEO`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Georgia:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
