import psycopg2
import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
import pickle
import sys


def main():
    '''
    try:
        # conn = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
        conn = psycopg2.connect(dbname='postgres', user='postgres', host='localhost', password='606902bB')
    except:
        print("I am unable to connect to the database")

    data_tsa = pd.read_sql("select rpa_tsa from receipts where plant='1006' and rpa_dep in ('53','54') order by random() LIMIT 70000",conn)

    data_tsa_list=[]

    for index, row in data_tsa.iterrows():
        data_tsa_list.append(row['rpa_tsa'])

    '''

    input_path=sys.argv[1]

    with open(input_path, 'rb') as fp:
        data_tsa_list = pickle.load(fp)


    data = np.array(data_tsa_list)
    print(np.ones_like(data))
    weights = np.ones_like(data) / len(data)

    num_bins = 6
    _n, bins, _patches = plt.hist(data, num_bins, weights=weights, label=['first'])
    plt.legend()

    plt.xlabel('Total Number of Products')
    plt.ylabel('Distribution in Percentage')


    plt.savefig()

if __name__ == '__main__':
     main()