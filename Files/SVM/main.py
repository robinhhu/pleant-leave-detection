import os
import h5py as h5py
import joblib
import numpy as np
import pandas as pd
import feature
import svm
import predict

h5_train_data = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM\Output\train_data.h5'
h5_train_labels = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM\Output\train_labels.h5'
h5_test_data = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM\Output\test_data.h5'
h5_test_labels = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM\Output\test_labels.h5'

def color(path):
    a = feature.getimagedata(path)
    return a

def prepareData():
    maindir = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2'
    dataset = pd.read_csv(maindir + "\\annotations_handheld.csv", usecols=[0, 1, 2, 3, 4],
                          names=['image', 'x1', 'y1', 'x2', 'y2'])
    img_files = os.listdir(maindir + "\\images_handheld")
    dataset['flag'] = dataset['x1'] + dataset['y1'] + dataset['x2'] + dataset['y2'] == 0

    imgData = []
    labels = []
    i = 0
    for file in img_files:
        i += 1
        name = dataset.loc[dataset['image'] == file]
        label = name.iloc[0, 5]
        l = 1
        if label :
            l = 0
        path = maindir + "\\images_handheld\\" + file
        colorv = color(path)

        imgData.append(colorv)
        labels.append(l)
        if i > 1520:
            break

    print("Vector size {}".format(np.array(imgData).shape))
    print("Labels {}".format(np.array(labels).shape))

    from sklearn.preprocessing import MinMaxScaler

    scaler = MinMaxScaler(feature_range=(0, 1))
    scaler.fit(imgData)
    scaler_filename = "scaler.save"
    joblib.dump(scaler, scaler_filename)
    scaled = scaler.transform(imgData)

    h5fd = h5py.File(h5_train_data,'w')
    h5fd.create_dataset('dataset_1', data=np.array(scaled))

    h5fl = h5py.File(h5_train_labels,'w')
    h5fl.create_dataset('dataset_1', data=np.array(labels))

    h5fd.close()
    h5fl.close()

def prepareTestData():
    maindir = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2'
    dataset = pd.read_csv(maindir + "\\annotations_test.csv", usecols=[0, 1, 2, 3, 4],
                          names=['image', 'x1', 'y1', 'x2', 'y2'])
    img_files = os.listdir(maindir + "\\testset")
    dataset['flag'] = dataset['x1'] + dataset['y1'] + dataset['x2'] + dataset['y2'] == 0

    imgData = []
    labels = []
    i = 0
    for file in img_files:
        i += 1
        name = dataset.loc[dataset['image'] == file]
        label = name.iloc[0, 5]
        l = 1
        if label:
            l = 0
        path = maindir + "\\testset\\" + file
        #print(path)
        colorv = color(path)

        imgData.append(colorv)
        #print(image_data)
        labels.append(l)
        #print(labels)
        if i > 260:
            break

    print("Vector {}".format(np.array(imgData).shape))
    print("Labels {}".format(np.array(labels).shape))

    sfile = "scaler.save"
    scaler = joblib.load(sfile)
    scaled = scaler.transform(imgData)

    h5fd = h5py.File(h5_test_data,'w')
    h5fd.create_dataset('testset_1', data=np.array(scaled))

    h5fl = h5py.File(h5_test_labels,'w')
    h5fl.create_dataset('testset_1', data=np.array(labels))

    h5fd.close()
    h5fl.close()

def train():
    h5fd = h5py.File(h5_train_data, 'r')
    h5fl = h5py.File(h5_train_labels, 'r')
    featuresAll = h5fd['dataset_1']
    labelsAll = h5fl['dataset_1']

    features = np.array(featuresAll)
    labels = np.array(labelsAll)

    h5fd.close()
    h5fl.close()

    svm.SVM(np.array(features), np.array(labels))

def test():
    h5fd = h5py.File(h5_test_data, 'r')
    h5fl = h5py.File(h5_test_labels, 'r')
    featuresAll = h5fd['testset_1']
    labelsAll = h5fl['testset_1']

    features = np.array(featuresAll)
    print(features)
    labels = np.array(labelsAll)

    h5fd.close()
    h5fl.close()

    predict.predict(np.array(features), np.array(labels))


if __name__ == '__main__':
    # prepareTestData()
    test()
    # prepareData()
    # train()


