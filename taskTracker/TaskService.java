import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService {

    private static String path = "taskList.json";
    private List<Task> tasks = new ArrayList<>();

    public TaskService(){}

    public void addTask(String description) throws IOException {
        if(Files.exists(Path.of(path))){
            tasks = readJsonFile();
            tasks.add(new Task(description));
            tasks.get(tasks.size()-1).setId(tasks.size());
            createJsonFile(formatJson(tasks));
        } else {
            Files.createFile(Path.of(path));
            tasks.add(new Task(description));
            createJsonFile(formatJson(tasks));
        }
        System.out.println("Task added successfully (ID: "+ tasks.size() + ")");
    }

    public void updateTask(int id, String description) {
        if(Files.exists(Path.of(path))){
            tasks = readJsonFile();
            if(indexValid(id)){
                tasks.stream().filter(task -> task.getId() == id).forEach(task -> task.setDescription(description));
                createJsonFile(formatJson(tasks));
            }

        }
    }

    public void delete(int id) {
        if(Files.exists(Path.of(path))){
            tasks = readJsonFile();
            if(indexValid(id)){
                Task toRemove = tasks.stream().filter(task -> task.getId() == id).findAny().orElse(null);
                tasks.remove(toRemove.getId()-1);
                createJsonFile(formatJson(tasks));
            }

        }
    }

    public void markTask(int id, Status status) {
        if(Files.exists(Path.of(path))){
            if(indexValid(id)){
                tasks = readJsonFile();
                tasks.stream().filter(task -> task.getId() == id).forEach(task -> task.setStatus(status));
                createJsonFile(formatJson(tasks));
            }
        }
    }

    public void listTask(String arg) {
        tasks = readJsonFile();
        List<Task> taskList;
        switch (arg){
            case "done" ->  taskList =tasks.stream().filter(task -> task.getStatus().equals(Status.DONE)).toList();
            case "todo" -> taskList = tasks.stream().filter(task -> task.getStatus() == Status.TODO).toList();
            case "in-progress" -> taskList = tasks.stream().filter(task -> task.getStatus() == Status.IN_PROGRESS).toList();
            default -> taskList = tasks;
        }
        System.out.println(formatJson(taskList));
    }

    private void createJsonFile(String jsonTask){
        try(FileWriter fileWriter = new FileWriter(path)){
            fileWriter.write(jsonTask);
        }catch (IOException e){
            System.err.println("Erreur lors de l'écriture du fichier JSON :" + e.getMessage());
        }
    }

    private List<Task> readJsonFile(){
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Nettoyage du JSON et extraction des objets
        String jsonString = stringBuilder.toString().trim();

        // Vérifier que le JSON commence bien par un tableau
        if (!jsonString.startsWith("[") || !jsonString.endsWith("]")) {
            System.err.println("Format JSON invalide !");
            return null;
        }

        // Supprimer les crochets du tableau
        jsonString = jsonString.substring(1, jsonString.length() - 1).trim();

        // Diviser chaque objet JSON (en supposant qu'ils sont séparés par "},{")
        String[] jsonObjects = jsonString.split("},\\s*\\{");

        for(String jsonObject : jsonObjects){
            // Remettre les accolades manquantes
            if (!jsonObject.startsWith("{")) jsonObject = "{" + jsonObject;
            if (!jsonObject.endsWith("}")) jsonObject = jsonObject + "}";

            //Extraire les valeurs
            int id = Integer.parseInt(extractValue(jsonObject, "id"));
            String description = extractValue(jsonObject, "description");
            Status status = Status.valueOf(extractValue(jsonObject, "status"));
            String createdAt = extractValue(jsonObject, "createdAt");
            String updatedAt = extractValue(jsonObject, "updatedAt");

            tasks.add(new Task(id, description, status, createdAt, updatedAt));

        }

        // Affichage de toutes les tâches
        return tasks;

    }

    private String extractValue(String json, String key){
        int keyIndex = json.indexOf("\""+ key +"\"");
        if(keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex);
        int startIndex = colonIndex + 1;

        while(Character.isWhitespace(json.charAt(startIndex))){
            startIndex++;
        }

        char firstChar = json.charAt(startIndex);
        int endIndex;
        if(firstChar == '"'){ // Cas d'une chaine de charactère
            startIndex++;
            endIndex = json.indexOf("\"", startIndex);
        } else {
            endIndex = json.indexOf(",", startIndex);
            if(endIndex == -1){
                endIndex = json.indexOf("}", startIndex);
            }
        }

        return json.substring(startIndex, endIndex).trim();
    }

    @Override
    public String toString() {
        return "["+
                "\"tasks\": \"" + tasks + "\","+
                ']';
    }

    private String formatJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("    {\n");
            json.append("        \"id\": ").append(task.getId()).append(",\n");
            json.append("        \"description\": \"").append(task.getDescription()).append("\",\n");
            json.append("        \"status\": \"").append(task.getStatus()).append("\",\n");
            json.append("        \"createdAt\": \"").append(task.getCreatedAt()).append("\",\n");
            json.append("        \"updatedAt\": \"").append(task.getUpdatedAt()).append("\"\n");
            json.append("    }");
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    private boolean indexValid(int id){
        if(id < tasks.size()){
            return true;
        } else {
            System.out.println("There not task with this ID : "+ id);
            return false;
        }
    }

}
