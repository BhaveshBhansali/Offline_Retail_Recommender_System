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

class DistanceMetric:


    # function to create distribution of category products

    def create_distribution_vector(self,cat_list,rec_no,dict_of_transactions,index):

        count_cat_level_dict = {}
        distribution_cat_level_dict = {}
        no_of_items_in_transaction_without_minus1_group = 0

        # For each material of receipt, counting number of materials in appeared category
        for material in dict_of_transactions[rec_no]:

            # Ignoring the impact of materials of category '-1
            if dict_of_transactions[rec_no][material][index]!='-1':
                if dict_of_transactions[rec_no][material][index] not in count_cat_level_dict.keys():
                    count_cat_level_dict[dict_of_transactions[rec_no][material][index]] = 1
                    no_of_items_in_transaction_without_minus1_group+=1
                else:
                    count_cat_level_dict[dict_of_transactions[rec_no][material][index]] += 1
                    no_of_items_in_transaction_without_minus1_group += 1

        # calculating distribution(in %) of each appeared category in receipt
        '''
        for key in count_cat1_level_dict.keys():
            distribution_cat1_level_dict[key] = round((count_cat1_level_dict[key] / len(dict_of_transactions[key1])),2)

        '''

        for key in count_cat_level_dict.keys():
            distribution_cat_level_dict[key] = round((count_cat_level_dict[key] /no_of_items_in_transaction_without_minus1_group), 2)


        #Creating null vector
        level_cat_vector_list = [0] * len(cat_list)

        # Updating default distribution vector for receipt
        for key in distribution_cat_level_dict.keys():
            level_cat_vector_list[cat_list.index(key)] = distribution_cat_level_dict[key]



        return level_cat_vector_list


    def create_material_list(self,dict_of_transactions,receipt_matl_dict,key1):

        value_mat1_list = []

        for key_transaction1 in dict_of_transactions[key1]:
            value_mat1_list.append(key_transaction1)
        receipt_matl_dict[key1] = value_mat1_list

        del value_mat1_list


    def find_cat_list(self, query,level, conn):

        cat_list = []
        cat = pd.read_sql(query, conn)

        for index, row in cat.iterrows():
            cat_list.append(row['level'+str(level)+'_value'])

        return cat_list



    def jaccard(self,val1, val2):
        return round(1 - (len(val1.intersection(val2)) / len(val1.union(val2))), 2)


    def create_dict_of_transactions(self,dict_of_transactions,price_list,no_of_products_list,gp):

        for name, group in gp:
            df = pd.DataFrame(data=group)

            setOfTransactions = {}

            materials = df['material'].values

            level7_value = df['level7_value'].values

            level6_value = df['level6_value'].values
            level5_value = df['level5_value'].values
            level4_value = df['level4_value'].values


            # filtering materils from leergut and tasche metl_grps
            for i in range(len(materials)):
                if materials[i] not in setOfTransactions.keys() and level7_value[i] not in ['714W0020', '850W0200', '850W9600','851W0100', '851W9600', '852W0100','852W0200', '852W9600']:
                    setOfTransactions[materials[i]] = list([level7_value[i], level6_value[i], level5_value[i],level4_value[i]])

            # Storing receipte with minimum of 6 materials
            dict_of_transactions.update({name: setOfTransactions})

            price_list.append(df['rpa_tsa'].unique())
            no_of_products_list.append(df['total_number_of_products'].unique())



    def  create_diagonal_distance_metric(self,cat_list,keys,dict_of_transactions,index,level):

        level_cat_vector_matrix = []

        for key in keys:
            level_cat_vector_matrix.append(self.create_distribution_vector(cat_list, key, dict_of_transactions,index))


        level_pair_distance = distance.pdist(level_cat_vector_matrix, metric='minkowski', p=1).astype('float16')

        del level_cat_vector_matrix

        with open('./diagonalDistanceMatrixCategory'+str(level)+'JAC_MINKOWSKI_70000.p', 'wb') as fp:
            pickle.dump(level_pair_distance, fp, protocol=4)


    def dist_metric(self,keys,dict_of_transactions,level):


        try:
            #conn = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
            conn = psycopg2.connect(dbname='postgres',user='postgres', host='localhost', password='606902bB')
        except:
            print("I am unable to connect to the database")
        '''
        data=pd.read_sql("select a.rpa_tnr,a.rpa_bdd,a.rpa_dep,a.plant,a.rpa_wid,a.rpa_tsa,a.rpa_trnov,a.rpa_lnecnt,a.rpa_bts,a.umsatzdatum,a.total_number_of_products,a.total_number_of_distinct_products,b.material,c.level7_value,c.level6_value,c.level5_value,c.level4_value from receipts a,receipt_articles b,category_core c where a.receipt_id=b.receipt_id and b.matl_group=c.level7_value and a.plant='1006' and rpa_dep in ('53','54')",conn)

        #print(len(data))
        #print(data)
        #print(data['level6_value'])
        '''


        # Category level7

        if level==7:

            cat_list=self.find_cat_list("select distinct level7_value from category_core",7,conn)
            index=0


        # Category level6
        elif level==6:

            cat_list = self.find_cat_list("select distinct level6_value from category_core",6, conn)
            index = 1


        # Category level5

        elif level==5:
            cat_list = self.find_cat_list("select distinct level5_value from category_core",5,conn)
            index=2


        # Category level4
        elif level==4:
            cat_list = self.find_cat_list("select distinct level4_value from category_core",4, conn)
            index=3



        '''
        # Grouping data by transaction nr and booking date
        gp = data.groupby(by=['rpa_tnr', 'rpa_bdd','plant','rpa_dep','rpa_wid','rpa_tsa','rpa_trnov','rpa_lnecnt','rpa_bts','umsatzdatum','total_number_of_products','total_number_of_distinct_products'], sort=False, group_keys=False, squeeze=True)

        # Creating dictionary of transactions
        dict_of_transactions = dict()
        self.create_dict_of_transactions(dict_of_transactions,gp)
        '''
        '''

        # Storing details of receipts, can be used later for inspecting cluster quality
        with open('./transactionAsSetofMaterialsMtl_grpCat7654.p', 'wb') as fp:
            pickle.dump(dict_of_transactions, fp)
        '''




        '''
        number_groups = len(dict_of_transactions)
        print(number_groups)
        receipts_bdd = list(dict_of_transactions.keys())

        keys = random.sample(receipts_bdd, 70000)
        print(keys)

        del receipts_bdd
        del number_groups

        # Storing details of receipts, can be used later for inspecting cluster quality
        with open('./keys_70000.p', 'wb') as fp:
            pickle.dump(keys, fp)

        '''

        self.create_diagonal_distance_metric(cat_list,keys,dict_of_transactions,index,level)


