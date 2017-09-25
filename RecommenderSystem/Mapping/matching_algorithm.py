import psycopg2
import pandas as pd
import pickle
import numpy as np
import time
import operator
import matplotlib.pyplot as plt
from collections import Counter, OrderedDict
import sys
'''

def check_trace_move_upstairs(trace_coordinates):


    escalator_up_time = 0
    escalator_down_time = 0



    escalator_nearby = False

    start_time = 0

    up_nearby_count = 0
    up_stairs_count = 0

    down_nearby_count = 0
    down_stairs_count = 0


    up_down_flag=False
    up_flag=False

    upper = True
    down = False

    time_spent_on_upper_area=0

    for index, row in trace_coordinates.iterrows():

        if escalator_nearby is False:
            if escalator_nearby_area(row['lon'], row['lat']) == True and upper == True:
            #if escalator_upper_area(row['lon'], row['lat']) == 2 and upper is False:
                escalator_nearby = True
                start_time = row['epoch_time']
                escalator_up_time = row['epoch_time'] / 60 + (40 / 60)
                upper = True

            elif escalator_nearby_area(row['lon'], row['lat']) == True and upper == False:
            #elif escalator_down_area(row['lon'], row['lat']) == 2 and upper is True and down is False:
                escalator_nearby = True
                start_time = row['epoch_time']
                escalator_down_time = row['epoch_time'] / 60
                down = True

        elif escalator_nearby is True and row['epoch_time'] <= start_time + 40 and upper == True and down == False:

            if escalator_upper_area(row['lon'], row['lat']) == 2:
                up_nearby_count += 1
                up_stairs_count += 1
            elif escalator_upper_area(row['lon'], row['lat']) == 1:
                up_nearby_count += 1

        elif escalator_nearby is True and row['epoch_time'] <= start_time + 40 and upper == False and down == True:

            if escalator_down_area(row['lon'], row['lat']) == 2:
                down_nearby_count += 1
                down_stairs_count += 1

            elif escalator_down_area(row['lon'], row['lat']) == 1:
                down_nearby_count += 1

        if row['epoch_time'] >= start_time + 40 and upper == True and escalator_nearby==True:
            escalator_nearby = False
            upper=False

        if row['epoch_time'] >= start_time + 40 and down == True and escalator_nearby==True:
            escalator_nearby = False
            down=False
            upper=True
            time_spent_on_upper_area += (escalator_down_time - escalator_up_time)


    #print(up_nearby_count)
    #print(up_stairs_count)
    #print(down_nearby_count)
    #print(down_stairs_count)



    # Calculate time spent on upper area
    time_spent_on_upper_area = (escalator_down_time - escalator_up_time)


    if up_stairs_count>0:
        up_flag=True


    if time_spent_on_upper_area > 0:
        print("upper area: " + str(time_spent_on_upper_area))
        up_down_flag=True
    else:
        print('no upper area')
        up_down_flag=False

    return time_spent_on_upper_area,up_down_flag


'''


def save_pickle(data, filename):
    with open('./' + filename + '.p', 'wb') as fp:
        pickle.dump(data, fp)


def shelfmeter_distance(trace_geometry, shelfmeter_geometry, cur2):
    short_distance = 10000
    for j in range(len(trace_geometry)):
        query = 'select ST_distance(%s,%s)';
        try:
            cur2.execute(query,
                         [str(trace_geometry['geometry'].values[j]), str(shelfmeter_geometry['geometry'].values[0])])
            current_short_distance = cur2.fetchall()
        except psycopg2.OperationalError:
            time.sleep(1000)
            conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',
                                     port=5432, password='LrVYI%TMT%d3')
            cur2 = conn2.cursor()
            cur2.execute(query,
                         [str(trace_geometry['geometry'].values[j]), str(shelfmeter_geometry['geometry'].values[0])])
            current_short_distance = cur2.fetchall()

        if current_short_distance[0][0] < short_distance:
            short_distance = current_short_distance[0][0]

    return short_distance


def cat_distance(cur_geometry, trace_geometry, conn2, cur2):
    short_distance = 10000
    for j in range(len(trace_geometry)):
        query = 'select ST_distance(%s,%s)';
        # print(str(trace_geometry['geometry'].values[j]))
        # print(str(category_geometry[0][0]))
        try:
            cur2.execute(query, [str(trace_geometry['geometry'].values[j]), cur_geometry])
            current_short_distance = cur2.fetchall()
        except psycopg2.OperationalError:
            time.sleep(1000)
            conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',
                                     port=5432, password='LrVYI%TMT%d3')
            cur2 = conn2.cursor()
            cur2.execute(query, [str(trace_geometry['geometry'].values[j]), cur_geometry])
            current_short_distance = cur2.fetchall()

        # print(trace_geometry['geometry'].values[j])
        # print(category_geometry[0][0])
        # print(current_short_distance[0][0])

        if current_short_distance[0][0] < short_distance:
            short_distance = current_short_distance[0][0]

    return short_distance


