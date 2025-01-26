# Java Code listing for the paper Directional Node Strength Entropy Centrality: Ranking Influential Nodes in Complex Networks

Required Java Library: jung 2.1.1 (all jars) provided in the Lib_jar folder

Various baseline ranking algorithms are implemented in sna.physica.ranking
The proposed DNSE method is also present inside the sna.physica.ranking and named as "DirectionalNodeStrengthEntropy.java"
All the experiments are available inside sna.dns.experiments package. Each Java file computes one resulting table or figure.
Dataset folder contains some sample network dataset. All dataset may be downloaded from SNAP
All code may be imported to a Java workspcae project and experiments can be run to generate output files inside "results" folder.
Example run: sna.dns.experiment.SIR_Spreading_Score would generate SIR simulation scores for all networks in "results" directoiry.
