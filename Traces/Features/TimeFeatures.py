import pandas as pd
import psycopg2
import numpy as np
import sys



def checkCashoneZone(x,y):

    '''
    function checks whether coordinates x and y are in cash zone area or not

    :param x: smoothed_lon field value of trace
    :param y: smoothed_lat field value of trace
    :return: boolean value True if coordinates are in cash zone otherwise False
    '''

    if (89.36 <=x <=92.35 and 55.7 <= y <= 79):
        return True
    elif (93.58 <=x <=96.25 and 13.5 <= y <= 46.6):
        return True
    else:
        return False


def trace_featureNN(conn,file_location,filename,feature_length,feature_name,number_of_traces):

    '''

    function writes time prediction features from traces based on cash zone area:
        1. Extract subset of features with feature_length=None
            1.1. returns fetures with each feature's first '24389' columns as features and next 1 column as label
        2. Extract features with feature_length = some integer value
            2.1 returns features with each feature's first 'feature_length' as features and next 1 column as label


    :param conn: database connection
    :param feature_length: 'None' for subset of features from trace and integer_value (i.e. 300 for 5 minutes traces)
                            for extracting integer number of information from trace
    :param filename: file name to save features
    :param number_of_traces: number of traces to extract features
    :return: file with set of features
    '''
    feature_name=str(feature_name)
    feature_length=int(feature_length)*60

    #trace_id = pd.read_sql("select trace_id from traces_meta order by random() limit "+str(number_of_traces), conn)

    trace_id = pd.read_sql("select trace_id from traces_meta" , conn)
    query = "select "+feature_name+",smoothed_lon,smoothed_lat from enriched_locations where trace_id=%s order by epoch_time";

    features = []
    feature_labels=[]

    max_trace_length=24389

    output_file=open(file_location+filename+'.csv',mode='a')

    # Each trace computation
    for index, row in trace_id.iterrows():
        cash_zone=False

        res = pd.read_sql(query, conn, params=[int(row['trace_id'])])
        res = res.fillna(0)
        print(index)
        print(str(row['trace_id']))
        current_feature = []

        # Find out the cash zone and mark that point by indentifier -1
        for index, row in res.iterrows():
            if checkCashoneZone(row['smoothed_lon'], row['smoothed_lat']) == False:
                current_feature.append(row[feature_name])
            else:
                current_feature.append(-1)
                cash_zone=True
                break



        if cash_zone==True and len(current_feature)>=feature_length:
            # Find position of cash zone coordinate in trace
            cash_zone_index=current_feature.index(-1)


            # create features and labels
            if feature_length==None:
                for i in range(cash_zone_index+1):
                    new_time_prediction_feature = []
                    for j in range(i+1):
                        new_time_prediction_feature.append(current_feature[j])

                    time_prediction_label=cash_zone_index+1-len(new_time_prediction_feature)

                    padd_length = max_trace_length - len(new_time_prediction_feature)
                    new_time_prediction_feature = new_time_prediction_feature + [-2] * padd_length


                    new_time_prediction_feature_str=''
                    for k in range(len(new_time_prediction_feature)):
                        new_time_prediction_feature_str+=str(new_time_prediction_feature[k])+','

                    new_time_prediction_feature_str+=str(time_prediction_label)

                    output_file.write(str(new_time_prediction_feature_str))
                    output_file.write('\n')
            else:


                new_time_prediction_feature_str = ''
                for j in range(feature_length):
                    new_time_prediction_feature_str += str(current_feature[j]) + ','

                time_prediction_label =  cash_zone_index+1-feature_length


                new_time_prediction_feature_str += str(time_prediction_label)

                output_file.write(str(new_time_prediction_feature_str))
                output_file.write('\n')


        '''
        features.append(new_time_prediction_feature)
        feature_labels.append([time_prediction_label])
        '''


        '''
        numpy_features = np.array(features)
        numpy_features_labels = np.array(feature_labels)

        print(numpy_features.shape)
        print(numpy_features_labels.shape)
        '''

def main():

    try:
        conn = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    cur = conn.cursor()


    # Second paramter for fixed size of features (5 minutes: 300) and None for diffrent set of features from trace
    trace_featureNN(conn,sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],4)


if __name__ == '__main__':
    main()