def category_distance(category_id, trace_geometry, conn2, cur2):
    category_geometry = pd.read_sql('select geometry from globus_department where id=' + str(category_id), conn2)

    short_distance = 10000
    for j in range(len(trace_geometry)):
        query = 'select ST_distance(%s,%s)';
        # print(str(trace_geometry['geometry'].values[j]))
        # print(str(category_geometry[0][0]))
        try:
            cur2.execute(query,
                         [str(trace_geometry['geometry'].values[j]), str(category_geometry['geometry'].values[0])])
            current_short_distance = cur2.fetchall()
        except psycopg2.OperationalError:
            time.sleep(1000)
            conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',
                                     port=5432, password='LrVYI%TMT%d3')
            cur2 = conn2.cursor()
            cur2.execute(query, [str(trace_geometry['geometry'].values[j]), str(category_geometry[0][0])])
            current_short_distance = cur2.fetchall()

        # print(trace_geometry['geometry'].values[j])
        # print(category_geometry[0][0])
        # print(current_short_distance[0][0])

        if current_short_distance[0][0] < short_distance:
            short_distance = current_short_distance[0][0]

    return short_distance


def find_category_geometry(category, cur2):
    category_id = ''

    query1 = 'select dep_id,parent_category_id from globus_productcategory where value=%s';
    query2 = 'select dep_id,parent_category_id from globus_productcategory where id=%s';

    cur2.execute(query1, [str(category['level7_value'].values[0])])
    cat7_values = cur2.fetchall()

    if cat7_values[0][0] != None:
        category_id = cat7_values[0][0]
    else:

        cur2.execute(query2, [str(cat7_values[0][1])])
        cat6_values = cur2.fetchall()

        if cat6_values[0][0] != None:
            category_id = cat6_values[0][0]
        else:

            # To check if category parent is null (for product category -1)
            if cat6_values[0][1] != None:
                cur2.execute(query2, [str(cat6_values[0][1])])
                cat5_values = cur2.fetchall()

                if cat5_values[0][0] != None:
                    category_id = cat5_values[0][0]
                else:
                    cur2.execute(query2, [str(cat5_values[0][1])])
                    cat4_values = cur2.fetchall()

                    if cat4_values[0][0] != None:
                        category_id = cat4_values[0][0]
                    else:
                        cur2.execute(query2, [str(cat4_values[0][1])])
                        cat3_values = cur2.fetchall()

                        if cat3_values[0][0] != None:
                            category_id = cat3_values[0][0]
                        else:
                            cur2.execute(query2, [str(cat3_values[0][1])])
                            cat2_values = cur2.fetchall()

                            if cat2_values[0][0] != None:
                                category_id = cat2_values[0][0]
                            else:
                                cur2.execute(query2, [str(cat2_values[0][1])])
                                cat1_values = cur2.fetchall()

                                if cat1_values[0][0] != None:
                                    category_id = cat1_values[0][0]
    return category_id


def escalator_upper_area(x, y):
    if 565 < x < 715 and 837 < y < 883:
        if 565 < x < 710 and 858 < y < 868:
            return 2
        else:
            return 1
    else:
        return 0


def escalator_down_area(x, y):
    if 565 < x < 715 and 837 < y < 883:
        if 565 < x < 710 and 842 < y < 852:
            return 2
        else:
            return 1
    else:
        return 0


def escalator_nearby_area(x, y):
    if 565 < x < 715 and 837 < y < 883:
        return True


def check_trace_move_upstairs(trace_coordinates):
    escalator_up_time = 0
    escalator_down_time = 0

    upper = False
    down = False

    escalator_nearby = False

    start_time = 0

    up_nearby_count = 0
    up_stairs_count = 0

    down_nearby_count = 0
    down_stairs_count = 0

    up_down_flag = False
    up_flag = False

    escalator_up_epoch_time = 0
    escalator_down_epoch_time = 0

    for index, row in trace_coordinates.iterrows():

        if escalator_nearby is False:

            if escalator_nearby_area(row['lon'], row['lat']) == True and upper == False:
                # if escalator_upper_area(row['lon'], row['lat']) == 2 and upper is False:
                escalator_nearby = True
                start_time = row['epoch_time']
                escalator_up_epoch_time = row['epoch_time']
                escalator_up_time = row['epoch_time'] / 60 + (40 / 60)
                upper = True

            elif escalator_nearby_area(row['lon'], row['lat']) == True and upper == True and down == False:
                # elif escalator_down_area(row['lon'], row['lat']) == 2 and upper is True and down is False:
                escalator_nearby = True
                start_time = row['epoch_time']
                escalator_down_epoch_time = row['epoch_time']
                escalator_down_time = row['epoch_time'] / 60
                down = True


        elif escalator_nearby is True and row['epoch_time'] <= start_time + 40 and upper is True and down is False:

            if escalator_upper_area(row['lon'], row['lat']) == 2:
                up_nearby_count += 1
                up_stairs_count += 1
            elif escalator_upper_area(row['lon'], row['lat']) == 1:
                up_nearby_count += 1

        elif escalator_nearby is True and row['epoch_time'] <= start_time + 40 and upper is True and down is True:

            if escalator_down_area(row['lon'], row['lat']) == 2:
                down_nearby_count += 1
                down_stairs_count += 1

            elif escalator_down_area(row['lon'], row['lat']) == 1:
                down_nearby_count += 1

        if row['epoch_time'] >= start_time + 40:
            escalator_nearby = False

    # print(up_nearby_count)
    # print(up_stairs_count)
    # print(down_nearby_count)
    # print(down_stairs_count)

    # Calculate time spent on upper area
    time_spent_on_upper_area = (escalator_down_time - escalator_up_time)

    '''
    if up_stairs_count>0:
        up_flag=True
    '''

    if time_spent_on_upper_area > 0:
        print("upper area: " + str(time_spent_on_upper_area))
        up_down_flag = True
    else:
        print('no upper area')
        up_down_flag = False

    return time_spent_on_upper_area, up_down_flag, escalator_up_epoch_time, escalator_down_epoch_time


