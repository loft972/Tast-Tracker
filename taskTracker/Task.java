import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private int id;
    private String description;
    private Status status;
    private String createdAt;
    private String updatedAt;

    public Task() {}

    public Task(String description) {
        this.id = 1;
        this.description = description;
        this.status = Status.TODO;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.createdAt = LocalDateTime.now().format(formatter);
        this.updatedAt = null;
    }

    public Task(int id, String description, Status status, String createdAt, String updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.updatedAt = LocalDateTime.now().format(formatter);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \""+  id +"\"," +
                "\"description\": \""+  description +"\"," +
                "\"status\": \""+ status +"\"," +
                "\"createdAt\": \""+  createdAt +"\"," +
                "\"updatedAt\": \""+  updatedAt +"\"" +
                "}";
    }
}
