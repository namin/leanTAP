leanTAP: A Theorem Prover for First-Order Classical Logic
=========================================================

This project implements leanTAP in clojure.core.logic.nominal closely
following the alphaleanTAP implementation described in
_alphaleanTAP: A Declarative Theorem Prover for First-Order Classical Logic_
([PDF](http://webyrd.net/alphaleantap/alphatap.pdf))
and chapter of 10 of
[William E. Byrd](http://webyrd.net/)'s thesis
([PDF](https://search.proquest.com/docview/304903505)).

The
[`alphaleantap`](https://github.com/namin/leantap/tree/master/alphaleantap)
directory contains the original implementation in Scheme.

The [`cljtap`](https://github.com/namin/leantap/tree/master/cljtap)
directory contains the implementation in Clojure, using
[core.logic.nominal](https://github.com/namin/minikanren-confo).

Statistics
-----------------------------------

On the Pelletier problems, the Clojure implementation is roughly 1/3
faster on average than the original implementation tested with Petite
Chez Scheme.

[![Chart with Clojure and Scheme results on Pelletier Problems](https://docs.google.com/spreadsheet/oimg?key=0Aq6lPvMWlyvwdGRtbDRYZGpmcXI1OG9RM2swNWxyc1E&oid=2&zx=oejhl3v763go)](https://docs.google.com/spreadsheet/ccc?key=0Aq6lPvMWlyvwdGRtbDRYZGpmcXI1OG9RM2swNWxyc1E&hl=en#gid=1)

Warning: the results in the
[spreadsheet](https://docs.google.com/spreadsheet/ccc?key=0Aq6lPvMWlyvwdGRtbDRYZGpmcXI1OG9RM2swNWxyc1E&hl=en#gid=0)
are based on a single run of each implementation. It would still be
interesting for core.logic's sake to understand why the implementation
in Clojure performs much worse on Problem 20 than the original one in
Petite Chez Scheme.