def main():


    try:

        conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh', host='geoserver.sb.dfki.de', port=5432, password='LrVYI%TMT%d3')
        conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de', port=5432,
                                 password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    cur1 = conn1.cursor()
    cur2 = conn2.cursor()

    random_traces = pd.read_sql('select traceid from (select distinct traceid from boninfos where timestamp>'+str(int(sys.argv[1]))+' and timestamp<'+str(int(sys.argv[2]))+') as dummy_table', conn1)

    traces_list = []

    for index, row in random_traces.iterrows():
        traces_list.append(row['traceid'])


    '''
    traces_list = traces_list + [1, 9, 13, 15, 16, 18, 20, 22, 24, 240, 400, 650, 1275, 3010, 4290, 5570, 8000, 9033,
                                 11210, 11860, 12130, 19830, 22335, 26500, 30326, 37700, 40917, 2, 3, 5, 7, 10, 14, 17,
                                 19, 21, 23, 25, 28, 80, 101, 340, 345, 969, 1000, 7496, 3200, 18691, 24679, 26966,
                                 28387, 32725, 32926, 38347, 38771, 39716, 43804, 46373]
    '''

    # with open('traces_list.p', 'rb') as fp:
    # traces_list = pickle.load(fp)

    # print(len(traces_list))
    # print(traces_list)
    # print(type(traces_list))

    # Saving Recceipt List
    # save_pickle(traces_list, 'traces_list')


    #file1 = open('file1.txt', mode='w')
    #file2 = open('file2.txt', mode='w')
    #file3 = open('file3.txt', mode='w')

    shelf_meter_dept_no_contain = []
    shelf_meter_dept_no_overlap = []

    #traces_list=[16,19830]
    # traces_list=[1,9,13,15,16,18,20,22,24,240,400,650,1275,3010,4290,5570,8000,9033,11210,11860,12130,19830,22335,26500,30326,37700,40917]  # No Upper Area
    # traces_list=[2,3,5,7,10,14,17,19,21,23,25,28,80,101,340,345,969,1000,7496,3200,18691,24679,26966,28387,32725,32926,38347,38771,39716,43804,46373]  # Upper Area
    # traces_list=[20,1275,26500,240,38347]

    trace_tuple = tuple(traces_list)
    print(trace_tuple)

    bonmatchings = pd.read_sql(
        'select traceid,receipt_id,matchingtype,timestamp,material from bonInfos where traceid in ' + str(trace_tuple),
        conn1)
    traces = bonmatchings.groupby(by=['traceid'], sort=False, group_keys=False, squeeze=True)
    print('length of trace groups')
    print(len(traces))

    count = 1
    for trace_group_name, trace_group in traces:
        data_trace = pd.DataFrame(data=trace_group)
        print("Trace: " + str(trace_group_name))
        print(count)
        count += 1

        # To find out the duration of the trace
        trace_epoch_time = pd.read_sql(
            'select epoch_time from core_location where trace_id=' + str(trace_group_name) + ' order by epoch_time',
            conn2)
        trace_total_time = (trace_epoch_time['epoch_time'].values[len(trace_epoch_time) - 1] -
                            trace_epoch_time['epoch_time'].values[0]) / 60
        print("Time: " + str(trace_total_time))
        #file3.write('Trace: ' + str(trace_group_name) + ', Trace Time Period: ' + str(trace_total_time))
        #file3.write('\n')

        trace_coordinates = pd.read_sql('select lon,lat,epoch_time from core_location where trace_id=' + str(
            trace_group_name) + ' order by epoch_time', conn2)

        # To check if customer has moved through escalator or not
        time_spent_on_upper_area, up_down_flag, escalator_up_time, escalator_down_time = check_trace_move_upstairs(
            trace_coordinates)
        # print(up_down_flag)
        # print(time_spent)
        # print(up_flag)



        #file3.write('Trace: ' + str(trace_group_name) + ', Trace Upper Area Time Period: ' + str(time_spent_on_upper_area))
        #file3.write('\n')

        # to check if customer moved up or not, if yes then remove upstairs coordinates' geometry
        if up_down_flag is False:
            try:
                trace_geometry = pd.read_sql('select geometry from core_location where trace_id=' + str(
                    trace_group_name) + ' order by epoch_time', conn2)
                print('down....................')
            except psycopg2.OperationalError:
                time.sleep(1000)
                conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',
                                         password='LrVYI%TMT%d3')
                cur2 = conn2.cursor()
                trace_geometry = pd.read_sql('select geometry from core_location where trace_id=' + str(
                    trace_group_name) + ' order by epoch_time', conn2)
                print('down....................')
        else:

            try:
                trace_geometry = pd.read_sql('select geometry from core_location where trace_id=' + str(
                    trace_group_name) + ' and epoch_time<' + str(escalator_up_time) + ' or trace_id=' + str(
                    trace_group_name) + ' and epoch_time>' + str(escalator_down_time) + ' order by epoch_time', conn2)
                print('up....................')
            except psycopg2.OperationalError:
                time.sleep(1000)
                conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',
                                         password='LrVYI%TMT%d3')
                cur2 = conn2.cursor()
                trace_geometry = pd.read_sql('select geometry from core_location where trace_id=' + str(
                    trace_group_name) + ' and epoch_time<' + str(escalator_up_time) + ' or trace_id=' + str(
                    trace_group_name) + ' and epoch_time>' + str(escalator_down_time) + ' order by epoch_time', conn2)

        # Grouping by receipts
        receipts = data_trace.groupby(by=['receipt_id'], sort=False, group_keys=False, squeeze=True)

        trace_receipts_cat_dist_dict = {}
        trace_receipts_cat_dist_dict_new = {}
        trace_receipts_shelf_dist_dict = {}
        trace_receipts_shelf_dist_dict_new = {}
        trace_receipts_shelf_dist_no_of_product_dict = {}
        trace_receipts_dept_dist_no_of_product_dict = {}
        trace_receipts_total_dist_no_of_product_dict = {}

        # new var
        trace_receipts_no_dict = {}

        for receipt_group_name, receipt_group in receipts:
            trace_receipt_data = pd.DataFrame(data=receipt_group)

            material = trace_receipt_data['material'].unique()
            receipt_matching = trace_receipt_data['matchingtype'].unique()

            total_shelfmeter_distance = 0
            total_cat_distance = 0
            shelfmeter_geometry_count = 0
            category_geometry_count = 0

            no_upstairs_but_non_food = False
            trace_total_time_product_match = True
            upstairs_total_time_product_match = True

            upstairs_non_food_quantity = 0

            # Checking total time spent and no of product match
            if trace_total_time > 150 and len(material) < 25:
                trace_total_time_product_match = False

            if trace_total_time > 100 and len(material) < 20:
                trace_total_time_product_match = False

            if trace_total_time > 50 and len(material) < 10:
                trace_total_time_product_match = False

            if trace_total_time > 40 and len(material) < 5:
                trace_total_time_product_match = False

            if trace_total_time > 30 and len(material) < 3:
                trace_total_time_product_match = False

            if trace_total_time < 20 and len(material) > 25:
                trace_total_time_product_match = False

            if trace_total_time_product_match is True:
                for i in range(len(material)):

                    try:
                        shelfmeter_geometry = pd.read_sql(
                            'select c.geometry,b.shelfmeter_id from globus_product a,globus_shelfmeter_products b, globus_shelfmeter c where a.matnr=' + str(
                                material[i]) + ' and a.id=b.product_id and b.shelfmeter_id=c.id', conn2)
                    except psycopg2.OperationalError:
                        time.sleep(1000)
                        conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh',
                                                 host='geoserver.sb.dfki.de',
                                                 port=5432, password='LrVYI%TMT%d3')
                        cur2 = conn2.cursor()
                        shelfmeter_geometry = pd.read_sql(
                            'select c.geometry from globus_product a,globus_shelfmeter_products b, globus_shelfmeter c where a.matnr=' + str(
                                material[i]) + ' and a.id=b.product_id and b.shelfmeter_id=c.id', conn2)

                    if (shelfmeter_geometry.empty == False):
                        print('shelfmeter loop')
                        # print(str(shelfmeter_geometry['geometry'].values[0]))
                        shelfmeter_geometry_count += 1
                        # Calling function for shelfmeter distance
                        short_distance = shelfmeter_distance(trace_geometry, shelfmeter_geometry, cur2)
                        total_shelfmeter_distance += short_distance

                        try:
                            dept_geometry = pd.read_sql('select geometry from globus_department', conn2)
                        except psycopg2.OperationalError:
                            time.sleep(1000)
                            conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh',
                                                     host='geoserver.sb.dfki.de', port=5432, password='LrVYI%TMT%d3')
                            cur2 = conn2.cursor()
                            dept_geometry = pd.read_sql('select geometry from globus_department', conn2)

                        dept_geometry_query = 'select ST_contains(%s::geometry,%s::geometry)';

                        cur_geometry = ''
                        cur_geometry_flag = False

                        for index, row in dept_geometry.iterrows():
                            cur_geometry = str(row['geometry'])
                            # print(row['geometry'])
                            # print(shelfmeter_geometry['geometry'].values[0])
                            try:
                                cur2.execute(dept_geometry_query,
                                             [cur_geometry, str(shelfmeter_geometry['geometry'].values[0])])
                                cur_geometry_flag = cur2.fetchall()
                                # print(cur_geometry_flag[0][0])

                            except psycopg2.OperationalError:

                                time.sleep(1000)
                                conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh',
                                                         host='geoserver.sb.dfki.de', port=5432,
                                                         password='LrVYI%TMT%d3')
                                cur2 = conn2.cursor()
                                cur2.execute(dept_geometry_query,
                                             [cur_geometry, str(shelfmeter_geometry['geometry'].values[0])])
                                cur_geometry_flag = cur2.fetchall()

                            if cur_geometry_flag[0][0] == True:
                                break

                        if cur_geometry_flag[0][0] == True:
                            category_geometry_count += 1
                            print('new dept loop')
                            short_distance = cat_distance(cur_geometry, trace_geometry, conn2, cur2)
                            total_cat_distance += short_distance

                        else:

                            if str(shelfmeter_geometry['shelfmeter_id'].values[0]) not in shelf_meter_dept_no_contain:
                                shelf_meter_dept_no_contain.append(str(shelfmeter_geometry['shelfmeter_id'].values[0]))
                                #file1.write("shelfmeter: " + str(shelfmeter_geometry['shelfmeter_id'].values[0]))
                                shelf_meter_dept_no_contain.append(str(shelfmeter_geometry['shelfmeter_id'].values[0]))
                                #file1.write('\n')

                            dept_geometry_query_overlap = 'select ST_overlaps(%s::geometry,%s::geometry)';

                            cur_geometry = ''
                            cur_geometry_flag = False

                            for index, row in dept_geometry.iterrows():
                                cur_geometry = str(row['geometry'])
                                # print(row['geometry'])
                                # print(shelfmeter_geometry['geometry'].values[0])
                                try:
                                    cur2.execute(dept_geometry_query_overlap,
                                                 [cur_geometry, str(shelfmeter_geometry['geometry'].values[0])])
                                    cur_geometry_flag = cur2.fetchall()
                                    # print(cur_geometry_flag[0][0])

                                except psycopg2.OperationalError:

                                    time.sleep(1000)
                                    conn2 = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh',
                                                             host='geoserver.sb.dfki.de', port=5432,
                                                             password='LrVYI%TMT%d3')
                                    cur2 = conn2.cursor()
                                    cur2.execute(dept_geometry_query_overlap,
                                                 [cur_geometry, str(shelfmeter_geometry['geometry'].values[0])])
                                    cur_geometry_flag = cur2.fetchall()

                                if cur_geometry_flag[0][0] == True:
                                    break

                            if cur_geometry_flag[0][0] == True:
                                category_geometry_count += 1
                                print('new dept loop22')
                                short_distance = cat_distance(cur_geometry, trace_geometry, conn2, cur2)
                                total_cat_distance += short_distance

                            else:

                                if str(shelfmeter_geometry['shelfmeter_id'].values[
                                           0]) not in shelf_meter_dept_no_overlap:
                                    shelf_meter_dept_no_overlap.append(
                                        str(shelfmeter_geometry['shelfmeter_id'].values[0]))
                                    #file2.write("shelfmeter: " + str(shelfmeter_geometry['shelfmeter_id'].values[0]))
                                    #file2.write('\n')

                                try:
                                    category = pd.read_sql(
                                        'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                            material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)
                                except psycopg2.OperationalError:
                                    time.sleep(1000)
                                    conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh',
                                                             host='geoserver.sb.dfki.de',
                                                             password='LrVYI%TMT%d3')
                                    cur1 = conn1.cursor()

                                    category = pd.read_sql(
                                        'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                            material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)

                                # print(category.values[0][0])

                                # Category distance
                                if (category.empty == False):

                                    category_id = find_category_geometry(category, cur2)

                                    if category_id != '':
                                        print('department loop')

                                        category_geometry_count += 1
                                        short_distance = category_distance(category_id, trace_geometry, conn2, cur2)
                                        total_cat_distance += short_distance

                                    else:
                                        print('Category Geometry Not Found')

                                else:
                                    print('Level7 Category not Found')


                    else:

                        try:
                            category = pd.read_sql(
                                'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                    material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)
                        except psycopg2.OperationalError:
                            time.sleep(1000)
                            conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh',
                                                     host='geoserver.sb.dfki.de',
                                                     password='LrVYI%TMT%d3')
                            cur1 = conn1.cursor()

                            category = pd.read_sql(
                                'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                    material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)

                            # print(category.values[0][0])

                        # Category distance
                        if (category.empty == False):

                            category_id = find_category_geometry(category, cur2)

                            if category_id != '':
                                print('department loop')

                                category_geometry_count += 1
                                short_distance = category_distance(category_id, trace_geometry, conn2, cur2)
                                total_cat_distance += short_distance

                            else:
                                print('Category Geometry Not Found')

                                # to check if customer not moved up and have products from upstairs
                                if up_down_flag is False:

                                    try:
                                        category = pd.read_sql(
                                            'select b.level1_value from article_core a,category_core b where a.material=CAST(' + str(
                                                material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)
                                    except psycopg2.OperationalError:
                                        time.sleep(1000)
                                        conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh',
                                                                 host='geoserver.sb.dfki.de',
                                                                 password='LrVYI%TMT%d3')
                                        cur1 = conn1.cursor()

                                        category = pd.read_sql(
                                            'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                                material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)

                                    if category['level1_value'].values[0] == 'KON20':
                                        no_upstairs_but_non_food = True
                                        break

                                elif up_down_flag is True:

                                    try:
                                        category = pd.read_sql(
                                            'select b.level1_value from article_core a,category_core b where a.material=CAST(' + str(
                                                material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)
                                    except psycopg2.OperationalError:
                                        time.sleep(1000)
                                        conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh',
                                                                 host='geoserver.sb.dfki.de',
                                                                 password='LrVYI%TMT%d3')
                                        cur1 = conn1.cursor()

                                        category = pd.read_sql(
                                            'select b.level7_value from article_core a,category_core b where a.material=CAST(' + str(
                                                material[i]) + ' as text) and a.matl_group=b.level7_value', conn1)

                                    if category['level1_value'].values[0] == 'KON20':
                                        upstairs_non_food_quantity += 1


                        else:
                            print('Level7 Category not Found')

            # Checking total time spent upstairs and no of product match
            if time_spent_on_upper_area > 100 and upstairs_non_food_quantity < 15:
                upstairs_total_time_product_match = False

            if time_spent_on_upper_area > 50 and upstairs_non_food_quantity < 10:
                upstairs_total_time_product_match = False

            if time_spent_on_upper_area > 30 and upstairs_non_food_quantity < 5:
                upstairs_total_time_product_match = False

            if time_spent_on_upper_area < 10 and upstairs_non_food_quantity > 25:
                upstairs_total_time_product_match = False

            if no_upstairs_but_non_food is False and trace_total_time_product_match is True and upstairs_total_time_product_match is True:
                if category_geometry_count != 0:
                    category_normalized_distance = total_cat_distance / category_geometry_count
                    category_normalized_distance_new = total_cat_distance / len(material)

                else:
                    category_normalized_distance = 0.0
                    category_normalized_distance_new = 0.0
                print("category normalized_distance: " + str(category_normalized_distance))

                trace_receipts_cat_dist_dict[receipt_matching[0]] = category_normalized_distance
                trace_receipts_cat_dist_dict_new[receipt_matching[0]] = category_normalized_distance_new

                if shelfmeter_geometry_count != 0:
                    shelfmeter_normalized_distance = total_shelfmeter_distance / shelfmeter_geometry_count
                    shelfmeter_normalized_distance_new = total_shelfmeter_distance / len(material)
                else:
                    shelfmeter_normalized_distance = 0.0
                    shelfmeter_normalized_distance_new = 0.0
                print("shelfmeter normalized_distance " + str(shelfmeter_normalized_distance))

                trace_receipts_shelf_dist_dict[receipt_matching[0]] = shelfmeter_normalized_distance
                trace_receipts_shelf_dist_dict_new[receipt_matching[0]] = shelfmeter_normalized_distance_new

                trace_receipts_shelf_dist_no_of_product_dict[receipt_matching[0]] = shelfmeter_geometry_count
                trace_receipts_dept_dist_no_of_product_dict[receipt_matching[0]] = category_geometry_count
                trace_receipts_total_dist_no_of_product_dict[receipt_matching[0]] = len(material)

                # new var
                trace_receipts_no_dict[receipt_matching[0]] = receipt_group_name


        '''
        sorted_trace_receipts_cat_dist_dict = sorted(trace_receipts_cat_dist_dict.items(), key=operator.itemgetter(1))
        print(sorted_trace_receipts_cat_dist_dict)
        save_pickle(sorted_trace_receipts_cat_dist_dict, 'sorted_trace_receipts_cat_dist_dict_trace_' + str(count - 1))

        print(trace_receipts_shelf_dist_dict)
        save_pickle(trace_receipts_shelf_dist_dict, 'trace_receipts_shelf_dist_dict_trace_' + str(count - 1))

        print(trace_receipts_shelf_dist_no_of_product_dict)
        save_pickle(trace_receipts_shelf_dist_no_of_product_dict,'trace_receipts_shelf_dist_no_of_product_dict_trace_' + str(count - 1))

        print(trace_receipts_dept_dist_no_of_product_dict)
        save_pickle(trace_receipts_dept_dist_no_of_product_dict,'trace_receipts_dept_dist_no_of_product_dict_trace_' + str(count - 1))

        print(trace_receipts_total_dist_no_of_product_dict)
        save_pickle(trace_receipts_total_dist_no_of_product_dict,'trace_receipts_total_dist_no_of_product_dict_trace_' + str(count - 1))



        # ordering shelf distance and corresponding number of produdcts basded on cat distance
        shelf_distance_ordered_list = []
        shelf_distance_ordered_product_list = []
        shelf_distance_ordered_product_list_dept = []
        shelf_distance_ordered_product_list_total = []
        shelf_distance_ordered_trace_receipts_cat_dist_dict_new=[]
        shelf_distance_ordered_trace_receipts_shelf_dist_dict_new=[]

        for i in range(len(sorted_trace_receipts_cat_dist_dict)):
            shelf_distance_ordered_list.append(trace_receipts_shelf_dist_dict[sorted_trace_receipts_cat_dist_dict[i][0]])
            shelf_distance_ordered_product_list.append(trace_receipts_shelf_dist_no_of_product_dict[sorted_trace_receipts_cat_dist_dict[i][0]])
            shelf_distance_ordered_product_list_dept.append(trace_receipts_dept_dist_no_of_product_dict[sorted_trace_receipts_cat_dist_dict[i][0]])
            shelf_distance_ordered_product_list_total.append(trace_receipts_total_dist_no_of_product_dict[sorted_trace_receipts_cat_dist_dict[i][0]])

            shelf_distance_ordered_trace_receipts_cat_dist_dict_new.append(trace_receipts_cat_dist_dict_new[sorted_trace_receipts_cat_dist_dict[i][0]])
            shelf_distance_ordered_trace_receipts_shelf_dist_dict_new.append(trace_receipts_shelf_dist_dict_new[sorted_trace_receipts_cat_dist_dict[i][0]])


        print(shelf_distance_ordered_list)
        save_pickle(shelf_distance_ordered_list, 'shelf_distance_ordered_list_trace_' + str(count - 1))

        print(shelf_distance_ordered_product_list)
        save_pickle(shelf_distance_ordered_product_list, 'shelf_distance_ordered_product_list_trace_' + str(count - 1))

        print(shelf_distance_ordered_product_list_dept)
        save_pickle(shelf_distance_ordered_product_list_dept, 'shelf_distance_ordered_product_list_dept_trace_' + str(count - 1))

        print(shelf_distance_ordered_product_list_total)
        save_pickle(shelf_distance_ordered_product_list_total, 'shelf_distance_ordered_product_list_total_trace_' + str(count - 1))

        print(shelf_distance_ordered_trace_receipts_cat_dist_dict_new)
        save_pickle(shelf_distance_ordered_trace_receipts_cat_dist_dict_new,'shelf_distance_ordered_trace_receipts_cat_dist_dict_new_trace_' + str(count - 1))

        print(shelf_distance_ordered_trace_receipts_shelf_dist_dict_new)
        save_pickle(shelf_distance_ordered_trace_receipts_shelf_dist_dict_new,'shelf_distance_trace_receipts_shelf_dist_dict_new_trace_' + str(count - 1))


        '''

        print(
            '............................................................................................................')

        sorted_trace_receipts_shelf_dist_dict = sorted(trace_receipts_shelf_dist_dict.items(),
                                                       key=operator.itemgetter(1))
        print(sorted_trace_receipts_shelf_dist_dict)

        # save_pickle(sorted_trace_receipts_shelf_dist_dict, 'sorted_trace_receipts_shelf_dist_dict_trace_' + str(count - 1))


        # ordering cat distance and corresponding number of produdcts basded on shelf distance
        cat_distance_ordered_list = []
        cat_distance_ordered_product_list = []
        cat_distance_ordered_product_list_dept = []
        cat_distance_ordered_product_list_total = []

        # new var
        shelf_distance_ordered_list = []
        receipt_ordered_list = []

        # cat_distance_ordered_trace_receipts_cat_dist_dict_new = []
        # cat_distance_ordered_trace_receipts_shelf_dist_dict_new = []

        for i in range(len(sorted_trace_receipts_shelf_dist_dict)):
            cat_distance_ordered_list.append(trace_receipts_cat_dist_dict[sorted_trace_receipts_shelf_dist_dict[i][0]])
            cat_distance_ordered_product_list.append(
                trace_receipts_shelf_dist_no_of_product_dict[sorted_trace_receipts_shelf_dist_dict[i][0]])
            cat_distance_ordered_product_list_dept.append(
                trace_receipts_dept_dist_no_of_product_dict[sorted_trace_receipts_shelf_dist_dict[i][0]])
            cat_distance_ordered_product_list_total.append(
                trace_receipts_total_dist_no_of_product_dict[sorted_trace_receipts_shelf_dist_dict[i][0]])

            # new var
            receipt_ordered_list.append(trace_receipts_no_dict[sorted_trace_receipts_shelf_dist_dict[i][0]])

            # new var
            shelf_distance_ordered_list.append(sorted_trace_receipts_shelf_dist_dict[i][1])


            # cat_distance_ordered_trace_receipts_cat_dist_dict_new.append(trace_receipts_cat_dist_dict_new[sorted_trace_receipts_shelf_dist_dict[i][0]])
            # cat_distance_ordered_trace_receipts_shelf_dist_dict_new.append(trace_receipts_shelf_dist_dict_new[sorted_trace_receipts_shelf_dist_dict[i][0]])

        # cat dist ordered by shelf distance
        print(cat_distance_ordered_list)
        # save_pickle(cat_distance_ordered_list, 'cat_distance_ordered_list_trace_' + str(count - 1))

        # no of shelf products
        print(cat_distance_ordered_product_list)
        # save_pickle(cat_distance_ordered_product_list, 'cat_distance_ordered_product_list_trace_' + str(count - 1))

        # no of dept products
        print(cat_distance_ordered_product_list_dept)
        # save_pickle(cat_distance_ordered_product_list_dept, 'cat_distance_ordered_product_list_dept_trace_' + str(count - 1))

        # total number of products
        print(cat_distance_ordered_product_list_total)
        # save_pickle(cat_distance_ordered_product_list_total, 'cat_distance_ordered_product_list_total_trace_' + str(count - 1))

        # new var shelf distance ordered
        print(shelf_distance_ordered_list)

        # print(cat_distance_ordered_trace_receipts_cat_dist_dict_new)
        # save_pickle(cat_distance_ordered_trace_receipts_cat_dist_dict_new,'cat_distance_ordered_trace_receipts_cat_dist_dict_new_trace_' + str(count - 1))

        # print(cat_distance_ordered_trace_receipts_shelf_dist_dict_new)
        # save_pickle(cat_distance_ordered_trace_receipts_shelf_dist_dict_new,'cat_distance_trace_receipts_shelf_dist_dict_new_trace_' + str(count - 1))

        print(shelf_distance_ordered_list)
        print(sorted(shelf_distance_ordered_list))

        print(cat_distance_ordered_list)
        print(sorted(cat_distance_ordered_list))

        # check if candidate receipts are more than 1

        if (len(shelf_distance_ordered_list)) > 1:

            matching_index = -1
            # Final matching based on distance and number of products

            # To check if both shelf and dept dist are in increasing order
            if sorted(shelf_distance_ordered_list) == shelf_distance_ordered_list and sorted(cat_distance_ordered_list) == cat_distance_ordered_list:

                # to check if we have enough number of shelf and dist products
                if cat_distance_ordered_product_list[0] > (.7 * cat_distance_ordered_product_list_total[0]) and cat_distance_ordered_product_list_dept[0] > (.7 * cat_distance_ordered_product_list_total[0]):

                    '''
                    # check if any receipt has more info about shelf
                    for i in range(len(cat_distance_ordered_product_list)-1):
                        if cat_distance_ordered_product_list[i]<.6*cat_distance_ordered_product_list_total[i+1]):
                    '''


                    if shelf_distance_ordered_list[0] < shelf_distance_ordered_list[1] / 3:
                        matching_index = 0

            if matching_index == -1:
                print('no matching')
                query = "INSERT INTO bonmatchings2 (trace_id, receipt_id) VALUES (%s, %s);"

                for j in range(len(shelf_distance_ordered_list)):

                    data = (str(trace_group_name), str(receipt_ordered_list[j]),)

                    try:
                        cur1.execute(query, data)
                        conn1.commit()

                    except psycopg2.OperationalError:

                        time.sleep(1000)
                        conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh',
                                                 host='geoserver.sb.dfki.de', port=5432, password='LrVYI%TMT%d3')
                        cur1 = conn1.cursor()

                        cur1.execute(query, data)
                        conn1.commit()

            else:
                print('matched receipt: ' + str(receipt_ordered_list[matching_index]))
                query = "INSERT INTO bonmatchings2 (trace_id, receipt_id) VALUES (%s, %s);"

                data = (str(trace_group_name), str(receipt_ordered_list[matching_index]),)

                try:
                    cur1.execute(query, data)
                    conn1.commit()

                except psycopg2.OperationalError:

                    time.sleep(1000)
                    conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh', host='geoserver.sb.dfki.de',
                                             port=5432, password='LrVYI%TMT%d3')
                    cur1 = conn1.cursor()

                    cur1.execute(query, data)
                    conn1.commit()


        else:

            if len(shelf_distance_ordered_list) == 1:
                print('already matched')
                print('matched receipt: ' + str(receipt_ordered_list[0]))
                query = "INSERT INTO bonmatchings2 (trace_id, receipt_id) VALUES (%s, %s);"

                data = (str(trace_group_name), str(receipt_ordered_list[0]),)

                try:
                    cur1.execute(query, data)
                    conn1.commit()

                except psycopg2.OperationalError:

                    time.sleep(1000)
                    conn1 = psycopg2.connect(dbname='globus_checkout_data', user='bhavesh', host='geoserver.sb.dfki.de',
                                             port=5432, password='LrVYI%TMT%d3')
                    cur1 = conn1.cursor()

                    cur1.execute(query, data)
                    conn1.commit()


if __name__ == "__main__":
    main()


