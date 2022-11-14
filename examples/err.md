# The XML cannot produce figure

1. figure_RelaxedClockTikzTree.xml

Create latex file, but it has fatal err.

```latex
\documentclass[11pt,class=article,border={5pt 5pt 5pt 5pt}]{standalone}
\usepackage{tikz,pgf}
\begin{document}
\begin{tikzpicture}[yscale=-1]
  \draw[dash pattern=on 1.0 off 1.0 ] (-14pt, 140pt) -- (154pt, 140pt);
  \node[anchor=west, dash pattern=on 1.0 off 1.0 ] at (154pt, 140pt) {\scriptsize{0}};
  \draw[dash pattern=on 1.0 off 1.0 ] (-14pt, 108.88889pt) -- (154pt, 108.88889pt);
  \node[anchor=west, dash pattern=on 1.0 off 1.0 ] at (154pt, 108.88889pt) {\scriptsize{1}};
  \draw[dash pattern=on 1.0 off 1.0 ] (-14pt, 46.66667pt) -- (154pt, 46.66667pt);
  \node[anchor=west, dash pattern=on 1.0 off 1.0 ] at (154pt, 46.66667pt) {\scriptsize{3}};
  \draw[dash pattern=on 1.0 off 1.0 ] (-14pt, 0pt) -- (154pt, 0pt);
  \node[anchor=west, dash pattern=on 1.0 off 1.0 ] at (154pt, 0pt) {\scriptsize{4.5}};
  \node[anchor=north, line width=1.25pt] at (0pt, 140pt) {A};
  \node[anchor=north, line width=1.25pt] at (46.66667pt, 140pt) {B};
  \draw[line width=1.25pt] (0pt, 140pt) -- (0pt, 108.88889pt) -- (23.33333pt, 108.88889pt);
\end{tikzpicture}
```

2. figure_StructuredInfectionTree.xml

```java
Clip: null
java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 3
at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
at java.base/java.util.Objects.checkIndex(Objects.java:359)
at java.base/java.util.ArrayList.get(ArrayList.java:427)
at beastgraphics.app.draw.tree.ColorTable.getColor(ColorTable.java:36)
at beastgraphics.app.draw.tree.TreeComponent.drawBranch(TreeComponent.java:242)
at beastgraphics.app.draw.tree.TreeComponent.draw(TreeComponent.java:330)
```

3. figure_YuleVsCoalescent.xml

```xml
validate and intialize error: token recognition error at: '[t'

Error detected about here:
  <beast>
      <tree id='yuletree' spec='beast.base.evolution.tree.TreeParser'>
```

4. testTikzTreeOrientation.xml

```java
Clip: null
java.lang.NullPointerException: Cannot invoke "Object.toString()" because "metaData" is null
	at beastgraphics.app.draw.tree.TreeComponent.draw(TreeComponent.java:337)
	at beastgraphics.app.draw.tree.TreeComponent.draw(TreeComponent.java:325)
	at beastgraphics.app.draw.tree.TreeComponent.draw(TreeComponent.java:325)
	at beastgraphics.app.draw.tree.TreeComponent.paintComponent(TreeComponent.java:430)
```
