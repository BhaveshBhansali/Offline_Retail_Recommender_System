import pandas as pd
import pickle
import psycopg2


def main():

    try:
        conn = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    cur = conn.cursor()

    
    # For receipts from trace-receipt pairs
    receipts=pd.read_sql("select receipt_id,traceid from bonmatchings group by receipt_id having count(receipt_id)=1 order by receipt_id",conn)

    receipt_list = []
    traceid_list = []
    for index, row in receipts.iterrows():
        receipt_list.append(row['receipt_id'])
        traceid_list.append(row['traceid'])

    # Saving Recceipts and Trace Id Lists from trace-receipt pairs
    with open('./receipts_list.p', 'wb') as fp:
        pickle.dump(receipt_list, fp)


    with open('./trace_list.p', 'wb') as fp:
        pickle.dump(receipt_list, fp)


if __name__ == '__main__':
    main()