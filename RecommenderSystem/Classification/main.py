from RecommenderSystem.Classification import Features
from RecommenderSystem.Classification.Classifiers import classifiers
import psycopg2
import pickle
import sys


def main():


    try:
        conn = psycopg2.connect(dbname='trajectory_simulator', user='bhavesh', host='geoserver.sb.dfki.de',password='LrVYI%TMT%d3')
    except:
        print("I am unable to connect to the database")

    cur = conn.cursor()


    # read cluster label matrix
    trace_cluster_label_matrix_path=sys.argv[1]

    with open(trace_cluster_label_matrix_path, 'rb') as fp:
        trace_cluster_label_matrix = pickle.load(fp)


    # read trace features
    trace_features_path=sys.argv[2]

    with open(trace_features_path, 'rb') as fp:
        trace_features_matrix = pickle.load(fp)


    '''

    # K Nearest Neighbour

    # test data split %
    test_split=sys.argv[3]/100

    # neighbours
    neighbours=sys.argv[4]

    classifiers().KNearestNeighboursClassifierModel(trace_features_matrix,trace_cluster_label_matrix,test_split, neighbours)
    '''




    # SVM Classifier

    # test data split %
    test_split = sys.argv[3] / 100

    # penalty
    penalty = sys.argv[4]

    classifiers().SVM_Classifier(trace_features_matrix, trace_cluster_label_matrix, test_split,penalty)




if __name__ == '__main__':
    main()