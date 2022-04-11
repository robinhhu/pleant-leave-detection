import os
import joblib


def predict(X,Y):
    os.chdir(r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM')
    model = joblib.load("train_model.m")

    pre = model.predict(X)
    print(pre, Y)

    score = model.score(X, Y)
    print("Accuracy= ", score)