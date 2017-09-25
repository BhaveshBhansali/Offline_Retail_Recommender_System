# This package consists of following files:



1. ## matching_algorithm.py (Find best candidate receipt for traces)

    This file contains implementation of the algorthm to find best candidate receipt (Phase 3).
    
     Following are the aspects considered to filter candidate receipts and find the best receipt mapping for each trace:
     
        (i) comparison between time spent by customer in the store and the number of purchased products from store, 
     
        (ii) comparison between time spent by customer upstairs in the store and the number of purchased products from upstairs, 
     
        (iii) if customer does not move upstairs, but receipt has products from upstairs, 
     
        (iv) distance between the receipt's products' location and customer movement.
    
    
    Input: 
    
        1st argument (epoch time): traces start date
        
        2nd argument (epohc time): traces end date
        
    Output:
    
        trace-receipt mapping in "Bonmatching" table.


2. ## trace_receipt_pairs.py

     This scripts used to get one-to-one trace-receipt pairs from "Bonmatching" table.
     
     This script can be executed as following:
     
    ./Mapping/>trace_receipt_pairs.py 
     
    
    Output:
    
        1. trace_list.p: This script generates list of traces with one-to-one trace-receipt pairs after all three phases of mapping.
        
        2. receipts_list.p: This script generates list of receipts with one-to-one trace-receipt pairs after all three phases of mapping.
    
    
    
     
     

