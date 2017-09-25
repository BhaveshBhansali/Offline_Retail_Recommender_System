import psycopg2
import pandas as pd
import gzip
import time
import calendar



class ReceiptDataInsertion:


    def date_to_epoch(self, date):
        '''
        function compute epoch time for date parameter with an assumption date as local time zone

        :param date: local time with time zone
        :return: epoch time
        '''

        time_struct = time.strptime(date, '%Y-%m-%d %H:%M:%S')

        # time_epoch = calendar.timegm(time_struct)  (if date parameter is in GMT time zone)
        time_epoch = time.mktime(time_struct)
        # print(time_epoch)
        return time_epoch



    def date_to_epoch_update(self, data, conn, cur):
        sql = """update receipts set rpa_bts_epoch=%s where receipt_id=%s"""

        for index, row in data.iterrows():
            print(row['rpa_bts'])
            print(row['receipt_id'])

            cur.execute(sql, (self.date_to_epoch(str(row['rpa_bts'])), row['receipt_id']))
            conn.commit()

            print(index)

    def receipt_article_insertion(self,data,conn,cur):

        file1 = open('exception_receipt.txt', mode='a')
        file2 = open('exception_receipt_article.txt', mode='a')

        gp = data.groupby(by=['RPA_TNR', 'RPA_BDD', 'RPA_DEP', 'PLANT','RPA_WID','RPA_TSA','RPA_TRNOV','RPA_LNECNT','RPA_BTS','UMSATZDATUM'], sort=False, group_keys=False, squeeze=True)
        print(len(gp))

        count1 = 10333131
        count2 = 103895226
        #count2 = 5464907
        for name, group in gp:

            total_number_of_products=0
            total_number_of_distinct_products=0
            df = pd.DataFrame(data=group)

            # counting total number of products of receipt
            for i in range(len(df)):
                # total_products=float(str(df['UMSATZMENGE'].values[i]).replace(',', '.'))
                if ',' in str(df['UMSATZMENGE'].values[i]) or '0' in str(df['UMSATZMENGE'].values[i]) or str(df['UMSATZMENGE'].values[i])== str(df['BASE_QTY'].values[i]) :
                    total_number_of_products += 1
                else:
                    total_number_of_products += int(str(df['UMSATZMENGE'].values[i]))

            # counting number of distinct products
            total_number_of_distinct_products=len(df.MATERIAL.unique())

            # Preparing data for receipt table
            query1 = "INSERT INTO receipts (receipt_id,rpa_tnr,rpa_bdd,rpa_dep,plant,rpa_wid,rpa_tsa,rpa_trnov,rpa_lnecnt,rpa_bts,umsatzdatum,total_number_of_products,total_number_of_distinct_products,rpa_bts_epoch) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s);"
            data1 = (count1, str(name[0]), str(name[1]), str(name[2]), str(name[3]), str(name[4]),
                     float(str(name[5]).replace(',', '.')), float(str(name[6]).replace(',', '.')),
                     float(str(name[7]).replace(',', '.')), str(name[8]), str(name[9]), total_number_of_products,total_number_of_distinct_products,self.date_to_epoch(str(name[8])),)



            try:
                cur.execute(query1, data1)
                count1 = count1 + 1
                print('Group: ' + str(count1 - 1))

            except Exception as e:
                print('exception at receipt group level' + str(count1 - 1))
                print(e)
                file1.write(str(name[0]) + ' ' + str(name[1]) + ' ' + str(name[2]) + ' ' + str(name[3]) + str(name[4]))
                file1.write('\n')

            conn.commit()

            # Preparing data for receipt_articles
            for i in range(len(df)):
                data2 = (
                    count2, count1 - 1, str(df['MATERIAL'].values[i]),
                    str(df['RPA_TIX'].values[i]),
                    str(df['VERSION'].values[i]), str(df['RPA_RFL'].values[i]), str(df['RPA_TTC'].values[i]),
                    str(df['RPA_OID'].values[i]), str(df['RPA_PQU'].values[i]), str(df['RPA_PAI'].values[i]),
                    str(df['RPA_TRG'].values[i]), str(df['RPA_OQU'].values[i]), str(df['RPA_RSI'].values[i]),
                    str(df['OI_PBLNR'].values[i]),
                    str(df['RPA_OI1'].values[i]), str(df['RPA_PA1'].values[i]), str(df['RPA_PA2'].values[i]),
                    str(df['ZAUTH'].values[i]),
                    str(df['ZGFKNR'].values[i]), str(df['ZACNNR'].values[i]), str(df['DOC_CURRCY'].values[i]),
                    str(df['LOC_CURRCY'].values[i]),
                    str(df['STAMMKUNDE_EAN'].values[i]), str(df['RPA_RQU'].values[i]),
                    str(df['RPA_RSN'].values[i]), str(df['RPA_DSN'].values[i]), str(df['RPA_TSN'].values[i]),
                    str(df['EANUPC'].values[i]),
                    str(df['MATL_GROUP'].values[i]), str(df['RPA_RTC'].values[i]), str(df['RPA_RRC'].values[i]),
                    str(df['RPA_IQU'].values[i]), str(df['RPA_EMC'].values[i]), str(df['RPA_DTC'].values[i]),
                    str(df['RPA_DRC'].values[i]),
                    str(df['RPA_DID'].values[i]), str(df['RPA_DTG'].values[i]), str(df['RPA_DRG'].values[i]),
                    str(df['RPA_TAC'].values[i]),
                    str(df['RPA_TIF'].values[i]), str(df['RPA_TTG'].values[i]), str(df['RPA_CSK'].values[i]),
                    str(df['RPA_RTG'].values[i]),
                    str(df['RPA_RDF'].values[i]), str(df['RPA_ITI'].values[i]), str(df['RPA_RRG'].values[i]),
                    str(df['RPA_RIC'].values[i]),
                    str(df['RPA_RISC'].values[i]), float(str(df['BASE_QTY'].values[i]).replace(',', '.')),
                    float(str(df['UMSATZMENGE'].values[i]).replace(',', '.')),
                    float(str(df['BUMS'].values[i]).replace(',', '.')),
                    float(str(df['RPA_NSA'].values[i]).replace(',', '.')),
                    float(str(df['RABATT'].values[i]).replace(',', '.')),
                    float(str(df['RPA_RQTYB'].values[i]).replace(',', '.')),
                    float(str(df['RPA_RQTYV'].values[i]).replace(',', '.')),
                    float(str(df['RPA_RETSAL'].values[i]).replace(',', '.')),
                    float(str(df['RPA_SATMER'].values[i]).replace(',', '.')),
                    float(str(df['RPA_REAMER'].values[i]).replace(',', '.')),
                    float(str(df['RPA_RETMER'].values[i]).replace(',', '.')), str(df['BASE_UOM'].values[i]),
                    str(df['SALES_UNIT'].values[i]), str(df['ZWGBNR'].values[i]),
                    float(str(df['NUMS'].values[i]).replace(',', '.')),
                    float(str(df['NETTORABATT'].values[i]).replace(',', '.')),
                    float(str(df['MWSTSATZ'].values[i]).replace(',', '.')), str(df['RT_BONBUY'].values[i]),
                    str(df['ZLGBONNR'].values[i]),)

                query2 = "INSERT INTO receipt_articles(id, receipt_id,material, rpa_tix,version_id,rpa_rfl,rpa_ttc,rpa_oid ,rpa_pqu ,rpa_pai ,rpa_trg ,rpa_oqu ,rpa_rsi ,oi_pblnr ,rpa_oi1 , rpa_pa1 , rpa_pa2 , zauth ,  zgfknr , zacnnr , doc_currcy ,  loc_currcy ,  stammkunde_ean ,  rpa_rqu ,  rpa_rsn ,  rpa_dsn ,  rpa_tsn ,  eanupc ,  matl_group ,  rpa_rtc ,  rpa_rrc ,  rpa_iqu ,  rpa_emc ,  rpa_dtc ,  rpa_drc ,  rpa_did ,  rpa_dtg ,  rpa_drg ,  rpa_tac ,  rpa_tif ,  rpa_ttg ,  rpa_csk ,  rpa_rtg ,  rpa_rdf ,  rpa_iti ,  rpa_rrg ,  rpa_ric ,  rpa_risc ,  base_qty ,  umsatzmenge ,  bums ,  rpa_nsa ,  rabatt ,  rpa_rqtyb ,  rpa_rqtyv ,  rpa_retsal ,  rpa_satmer ,  rpa_reamer ,  rpa_retmer ,  base_uom ,  sales_unit ,  zwgbnr ,  nums ,  nettorabatt ,  mwstsatz ,  rt_bonbuy ,  zlgbonnr ) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s);"

                try:
                    cur.execute(query2, data2)
                    count2 = count2 + 1

                except Exception as e:

                    print(e)
                    print('exception at receipt article level')
                    file2.write('exceptions of articles at group: ' + str(count1 - 1))
                    file2.write('\n')

                conn.commit()

                #else:
                    #count1 += 1


