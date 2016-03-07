**Note**: this is not a user guide.

**Note 2**: at the moment of writing this guide I had a low level of English. The guide has to be corrected or rewritten, but this is not going to happen in short or medium term. Sorry about this.

This guide is also available in [PDF](http://code.google.com/p/anothermonitor/downloads/detail?name=AnotherMonitor1.0.xTechnicalGuide.pdf).

# Index #


# 1 Introduction #
AnotherMonitor is an application for the Android platform which monitors and records the memory and CPU usage values of the mobile phone. Hence, this program is intended for anyone whom would be interested in knowing the mobile resources state and doing something in function of these values.
It has 2 main options:

  * It shows a graphic and several text labels wherein the values of the memory and CPU usage are updated in real time.

  * It can record in a CSV file the read values for a later usage and process in a spreadsheet program.he program, they could be implemented.

![http://anothermonitor.googlecode.com/files/main.png](http://anothermonitor.googlecode.com/files/main.png)

The program can be run in background. Then, the second option is specially interesting since, in background, AnotherMonitor consumes little resources and can monitoring and record the memory and CPU usage values that another applications in foreground are using.


# 2 Technical details #
AnotherMonitor is wrote in Java with the Eclipse IDE and the ADT plugin. The minimum requirements for the application are a compatible device with Android 1.5 or higher. AnotherMonitor has been tested successful up to Android 2.2.

As it could not be in another way, it uses the application components architecture of the Android platform, that is it, the so called activities and services.

In Android, In general terms, the activities are the visible part of the program which shows information and interacts with the user. It is the graphical user interface (GUI). In another hand, services are usually the part of the program which has no need to interact with the user, it has no GUI and often runs repetitive tasks.

AnotherMonitor has 3 activities, one for each window: the main one, the Preferences one and the About one. The more important activity is the main one. It is the first code of all the program what Dalvik Virtual Machine runs. It shows (but does not make) the graphic and the text labels, it starts the service and manage another minor task like the main menu. The Preferences activity opens a new window where the user can configure several parameters of the program. And the About activity shows a window where information about AnotherMonitor and his author appears.

The service takes care of reading, and if the user indicates it, it records in a CSV file the values of the memory and CPU usage every time interval (by default, 1 second). These CSV files are saved in the data/data/com.anothermonitor/files folder of the mobile. Afterwards, these values are used by the main activity to show them to the user. To do so, the activity connects to the service and then, the first receive a virtual object of the service class. It is a virtual object in place of a real object because another activities can simultaneously connect to the same service and they would receive the same object which if any connected activity would modify this object, the changes would be visible for all the rest of connected activities.

The code what reads and records the different values is in the service of the program instead of the main activity because when an activity go to the background it will be killed when the mobile has not memory (it occurs with frequency). In another hand, the services has more preference than activities and they be only killed in critic low memory situations (not often). Also, this allows another applications being run whereas the service reads values in the background.


## 2.1 Program structure ##
The calls among the different classes of the program are as it follows:

![http://anothermonitor.googlecode.com/files/Screenshotforguide-AnotherMonitorJavaarchitecture.png](http://anothermonitor.googlecode.com/files/Screenshotforguide-AnotherMonitorJavaarchitecture.png)

Below it is explained the most important features and methods of each class, that is it, not all the methods and types are explained. If you want to understand the program in detail and how it works the best way is you read the source code directly, it is well commented.

**AnotherMonitor.class**

It is the main activity and the first run code of all the program. In its onCreate() method it loads or creates the preferences, starts the service and loads the GUI from the main.xml file. The interface mConnection() is called as soon as the bind between the activity and the services is created. This implemented interface pass the vectors of each read value of AnReaderService class to AnGraphic class and starts to run the Runnable drawRunnable(). This Runnable is whom updates repetitively every time interval the text labels and the graphic.

![http://anothermonitor.googlecode.com/files/mainopenmenu.png](http://anothermonitor.googlecode.com/files/mainopenmenu.png)

Another less important features of AnotherMonitor class are the methods related with the management of the main menu: onCreateOptionsMenu(), onPrepareOptionsMenu(), and setMenuIcons().

The first one loads the menu from the menu.xml file and check if the flag myAnReaderService.RECORD is true. If it is true it means that this activity was relaunched  when the service was already created because the activity was in the background or it was killed by lack of memory. In this case, the record icon is changed to the stop recording and save icon. The onPrepareOptionsMenu() and setMenuIcons() methods are used to check and change the appearance of the menu icons between the normal icons and the happy ones. Yeah, this option is not very useful... But it is cool!

**AnReaderService.class**

The main scope of this class is to be run like a service and reading and (if selected) recording the memory and CPU values of the mobile Linux system. The first called method, onCreate(), creates the vector for every value and shows the Android status bar notifications.

The created vectors will be passed through AnotherMonitor class to an instance of AnGraphic to be drawn in the graphic. The view AnGraphic does not connect directly to the service because, as a view, it extends View, and then, it can not extends Activity to can connect with the service (see further in the section TODO).

The data structure selected to keep the values is a vector (implemented in Java by the class Vector) in place of another like queues because the class Vector is the one that allows to see, modify or remove any element of the structure without the need of that element be in the first position.

To read the values, from the onCreate() method is created and started a new thread where from which is called every time interval to the read() method. This method reads and saves values, and if the AnReaderService.RECORD flag is true, call to the record() method.

The memory values are read directly from the meminfo file of the Linux proc file system. In another hand, the CPU usage values do not appear in any place and they must be calculate from the values of the stat file and the other stat file of the AnotherMonitor process. It is possible that negative values or values higher than 100% appear. See with more detail how to calculate the CPU usage values on http://stackoverflow.com/questions/1420426 and http://kernel.org/doc/Documentation/filesystems/proc.txt.

The stopRecord() is called when we want to stop a record and the getOutputStreamWriter() and getDate() methods are exclusively used by record().

**AnGrapic.class**

The AnGraphic class takes care of drawing and updating every time interval the graphic. Implements View and then, on the onDraw() method that every class what implements View has to have, it draws line to line the background, their grid and edge lines and each one of the memory and CPU usage values lines through the methods drawRect() and drawLine().

This class is a big resources consumer because the reiterative calling to the drawRect() and drawLine() methods every time interval. Thus, the onDraw() method implements the less needed number of operations to show the graphic, but unlucky, they are much.

The initializeGraphic() is called exclusively by onDraw() every time we have to initialize the parameters of the graphic, it is to say, when it is the first time we draw the graphic or when the size or the colors of the graphic changes. In this case, the flag AnGrapic.INITIALICEGRAPHIC will be true and the initializeGraphic() will be called.

With setVectors() we set the vectors up to show. And the getPaint() method is exclusively used by initializeGraphic() to creates a new attributes Paint object.

**AnPreferences.class**

This class has only 2 methods: readPref() and writePref(). ReadPref() reads (or creates the default values if is there is no saved values) the permanently saved values from the mobile for the static parameters of AnotherMonitor through the SharedPreferences Android class. writePref() modifies and save this values when is called from the AnPreferencesWindows class.

**AnPreferencesWindow.class**

This class is intended to allow the user configuring some parameters of AnotherMonitor. It is the typical preferences window of any program. It shows (whereby an activity) a window with 4 tabs: Main, Appearance, Read/record and Draw. The Main tab allows configure the 3 most important parameters of the program, the Read interval, the Update interval and the Width interval. The Appearance tab allows to select the Happy menu icons and change the color of the graphic background and the graphic lines. Finally, the Read/record and Draw tabs allow to select or unselect the different values to be read/record and draw, respectively.

![http://anothermonitor.googlecode.com/files/mainpreferences.png](http://anothermonitor.googlecode.com/files/mainpreferences.png)

When the button OK is pressed the activity connect to the AnReaderService service and it removes all the elements of all the read values vectors if the Read interval changes or removes all the elements of some read value vector if it is unselected.

This activity could have used the classes of the android.preference packet to build the GUI, but I think the current preferences window is a little more intuitive and/or easy of understand. Anyway, it is not an important aspect.

**AnAbout.class**

Show a window with information about the program and the author. There is no much to explain.

![http://anothermonitor.googlecode.com/files/about.png](http://anothermonitor.googlecode.com/files/about.png)


## 2.2 Program performance ##
Since the program scope is to monitor the memory and CPU usage, that the own application consumes little resources is fundamental. Unlucky, this premise is not always carried out by AnotherMonitor.

The performance of the application is different depending in what mode is and, of course, the power of the mobile. If it is in foreground showing the graphic, the CPU usage to do so is high, around 40-70%, but it varies quite much depending of the Read, Draw and Width intervals. The lower parameters, the lower CPU usage. Play with different configurations yourself and select the best one for you.

On another note, if AnotherMonitor is in background reading values, the CPU usage is low, it depends of the read interval, but generally it is around 5-10%. If the application is also recording the usage does not almost increase.

In the foreground mode, the code that is recursively running is a new thread in an AnReaderService instance which is reading values, and a runnable in the main application thread updating the graphic. This runnable updates every interval of time all the text labels and call to the invalidate() inherited method of the AnGraphic instance (which call to onDraw()) to redraw newly all the whole graphic. Although it follows the recommend implementation to update views and layouts (http://developer.android.com/resources/articles/timed-ui-updates.html), this operation consume quite resources.

Besides, if the application is in the background mode, the one running code is the reading thread which call to the method read() to access to the memory and CPU statics of the Linux mobile machine. This operation consume little resources. This operation is in a new thread to not block the availability of the main activity thread, like it is recommended to operations what runs in background: http://developer.android.com/guide/topics/fundamentals.html#threads.


## 2.3 GUI and different screen resolutions ##
AnotherMonitor support all possible resolutions of the Android platform as a 2.2 version, that is it, from 320x240 up to 854x480. If in the future the Android platform is going to support more new high resolutions, the current version of AnotherMonitor should not have problems to manage them.

The main window has a portrait and a landscape version. The landscape version changes the screen schema from up-down to left-right. And to fit to small-ldpi screens in landscape mode there is another specific version. In total there is 3 different main.xml files. In another front, the 3 windows of the application, the Main one, the Preferences one and the About one, use SrcollView views to assure if the content of the window does not fill to scroll vertically the window.

The screenshots showed in the guide are token in a resolution of 480x320. Below, you can see different screenshots taken in the problematic resolutions of 320x240 and 854x800 on portrait and landscape mode:

![http://anothermonitor.googlecode.com/files/main320port.png](http://anothermonitor.googlecode.com/files/main320port.png)

The above screenshots are taken on a resolution of 320x240 in portrait mode. In this version it uses the same main.xml file than on higher resolutions. Although, programmatically, there are some control flow statements in the AnGraphic class that, depending of the screen configuration, modify 2 boolean fields, LITTLEPORT and LITTLELAND, which are used in the onDraw() method to draw the legend text in different sizes.

In the 320x240 landscape version it used a different main.xml with the manifest attributes of layout-small-land-ldpi. This main.xml, moreover than changing the screen schema from up-down to left-right, it shrinks the text to fit to the little screen.

![http://anothermonitor.googlecode.com/files/main320land.png](http://anothermonitor.googlecode.com/files/main320land.png)

To finish, they are showed some screenshots taken on the 854x480 landscape mode. In this mode, it can be saw there is much more space than enough.

![http://anothermonitor.googlecode.com/files/main854landscaled.png](http://anothermonitor.googlecode.com/files/main854landscaled.png)


## 2.4 Versioning ##
The way to versioning AnotherMonitor is easy. The number version of the application has three numbers. A change in any of the number should indicate as follows:

  * x.x.0 > x.x.1: the increase of the right number indicates that some kind of bug, little or big, has been resolved. Also, it can indicate little changes in the code to improve performance. Little changes in the GUI could be taken out.

  * x.0.x > x.1.x: the change of the middle number indicates as above and, in addition, some new feature (not very important) or relevant change of something already programmed.

  * 1.x.x > 2.x.x: the increase of the left number indicates as above, and furthermore, a big change like a new interesting options or big change in the GUI.

The increment of the number does not finish in 10, that is it, I could exist the 1.2.14 version or the 2.17.3 version. Every change in the code should be reflected in this guide, at less changes of the middle or left number.


# 3 Modifying the program #
AnotherMonitor is an open source application. Hence, you can modify the application in the way you want. But they are some key points of the program where would be interesting the modification to a more customizable use depending of the needs who is using it.


## 3.1 Add new values to read and record ##
By default, AnotherMonitor reads 8 memory values and 3 CPU usage values. But it is easy to add programmatically more values to read and record.

In the AnReaderService class is the read() method. This method is all what the program need to read values. Additionally, if it is recording values, the record() method is called once from read() every time interval. Let's see part of the read() method:

```
	while (memFree.size()>=AnotherMonitor.TOTAL_INTERVALS) memFree.remove(memFree.size()-1);

	while (buffers.size()>=AnotherMonitor.TOTAL_INTERVALS) buffers.remove(buffers.size()-1);

	...

	while (cPUAMP.size()>=AnotherMonitor.TOTAL_INTERVALS) cPUAMP.remove(dirty.size()-1);

	while (cPURestP.size()>=AnotherMonitor.TOTAL_INTERVALS) cPURestP.remove(dirty.size()-1);

	if (x.startsWith("MemTotal:")) memTotal = Integer.parseInt(x.split("[ ]+", 3)[1]);

	if (AnotherMonitor.MEMFREE_R && x.startsWith("MemFree:")) memFree.add(0, x.split("[ ]+", 3)[1]);

	...

	if (AnotherMonitor.SWAPTOTAL_R && x.startsWith("SwapTotal:")) swapTotal.add(0, x.split("[ ]+", 3)[1]);

	if (AnotherMonitor.DIRTY_R && x.startsWith("Dirty:")) dirty.add(0, x.split("[ ]+", 3)[1]);
```

The first block of sentences removes all the values of every vector that surpasses the total interval value (AnotherMonitor.TOTAL\_INTERVAL) value, that is it, if it is not going to be drawn it is not worthy and then we should get rid of it. A vector can surpass the total interval value when the width interval (AnotherMonitor.WIDTH\_INTERVAL) is changed from the AnPreferencesWindow activity.

The second block read each one of the values from the meminfo Linux file (http://en.wikipedia.org/procfs). It is here where you should add another memory values to read. If you do so remember to create a new vector to save the read values, to add the options to select/unselect the value in the AnPreferencesWindow class, modify ArGraphic class to draw the line value and modify AnotherMonitor class and the main.xml file to update the text label.

## 3.2 Modifying the graphic appearance ##
In the AnGraphic class are all the methods which manage the appearance of the graphic. The main method is the override onDraw(). This is called each time interval by the runnable of AnotherMonitor class and draws the graphic. If it is the first time the graphic is drawn or some parameter has changed, the AnGraphic.INITIALICEGRAPHIC flag will be true, and then, the initializeGraphic() method will be called. In this method it calculates the size of  the graphic and they are created the Paint objects that contain the properties of the drawn lines (color, width, etc) that will be used by the drawLine() methods on onDraw().

In the initializeGraphic() is where you could do deep changes to the appearance of the graphic changing the colours, adding/removing grid lines or anything whatever you want.


## 3.3 Modifying the time and graphic width intervals ##
In the application there is a number of static fields which are the application global parameters. These parameters are the read interval (AnotherMonitor.READ\_INTERVAL), the  update interval (AnotherMonitor.UPDATE\_INTERVAL) and the width interval (AnotherMonitor.WIDTH\_INTERVAL). The read interval is how often the values are read, by default, every second. The update interval is how often the graphic and the text labels are updated, by default, every 4 seconds. And the width interval is the width in pixels of the x axis interval, by default, 5 pixels. Every parameter has a default value and 3 additional values that the user can switch (the read interval, 0.5, 1, 2 and 4 s; the update interval, 1, 2, 4 and 8 s; and the widht interval, 1, 2, 5, 10 px). There is another static field, AnotherMonitor.TOTAL\_INTERVALS, which is the total initial number of elements in every vector. It has a default value of 440 but it is subsequently changed automatically with the number of values that they can be shown in the graphic (it based in the graphic width and the width interval value).

Then, you would be interested in add, modify or removes the values of these static fields. These parameters are load and set up to default in the AnPreferences class through the SharedPreferences Android class. Remember that if you do any modification in these parameters it will must be reflected in the AnPreferencesWindows class GUI.


## 3.4 Localization. Adding more languages ##
AnotherMonitor is available in languages: English (default), Italian, Spanish and French.

To localize an Android application is really easy (http://developer.android.com/guide/topics/resources/localization.html), the programmer has just to do each one of the strings.xml files with the corresponding language an put them in the respective res/values folder with the two letters code. The language of the application will be selected by Android based on the language configuration of the mobile.

But unlucky, the one way to select a language is automatically by Android, and the user can not select another different language, just change the language configuration of the mobile, and then, the application will be change to match with the new language configuration.

For that reason, the language automatic selection is a little intrusive and, may be, it could be in some cases deactivate it simply moving the values-(language-code) folder to another place.


## 3.5 Modifying any another aspect ##
AnotherMonitor is a open source application and it is not a complex project. So is quite easy to understand how it works. Go ahead with any modification or improvement what you want to do!


# 4 TODO #
As in any computer program, we could say that the finish of its development is almost impossible to reach. For that reason, here there are some suggests that, after cover the basic functions of the program, they could be implemented.

  * Best performance showing the graphic. This could be attain separating the graphic in another new independent view, so the legend and the edge lines would only be drawn the first time and not continuously as it happens currently. The problem is when the invalidate() method of the separated graphic is called. The graphic view is superimposed to the legend view, then, the first also invalidates the legend view and it is redrawn every time. So the result is the same that drawing the graphic in only one view.

Another possible solution to this would be to use the SurfaceView class to create the separated graphic view (http://developer.android.com/guide/topics/graphics).

  * To try connect the AnGraphic class directly to the AnReaderService service instead of through AnotherMonitor. May be with an inner class in AnGraphic?

  * To let to the user to choice saving CSV files in the internal memory of the mobile or in the memory card.

  * To create a more visible graphic to high resolution screens (higher than 480x320).

  * Assure at 100% that when the program is recording values, if the mobile suddenly turns off, the last part of the recorded data will not be lost. This would be happen because, in AnReaderService class, the record() method does not flush and close the buffer due to performance reasons (the read() and record() methods must consume the less resource as possible). Then, the part of the data stored in the buffer that has not been written to the memory will be lost. Usually it will not be more than 10-20 lines. A solution would be implement a new record() method which flush and close the buffer every time record() is called and allow to the user to choice between the 2 modes.

  * Use get(), set() and is() methods to know the value of static fields-flags instead of doing it directly. It will add clarity to the project.

  * From the version 1.0.0 to create a version log that reflects all changes in the code.

  * Improve this guide and its PDF version and keep them updated.

  * To can play Crysis on the mobile.

# 5 Frequently asked questions (FAQ) #

**Is AnotherMonitor FOSS? Where can I get the source code?**

Yes, AnotherMonitor is a Free and Open Source Software published under the GNU General Public License v3. You can get the source code of the application in Eclipse Project format, the APK Android application file, the original ODT (and the ODG graphic files) of this technical guide and the original CPT (Corel Photo-Paint) file icons in the project website: http://code.google.com/p/anothermonitor.

**After a record, where are the CSV files saved?**

The CSV files are saved in the data/data/com.anothermonitor/files folder of the mobile.

**The private key to sign the APK file is not provided. Why?**

The private key is that, private and personal. If you want to distribute a fork or derived program from AnotherMonitor you will must sign it with your own private key.

**Will AnotherMonitor be further developed?**

As a version 1.0.0, due to student affairs, the author has no intention to follow developing AnotherMonitor in short or middle term. But you know, AnotherMonitor is open source...

**Why does this guide look like if it was wrote by an illiterate, full of mistakes and weird phrases?**

The author of the program is a spanish. What can you expect about him? Anyway, a noted down task in the 4. TODO section is to improve this guide.

**Who is the author of AnotherMonitor?**

The author is a Spanish young Telecommunications engineer, Antonio Redondo López. Due to his visionary enterprising talent he is gonna to start a lecture tour around the world in company with Richard Stallman, Steve Jobs and Steve Ballmer (yes, all together) where the importance of the lectures contents will be insignificant compared with the created hype. Meanwhile, you can have the privilege of being friend of him on Facebook: Axe Effect.

**AnotherMonitor. What kind of name is that?**

In the Android platform there are another resources monitors (as for instance the recommended aiSystemWidget) although they falls in aspects like open source or value records storage. AnotherMonitor becomes a complementary solution. An possibility would be call it AndroidResourcesMonitor or something else, but these kinds of names are often used yet, and I was fed up of thinking. Then, AnotherMonitor was the easy solution for the name. I think I will use this methodology when I will have a second son.

**Will I able to play Crysis in my Android mobile with AnotherMonitor?**

Yeah, of course! There is a ongoing development which you will be able to play Crysis in your Android mobile with improved graphics and HDfull resolution. The release of the new version will be soon!

**Are you convinced these silliness are FAQs?**

Eih, c'mon! I try to do it as better as I know!