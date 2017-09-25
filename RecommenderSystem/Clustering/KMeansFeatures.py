import psycopg2
import pandas as pd
import gzip
import random
import pickle
import sys

from scipy.spatial import distance
import psutil
import numpy as np
import h5py
from RecommenderSystem.Clustering.ClusterDistance import DistanceMetric
from RecommenderSystem.Clustering.Clusters import clusters




class features:


    def create_dict_of_transaction(self,number_of_receipts):

        try:
            #conn = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
            conn = psycopg2.connect(dbname='postgres',user='postgres', host='localhost', password='606902bB')
        except:
            print("I am unable to connect to the database")



        # Reading 70k Receipts
        receipts = pd.read_sql("select receipt_id from receipts where plant='1006' and rpa_dep in ('53','54') and total_number_of_distinct_products>5 order by random() limit "+ int(number_of_receipts),conn)
        receipt_list = []

        for index, row in receipts.iterrows():
            receipt_list.append(row['receipt_id'])

        # Saving Recceipt List
        with open('receipts.p', 'wb') as fp:
            pickle.dump(receipt_list, fp)

        receipts_tuple = tuple(receipt_list)

        df1 = pd.read_sql('select a.rpa_tsa,a.total_number_of_products,b.material,c.level7_value,c.level6_value,c.level5_value,c.level4_value,a.receipt_id from receipts a,receipt_articles b,category_core c where a.receipt_id in ' + str(receipts_tuple) + ' and a.receipt_id=b.receipt_id and b.matl_group=c.level7_value', conn)
        gp = df1.groupby(by=['receipt_id'], sort=False, group_keys=False, squeeze=True)

        dict_of_transactions = {}
        price_list=[]
        no_of_products_list=[]

        DistanceMetric().create_dict_of_transactions(dict_of_transactions,price_list,no_of_products_list,gp)

        # saving transaction, price list and no of product list for analysis
        with open('./dict_of_transactions.p', 'wb') as fp:
            pickle.dump(dict_of_transactions, fp)

        with open('./price_list.p', 'wb') as fp:
            pickle.dump(price_list, fp)

        with open('./no_of_products_list.p', 'wb') as fp:
            pickle.dump(no_of_products_list, fp)



    def kmeans_features_creation(self, dict_of_transactions, receipts):

        try:
            #conn = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
            conn = psycopg2.connect(dbname='postgres',user='postgres', host='localhost', password='606902bB')
        except:
            print("I am unable to connect to the database")


        # Category level7

        cat7_list=DistanceMetric().find_cat_list("select distinct level7_value from category_core",7,conn)
        print(len(cat7_list))

        # Category level6

        cat6_list = DistanceMetric().find_cat_list("select distinct level6_value from category_core",6, conn)
        print(len(cat6_list))

        # Category level5

        cat5_list = DistanceMetric().find_cat_list("select distinct level5_value from category_core",5,conn)
        print(len(cat5_list))

        # Category level4
        cat4_list = DistanceMetric().find_cat_list("select distinct level4_value from category_core",4, conn)
        print(len(cat4_list))





        k_means_features_matrix = np.empty((70000, 5535))


        #Normalization values from graphs
        productNormalization=70
        priceNormalization=200


        # Creating k-means feature vector matrix
        count_append=0
        # Creating distribution vector for each receipt for categories 7,6 and 5
        for key in receipts:
            k_means_features = []
            k_means_features=DistanceMetric().create_distribution_vector(cat7_list, key, dict_of_transactions,0)+DistanceMetric().create_distribution_vector(cat6_list, key, dict_of_transactions,1)+DistanceMetric().create_distribution_vector(cat5_list, key, dict_of_transactions,2)+DistanceMetric().create_distribution_vector(cat4_list, key, dict_of_transactions,3)+[abs(key[5]/priceNormalization)]+[abs(key[10]/productNormalization)]

            print(len(k_means_features))

            k_means_features_matrix[count_append]=k_means_features

            count_append+=1


        del dict_of_transactions

        with open('./k_means_features_posPriceNoOfProd.p', 'wb') as fp:
            pickle.dump(k_means_features_matrix, fp, protocol=4)

















