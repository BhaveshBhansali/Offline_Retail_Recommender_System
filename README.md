# Retail Data Analyzer: 
This project is a collection of retail data specific scripts. It consist of work related to product recommendation based on customer trajectories, checkout database creation and data insertion, and trace features.

## Packages:

1. ### Database: 
    This package consists of sub-packages related to schema creation and checkout data insertion in checkout database.

    #### 1.1 CheckoutDataInsertion: 
    This package consists of scripts for data insertion.
    
    
    https://github.com/BhaveshBhansali/Retail-Analysis/blob/master/Database/CheckoutDataInsertion/README.md
    
    
    #### 1.2 Database Table Creation Queries.txt:
    This file contains table creation commands for checkout, product meta, and product hierarchy data.   

2. ### RecommenderSystem: 

    The workflow of our approach to build an offline recommendation system is as follows: We cluster receipts
to find types of customers. Then, we map the receipts to traces i.e. find receipts to
a customer journey. Once we have a trace-receipt mapping, we assign a cluster label
to a trace based on the distance between receipt and the cluster centers. Thereafter,
we train a classification model with the trace information as features and cluster as
labels. After training a model, when a new customer comes in for shopping, our trained
classification model classifies him/her to a type of customer (cluster label). Once we
have a cluster (of similar receipts/customers) the new customer belongs to, we run an
Apriori algorithm 9 on cluster receipts to get top
frequent products. Finally, we recommend products (top frequent products) from the cluster to customer if the customer has not moved into the path where these products
are kept in the store.

    
    This package consists of sub-packages related to different machine learning techniques and product analysis for recommender systems.

    #### 2.1 Analysis: 
    This package consists of scripts related to different analysis of product distribution. 
    
    
    http://github.com/BhaveshBhansali/Retail-Analysis/blob/master/RecommenderSystem/Analysis/README.md

    #### 2.2 Clustering: 
    This package consists of scripts related to distance metric, feature creation for clustering techniques. This package also includes script consisting of different methods used for clustering.
    
    
    http://github.com/BhaveshBhansali/Retail-Analysis/blob/master/RecommenderSystem/Clustering/README.md
    
    #### 2.3 Classification: 
    This package consists of scripts related to feature creation for classification techniques. This package also includes script consisting different methods used for classification.
    
    
    http://github.com/BhaveshBhansali/Retail-Analysis/blob/master/RecommenderSystem/Classification/README.md
    
    #### 2.4 Mapping: 
    
    We need to map each trace to their corresponding
customer type for building the classification model to predict the customer type based
on the customer's movement. Each trace could be mapped to a customer type based
the on receipt generated after each trace. Therefore, there has to be a mapping between
traces and receipts since both come from different data sources. The trace data is
anonymous, i.e. there is no information which receipt is linked to a particular trace.


    
    Trace-receipt mapping is achieved in following three phases:
    
    (Phase 1) Approximate Payment Time and Cash Desk of Traces
    
    (Phase 2) Approximate Candidate Receipts for Traces based on its Payment Time and Cash Desk
    
    (Phase 3) Final Trace-Receipt Mapping from Candidate Trace-Receipts Pairs
    
    
    This package consists of scripts related to 3rd phase (Phase 3) of trace-receipts mapping.
    
    
    http://github.com/BhaveshBhansali/Retail-Analysis/blob/master/RecommenderSystem/Mapping/README.md

3. ### Traces: 
    This package consists of sub-packages related to features based on trace data.

    #### 3.1 Features: 
    This package consists of scripts related to feature creation based on trace data.
    
    
    http://github.com/BhaveshBhansali/Retail-Analysis/blob/master/Traces/Features/README.md
    
4. ### Prediction (Mapping Proje)  
    This Jave project is to map shopping cart traces with their receipts.

    #### 4.1 Prediction\src\de\dfki\irl\darby\prediction\matching\Matching.java 
    
    (Phase 1) Approximate Payment Time and Cash Desk of Traces
    
    The Matching.java file contains a main function which calls other functions to approximate cash counter and payment time after shopping trips (traces). The payment time and cash 
    counters are computed based on when customer is nearby cash counter area for payment. The resultant data is stored in the database table "traceinfo".
    
    #### 4.2 Prediction\src\de\dfki\irl\darby\prediction\matching\bonmatching\BonMatcher.java 
    
    (Phase 2) Approximate Candidate Receipts for Traces based on its Payment Time and Cash Desk
    
    The BonMatcher.java contains a main function which calls other functions to find candidate receipts for the traces. Due to inaccuracies of the positioning system, receipts at nearby
    cash counters and payment are considered as probable final trace-receipt mapping. The resultant data is stored in the database table "boninfos".
    
## Requirements
    
    python- version 3.4.4
    numpy- version 1.11.2
    java- java 7 (for matching/prediction)
        
