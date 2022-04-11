import os
from sklearn import svm
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.model_selection import train_test_split as tts
import joblib

def SVM(X,Y):
    x_train, x_test, y_train, y_test = tts(X, Y, test_size = 0.3)
    cv = StratifiedKFold(n_splits = 10)
    classifier = svm.SVC(kernel='rbf',C = 8)
    results = cross_val_score(classifier, x_train, y_train, cv = cv, scoring="accuracy")

    msg = "%s:%f%s: (%f)" % ("resultMean: ",results.mean(), "resultStd: ", results.std())
    print(msg)

    classifier.fit(x_train, y_train)
    pre = classifier.predict(x_test)
    print(pre,y_test)

    confustionm = confusion_matrix(y_test, pre)
    print(confustionm)
    print(classification_report(y_test, pre))
    score = classifier.score(x_test, y_test)
    print("Accuracy: ", score)

    os.chdir(r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2\SVM')
    joblib.dump(classifier, "train_model.m")