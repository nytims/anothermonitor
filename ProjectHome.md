# AnotherMonitor #

### <font color='red'><b>WARNING</b></font> (January 2015): the code and documentation of this project is totally outdated, it contains bugs, it does not support latest Android versions and it contains low quality, convoluted and [smelly code](http://en.wikipedia.org/wiki/Code_smell). DO NOT USE IT excepting for (at your own risk) checking specific or isolated parts of the project. A written-from-scratch version of AnotherMonitor can be downloaded from [Google Play](https://play.google.com/store/apps/details?id=org.anothermonitor). ###

### <font color='red'><b>GOOD NEWS!</b></font> The source code of the new version is available on [GitHub](https://github.com/AntonioRedondo/AnotherMonitor). ###

**AnotherMonitor** is an application for Android which monitors and records the memory and CPU usage values of the mobile phone. Thus, the program is intended for anyone whom would be interested in knowing the mobile resources state.

It has 2 main options:

  * It shows a graphic and several text labels wherein the values of the memory and CPU usage are updated in real time.

  * It can record in a CSV file the read values for a later usage and process in a spreadsheet program.

The program can be run in background. Then, the second option is specially interesting since, in background, **AnotherMonitor** consumes little resources and can monitor and record the memory and CPU usage values that another applications in foreground are using.

# Screenshots #

![http://anothermonitor.googlecode.com/files/main.png](http://anothermonitor.googlecode.com/files/main.png)
![http://anothermonitor.googlecode.com/files/mainopenmenu.png](http://anothermonitor.googlecode.com/files/mainopenmenu.png)
![http://anothermonitor.googlecode.com/files/mainrecordingmenu.png](http://anothermonitor.googlecode.com/files/mainrecordingmenu.png)
![http://anothermonitor.googlecode.com/files/mainpreferences.png](http://anothermonitor.googlecode.com/files/mainpreferences.png)
![http://anothermonitor.googlecode.com/files/about.png](http://anothermonitor.googlecode.com/files/about.png)
![http://anothermonitor.googlecode.com/files/mainopenmenucrazyicons.png](http://anothermonitor.googlecode.com/files/mainopenmenucrazyicons.png)

# GUI and different screen resolutions #
**AnotherMonitor** has been tested in screen resolutions from 320x240 up to 1280x720 with no problems. In screens with high resolutions (above 480x320) or high densities (above 160 dpi) the lines of the graphic are not very visible. This should be addressed in a hypothetical update.

The main window has a portrait and a landscape version. The landscape version changes the screen schema from up-down to left-right. And to fit small-ldpi screens in landscape mode there is another specific version. In total there are three different main.xml files. The three different windows of the application (Main, Preferences and About) use ScrollView class to assure that if the content of the window does not fill to scroll vertically the window.

The above screenshots are taken in a resolution of 480x320 portrait. Below there are another ones in 320x240 portrait, 320x240 landscape and 854x480 landscape (rescaled to 427x240).

![http://anothermonitor.googlecode.com/files/main320port.png](http://anothermonitor.googlecode.com/files/main320port.png)
![http://anothermonitor.googlecode.com/files/main320land.png](http://anothermonitor.googlecode.com/files/main320land.png)
![http://anothermonitor.googlecode.com/files/main854landscaled.png](http://anothermonitor.googlecode.com/files/main854landscaled.png)

# GUI languages #
**AnotherMonitor** interface is multilingual available in English, Spanish and Italian. If you are not using any of these languages English will be selected.

![http://anothermonitor.googlecode.com/files/LanguagesEN360.png](http://anothermonitor.googlecode.com/files/LanguagesEN360.png)
![http://anothermonitor.googlecode.com/files/LanguagesES360b.png](http://anothermonitor.googlecode.com/files/LanguagesES360b.png)
![http://anothermonitor.googlecode.com/files/LanguagesIT360b.png](http://anothermonitor.googlecode.com/files/LanguagesIT360b.png)

# Technical guide #
**AnotherMonitor** has a technical guide in PDF (with the original in ODT format) and in Wiki-HTML format where most of the aspects about the application programming are explained: the six classes, most important methods, notes about the program structure and performance, how to modify the program, a TODO list and FAQs.

Download the [PDF](http://code.google.com/p/anothermonitor/downloads/detail?name=AnotherMonitor1.0.xTechnicalGuide.pdf) or see the [Wiki-HTML version](http://code.google.com/p/anothermonitor/wiki/AnotherMonitorV10xTechnicalGuide).

# Current version and future releases #
To run the program you need some device with Android 1.6 (API level 4) or higher. The current version is the 1.0.1, built in July 2010. The app has been tested successfully in the emulator up to Android 4.0.3 (API level 15). **AnotherMonitor** is not going to be further developed. So I apologize for bugs that exist and will not be fixed.

# License and source code #
**AnotherMonitor** is Free and Open Source Software published under the GNU General Public License v3.

The source code is available at the [project SVN repository](http://code.google.com/p/anothermonitor/source/browse/#svn%2Ftrunk) and in the file [AnotherMonitor1.0.1AllFiles.zip](http://code.google.com/p/anothermonitor/downloads/detail?name=AnotherMonitor1.0.1AllFiles.zip) from the [Downloads](http://code.google.com/p/anothermonitor/downloads/list) section.