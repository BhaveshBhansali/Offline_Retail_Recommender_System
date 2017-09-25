from sklearn.cluster import DBSCAN, KMeans
import pickle
import numpy as np
import matplotlib.pyplot as plt
from collections import Counter
import codecs
from scipy.cluster.hierarchy import dendrogram,linkage
import hdbscan
from sklearn.metrics import silhouette_score



class clusters:

    def transaction_to_space_separated(self, transaction_data, keys, k):

        space_separated_file_material = codecs.open("space_separated_material_cluster" + str(k) + "_transaction_file.txt", mode='w', encoding='utf-8')
        space_separated_file_level7 = codecs.open("space_separated_level7_cluster" + str(k) + "_transaction_file.txt",mode='w', encoding='utf-8')
        space_separated_file_level6 = codecs.open("space_separated_level6_cluster" + str(k) + "_transaction_file.txt",mode='w', encoding='utf-8')
        space_separated_file_level5 = codecs.open("space_separated_level5_cluster" + str(k) + "_transaction_file.txt",mode='w', encoding='utf-8')
        space_separated_file_level4 = codecs.open("space_separated_level4_cluster" + str(k) + "_transaction_file.txt",mode='w', encoding='utf-8')

        for i in range(len(keys)):

            for key in transaction_data[keys[i]].keys():
                space_separated_file_material.write(str(key) + " ")
                space_separated_file_level7.write(str(transaction_data[keys[i]][key][0]) + " ")
                space_separated_file_level6.write(str(transaction_data[keys[i]][key][1]) + " ")
                space_separated_file_level5.write(str(transaction_data[keys[i]][key][2]) + " ")
                space_separated_file_level4.write(str(transaction_data[keys[i]][key][3]) + " ")

            space_separated_file_material.write("\n")
            space_separated_file_level7.write("\n")
            space_separated_file_level6.write("\n")
            space_separated_file_level5.write("\n")
            space_separated_file_level4.write("\n")



    def cluster_output(self,transaction_data,cluster_dict,cluster_op_file):

        for k in range(len(cluster_dict)):

            cluster_op_file.write("cluster: " + str(k))
            cluster_op_file.write("\n")

            # transaction to space separated for Association mining input
            self.transaction_to_space_separated(transaction_data, cluster_dict[k], k)

            for l in range(len(cluster_dict[k])):

                # For transaction as MATERIAL, Material group and level6 list
                for key in transaction_data[cluster_dict[k][l]].keys():
                    cluster_op_file.write(str(key) + ':' + str(transaction_data[cluster_dict[k][l]][key]) + ' ')
                cluster_op_file.write('\n')




    def DBSCAN_create_clusters(self,dist_data,transaction_data,threshold_distance,min_members):

        # File pointer for of cluster details file
        cluster_op_file = open("./cluster_details_70000_"+str(threshold_distance)+"_"+str(min_members)+"_DBSCAN.txt", mode='w', encoding='utf-8')

        # Implementing DBScan algorithm
        db = DBSCAN(eps=threshold_distance, min_samples=min_members, metric='precomputed').fit(dist_data)

        #core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
        #core_samples_mask[db.core_sample_indices_] = True
        labels = db.labels_

        # Number of clusters in labels, ignoring noise if present.
        n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
        print('Estimated number of clusters: %d' % n_clusters_)

        # clusters = [data[labels == i] for i in range(n_clusters_)]

        # storing cluster centers
        with open('ClusterCenters.p', 'wb') as fp:
            pickle.dump(db.cluster_centers_, fp)

        # Count Number of elements in each cluster
        print(Counter(labels))
        cluster_op_file.write('Estimated number of clusters: %d' % n_clusters_)
        cluster_op_file.write("\n")

        cluster_op_file.write(str(Counter(labels)))
        cluster_op_file.write("\n")

        # Dictionary of clusters containing transaction numbers
        cluster_dict = {}
        #col_list = list(dist_data.columns.values)
        with open('./keys70000.p', 'rb') as fp:
            col_list = pickle.load(fp)

        for j in range(len(labels)):
            if labels[j] != -1:
                # print(j)
                if labels[j] in cluster_dict.keys():
                    cluster_dict[labels[j]].append(col_list[j])
                else:
                    cluster_dict[labels[j]] = [col_list[j]]

        print(cluster_dict)


        self.cluster_output(transaction_data,cluster_dict,cluster_op_file)







    def HDBScan_create_clusters(self,dist_data,transaction_data,min_members):

        # File pointer for of cluster details file
        cluster_op_file = open("./cluster_details_70000_"+"_"+str(min_members)+"_HDBSCAN.txt", mode='w', encoding='utf-8')

        # Implementing DBScan algorithm
        db = hdbscan.HDBSCAN(min_cluster_size=min_members, metric='precomputed').fit(dist_data)

        #core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
        #core_samples_mask[db.core_sample_indices_] = True
        labels = db.labels_

        # Number of clusters in labels, ignoring noise if present.
        n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
        print('Estimated number of clusters: %d' % n_clusters_)

        # clusters = [data[labels == i] for i in range(n_clusters_)]


        # storing cluster centers
        with open('ClusterCenters.p', 'wb') as fp:
            pickle.dump(db.cluster_centers_, fp)

        # Count Number of elements in each cluster
        print(Counter(labels))

        cluster_op_file.write(str(Counter(labels)))
        cluster_op_file.write("\n")

        # Dictionary of clusters containing transaction numbers
        cluster_dict = {}
        #col_list = list(dist_data.columns.values)
        with open('./keys_70000.p', 'rb') as fp:
            col_list = pickle.load(fp)

        for j in range(len(labels)):
            if labels[j] != -1:
                # print(j)
                if labels[j] in cluster_dict.keys():
                    cluster_dict[labels[j]].append(col_list[j])
                else:
                    cluster_dict[labels[j]] = [col_list[j]]

        print(cluster_dict)

        self.cluster_output(transaction_data,cluster_dict,cluster_op_file)



    def Hierarchical_create_clusters(self,data,linkage_method):

        # Forming hierarchical linkage using distance matrix
        Z = linkage(data, method=linkage_method)
        # dendrogram(Z, color_threshold=0, show_contracted=True, truncate_mode='lastp', p=30, leaf_rotation=90, leaf_font_size=8)
        dendrogram(Z, color_threshold=0)
        nodes = list(data.columns.values)
        # plt.show()


        clusters = {}
        # threshold distance (clusters below this distance are considered) estimated from Dendrogram plot
        maxdist = 1.30
        n = len(Z) + 1
        # print(n)

        # Extracting clusters from the linkage matrix based on maxdist
        # Z's format --> [idx1, idx2, dist, sample_count]
        for i in range(n):
            if Z[i, 2] > maxdist:
                break;

            x = int(Z[i, 0])
            y = int(Z[i, 1])

            if x < n and y < n:
                templist = []
                templist.append(nodes[x])
                templist.append(nodes[y])
                clusters[i] = templist

            elif x < n and y >= n:
                templist = []
                y = y - n
                templist = list(clusters[y])
                templist.append(nodes[x])
                clusters[i] = templist
                clusters.pop(y, None)


            elif x >= n and y < n:
                templist = []
                x = x - n
                templist = list(clusters[x])
                templist.append(nodes[y])
                clusters[i] = templist
                clusters.pop(x, None)


            elif x >= n and y >= n:
                templist = []
                x = x - n
                y = y - n
                listx = list(clusters[x])
                listy = list(clusters[y])
                templist = listx + listy
                clusters[i] = templist
                clusters.pop(x, None)
                clusters.pop(y, None)

        print("Total number fo clusters {}".format(len(clusters)))
        print("Displaying clusters more than 2 elements :")
        count = 0
        for key, value in clusters.items():
            print(key, value, len(value))
            '''
            if len(value) > 2:
                print(key, value, len(value))
                count+=1
            '''
        print("number of clusters with size > 2 {}".format(count))





    def calculateSilhouette(self,X):
        file=open('./silhouette.txt','a')
        s = [1, 1]
        for n_clusters in range(17, 27):

            kmeans = KMeans(n_clusters=n_clusters, init='k-means++', random_state=0).fit(X)

            labels = kmeans.labels_

            score = silhouette_score(X, labels, metric='euclidean',sample_size=10000)
            file.write('K: '+str(n_clusters)+' '+str(score))
            file.write('\n')
            s.append(score)
            print(n_clusters)

        # plt.xticks([2,3])


        plt.plot(s)
        #plt.xticks([2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50])
        plt.ylabel("The silhouette coefficient values")
        plt.xlabel("Values of k")
        plt.title("Silouette for K-means behaviour")
        plt.savefig('silhouette_10000_10_10_40_10_15_15.png')


    def KMeansPlusPlus_create_clusters(self,dist_data,transaction_data,num_clusters):

        # File pointer for of cluster details file
        cluster_op_file = open("./Kmeans_cluster_details_70000_"+str(num_clusters)+".txt", mode='w', encoding='utf-8')

        # Implementing DBScan algorithm
        kmeans = KMeans(n_clusters=num_clusters, init='k-means++', random_state=0).fit(dist_data)

        labels = kmeans.labels_

        '''

        score=silhouette_score(dist_data,labels,metric='euclidean',sample_size=10000)
        print(score)
        file = open('./silhouette_10_10_40_30_5_5.txt', 'a')
        file.write('K: ' + str(num_clusters) + ' ' + str(score))
        file.write('\n')
        '''

        inertia=kmeans.inertia_

        # Number of clusters in labels, ignoring noise if present.
        n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
        print('Estimated number of clusters: %d' % n_clusters_)

        with open('ClusterCenters.p', 'wb') as fp:
            pickle.dump(kmeans.cluster_centers_, fp)

        # clusters = [data[labels == i] for i in range(n_clusters_)]

        # print(clusters[1])
        # print(labels)



        # Count Number of elements in each cluster
        print(Counter(labels))

        cluster_op_file.write('Estimated number of clusters: %d' % n_clusters_)
        cluster_op_file.write("\n")

        cluster_op_file.write("Inertia: Sum of distances of samples to their closest cluster center: "+str(inertia))
        cluster_op_file.write("\n")

        cluster_op_file.write("Cluster Centeres: "+str(kmeans.cluster_centers_))
        cluster_op_file.write("\n")

        cluster_op_file.write(str(Counter(labels)))
        cluster_op_file.write("\n")

        # Dictionary of clusters containing transaction numbers
        cluster_dict = {}
        #col_list = list(dist_data.columns.values)
        with open('./keys70000.p', 'rb') as fp:
            col_list = pickle.load(fp)

        for j in range(len(labels)):
            if labels[j] != -1:
                # print(j)
                if labels[j] in cluster_dict.keys():
                    cluster_dict[labels[j]].append(col_list[j])
                else:
                    cluster_dict[labels[j]] = [col_list[j]]

        print(cluster_dict)

        self.cluster_output(transaction_data,cluster_dict,cluster_op_file)





