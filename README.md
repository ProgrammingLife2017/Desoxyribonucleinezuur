# Multi genome visualizer

## Quick overview
This is a program for visualizing the differences between genomes in huge data files. It will represent these as graphs, where each node represents a nucleotide sequence. Differences in nucleotide sequences are represented as nodes splitting into multiple different nodes, connected by edges. There is support for bookmarks, a minimap for quick navigataion, the highlighting of user-specified genomes, and info about certain nodes.
You can download a binary (`.jar`) of this program in code -> releases. Note that you need a 64 bit JVM (Java Virtual machine) with Java 8 to run it. You can download the latest Java JVM at this link: https://www.java.com/en/download/.

## Detailed overview
### Opening files
This program opens `.gfa` files. The first time these are opened, their structure will be saved in a more time-efficient format to caches. This means that opening a file the first time can take quite long (~20 minutes for 2GB gfa file), but after that first time it will open files within a few seconds.
To open a graph, go to file -> open GFA and select the file you want to open, or use file -> Open Recent GFA for recently opened files.

The cache files are saved in a folder named caches in the same folder as the jar. You can delete this folder or individual files in this folder if you need to, but it means that the file will need to be parsed again if you open it. Also note that these caches are often bigger than the original file, to make it easier to fetch all the required information.

### Visual information
The color of nodes turns a darker blue as more genomes go through that node. Edges become darker and wider when more genomes flow through them. It is possible to highlight genomes. Select the "Highlight" tab on the right pane. There is a search box for filtering genomes, and two lists. The left list shows all genomes that are not highlighted, the right list shows all genomes that are highlighted with their color. You can move genomes between lists by double clicking or selecting them (possible to use CTRL or SHIFT to select multiple) and then clicking the `->` or `<-` button between the lists.
You can also filter on nodes with a certain minimum and/or maximum number of genomes, their outline will get slightly thicker.

### Navigating the graph
You can navigate the graph by dragging on the main screen to pan, or use the scroll wheel or mouse pad (pinch) to zoom. The graph will automatically be loaded and unloaded outside the screen to provide smooth navigation and reduce memory impact. There is a minimap showing your approximate locaion in the graph.

For going to specific locations, there are three ways:
- use bookmarks, see next section
- In the left pane, you can fill in a node ID to navigate to and then press ENTER or use the 'go to node' button
- You can click on the minimap to navigate to that location.
- Use the 'surprise me' button in the left pane. It just loads a random part of the graph.

### Bookmarks
If you find an interesting location, you can save it using the bookmark button on the left pane. This opens a popup where you can fill in a name and a description. Should you need to, you can also give the exact node id you want the bookmark to be about. The node ID defaults to the highest node in the column in the middle of the screen, which should be close enough for almost all cases.
You can open bookmarks with Bookmarks -> Open Bookmark (CTRL+B). It will show a list of all files you have bookmarks for, with the current graph (if any) already open. You can open a bookmark by double clicking or selecting it and then clicking the open button.

### Miscellaneous features
- Dark theme: use view -> Toggle Dark or CTRL + Z to switch to a dark theme.
- Console: use view -> Toggle console to show a console with a bit of debugging information.
- Instructions: Use help -> instructions to get some text with basic instructions on using the program. 
- About: In help -> about, there is some basic information about the program and its creators.

## About this project
This program was made for a project course for computer science at the TU Delft in the Netherlands, as a first "real world" development experience.
It was developed over the course of two months by 5 computer science students in a group named "Desoxyribonucleïnezuur", which is the Dutch word for DNA.
