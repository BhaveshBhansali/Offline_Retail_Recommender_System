# This package consists of following files:

1. ## ClusterDistance.py

    This file contains DistanceMetric class consisting many functions to create distance metric for clustering techniques. 
    It uses "MINKOWSKI/Euclidean Norm 1" distance to calculate distance metric.
    
    
    This script can be run from main.py using following command: 
    
    ./clustering/> main.py ./receipt_id.p ./dict_of_transactions.p 7(category level) 
    
    
    Above command calls following:
    
    [ClusterDistance.DistanceMetric().dist_metric(keys,dict_of_transactions,level)]
   
   
    Input: 
    
        1st argument: list of receipts 
    
        2nd argument: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
           
        3rd argument: level: distance metric for category level (i.e.  level 7, 6,....)
           
           
    Output: 
    
        Diagonal distance metric of a level_category information (could be run for level7, level6, level5, level4) in "diagonalDistanceMatrixCategory'+level+'JAC_MINKOWSKI_70000.p" file where 
        level indicates categroy level information, i.e. 7, 6, 5, 4
        
        
        This diagonal distance metric could be convert into n*n dimension distance metric using scipy.spatial.distance.squareform(diagonalDistanceMetric).
    
    
2. ## Clusters.py

    This file contains clusters class consisting different functions for clustering techniques to create clusters 
    
    It consist of following functions for different cluster methods:
    
    
    
    ### DBSCAN_create_clusters
    
    This function is used to call DBSCAN algorithm for clustering.
    This can be run by following command: 
    
    ./clustering/> main.py ./diagonal_metric.p ./dict_of_transactions.p 0.4 10
    
    
    Above command calls following:
    
    [Clusters.clusters().DBSCAN_create_clusters(distance_metric,dict_of_transactions,max_distance,min_neighbours)]
    
    
        Input: 
    
            1st argument: diagonal distance metric
    
            2nd argument: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
    
            3rd argument: threshold_distance (max distance to find neighbours)
    
            4th argument: min_members (minimum numbers to create cluster)
    
    
        Output:
    
            This script generates following files:
            
            1. ./cluster_details_70000_"+str(threshold_distance)+"_"+str(min_members)+"_DBSCAN.txt: It writes number of computed clusters. It also writes number of members corresponding each
            cluster. Then it writes cluster wise receipt information. For example:
            
            Number of clusters: 5
            
            Cluster1: 100, Cluster2: 762, .........
            
            
            Cluster1:
            
            receipt_id: {{material_nr1: [level7_category, level6_category, level5_category, level4_category]}, {material_nr2: [level7_category, level6_category, level5_category, level4_category]},........}
            
            
            
            .
            
            .
            
            Cluster2:
            
            .
            
            .
    
            
            2. It also generates level wise category information for each clusters into the files space_separated_level7_cluster.txt, space_separated_level6_cluster.txt,, space_separated_level5_cluster.txt,
            space_separated_level4_cluster.txt, space_separated_material_cluster.txt. For example:
            
            space_separated_level4_cluster.txt contains following where each line represents one receipt's products categories:
            
            672 741 620
            
            521 463 672 741 961
            
            .
            
            .
            
            
            
            This file is used to find freuquent itemsets from association mining algorithm (apriori requires space separated transactions).
            
            
            3. ClusterCenters.p: It stores a pickle file consisting of list of cluster centers of all the clusters. This cluster centers are used for assigning cluster labels
            corresponding traces.
            
            
            
    ### HDBSCAN_create_clusters
    
    This function is used to call HDBSCAN algorithm for clustering.
    
    This can be run by following command: 
    
    ./clustering/> main.py ./diagonal_metric.p ./dict_of_transactions.p 10
    
    Above command calls following:
    
    [Clusters.clusters().HDBSCAN_create_clusters(distance_metric,dict_of_transactions,min_neighbours)]
    
    
        Input: 
    
            1st argument: diagonal distance metric
    
            2nd argument: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
    
            3rd argument: min_members (minimum numbers to create cluster)
    
    
        Output:
    
            This script generates following files:
            
            1. ./cluster_details_70000_"+str(min_members)+"_HDBSCAN.txt: It writes number of computed clusters. It also writes number of members corresponding each
            cluster. Then it writes cluster wise receipt information. For example:
            
            Number of clusters: 5
            
            Cluster1: 100, Cluster2: 762, .........
            
            
            Cluster1:
            
            receipt_id: {{material_nr1: [level7_category, level6_category, level5_category, level4_category]}, {material_nr2: [level7_category, level6_category, level5_category, level4_category]},........}
            
            .
            
            .
            
            .
            
            Cluster2:
            
            .
            
            .
    
            
            2. It also generates level wise category information for each clusters into the files space_separated_level7_cluster.txt, space_separated_level6_cluster.txt,, space_separated_level5_cluster.txt,
            space_separated_level4_cluster.txt, space_separated_material_cluster.txt. For example:
            
            space_separated_level4_cluster.txt contains following where each line represents one receipt's products categories:
            
            672 741 620
            
            521 463 672 741 961
            
            .
            
            .
            
            
            
            This file is used to find freuquent itemsets from association mining algorithm (apriori requires space separated transactions).
            
            
            
            3. ClusterCenters.p: It stores a pickle file consisting of list of cluster centers of all the clusters. This cluster centers are used for assigning cluster labels
            corresponding traces.
    
                
                
    ### KMeansPlusPlus_create_clusters
        
        This function is used to call K-means++ algorithm for clustering.
        
        This can be run by following command: 
    
        ./clustering/> main.py ./k_means_feature_matrix.p 10 10 10 10 30 30 ./dict_of_transactions.p 9
        
        Above command calls following:
        
        [Clusters.clusters().KMeansPlusPlus_create_clusters(k_means_features_matrix,dict_of_transactions,number_of_clusters)]
        
    
        Input: 
    
            1st argument: 
            
            2nd argument: percentage of level_7 category feature
            
            3rd argument: percentage of level_6 category feature
            
            4th argument: percentage of level_5 category feature
            
            5th argument: percentage of level_4 category feature
            
            6th argument: percentage of price feature
            
            7th argument: percentage of number of products feature
            
            8th argument: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
    
            9th argument: number of clusters
    
    
        Output:
    
            This script generates following files:
            
            1. ./cluster_details_70000_"+str(min_members)+"_HDBSCAN.txt: It writes number of computed clusters. It also writes number of members corresponding each
            cluster. Then it writes cluster wise receipt information. For example:
            
            Number of clusters: 5
            
            Cluster1: 100, Cluster2: 762, .........
            
            
            Cluster1:
            
            receipt_id: {{material_nr1: [level7_category, level6_category, level5_category, level4_category]}, {material_nr2: [level7_category, level6_category, level5_category, level4_category]},........}
            
            .
            
            .
            
            .
            
            Cluster2:
            
            .
            
            .
    
            
            2. It also generates level wise category information for each clusters into the files space_separated_level7_cluster.txt, space_separated_level6_cluster.txt,, space_separated_level5_cluster.txt,
            space_separated_level4_cluster.txt, space_separated_material_cluster.txt. For example:
            
            space_separated_level4_cluster.txt contains following where each line represents one receipt's products categories:
            
            672 741 620
            
            521 463 672 741 961
            
            .
            
            .
            
            
            
            This file is used to find freuquent itemsets from association mining algorithm (apriori requires space separated transactions).
            
            
            
            3. ClusterCenters.p: It stores a pickle file consisting of list of cluster centers of all the clusters. This cluster centers are used for assigning cluster labels
            corresponding traces.
    
    
