(ns culture.facts
  "Country-level regional-culture catalog for Georgia (GEO) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"GEO"
   [{:culture/id "geo.dish.khachapuri"
     :culture/name "Khachapuri"
     :culture/country "GEO"
     :culture/kind :dish
     :culture/summary "Georgian dish of cheese-filled bread, holding status as the country's national dish."
     :culture/url "https://en.wikipedia.org/wiki/Khachapuri"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.dish.khinkali"
     :culture/name "Khinkali"
     :culture/country "GEO"
     :culture/kind :dish
     :culture/summary "Georgian dumpling with spiced meat, fish or vegetable filling, originating in the mountainous regions of eastern Georgia."
     :culture/url "https://en.wikipedia.org/wiki/Khinkali"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.dish.lobio"
     :culture/name "Lobio"
     :culture/country "GEO"
     :culture/kind :dish
     :culture/summary "Traditional Georgian bean stew made with walnuts, garlic, onions and spices, served in hot and cold varieties."
     :culture/url "https://en.wikipedia.org/wiki/Lobio"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.dish.churchkhela"
     :culture/name "Churchkhela"
     :culture/country "GEO"
     :culture/kind :dish
     :culture/summary "Traditional Georgian candle-shaped confection made by threading nuts onto a string and dipping them in thickened grape juice."
     :culture/url "https://en.wikipedia.org/wiki/Churchkhela"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.beverage.georgian-wine"
     :culture/name "Georgian wine"
     :culture/country "GEO"
     :culture/kind :beverage
     :culture/summary "Georgia has produced wine for at least 8,000 years, with wine traditionally fermented in clay vessels called kvevris."
     :culture/url "https://en.wikipedia.org/wiki/Georgian_wine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.craft.kvevri"
     :culture/name "Kvevri"
     :culture/country "GEO"
     :culture/kind :craft
     :culture/summary "Large earthenware vessel used for fermenting, storing and ageing traditional Georgian wine; the qvevri winemaking method was added to UNESCO's intangible cultural heritage list in 2013."
     :culture/url "https://en.wikipedia.org/wiki/Kvevri"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.festival.tbilisoba"
     :culture/name "Tbilisoba"
     :culture/country "GEO"
     :culture/kind :festival
     :culture/summary "Annual October festival celebrating the diversity and history of Tbilisi, the capital of Georgia."
     :culture/url "https://en.wikipedia.org/wiki/Tbilisoba"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "geo.heritage.gelati-monastery"
     :culture/name "Gelati Monastery"
     :culture/country "GEO"
     :culture/kind :heritage
     :culture/summary "Medieval monastic complex near Kutaisi in the Imereti region of western Georgia, inscribed as a UNESCO World Heritage Site in 1994."
     :culture/url "https://en.wikipedia.org/wiki/Gelati_Monastery"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-geo culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "GEO"))
                 " GEO entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
