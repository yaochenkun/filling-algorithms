# FillingAlgorithms
A testing program that gathers 4 Filling Algorithms based on Android and implemented in Java. These algorithms are:
* __Seed Filling Algorithm (种子填充算法)__:<br>
  需事先给定一像素点作为种子点，再以该种子点为起点朝四连通或八连通方向递归填充，但仍基于像素，且区域内每一像素点均需入栈，易堆栈溢出。<br>
  In this program this algorithm is easily to cause 'Stack Overflow', so it is usually Not Recommended。
  
* __Point-by-point Filling Algorithm (逐点判断算法)__:<br>
  基于像素，对绘图窗口内每一像素点进行射线环绕探测来实现内点判定，孤立地考虑像素点与区域间的关系，计算量较大。
  
* __Scanline Filling Algorithm (扫描线填充算法)__:<br>
  扫描线填充算法也需给定一种子点，分别水平向右和向左地探测得到图形边界点，填充两端点之间的线段，并让该线段之上和之下的任意内点入栈，继而栈顶像素点出栈作为新的种子点，重复上述操作至栈空。
  
* __Active-edge-table Filling Algorithm (活性边表填充算法)__:<br>
  该算法让扫描线以1像素的增量自下而上地扫描多边形，并利用边的斜率k计算增量值，从而快速定位下一步的端点坐标。<br>
  In this program, I have improved the traditional active-edge-table filling algorithm and make it enable to adaptively find the better scanning direction.If you want to learn more please refer to file '一种强鲁棒性自适应活性边表算法.pdf' in this repository.
  
## ScreenShot
![](http://yaochenkun.cn/wordpress/wp-content/uploads/2017/01/Screenshot11.png)

## How to Use and Test
1. Draw a polygon on the canvas as you want.
2. Click a button below (i.e. a filling algorithm) to fill the polygon with red color.__Notes__:
 * if you choose __'Seed Filling Algorithm (the first button)'__ or __'Scanline Filling Algorithm (the third button)'__, you must have to click anywhere in polygon to appoint a point (i.e. the seed). Only in this way can these 2 filling processes begin.
 * if you choose __the other algorithms__, you don't have to take any other subsequent actions.
3. __The other buttons' usages__:
 * the __first three__ buttons in __the second row__ respectively mean scaning the polygon in portrait, landscape and the best direction __when use Active-edge-table Filling Algorithm__.
 * the __last__ button in __the first row__ means scanning without checking and correcting the x coordinates of the adjoining crossover points in polygon __when use Active-edge-table Filling Algorithm__.
 * the __last__ button in __the second row__ means clearing the canvas.

## Environment and Settings
* Download and install [Android Studio](http://www.android-studio.org/index.php/component/content/category/88-download).
* Install __Android SDK 4.4__ but is not necessary.
* Import the whole project __'ScanlineTest'__ into Android Studio.
* At last, maybe you should enter __'File > Settings > Editor > File Encodings'__ and adjust __'IDE Encoding'__, __'Project Encoding'__ and __'Default encoding for properties files'__ to __'GBK'__.

## For More
If you are interested in 'Filling Algorithms', you could refer to some books about 'Computer Graphics'.