3. ## KMeansFeatures.py

    This file contains class features consisting of different functions for creating features for K-Means clustering.
    
    
     It consist of following functions:
     
    ### create_dict_of_transaction: 
        
        This function is used to create dictionary of transactions, price_list, number of product list, reecipt list from (70k) random receipts. 
    

        It can be run by following command: 
    
        ./clustering/> main.py 70000
        
        
        Above command calls following:
        
        [KMeansFeatures.features().create_dict_of_transaction(number_of_receipts)]
        
    
        Input:
            
            1st argument: number of random receipts
            
        Output:
        
            It generates following files:
            
            1. dict_of_transactions.p: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
            
            2. receipts.p: list of random receipts
            
            3. price_list.p: list of price from receipts
            
            4. no_of_products_list.p: list of number of products from receipts
    
    
    
    
    ### kmeans_features_creation
    
        This function is used to create features for K-means algorithm.
        
        
        It can be run by following command: 
    
        ./clustering/> main.py ./dict_of_transactions.p ./receipts.p 
        
        
        Above command calls following:
        
        KMeansFeatures.features().kmeans_features_creation(dict_of_transactions, receipts)
        
        
    
        Input: 
    
            1st argument: dictionary of transactions (each entry is like {receipt_id: {material_id: [level7_category, level6_category, level5_category, level4_category]}} )
            
            2nd argument: list of receipts (receipts.p)
    
           
           
        Output: 
    
            Distance metric of all product categories levels of dimenstion (70000, 5535) where 70000 is number of receipts and 5535 is total categories sublevels from levels 7, 6, 5, 4. 
            It stores metric into k_means_features_posPriceNoOfProd.p file. 
    