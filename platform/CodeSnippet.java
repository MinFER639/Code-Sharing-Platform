package platform;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.UUID;

@Entity
@Table(name = "codeSnippet")
public class CodeSnippet {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "code_id")
    private String id;

    @NotBlank
    @Column(name = "name")
    private String code;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    @Column(name = "time")
    private long time;

    @Column(name = "timeOfExpire")
    private LocalDateTime timeOfExpire;

    @Column(name = "views")
    private long views;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "isSecret")
    private boolean isSecret;

    public CodeSnippet() {
    }

    public CodeSnippet(String code, LocalDateTime dateTime, long time, long views) {
        this.code = code;
        this.dateTime = dateTime;
        this.time = time;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDateTime() {
        final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return this.dateTime.format(formatter);
    }

    public LocalDateTime getDateTimeAcc() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LocalDateTime getTimeOfExpire() {
        return timeOfExpire;
    }

    public void setTimeOfExpire() {
        this.timeOfExpire = this.dateTime.plusSeconds(this.time);
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean getisSecret() {
        return isSecret;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
    }

}
