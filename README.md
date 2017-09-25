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
        
