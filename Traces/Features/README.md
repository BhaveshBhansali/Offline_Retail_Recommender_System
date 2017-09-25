# This package consists of following files:

1. ## TimeFeatures.py

    This file creates features (time to reach cash counter) based on customer's initial movement in the store.
    
    The file is executed in following way:

    ./Features/> pyhton TimeFeatures.py ./file_location ./file_name 10 department_name
     
     
    Input:
    
        1st argument: file location to store features
        
        2nd argument: file name of features
        
        3rd argument: minutes of information from initial movement of trace i.e. 5 or 10 or other ineteger value/ 'None' 
        
        4th argument: feature value (department_name or shelf_name or shelfmeter_id)
        
    Output:
    
        This script generates file_name.csv file consisting of features. Each line in the file is a feature and labels from one trace. 
    
    Feature and label looks likes as below:

    if feature_length!='None' or feature_length==some_integer_value
    
        dept1, dept1, dept4, dept9, dept2,......................................................................., 720
        
        Suppose above feature has n values. n-1 values indicate where customer moved and nth value indicates seconds to reach cash counter (720 seconds to reach cash counter).
        
    
    if feature_length=='None' or feature_length!=some_integer_value
        
        n-1 features would be created for above example as below:
        
        dept1, 1450
        
        dept1, dept1, 1449
        
        dept1, dept1, dept4, 1448
        
        dept1, dept1, dept4, dept9, 1447
        
        .
        
        .
        
        .
        
        dept1, dept1, dept4, dept9, dept2,......................................................................., 720
        

        
        
        
    
        
    

2. ## CountFeatures.py

    This file creates features (of deptartment, shelf_name and shelfmeter counts from trace data) based on customer's initial movement in the store. These features indicate
    where and how customer moved in the store. It also indicates how much time customer spent during initial movement in the store.
    

    The file is executed in following way:

    ./Features/> pyhton CountFeatures.py ./file_location ./file_name 10 department_name
     
     
    Input:
    
        1st argument: file location to store features
        
        2nd argument: file name of features
        
        3rd argument: minutes of information from initial movement of trace i.e. 5 or 10 or other ineteger value/ 'None' 
        
        4th argument: feature value (department_name or shelf_name or shelfmeter_id)
        
    Output:
    
        This script generates file_name.csv file consisting of features. Each line in the file is a feature.
        
        
    Feature looks likes as below:
    
    Suppose store has 10 departments. After 5 minutes customer moved in following way:
    
    [120, 0, 0, 0, 60, 60, 0, 0, 0, 60]-> indicates in 5 minutes in the store, customer was at dept1 for 120 seconds, dept5 for 60 seconds and so forth.