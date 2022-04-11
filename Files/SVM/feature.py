import cv2
import numpy as np
import os
import mahotas

from matplotlib import pyplot as plt

def getimagedata(img):
    rgbi = cv2.imread(img)
    rgbi = cv2.resize(rgbi, (500,500))

    hsvi = rgb2hsv(rgbi)
    seg = img_segmentation(rgbi, hsvi)
    hm = huMoments(seg)
    h = haralick(seg)
    his = histogram(seg)
    gf = np.hstack([his, h, hm])

    return gf

def rgb2hsv(rgbi):
    hsvi = cv2.cvtColor(rgbi, cv2.COLOR_RGB2HSV)

    return hsvi

def img_segmentation(rgbi, hsvi):
    lbrown = np.array([10, 0, 10])
    ubrown = np.array([30, 255, 255])
    lgreen=np.array([25, 0, 20])
    ugreen=np.array([100, 255, 255])

    mask1 = cv2.inRange(hsvi, lgreen, ugreen)
    mask2 = cv2.inRange(hsvi, lbrown, ubrown)
    mask3 = mask1 + mask2

    feature = cv2.bitwise_and(rgbi, rgbi, mask=mask3)
    return feature

# Hu Moments
def huMoments(img):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    ft = cv2.HuMoments(cv2.moments(img)).flatten()
    return ft

# Haralick Texture
def haralick(img):
    hui = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    h = mahotas.features.haralick(hui).mean(axis=0)
    return h

def histogram(img):
    img=cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h=cv2.calcHist([img],[0,1,2],None, [8, 8,8],[0, 256,0, 256, 0,256])
    cv2.normalize(h, h)
    f = h.flatten()
    return f

if __name__ == '__main__':
    maindir = r'F:\emeraldfile\2021-2022S1\Project\project\images_handheld.tar_2'
    img_files = os.listdir(maindir + "\\images_handheld")
    a = getimagedata(img_files+"\\DSC00025.JPG")
    plt.imshow(a)
    plt.tight_layout()
    plt.show()