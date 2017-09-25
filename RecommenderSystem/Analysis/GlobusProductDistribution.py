import psycopg2
import pandas as pd
import collections
import codecs
import pickle


def write_file(filename,list_data):

    out_file = open('./'+str(filename)+'.txt', mode='a')

    out_file.write("Total Unavailable----------------------\n")
    out_file.write(str(collections.Counter(list_data)))
    out_file.write("\n")
    out_file.write(str(collections.Counter(list_data).most_common(15)))
    out_file.write("\n")
    out_file.write("Total Available---------------------------\n")
    out_file.write(str(collections.Counter(list_data)))
    out_file.write("\n")
    out_file.write(str(collections.Counter(list_data).most_common(15)))
    out_file.write("\n")
    out_file.write("Unique UnAvailable---------------------------\n")
    out_file.write(str(collections.Counter(list_data)))
    out_file.write("\n")
    out_file.write(str(collections.Counter(list_data).most_common(15)))
    out_file.write("\n")
    out_file.write("Unique Available---------------------------\n")
    out_file.write(str(collections.Counter(list_data)))
    out_file.write("\n")
    out_file.write(str(collections.Counter(list_data).most_common(15)))
    out_file.write("\n")


def main():

    try:
        conn2 = psycopg2.connect(dbname='trajectory_simulator',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
        conn1 = psycopg2.connect(dbname='postgres', user='postgres', host='localhost', password='606902bB')
    except:
        print("I am unable to connect to the database")

    cur1=conn1.cursor()
    cur2=conn2.cursor()

    #Creating material list from globus_product
    globus_product_matl_list=[]
    globus_product_matl=pd.read_sql('select distinct(matnr) from globus_product',conn2)

    for index,row in globus_product_matl.iterrows():
        globus_product_matl_list.append(str(row['matnr']))
    print(globus_product_matl_list)

    # Creating randome 50k Receipts
    receipts = pd.read_sql("select receipt_id from receipts where plant='1006' and rpa_dep in ('53','54') and total_number_of_distinct_products>5 order by random() limit 50000",conn1)

    receipt_list = []
    for index, row in receipts.iterrows():
        receipt_list.append(row['receipt_id'])

    receipts_tuple = tuple(receipt_list)
    #print(receipts_tuple)


    #Gettting checkout details of 50k receipts
    df = pd.read_sql('select b.material,c.level7_value,c.level6_value,c.level5_value,c.level4_value,c.level3_value,c.level2_value,c.level1_value from receipts a,receipt_articles b,category_core c where a.receipt_id in ' + str(receipts_tuple) + ' and a.receipt_id=b.receipt_id and b.matl_group=c.level7_value', conn1)

    print(len(df))

    total_materials=len(df)
    unique_materials=len(df['material'].unique())
    print(total_materials)
    print(unique_materials)


    unique_available_material_list=[]
    unique_unavailable_material_list=[]

    level7_list_unavail=[]
    level6_list_unavail = []
    level5_list_unavail = []
    level4_list_unavail = []
    level3_list_unavail = []
    level2_list_unavail = []
    level1_list_unavail = []

    level7_list_avail = []
    level6_list_avail = []
    level5_list_avail = []
    level4_list_avail = []
    level3_list_avail = []
    level2_list_avail = []
    level1_list_avail = []

    total_matl_available=0
    for index,row in df.iterrows():
        print(index)

        if row['material'] in globus_product_matl_list:
            total_matl_available+=1
            level7_list_avail.append(row['level7_value'])
            level6_list_avail.append(row['level6_value'])
            level5_list_avail.append(row['level5_value'])
            level4_list_avail.append(row['level4_value'])
            level3_list_avail.append(row['level3_value'])
            level2_list_avail.append(row['level2_value'])
            level1_list_avail.append(row['level1_value'])

            if row['material'] not in unique_available_material_list:
                unique_available_material_list.append(row['material'])

        else:
            level7_list_unavail.append(row['level7_value'])
            level6_list_unavail.append(row['level6_value'])
            level5_list_unavail.append(row['level5_value'])
            level4_list_unavail.append(row['level4_value'])
            level3_list_unavail.append(row['level3_value'])
            level2_list_unavail.append(row['level2_value'])
            level1_list_unavail.append(row['level1_value'])

            if row['material'] not in unique_unavailable_material_list:
                unique_unavailable_material_list.append(row['material'])


    print(len(unique_unavailable_material_list))
    print(unique_unavailable_material_list)

    print(len(unique_available_material_list))
    print(unique_available_material_list)

    unique_unavailable_material_tuple = tuple(unique_unavailable_material_list)
    df1 = pd.read_sql('select b.level7_value,b.level6_value,b.level5_value,b.level4_value,b.level3_value,b.level2_value,b.level1_value from article_core a,category_core b where a.material in ' + str(unique_unavailable_material_tuple) + ' and a.matl_group=b.level7_value', conn1)

    level7_list_unavail_unique = []
    level6_list_unavail_unique = []
    level5_list_unavail_unique = []
    level4_list_unavail_unique = []
    level3_list_unavail_unique = []
    level2_list_unavail_unique = []
    level1_list_unavail_unique = []

    for index, row in df1.iterrows():
        level7_list_unavail_unique.append(row['level7_value'])
        level6_list_unavail_unique.append(row['level6_value'])
        level5_list_unavail_unique.append(row['level5_value'])
        level4_list_unavail_unique.append(row['level4_value'])
        level3_list_unavail_unique.append(row['level3_value'])
        level2_list_unavail_unique.append(row['level2_value'])
        level1_list_unavail_unique.append(row['level1_value'])

    unique_available_material_tuple=tuple(unique_available_material_list)
    df2 = pd.read_sql('select b.level7_value,b.level6_value,b.level5_value,b.level4_value,b.level3_value,b.level2_value,b.level1_value from article_core a,category_core b where a.material in ' + str(unique_available_material_tuple) + ' and a.matl_group=b.level7_value', conn1)
    level7_list_avail_unique = []
    level6_list_avail_unique = []
    level5_list_avail_unique = []
    level4_list_avail_unique = []
    level3_list_avail_unique = []
    level2_list_avail_unique = []
    level1_list_avail_unique = []


    for index, row in df2.iterrows():
        level7_list_avail_unique.append(row['level7_value'])
        level6_list_avail_unique.append(row['level6_value'])
        level5_list_avail_unique.append(row['level5_value'])
        level4_list_avail_unique.append(row['level4_value'])
        level3_list_avail_unique.append(row['level3_value'])
        level2_list_avail_unique.append(row['level2_value'])
        level1_list_avail_unique.append(row['level1_value'])


    file_statistics=open('./globus_product.txt',mode='a')
    file_statistics.write('Total_Number_of_Materials: '+str(total_materials))
    file_statistics.write("\n")
    file_statistics.write('Total_Number_of_Unique_Materials: ' + str(unique_materials))
    file_statistics.write("\n")
    file_statistics.write('Total_Number_of_Materials_available: ' + str(total_matl_available))
    file_statistics.write("\n")
    file_statistics.write('Total_Number_of_Unique_Materials_available: ' + str(len(unique_available_material_list)))
    file_statistics.write("\n")


    write_file('level7_cat', level7_list_unavail)
    write_file('level6_cat', level6_list_unavail)
    write_file('level5_cat', level5_list_unavail)
    write_file('level4_cat', level4_list_unavail)
    write_file('level3_cat', level3_list_unavail)
    write_file('level2_cat', level2_list_unavail)
    write_file('level1_cat', level1_list_unavail)


    with open('./unique_unavailable_material_list.p','wb') as fp:
        pickle.dump(unique_unavailable_material_list,fp)

    with open('./unique_available_material_list.p', 'wb') as fp:
        pickle.dump(unique_available_material_list, fp)





if __name__ == '__main__':
    main()