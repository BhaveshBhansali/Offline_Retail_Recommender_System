import psycopg2
import pandas as pd
from collections import Counter
import sys


def count_features(conn,file_location,file_name,feature_length,feature_name):


    output_file = open(file_location + file_name + '.csv', mode='a')

    feature_name_list_index=[]

    if feature_name=='department_name':

        dept_name_list=pd.read_sql("select name from globus_department",conn)
        for index,row in dept_name_list.iterrows():
            feature_name_list_index.append(row['name'])

    elif feature_name=='shelfmeter_id':

        shelfmeter_id_list=pd.read_sql("select id from globus_shelfmeter",conn)
        for index, row in shelfmeter_id_list.iterrows():
            feature_name_list_index.append(row['id'])

    elif feature_name=='shelf_name':
        shelfpart_name_list=pd.read_sql("select name from globus_shelfpart where name!=''",conn)
        for index, row in shelfpart_name_list.iterrows():
            feature_name_list_index.append(row['name'])
        feature_name_list_index.append('')

    feature_name_list_index.append(0)

    feature_length = int(feature_length) * 60




    #trace_id = pd.read_sql("select trace_id from traces_meta order by random() limit 2", conn)
    trace_id = pd.read_sql("select trace_id from traces_meta", conn)


    query = "select " + feature_name + " from enriched_locations where trace_id=%s order by epoch_time";


    for index, row in trace_id.iterrows():

        cur_feature=[0]*len(feature_name_list_index)

        res = pd.read_sql(query, conn, params=[int(row['trace_id'])])
        res = res.fillna(0)
        print(index)
        print(str(row['trace_id']))

        trace_feature_list=[]

        for index, row in res.iterrows():
            trace_feature_list.append(row[feature_name])

        if len(trace_feature_list) >= feature_length:

            dept_count=Counter(trace_feature_list[:feature_length])

            for k,v in dept_count.items():
                cur_feature[feature_name_list_index.index(k)]=v

            #print(cur_feature)
            #print(len(cur_feature))

            cur_feature_str=''
            for i in range(len(cur_feature)-1):
                cur_feature_str+=str(cur_feature[i])+','
            cur_feature_str=cur_feature_str+str(cur_feature[-1])

            output_file.write(str(cur_feature_str))
            output_file.write('\n')




def main():

    try:
        conn = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    count_features(conn,sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4])




if __name__ == '__main__':
    main()