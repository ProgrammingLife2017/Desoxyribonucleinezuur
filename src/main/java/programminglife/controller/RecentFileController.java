package programminglife.controller;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import programminglife.gui.controller.GuiController;
import programminglife.utility.Alerts;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Class that handles the recentFiles menuItem.
 */
public class RecentFileController {
    private GuiController guiController;
    private File recentFile;
    private Menu menuRecent;
    private File file;
    private String recentItems = "";
    private int lineAmount;
    private String[] lines;
    private String file1;
    private String file2;
    private String file3;
    private String file4;
    private String file5;


    /**
     * Constructor for the recent file handler.
     * @param recentFile File containing the recent entries.
     * @param menuRecent Menu containing the recent entries.
     */
    public RecentFileController(File recentFile, Menu menuRecent) {
        try {
            findLines(recentFile);
        } catch (IOException e) {
            programminglife.utility.Console.println("Recent.txt doesn't exist. A new one will be created.");
        }

        this.recentFile = recentFile;
        this.menuRecent = menuRecent;
        initRecent();
        updateLines();
        doesFileExist();
    }

    private void updateLines() {
        lines = recentItems.split(System.getProperty("line.separator"));

        for (int i = 0; i < lineAmount; i++) {
            switch (i) {
                default:
                    continue;
                case 0:
                    file1 = lines[i];
                    break;
                case 1:
                    file2 = lines[i];
                    break;
                case 2:
                    file3 = lines[i];
                    break;
                case 3:
                    file4 = lines[i];
                    break;
                case 4:
                    file5 = lines[i];
                    break;
            }
        }
    }

    /**
     * Find the amount of lines in a given file.
     * @param recentFile File to check the amount of lines of.
     * @throws IOException Exception to be thrown when file can't be found.
     */
    private void findLines(File recentFile) throws IOException {
        LineNumberReader reader  = new LineNumberReader(new FileReader(recentFile));
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {
            lineAmount++;
        }
        reader.close();
    }

    /**
     * Read out the file which contains all the recently opened files.
     */
    private void initRecent() {
        try {
            Files.createFile(recentFile.toPath());
        } catch (FileAlreadyExistsException e) {
            //This will always happen if a user has used the program before.
            //Therefore it is unnecessary to handle further.
        } catch (IOException e) {
            Alerts.error("Recent.txt can't be opened");
            return;
        }
        if (recentFile != null) {
            try (Scanner sc = new Scanner(recentFile)) {
                menuRecent.getItems().clear();
                while (sc.hasNextLine()) {
                    String next = sc.nextLine();
                    MenuItem mi = new MenuItem(next);
                    mi.setOnAction(event -> {
                        try {
                            file = new File(mi.getText());
                            guiController.openFile(file);
                        } catch (IOException e) {
                            Alerts.error("Recent.txt can't be opened");
                        }
                    });
                    menuRecent.getItems().add(mi);
                    recentItems = recentItems.concat(next + System.getProperty("line.separator"));
                }
            } catch (FileNotFoundException e) {
                Alerts.error("Recent.txt can't be found.");
            }
        }
    }

    /**
     * Updates the recent files file after opening a file.
     * @param recentFile File containing the recent entries.
     * @param file File to check if it already contained.
     */
    public void updateRecent(File recentFile, File file) {
        updateLines();
        if (checkDuplicate(file)) {
            moveFiles(file);
            updateRecent(recentFile);
        }
    }

    /**
     * Updates the recent files file after opening a file.
     * @param recentFile File containing the recent entries.
     */
    private void updateRecent(File recentFile) {
        try (BufferedWriter recentWriter = new BufferedWriter(new FileWriter(recentFile))) {
            if (file1 != null) {
                recentWriter.write(file1 + System.getProperty("line.separator"));
            }
            if (file2 != null) {
                recentWriter.write(file2 + System.getProperty("line.separator"));
            }
            if (file3 != null) {
                recentWriter.write(file3 + System.getProperty("line.separator"));
            }
            if (file4 != null) {
                recentWriter.write(file4 + System.getProperty("line.separator"));
            }
            if (file5 != null) {
                recentWriter.write(file5 + System.getProperty("line.separator"));
            }
            recentWriter.flush();
            recentWriter.close();
            initRecent();
        } catch (IOException e) {
            Alerts.error("Recent.txt cannot be updated");
        }
    }

    /**
     * Checks if there is a duplicate.
     * @param file File to be added to the list.
     * @return boolean, true if it is not a duplicate.
     */
    private boolean checkDuplicate(File file) {
        boolean duplicate = true;
        for (String s : lines) {
            if (s.equals(file.getAbsolutePath())) {
                duplicate = false;
            }
        }
        return duplicate;
    }

    /**
     * Removes a file that cannot be opened anymore in any way.
     */
    private void doesFileExist() {
        if (file1 != null) {
            File f1 = new File(file1);
            if (!f1.exists()) {
                file1 = null;
            }
        }
        if (file2 != null) {
            File f2 = new File(file2);
            if (!f2.exists()) {
                file2 = null;
            }
        }
        if (file3 != null) {
            File f3 = new File(file3);
            if (!f3.exists()) {
                file3 = null;
            }
        }
        if (file4 != null) {
            File f4 = new File(file4);
            if (!f4.exists()) {
                file4 = null;
            }
        }
        if (file5 != null) {
            File f5 = new File(file5);
            if (!f5.exists()) {
                file5 = null;
            }
        }
        updateRecent(this.recentFile);
    }

    /**
     * Moves all the recentFiles down by 1 position.
     * @param file File to be added to the list.
     */
    private void moveFiles(File file) {
        file5 = file4;
        file4 = file3;
        file3 = file2;
        file2 = file1;
        file1 = file.getAbsolutePath();
    }

    /**
     * Sets the guicontroller for controlling the menu.
     * @param guiController The gui controller
     */
    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
}
