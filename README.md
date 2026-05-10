# Secure Routing Algorithms


## src/networks
Pre-made networks in csv-files.

### File naming:
The numbers indicate how many of the given node type are present in the network:
- S = source node
- N = inner node
- D = destination node

The affix indicates the completeness of the given values, where each higher level includes all levels below:
- full: edges and all values for objectives
- c/f/p: edges and the values for the listed objectives
  - c = cost
  - f = flow
  - p = probability
- edges: only edges, without values for the objectives

E.g., 1s3n1d-edges.csv:
- 1 source node
- 3 inner nodes
- 1 destination node
- only edges

E.g., 2s10n1d-cp.csv:
- 2 source nodes
- 10 inner nodes
- 1 destination node
- edges and the values for cost and flow