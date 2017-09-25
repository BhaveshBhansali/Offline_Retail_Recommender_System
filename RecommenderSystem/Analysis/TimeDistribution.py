import psycopg2
import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
import pickle
import sys


def main():
    try:
        # conn = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
        conn = psycopg2.connect(dbname='postgres', user='postgres', host='localhost', password='606902bB')
    except:
        print("I am unable to connect to the database")
    cur = conn.cursor()

    data_bts = pd.read_sql(
        "select rpa_bts from receipts where plant='1006' and rpa_dep in ('53','54') and rpa_bts_epoch>"+sys.argv[1]+" and rpa_bts_epoch<"+sys.argv[2],conn)
    print(data_bts)

    data_bts_list = []

    for index, row in data_bts.iterrows():
        query = """select extract(hour from timestamp %s)"""
        cur.execute(query, [row['rpa_bts']])
        res = cur.fetchall()

        for i in range(len(res)):
            # print(res[i][0])
            data_bts_list.append(res[i][0])

    data = np.array(data_bts_list)
    print(np.ones_like(data))
    weights = np.ones_like(data) / len(data)

    num_bins = 10
    _n, bins, _patches = plt.hist(data, num_bins, weights=weights, label=['first'])
    plt.legend()

    plt.xlabel('Time Interval')
    plt.ylabel('Distribution of Receipts in Percentage')

    plt.savefig()


if __name__ == '__main__':
    main()