(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Georgian procurement law, whether a claimed engagement fee
  actually equals base + months x rate, whether an engagement's own
  declared Black List membership was actually checked, whether an
  engagement that RELIES ON White List simplified-procedure treatment
  has actually had that status verified, whether the NAPR-issued
  identification number has been verified for a filing that requires
  it, or when a draft stops being a draft and becomes a real-world
  Unified Electronic System of Public Procurement submission, so this
  MUST be a separate system able to *reject* a proposal and fall back
  to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Black List membership       -- for `:filing/submit`,
                                       INDEPENDENTLY verify the
                                       engagement's own declared
                                       `:on-black-list?` is not true.
                                       UNCONDITIONAL (evaluated for
                                       every `:filing/submit`, not
                                       gated behind a `:requires-X?`
                                       flag) and a boolean registry-
                                       membership check -- the same
                                       'always applies' SHAPE Armenia's
                                       ineligible-bidders-list check,
                                       Azerbaijan's unreliable-supplier
                                       check and Moldova's interdiction-
                                       list check each already use.
                                       Grounded in Georgia's own Law of
                                       Georgia on Public Procurement
                                       Article 3(1)(l): the Black List
                                       ('register of mala fide
                                       participants'), maintained by the
                                       State Procurement Agency per
                                       Article 4(6)(j) -- honestly
                                       reused because Georgia's own law
                                       genuinely supports the same
                                       mechanism, NOT the flagship (see
                                       check 4 below for what genuinely
                                       differs).
    4. White List claim unverified -- FLAGSHIP. For `:filing/submit`,
                                       when the engagement's own
                                       proposal declares
                                       `:claims-white-list-benefit?
                                       true` (i.e. the advisor is
                                       relying on White List simplified-
                                       procedure treatment for this
                                       filing), INDEPENDENTLY check
                                       `:white-list-verified?`.
                                       CONDITIONAL on the engagement's
                                       own claim -- and, unlike checks 3
                                       and its Armenia/Azerbaijan/
                                       Moldova analogues, this is NOT a
                                       bar-screening check at all: it
                                       verifies a claimed POSITIVE
                                       statutory BENEFIT (Law of Georgia
                                       on Public Procurement Article
                                       3(1)(l1): White List members
                                       'enjoy simplified procedures')
                                       against independently-verified
                                       ground truth, rather than
                                       screening for a known
                                       disqualifying bar. A false White-
                                       List claim would let an engagement
                                       proceed under a simplified
                                       procedure it was never actually
                                       entitled to -- the governor must
                                       catch that the same way it
                                       catches a fabricated exclusion-
                                       list denial.
    5. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    6. Identification-number
       unverified                    -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-id-number? true`,
                                       INDEPENDENTLY check
                                       `:id-number-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in the
                                       9-digit identification number
                                       NAPR (National Agency of Public
                                       Registry) issues at the SAME
                                       moment as business registration,
                                       per the OECD's own hosted TIN
                                       documentation for Georgia -- see
                                       `marketentry.facts`.
    7. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(NAPR登記/SPA登録/識別番号等)が充足していない状態での提案"}]))))

(defn- black-list-violations
  "For `:filing/submit`, INDEPENDENTLY verify the engagement's own
  declared Black List membership is not true. UNCONDITIONAL (not
  gated behind a `:requires-X?` engagement flag), because Black List
  membership is itself a categorical bar to procurement participation
  under Article 3(1)(l) of the Law of Georgia on Public Procurement --
  there is no engagement-declared precondition to check first. This is
  the standard, honestly-reused registry-membership SHAPE -- NOT this
  vertical's flagship (see `white-list-claim-unverified-violations`
  below)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (true? (:on-black-list? e))
        [{:rule :black-list-listed
          :detail (str subject " არის შავ სიაში რეგისტრირებული (mala fide participant on the Black List, Law of Georgia on Public Procurement Article 3(1)(l)) -- 提出提案は進められない")}]))))

(defn- white-list-claim-unverified-violations
  "FLAGSHIP check for this repo. For `:filing/submit`, when the
  engagement's OWN proposal declares `:claims-white-list-benefit?
  true` -- i.e. the advisor is relying on Law of Georgia on Public
  Procurement Article 3(1)(l1) White List simplified-procedure
  treatment for this filing -- INDEPENDENTLY check
  `:white-list-verified?`. CONDITIONAL on the engagement's own claim,
  a genuinely different SHAPE from `black-list-violations` above (and
  from Armenia/Azerbaijan/Moldova's own flagship checks): those verify
  a claimed ABSENCE of a bar; this verifies a claimed PRESENCE of a
  positive statutory benefit. An unverified White List claim would let
  a filing proceed under a simplified procedure ('qualified suppliers
  registered in the White List shall enjoy simplified procedures
  provided under a subordinate normative act', Article 3(1)(l1)) that
  was never actually earned -- structurally the same class of harm as
  a suppressed Black List hit, just on the opposite (benefit rather
  than bar) side of the same Article."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:claims-white-list-benefit? e))
                 (not (true? (:white-list-verified? e))))
        [{:rule :white-list-claim-unverified
          :detail (str subject " არის თეთრ სიაში რეგისტრირებული უპირატესობის (White List simplified-procedure benefit, Law of Georgia on Public Procurement Article 3(1)(l1)) პრეტენზია დამოწმებული არ არის -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- id-number-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-id-number? true`, INDEPENDENTLY check
  `:id-number-verified?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in the 9-digit identification number NAPR (National
  Agency of Public Registry) issues at the same moment as business
  registration -- see `marketentry.facts` for the OECD-sourced single-
  act confirmation."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-id-number? e))
                 (not (true? (:id-number-verified? e))))
        [{:rule :id-number-unverified
          :detail (str subject " はNAPR発行の識別番号(identification number)確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (black-list-violations request st)
                           (white-list-claim-unverified-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (id-number-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
