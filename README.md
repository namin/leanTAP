leanTAP: A Theorem Prover for First-Order Classical Logic
=========================================================

This project implements leanTAP in clojure.core.logic.nominal closely
following the alphaleanTAP implementation described in
_alphaleanTAP: A Declarative Theorem Prover for First-Order Classical Logic_
([PDF](http://www.cs.indiana.edu/~webyrd/alphaleantap/alphatap.pdf))
and chapter of 10 of
[William Byrd](http://www.cs.indiana.edu/~webyrd/)'s thesis
([PDF](http://gradworks.umi.com/3380156.pdf)).

The
[`alphaleantap`](https://github.com/namin/leantap/tree/master/alphaleantap)
directory contains the original implementation in Scheme.

The [`cljtap`](https://github.com/namin/leantap/tree/master/alphaleantap)
directory contains the implementation in Clojure.

Statistics
-----------------------------------

On the Pelletier problems, the Clojure implementation is roughly 1/3
faster on average than the original implementation tested with Petite
Chez Scheme.

<script type="text/javascript" src="//ajax.googleapis.com/ajax/static/modules/gviz/1.0/chart.js"> {"dataSourceUrl":"//docs.google.com/spreadsheet/tq?key=0Aq6lPvMWlyvwdGRtbDRYZGpmcXI1OG9RM2swNWxyc1E&transpose=0&headers=1&range=A1%3AC47&gid=0&pub=1","options":{"titleTextStyle":{"bold":true,"color":"#000","fontSize":16},"vAxes":[{"title":null,"useFormatFromData":true,"minValue":null,"viewWindow":{"min":null,"max":null},"maxValue":null},{"useFormatFromData":true,"minValue":null,"viewWindow":{"min":null,"max":null},"maxValue":null}],"title":"All Pelletier Problems","booleanRole":"certainty","animation":{"duration":0},"legend":"right","useFirstColumnAsDomain":true,"hAxis":{"useFormatFromData":true,"minValue":null,"viewWindowMode":null,"viewWindow":null,"maxValue":null},"isStacked":false,"tooltip":{},"width":1237,"height":426},"state":{},"view":{"columns":[{"calc":"stringify","type":"string","sourceColumn":0},1,2]},"chartType":"ColumnChart","chartName":"Chart 1"} </script>

