package windoctor7.github.io.spring5.reactive.springwebflux2;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 19/10/2017.
 */
@Document(collection = "students")
public class Student {

    private String id;
    private String name;
    private List<Score> scores;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}
