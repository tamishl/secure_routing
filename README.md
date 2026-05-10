# Secure Routing Algorithms

## General:
Node type abbreviations:
- S = source node
- N = inner node
- D = destination node


## src/NetworkReader
Used to extract a network from a csv file.
Has inner class Builder with default values for random generation of values for the objectives.

### Accepted file structures
Sections:
- Two sections, nodes and edges, indicated by [NODES] and [EDGES], respectively!
  - Only necessary if the probability (node objective) must be provided or if the network is not connected.
- One section, edges (no section label needed). Nodes will be inferred.

Headers per section:
- [NODES]:
  - id (required)
  - probability (optional)
- [EDGES] (or no label):
  - from (required)
  - to (required)
  - capacity (optional)
  - cost (optional)


NOTE: can not handle mistakes in csv-file (such as typos or whitespaces).

Find examples of valid files in src/networks.


## src/networks
Pre-made networks in csv-files.

### File naming
The numbers indicate how many of the given node type (Source, inner Node, Destination) are present in the network.

The affix indicates the completeness of the data, where each higher level includes all levels below:
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
- edges and the values for cost and probability

NOTE: when flow is included, this must be provided in a section [NODES]