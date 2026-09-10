(ns marketentry.facts
  "Per-jurisdiction public-procurement market-entry regulatory catalog
  -- the G2-style spec-basis table the Market-Entry Compliance Governor
  checks every `:jurisdiction/assess` proposal against ('did the advisor
  cite an OFFICIAL public source for this jurisdiction's requirements,
  or did it invent one?').

  Georgia's real market-entry surface (WebFetch-verified 2026-07-23,
  see each entry's own citation):

  Procurement: this iteration downloaded and directly read (via curl
  with a standard user-agent + `pdftotext`, since a plain WebFetch on
  the PDF returned only undecoded binary -- the same fallback prior
  loop iterations used for Armenia/Azerbaijan/Albania/South Korea) the
  State Procurement Agency's own hosted English translation of the LAW
  OF GEORGIA ON PUBLIC PROCUREMENT, obtained from
  `https://www.procurement.gov.ge/Files/ShowFiles?id=3cf3c0e5-88f6-4e18-94d4-01ad95ae7f0e`.
  The extracted text opens 'LAW OF GEORGIA ON PUBLIC PROCUREMENT' and
  closes with the President's signature block: 'President of Georgia
  M. Saakashvili, Tbilisi, 20 April 2005, No 1388-IS', Article 26
  stating the Law 'shall enter into force from 1 January 2006'. Article
  4(1), read directly, states: 'An independent legal entity under
  public law - the Public Procurement Agency (\"the Agency\") set up
  under this Law shall be a body authorised to ensure compliance with
  and fulfilment of the provisions of this Law' -- confirming LEPL
  status directly from the primary text, not merely trusted from a
  secondary source. The live operating portal
  (`https://www.procurement.gov.ge/en`, `https://tenders.procurement.gov.ge`,
  both WebFetch-read 2026-07-23) brands the same Agency as the 'State
  Procurement Agency' (SPA) and states the electronic system is 'the
  official portal for activities related to state procurement in
  Georgia', with mandatory registration for buyers and suppliers.
  NOTE: this iteration could NOT independently confirm a secondary
  claim (already present in this repo's pre-existing `README.md` /
  `docs/business-model.md`, written before this iteration) that the
  Agency was 'established 2014' -- the primary Law text this iteration
  actually read dates the Agency's own establishing Law to 2005/2006.
  Rather than repeat an unverified date, this catalog cites only what
  was directly confirmed this session (LEPL status, Law No 1388-IS of
  20 April 2005, in force 1 January 2006) and leaves the 2014 claim
  uncorrected in the pre-existing docs as an honest, disclosed gap
  (possibly a later reorganisation/rename this iteration did not reach
  a primary source for -- `spa.ge` itself returned a connection error
  on every attempt, so `procurement.gov.ge`, the domain the live portal
  actually redirects through, was used instead).

  Business/tax identity: National Agency of Public Registry (NAPR,
  `napr.gov.ge`), a Ministry-of-Justice-system LEPL. This iteration
  specifically investigated, rather than assumed, whether business
  registration and taxpayer-identification-number issuance are ONE act
  or TWO separate ones (the same question ARM/AZE/BGR/ALB's own
  iterations each investigated for their own countries): the OECD's own
  hosted Common Reporting Standard TIN documentation for Georgia
  (`https://www.oecd.org/content/dam/oecd/en/topics/policy-issue-focus/aeoi/georgia-tin.pdf`,
  curl+pdftotext-verified, WebFetch again returned only undecoded
  binary on the PDF) states in its own words: 'Registration of
  Commercial entities and Non-Entrepreneurial (Non-Commercial) Legal
  Entities is carried out by LEPL National Agency of Public Registry',
  and for entities 'The taxpayer / enterprise identification number is
  the 9-digit identification number assigned by the registering body
  defined by the Georgian legislation (Public registry)' -- i.e. a
  SINGLE act by NAPR, the registration authority, not a separate
  subsequent act by the Revenue Service (the tax authority) -- the same
  single-act shape Armenia's own catalog documents, for the same class
  of reason (the registering body's own admission, not an assumption
  that 'taxpayer ID' implies 'issued by the tax authority'). NAPR's own
  `en/page/fees-and-terms` page (WebFetch-read 2026-07-23) states
  standard registration of an entrepreneur (other than an individual)
  or a non-commercial legal entity takes '1 working day' for a 200 GEL
  fee, with a same-day expedited option for 400 GEL; individual-
  entrepreneur registration is '1 working day' for 26 GEL, same-day
  expedited for 75 GEL -- the real basis for this blueprint's business-
  model claim that Georgian business registration is fast (same-day
  available, one-working-day standard). The Law of Georgia on
  Entrepreneurs (Law No 875-Vრს-Xმპ, adopted 2 August 2021 -- matsne.gov.ge
  document 5230186, WebFetch-read 2026-07-23, Article 1: 'This Law
  regulates the legal forms of an entrepreneur, the procedures for
  their incorporation and registration, and issues related to their
  activities') is the current (2021 recodification, superseding the
  1994 law of the same name) legal basis for the registration procedure
  itself.

  `black-list-spec-basis` and `white-list-spec-basis` ground this
  vertical's two list-membership regimes -- see the `catalog` docstring
  for why `white-list-claim-unverified` (NOT `black-list-listed`) was
  chosen as the FLAGSHIP governor check: Article 3(1)(l)/(l1) of the
  Law of Georgia on Public Procurement, read directly from the same
  primary-source PDF above, defines BOTH a negative exclusion list (the
  Black List, 'register of mala fide participants of the procurement')
  AND a positive qualification list (the White List, 'register of
  qualified suppliers participating in procurements' who 'enjoy
  simplified procedures') -- a genuinely different shape from a plain
  boolean bar.

  This iteration also found, on the live portal's own navigation
  (`procurement.gov.ge/en`, WebFetch-read 2026-07-23), a THIRD tier --
  a 'Warned Suppliers List' / 'Warned Suppliers Registry'
  (`/en/n/WarnedSuppliersRegistry`) -- that exists operationally but
  whose specific statutory basis this iteration could NOT locate within
  Article 3's defined-terms list (only Black List and White List are
  defined there); it is likely created by a subordinate normative act
  under the chairperson's Article 5(1)(a) authority, but that specific
  act was not independently located and read this session. This catalog
  therefore does NOT model a 'warned' tier -- an honest, disclosed gap,
  not a fabricated third state.

  This iteration also specifically checked (and could NOT find) a
  Georgian equivalent of Armenia's/Azerbaijan's bidder's-representative
  disqualification provision -- the Law of Georgia on Public
  Procurement's text, read in full, does not contain an analogous
  representative-of-the-bidder disqualification clause. `rep-spec-basis`
  therefore honestly returns nil for GEO (no `:rep-owner-authority` key
  in this catalog entry) -- not a fabricated regime.

  Coverage is reported HONESTLY (see `coverage`): a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:jurisdiction/assess` proposal can commit.
  `:black-list-*` grounds the standard categorical-bar check (same
  registry-membership SHAPE Armenia/Azerbaijan/Moldova's own catalogs
  already establish for this family -- Georgia's own law genuinely
  supports it, so it is honestly reused, not invented to pad the
  catalog). `:white-list-*` grounds this vertical's FLAGSHIP check --
  a genuinely different shape, verifying a CLAIMED statutory BENEFIT
  against independently-verified ground truth, rather than screening
  for a known bar (see `marketentry.governor`)."
  {"GEO" {:name "Georgia"
          :owner-authority "სახელმწიფო შესყიდვების სააგენტო (State Procurement Agency, SPA -- the independent LEPL 'Agency' established under the Law of Georgia on Public Procurement, Article 4(1)) / Unified Electronic System of Public Procurement"
          :legal-basis "Law of Georgia on Public Procurement, Law No 1388-IS, adopted 20 April 2005, in force since 1 January 2006 (Article 26) -- Article 4 (Authorised body: the Agency's LEPL status and its functions, including maintaining the Black List (Art 4(6)(j)) and White List (Art 4(6)(j1))) + Article 3(1)(b) (contracting authority must be registered in the Unified Electronic System of Public Procurement)"
          :national-spec "Unified Electronic System of Public Procurement (tenders.procurement.gov.ge) supplier/participant registration and electronic-tender participation, per Article 3(1)(b)/Article 4"
          :provenance "https://www.procurement.gov.ge/en"
          :required-evidence ["სახელმწიფო შესყიდვების სააგენტოს ერთიან ელექტრონულ სისტემაში მომწოდებლის რეგისტრაცია (Unified Electronic System of Public Procurement supplier/participant registration record, State Procurement Agency)"
                              "საჯარო რეესტრის ეროვნული სააგენტოს ამონაწერი (National Agency of Public Registry business-registry extract, NAPR)"
                              "იდენტიფიკაციო ნომრის ჩანაწერი (9-digit identification number record, issued by NAPR at registration and used as the taxpayer identification number per the OECD's own Georgia TIN documentation)"
                              "შავი/თეთრი სიის სტატუსის შემოწმება (Black List / White List status check, State Procurement Agency)"]
          :black-list-owner-authority "სახელმწიფო შესყიდვების სააგენტო (State Procurement Agency), maintaining the Black List per Article 4(6)(j) of the Law of Georgia on Public Procurement"
          :black-list-legal-basis "Law of Georgia on Public Procurement Article 3(1)(l) -- the Black List ('register of mala fide participants of the procurement') includes 'mala fide persons, bidders and suppliers participating in public procurement, who may not participate in public procurement and be awarded a public procurement contract within one year after they are entered into the Black List.' Maintained electronically by the Agency and published on its official website; available to every person."
          :black-list-provenance "https://www.procurement.gov.ge/Files/ShowFiles?id=3cf3c0e5-88f6-4e18-94d4-01ad95ae7f0e"
          :white-list-owner-authority "სახელმწიფო შესყიდვების სააგენტო (State Procurement Agency), maintaining the White List per Article 4(6)(j1) of the Law of Georgia on Public Procurement"
          :white-list-legal-basis "Law of Georgia on Public Procurement Article 3(1)(l1) -- the White List ('register of qualified suppliers participating in procurements') 'includes data on qualified suppliers participating in procurements who meet the criteria determined by a subordinate normative act for inclusion in the White List. When participating in public procurement, qualified suppliers registered in the White List shall enjoy simplified procedures provided under a subordinate normative act.' A POSITIVE entitlement, not an exclusion -- the flagship check this vertical adds independently re-verifies any engagement that RELIES ON this benefit, rather than merely screening for a bar."
          :white-list-provenance "https://www.procurement.gov.ge/Files/ShowFiles?id=3cf3c0e5-88f6-4e18-94d4-01ad95ae7f0e"
          :corporate-number-owner-authority "საჯარო რეესტრის ეროვნული სააგენტო (National Agency of Public Registry, NAPR)"
          :corporate-number-legal-basis "Per the OECD's own hosted Common Reporting Standard TIN documentation for Georgia: 'Registration of Commercial entities ... is carried out by LEPL National Agency of Public Registry' and 'The taxpayer / enterprise identification number is the 9-digit identification number assigned by the registering body ... (Public registry)' -- a SINGLE act at registration, cross-referenced by Article 66(9) of the Tax Code of Georgia ('A taxpayer shall indicate its taxpayer identification number in a tax return...'). Registration procedure itself: Law of Georgia on Entrepreneurs, Law No 875-Vრს-Xმპ, adopted 2 August 2021, Article 1."
          :corporate-number-provenance "https://www.oecd.org/content/dam/oecd/en/topics/policy-issue-focus/aeoi/georgia-tin.pdf"}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to assess or file
  on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-geo R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog for market-entry navigation, "
                 "not a survey of all ~194 jurisdictions -- extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))

(defn rep-spec-basis
  "The jurisdiction's representative-related requirement map, or nil when
  this catalog has no such regime. For GEO this is honestly nil -- this
  iteration specifically checked the Law of Georgia on Public
  Procurement's full text for an Armenia/Azerbaijan-style bidder's-
  representative disqualification clause and found none (see the
  `catalog` docstring)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-id regime, or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                       :corporate-number-legal-basis
                       :corporate-number-provenance]))))

(defn black-list-spec-basis
  "The jurisdiction's Black List (categorical exclusion) regime, or nil.
  For GEO this is real and current -- Law of Georgia on Public
  Procurement Article 3(1)(l)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:black-list-owner-authority sb)
      (select-keys sb [:black-list-owner-authority
                       :black-list-legal-basis
                       :black-list-provenance]))))

(defn white-list-spec-basis
  "The jurisdiction's White List (positive qualification benefit) regime,
  or nil. For GEO this is real and current -- Law of Georgia on Public
  Procurement Article 3(1)(l1). Grounds this vertical's FLAGSHIP governor
  check, `white-list-claim-unverified` -- a genuinely new shape (verifying
  a claimed BENEFIT, not screening for a bar)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:white-list-owner-authority sb)
      (select-keys sb [:white-list-owner-authority
                       :white-list-legal-basis
                       :white-list-provenance]))))
