package com.fhm.filehistnew;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderProcess {

    public void process() {
        // get path form user
        String path = getFolderPath();
        processFiles(path);
    }

    private String getFolderPath() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path of the folder to be checked: ");
        String pathToFolder = scanner.nextLine();
        return pathToFolder;
    }

    private List<File> getFilesInPath(String path) {
        try {
            Stream<Path> walk = Files.walk(Paths.get(path));
            List<File> filesInFolder = walk.filter(Files::isRegularFile).map(name -> name.toFile()).collect(Collectors.toList());
            return filesInFolder;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ValidationException("File path not correct! Please provide a correct file path.");
        }
    }

    private FileDetails mapFileDetails(File file) {
        return new FileDetails(file);
    }

    private void saveAsJson(List<FileDetails> fileDetails, String path) {
        String json = new Gson().toJson(fileDetails);
        File file = new File(path + "\\folderRecord.json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            printWriter.write(json);
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watchForChanges(String path){

        try(WatchService watchService = FileSystems.getDefault().newWatchService()){
            Map<WatchKey, Path> keyMap = new HashMap<>();
            Path folderPath = Paths.get(path);
            keyMap.put(folderPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY), folderPath);

            WatchKey watchKey;

            do {
                watchKey = watchService.take();
                Path eventDir = keyMap.get(watchKey);

                for(WatchEvent<?> event : watchKey.pollEvents()){
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();
                    System.out.println(eventDir + " " + kind + " " + eventPath);
                }
                processFiles(path);
            }while (watchKey.reset());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void processFiles(String path){
        // get files in that pats using stream
        List<File> filesList = getFilesInPath(path);
        // map to file details
        List<FileDetails> fileDetails = filesList.stream().map(this::mapFileDetails).collect(Collectors.toList());
        // save in json
        saveAsJson(fileDetails, path);
        //track file history
        watchForChanges(path);

    }

}
