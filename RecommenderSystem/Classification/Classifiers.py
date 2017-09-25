import pickle
import numpy as np
import psycopg2

from sklearn.naive_bayes import GaussianNB, MultinomialNB, BernoulliNB
from sklearn.neighbors import KNeighborsClassifier,RadiusNeighborsClassifier
from sklearn.svm import SVC
from sklearn.cross_validation import train_test_split,cross_val_score


class classifiers:

    def split_data_train_test(self,features,labels,test_size):
        return train_test_split(features, labels, test_size=test_size)

    def cross_validation(self,classifier_features_matrix,classifier_labels,k_range):

        score_mean_list=[]
        for i in range(1,k_range):
            KNC = KNeighborsClassifier(n_neighbors=i)
            scores = cross_val_score(KNC,classifier_features_matrix, classifier_labels, cv=5)
            score_mean_list.append(scores.mean())

        return score_mean_list


    def countLabels(self,classifier_labels):

        count0 = 0
        count1 = 0
        count2 = 0
        count3 = 0
        count4 = 0
        count5 = 0
        count6 = 0
        count7 = 0

        for i in range(len(classifier_labels)):
            if classifier_labels[i] == 0:
                count0 = count0 + 1
            elif classifier_labels[i] == 1:
                count1 = count1 + 1
            elif classifier_labels[i] == 2:
                count2 = count2 + 1
            elif classifier_labels[i] == 3:
                count3 = count3 + 1
            elif classifier_labels[i] == 4:
                count4 = count4 + 1
            elif classifier_labels[i] == 5:
                count5 = count5 + 1
            elif classifier_labels[i] == 6:
                count6 = count6 + 1
            elif classifier_labels[i] == 7:
                count7 = count7 + 1
        print(count0)
        print(count1)
        print(count2)
        print(count3)
        print(count4)
        print(count5)
        print(count6)
        print(count7)


    def NaiveBayesClassifierModel(self,classifier_features_matrix,classifier_labels):


        x_train,y_train,x_test,y_test=self.split_data_train_test(classifier_features_matrix,classifier_labels,0.2)
        gnb = GaussianNB()
        gnb.fit(x_train, y_train)


        print(gnb.score(x_test,y_test))



    def KNearestNeighboursClassifierModel(self,classifier_features_matrix,classifier_labels, percentage_test_split,min_neighbours):

        KNC = KNeighborsClassifier(n_neighbors=min_neighbours)

        x_train, y_train, x_test, y_test = self.split_data_train_test(classifier_features_matrix, classifier_labels,percentage_test_split)

        KNC.fit(x_train, y_train)

        print(KNC.score(x_test,y_test))




    def SVM_Classifier(self,classifier_features_matrix,classifier_labels, percentage_test_split, penalty):

         x_train, y_train, x_test, y_test = self.split_data_train_test(classifier_features_matrix, classifier_labels,percentage_test_split)

         SVC_Classifier= SVC(decision_function_shape='ovo',c=penalty)
         SVC_Classifier.fit(x_train, y_train)
         print(SVC_Classifier.score(x_test,y_test))




