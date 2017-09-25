# This package consists of following files:

1. ## GlobusProductDistribution.py: 

    This script computes distribution of products from 50k random receipts. 
    The distribution includes total number of products, total number of unique products, total products for which shelfmeter information is available, 
    total unique products for which shelfmeter information is available, and distribution of products (categorical wise) for which shelfmeter information is not available.
    
    
    This script can be run by following command:
    
    script folder> python GlobusProductDistribution.py
    
    
    
    This script produces following files:
    
        globus_product.txt: write Total Number of products and Total Number of Unique Products from 50k receipts into this file. It also writes Total Number of products available and Total Number of Unique products available 
        from globus_product table.
    
        It also produces category level products information (Total Number of products, Total Number of Unique Products, Total Number of products available, Total Number of Unique products available 
        from globus_product) into level7_cat.txt, level6_cat.txt, level5_cat.txt, level4_cat.txt, level3_cat.txt, level2_cat.txt, level1_cat.txt files. 


2. ## PriceAndNoOfProductNormalization.py
    
    This script generates graphs (histograms) showing distribution of price and total number of products from random receipts.

    Input: 
    
        1st argument: list of number of products from set of receipts i.e. list of number of products from 5 receipts [4, 10, 6, 15, 12]. 
        
        or
        
        1st argument: list of price of receipts from set of receipts i.e. price of receipts from 5 receipts [10.50, 72.1, 3.89, 15.23, 23.75].
    
    
    output: 
    
        histogram of distribution of number of products from list (of number of products from receipts).
        
        or
        
        histogram of distribution of price from list (of total price from receipts).

    
    This script can be run by following command:
    
    script folder> python PriceAndNoOfProductNormalization.py file path/no_of_products_list.p
    
    script folder> python PriceAndNoOfProductNormalization.py file path/price_list.p
    
    
    
3. ## TimeDistribution.py

    This script generates graph (histogram) showing distribution of receipt's payment time from random receipts between specified time interval.
    
    It extracts "hours" information from timestamp (hours:minutes:seconds) and plots histogram distribution of it.
    
    Input: 
    
        1st argument: start epoch time
        
        2nd argument: end eppoch time
    
    
    output: histogram of distribution of time information (in hours) for random receipts.    
    
    
    
    This script can be run by following command:
    
    script folder> python TimeDistribution.py 1420167600 1420282800

    