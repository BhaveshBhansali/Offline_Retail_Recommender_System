import pickle
import pandas as pd
from RecommenderSystem.Clustering.ClusterDistance import DistanceMetric
import numpy as np
import sys
import psycopg2
from sklearn import preprocessing


def main():

    try:

        conn = psycopg2.connect(dbname='postgres', user='postgres', host='localhost', password='606902bB')
    except:
        print("I am unable to connect to the database")

    cluster_centers=sys.argv[1]
    receipt_list=sys.argv[2]
    trace_list=sys.argv[3]

    receipts_tuple = tuple(receipt_list)

    df1 = pd.read_sql('select a.rpa_tsa,a.total_number_of_products,b.material,c.level7_value,c.level6_value,c.level5_value,c.level4_value,a.receipt_id from receipts a,receipt_articles b,category_core c where a.receipt_id in ' + str(receipts_tuple) + ' and a.receipt_id=b.receipt_id and b.matl_group=c.level7_value', conn)
    gp = df1.groupby(by=['receipt_id'], sort=False, group_keys=False, squeeze=True)

    dict_of_transactions = {}
    price_list = []
    no_of_products_list = []

    DistanceMetric().create_dict_of_transactions(dict_of_transactions, price_list, no_of_products_list, gp)



    # Category level7

    cat7_list = DistanceMetric().find_cat_list("select distinct level7_value from category_core", 7, conn)
    print(len(cat7_list))

    # Category level6

    cat6_list = DistanceMetric().find_cat_list("select distinct level6_value from category_core", 6, conn)
    print(len(cat6_list))

    # Category level5

    cat5_list = DistanceMetric().find_cat_list("select distinct level5_value from category_core", 5, conn)
    print(len(cat5_list))

    # Category level4
    cat4_list = DistanceMetric().find_cat_list("select distinct level4_value from category_core", 4, conn)
    print(len(cat4_list))

    # Normalization values from graphs
    productNormalization = 80
    priceNormalization = 230


    features = np.empty((5535,))
    labels_list = []

    for i in range(len(receipt_list)):
        print(i)

        features_list = []
        features_list = DistanceMetric().create_distribution_vector(cat7_list, receipt_list[i], dict_of_transactions, 0) + DistanceMetric().create_distribution_vector(cat6_list, receipt_list[i],dict_of_transactions,1) + DistanceMetric().create_distribution_vector(
            cat5_list, receipt_list[i], dict_of_transactions, 2) + DistanceMetric().create_distribution_vector(cat4_list, receipt_list[i], dict_of_transactions, 3) + [abs(price_list[i] / priceNormalization)] + [abs(no_of_products_list[i] / productNormalization)]

        print(len(features_list))


        # For finding cluster labels for traces
        #print(len(features_list))

        features = features_list

        print(features)
        for index, item in enumerate(features):
            features[index] = float(item)
        print(type(features))


        for m in range(5535):
            if m<4235:
                features[m]=features[m]*.1
            elif m<5105:
                features[m]=features[m]*.1
            elif m<5443:
                features[m]=features[m]*.2
            elif m<5533:
                features[m]=features[m]*.3
            elif m<5534:
                features[m]=features[m]*.1
            elif m<5535:
                features[m]=features[m]*.1

        print(features)

        label_pos=0
        min_value=100000
        for j in range(len(cluster_centers)):
            #print(features)

            diff=features-cluster_centers[j]

            sum1 = 0
            for k in range(len(diff)):
                sum1 = sum1 + diff[k]
            print(sum1)

            if(abs(sum1)<min_value):
                min_value=abs(sum1)
                label_pos=j

        labels_list.append([label_pos])

        print('--------------------')

    print(len(labels_list))

    classifier_labesl_matrix=np.array(labels_list)

    with open('./trace_cluter_label_matrix.p', 'wb') as fp:
        pickle.dump(classifier_labesl_matrix, fp, protocol=4)

    


if __name__ == '__main__':
    main()