class CategoryDataInsertion:

    def category_data_insertion(self,data,conn,cur):
        for index, row in data.iterrows():
            query = "INSERT INTO category_core (level1_value, level1_bez_de, level2_value, level2_bez_de, level3_value, level3_bez_de, level4_value, level4_bez_de, level5_value, level5_bez_de, level6_value, level6_bez_de, level7_value, level7_bez_de) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);"

            data = (
            row['LEVEL1_VALUE'], row['LEVEL1_BEZ_DE'], row['LEVEL2_VALUE'], row['LEVEL2_BEZ_DE'], row['LEVEL3_VALUE'],
            row['LEVEL3_BEZ_DE'], row['LEVEL4_VALUE'], row['LEVEL4_BEZ_DE'], row['LEVEL5_VALUE'], row['LEVEL5_BEZ_DE'],
            row['LEVEL6_VALUE'], row['LEVEL6_BEZ_DE'], row['LEVEL7_VALUE'], row['LEVEL7_BEZ_DE'],)

            cur.execute(query, data)
            conn.commit()

class ArticleDataInsertion:

    def article_data_insertion(self,data,conn,cur):
        count = 0

        for index, row in data.iterrows():
            material = ''
            eanupc = ''

            if '.' in str(row['EANUPC']):
                eanupc = str(row['EANUPC']).partition('.')[0]
            else:
                eanupc = row['EANUPC']

            if '.' in str(row['MATERIAL']):
                material = str(row['MATERIAL']).partition('.')[0]
            else:
                material = row['MATERIAL']

            if row['MATL_GROUP'] != '':

                query = "INSERT INTO article_core (material,txtmd,eanupc) VALUES(%s,%s,%s,%s);"
                data = (material, row['TXTMD'], eanupc,row['matl_grp'],)

                try:
                    cur.execute(query, data)
                    print(index)
                except:
                    print('exception' + str(index))
                    # file.write(str(material))
                    # file.write('\n')

            else:

                query = "INSERT INTO article_core (material,txtmd,eanupc) VALUES(%s,%s,%s);"
                data = (material, row['TXTMD'], eanupc,)

                try:
                    cur.execute(query, data)
                    print(index)
                except:
                    print('exception' + str(index))
                    # file.write(str(material))
                    # file.write('\n')

            conn.commit()


