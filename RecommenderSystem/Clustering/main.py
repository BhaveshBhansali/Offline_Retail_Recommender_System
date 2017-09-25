
import pickle
import pandas as pd
import sys
import hdbscan
from collections import Counter
from scipy.spatial import distance
import psutil
import sys

from RecommenderSystem.Clustering.ClusterDistance import DistanceMetric
from RecommenderSystem.Clustering.Clusters import clusters
from RecommenderSystem.Clustering.KMeansFeatures import features

def main():


    ## create transaction of receipts

    '''
    number_of_receipts = sys.argv[1]
    features().create_dict_of_transaction(number_of_receipts)

    '''

    # To create cluster distance (Diagonal Distance Metric)

    '''
    keys_path=sys.argv[1]
    with open(keys, 'rb') as fp:
        keys = pickle.load(fp)


    dict_of_transactions_path=sys.argv[2]
    with open(dict_of_transactions_path, 'rb') as fp:
        dict_of_transactions = pickle.load(fp)



    level=sys.argv[3]

    # Distance metric
    DistanceMetric().dist_metric(keys,dict_of_transactions,level)

    '''



    # DBScan Clutering
    '''

    # Reading diagonal distance matric and transform to full diagoanal metric

    diagonal_distance_metric_path=sys.argv[1]

    with open(diagonal_distance_metric_path, 'rb') as fp:
        diagonal_distance_metric = pickle.load(fp)

    distance_metric=distance.squareform(diagonal_distance_metric)


    # Reading transaction details
    dict_of_transactions_path=sys.argv[2]

    with open(dict_of_transactions_path, 'rb') as fp:
        dict_of_transactions = pickle.load(fp)

    max_distance=sys.argv[3]
    min_neighbours=sys.argv[4]

    clusters().DBSCAN_create_clusters(distance_metric,dict_of_transactions,max_distance,min_neighbours)

    '''




    # HDBSCAN Clustering

    '''

    # Reading diagonal distance matric and transform to full diagoanal metric

     diagonal_distance_metric_path=sys.argv[1]

    with open(diagonal_distance_metric_path, 'rb') as fp:
        diagonal_distance_metric = pickle.load(fp)

    distance_metric=distance.squareform(diagonal_distance_metric)


    # Reading transaction details
    dict_of_transactions_path=sys.argv[2]

    with open(dict_of_transactions_path, 'rb') as fp:
        dict_of_transactions = pickle.load(fp)


    min_neighbours=sys.argv[3]


    clusters().HDBScan_create_clusters(distance_metric,dict_of_transactions,min_neighbours)

    '''


    # K Means feature creation
    '''

    # Reading transaction details
    dict_of_transactions_path=sys.argv[2]

    with open(dict_of_transactions_path, 'rb') as fp:
        dict_of_transactions = pickle.load(fp)

    receipts_path=sys.argv[2]

    with open(receipts_path, 'rb') as fp:
        receipts = pickle.load(fp)

    features().kmeans_features_creation(dict_of_transactions,receipts)
    '''

    # K-Means Clutering

    '''

    k_means_feature_matrix_path=sys.argv[1]

    with open(k_means_feature_matrix_path, 'rb') as fp:
        k_means_features_matrix = pickle.load(fp)

    level7=sys.argv[2]/100
    level6=sys.argv[3]/100
    level5=sys.argv[4]/100
    level4=sys.argv[5]/100
    price=sys.argv[6]/100
    number_of_products=sys.argv[7]/100



    k_means_features_matrix[:, :4235] = k_means_features_matrix[:, :4235] * level7
    k_means_features_matrix[:, 4235:5105] = k_means_features_matrix[:, 4235:5105] * level7
    k_means_features_matrix[:, 5105:5443] = k_means_features_matrix[:, 5105:5443] * level7
    k_means_features_matrix[:, 5443:5533] = k_means_features_matrix[:, 5443:5533] * level7
    k_means_features_matrix[:, 5533:5534] = k_means_features_matrix[:, 5533:5534] * price
    k_means_features_matrix[:, 5534:5535] = k_means_features_matrix[:, 5534:5535] * number_of_products

    # Reading transaction details

    dict_of_transactions_path=sys.argv[8]
    with open(dict_of_transactions_path, 'rb') as fp:
        dict_of_transactions = pickle.load(fp)

    number_of_clusters=sys.argv[9]

    clusters().calculateSilhouette(k_means_features_matrix)
    clusters().KMeansPlusPlus_create_clusters(k_means_features_matrix, dict_of_transactions, number_of_clusters)

    '''


if __name__ == '__main__':
    main()







