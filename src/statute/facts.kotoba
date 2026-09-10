(ns statute.facts
  "General-law compliance catalog for Georgia (GEO) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm's
  `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-
  federation).

  Every entry cites an OFFICIAL Georgian government-hosted URL --
  never fabricated. Georgia's official legal-acts database is
  matsne.gov.ge ('საქართველოს საკანონმდებლო მაცნე', the Legislative
  Herald of Georgia). This iteration found that matsne.gov.ge's own
  `/search` endpoint returns a programmatic 'access denied' response
  (a plain error page, not a bot-detection/CAPTCHA challenge -- no
  browser-automation bypass was attempted, per this loop's hard
  prohibition on that class of workaround) and so did NOT rely on it;
  instead, each law below was located via an independent secondary
  route (the Revenue Service's / National Agency of Public Registry's
  own pages, or a plain web search for the law's name plus
  `matsne.gov.ge`) and then INDEPENDENTLY VERIFIED by directly fetching
  the resulting `matsne.gov.ge/en/document/view/<id>` URL and confirming
  its title, law number and adoption date matched -- the same
  verify-by-fetch discipline this fleet's other iterations use when a
  candidate citation surfaces from a secondary source. All three
  matsne.gov.ge document pages below rendered as plain, fully readable
  HTML on the first WebFetch attempt (no PDF-extraction fallback
  needed, unlike the Public Procurement Agency's own hosted PDF or the
  OECD's TIN documentation, both of which required curl+pdftotext --
  see `marketentry.facts`).

  - Tax Code of Georgia -- matsne.gov.ge/en/document/view/1043717,
    confirmed 'TAX CODE OF GEORGIA', Law No 3591, adopted 17 September
    2010 (LHG 54, 12 October 2010).
  - Labour Code of Georgia -- matsne.gov.ge/en/document/view/1155567,
    confirmed 'ORGANIC LAW OF GEORGIA -- LABOUR CODE OF GEORGIA', Law No
    4113-რს, adopted 17 December 2010.
  - Law of Georgia on Entrepreneurs -- matsne.gov.ge/en/document/view/5230186,
    confirmed title 'Law of Georgia on Entrepreneurs', Law No
    875-Vრს-Xმპ, adopted 2 August 2021 (published 4 August 2021) -- the
    CURRENT law (a 2021 recodification that supersedes an earlier, same-
    titled 1994 law, No 578-IS -- this iteration deliberately cites the
    current 2021 law, not the superseded 1994 one, having found both and
    checked which is current). Article 1, read directly: 'This Law
    regulates the legal forms of an entrepreneur, the procedures for
    their incorporation and registration, and issues related to their
    activities.'

  This iteration specifically looked for (and could NOT find) a
  Georgian 'Law on Investment Activity Promotion and Guarantees' via
  the same secondary-source-then-verify-by-fetch method used for the
  three laws above -- an honest, disclosed gap, not a fabricated
  fourth entry. A future iteration with working matsne.gov.ge search
  access, or a different secondary-source lead, should add it if it
  exists and is still current law.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"GEO"
   [{:statute/id "geo.entrepreneurs-law"
     :statute/title "საქართველოს კანონი მეწარმეთა შესახებ (Law of Georgia on Entrepreneurs)"
     :statute/jurisdiction "GEO"
     :statute/kind :law
     :statute/law-number "875-Vრს-Xმპ, adopted 2 August 2021"
     :statute/url "https://matsne.gov.ge/en/document/view/5230186"
     :statute/url-provenance :official-matsne
     :statute/enacted-date "2021-08-02"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "geo.labour-code"
     :statute/title "საქართველოს ორგანული კანონი საქართველოს შრომის კოდექსი (Organic Law of Georgia -- Labour Code of Georgia)"
     :statute/jurisdiction "GEO"
     :statute/kind :law
     :statute/law-number "4113-რს, adopted 17 December 2010"
     :statute/url "https://matsne.gov.ge/en/document/view/1155567"
     :statute/url-provenance :official-matsne
     :statute/enacted-date "2010-12-17"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:labor :employment}}
    {:statute/id "geo.tax-code"
     :statute/title "საქართველოს საგადასახადო კოდექსი (Tax Code of Georgia)"
     :statute/jurisdiction "GEO"
     :statute/kind :law
     :statute/law-number "3591, adopted 17 September 2010"
     :statute/url "https://matsne.gov.ge/en/document/view/1043717"
     :statute/url-provenance :official-matsne
     :statute/enacted-date "2010-09-17"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:tax :corporate-governance}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-geo statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "GEO")) " GEO statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
