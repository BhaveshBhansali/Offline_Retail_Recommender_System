# This package consists of following files:

1. ## Trace_cluster_label_mapping.py

    This script is used to assign cluster labels corresponding traces. The trace-cluster_label is used to build a classification model.
    
    
    The script can be run as following:
    
    ./Classification/> Trace_cluster_label_mapping.py cluster_centers.p receipt_list.p trace_list.p
    
    
    Input:
    
        1st argument: cluster centers computed from clustering
        
        2nd argument: receipt list (receipts from one-to-one trace-receipt pairs from mapping)
        
        3rd argument: trace list (traces with one-to-one trace-receipt pairs from mapping)
        
    
    Output:
    
        list of cluster labels corresponding traces (one cluster label corresponding each trace)
        
        

2. ## Classifier.py

    This file consists of different methods (algorithms) for training and evaluating classifier models.
    
    
    ### NearestNeighboursClassifierModel 
    
    This function is used to build a classification model using K Nearest Neighbour classifier.
    
    
    It can be executed as following:
    
    ./Classification/> main.py ./trace_features_matrix.p ./trace_cluster_label_matrix 20 5
    
    
    Above command calls below function:
    
    classifiers().KNearestNeighboursClassifierModel(trace_features_matrix,trace_cluster_label_matrix,test_split, neighbours)
    
    
    
    Input: 
    
    
        1st argument: trace features in 2d numpy array 
        
        2nd argument: cluster labels in 1d numpy array
        
        3rd argument: percentage to split training data (% signifies test data and rest is train data)
        
        4th argument: neighbours to find majority class for classification
        
    
    Output:
    
        Prints accuracy of the model on test data
        
        
        
    ### SVM_Classifier
    
    This function is used to build a classification model using Support Vector Machines classifier.
    
    
    It can be executed as following:
    
    ./Classification/> main.py ./trace_features_matrix.p ./trace_cluster_label_matrix 20 10
    
    
    Above command calls below function:
    
    classifiers().SVM_Classifier(trace_features_matrix, trace_cluster_label_matrix, test_split,penalty)
    
    
    
    Input: 
    
    
        1st argument: trace features in 2d numpy array 
        
        2nd argument: cluster labels in 1d numpy array
        
        3rd argument: percentage to split training data (% signifies test data and rest is train data)
        
        4th argument: Penalty parameter C of the error term 
        
    
    Output:
    
        Prints accuracy of the model on test data        
        
