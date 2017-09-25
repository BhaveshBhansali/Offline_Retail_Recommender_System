import psycopg2
import pandas as pd
import gzip
from Database.CheckoutDataInsertion.Insertion import ReceiptDataInsertion,CategoryDataInsertion,ArticleDataInsertion




def main():

    try:
        #conn_checkout = psycopg2.connect(dbname='checkout_data',user='bhavesh', host='geoserver.sb.dfki.de', password='LrVYI%TMT%d3')
        conn = psycopg2.connect(dbname='postgres',user='postgres', host='localhost', password='606902bB')
        #conn = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    cur = conn.cursor()



    # insertion of category_core information
    '''
        data = pd.read_excel('./BISM-8176_Sortimentshiearchie.xlsx')
        data=data.fillna('')
        CategoryDataInsertion().category_data_insertion(data,conn,cur)
    '''



    # insertion of artcles _core information
    '''
    data = pd.read_excel('./BISM-8176_Artikelstamm22.xlsx')
    data = data.fillna('')
    ArticleDataInsertion().article_data_insertion(data,conn,cur)
    '''



    # insertion of receipt information
    '''
    #INPUT_FILE = 'C:\\Users\\Student\\Desktop\\Bhavesh\\checkoutData\\ADHOC610_EXPORT_BONDATEN_IRL_201612.txt.gz'
    data = pd.read_table(gzip.open('./datafile'), sep='|', skipinitialspace=True,
                         dtype={'RPA_RFL': object, 'RPA_PQU': object, 'RPA_PAI': object, 'RPA_PA1': object,
                                'RPA_PA2': object, 'ZAUTH': object, 'ZGFKNR': object, 'ZACNNR': object,
                                'STAMMKUNDE_EAN': object, 'RPA_DSN': object, 'RPA_TSN': object, 'RPA_RRC': object,
                                'RPA_DTC': object, 'RPA_DRC': object, 'RPA_DID': object, 'RPA_DTG': object,
                                'RPA_DRG': object, 'RPA_TAC': object, 'RPA_TIF': object, 'RPA_TTG': object,
                                'RPA_CSK': object, 'RPA_RDF': object, 'RPA_RRG': object, 'RPA_ZWGBNR': object,
                                'RT_BONBUY': object, 'ZLGBONNR': object})

    data = data.fillna('')

    ReceiptDataInsertion().receipt_article_insertion(data,conn,cur)
    '''

if __name__ == "__main__":
    main()