class GlobusProductCategoryInsertion:

    def globus_product_category_insertion(self,conn,conn_checkout,cur):

        category_information = pd.read_sql('select * from category_core', conn_checkout)

        computed_category_list = []

        for index, row in category_information.iterrows():
            print(index)
            query1 = "insert into globus_productcategory(value,name,level) values(%s,%s,%s);"
            query2 = "insert into globus_productcategory(value,name,level,parent_category_id) values(%s,%s,%s,%s);"
            query3 = "select id from globus_productcategory where value=%s;"

            if row['level1_value'] not in computed_category_list:
                data = (row['level1_value'], row['level1_bez_de'], 1,)
                cur.execute(query1, data)
                conn.commit()
                computed_category_list.append(row['level1_value'])

            if row['level2_value'] not in computed_category_list:
                data = (row['level1_value'],)
                cur.execute(query3, data)
                parent = cur.fetchall()
                data = (row['level2_value'], row['level2_bez_de'], 2, parent[0][0],)
                cur.execute(query2, data)
                conn.commit()
                computed_category_list.append(row['level2_value'])

            if row['level3_value'] not in computed_category_list:
                data = (row['level2_value'],)
                cur.execute(query3, data)
                parent = cur.fetchall()
                data = (row['level3_value'], row['level3_bez_de'], 3, parent[0][0],)
                cur.execute(query2, data)
                conn.commit()
                computed_category_list.append(row['level3_value'])

            if row['level4_value'] not in computed_category_list:
                data = (row['level3_value'],)
                cur.execute(query3, data)
                parent = cur.fetchall()
                data = (row['level4_value'], row['level4_bez_de'], 4, parent[0][0],)
                cur.execute(query2, data)
                conn.commit()
                computed_category_list.append(row['level4_value'])
