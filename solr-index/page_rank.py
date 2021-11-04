import networkx as nx

nx_graph = nx.read_edgelist("C:/Users/13108/Desktop/info-retrieval/solr-index/edges.txt")
pagerank = nx.pagerank(nx_graph, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None)

with open("external_pageRankFile.txt", "w") as f:
    for key, value in pagerank.items():       
        f.write("/home/emil/Desktop/NYTIMES/nytimes/" + key + "=" + str(value) + "\n")